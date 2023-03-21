#include <algorithm>
#include <iostream>
#include <fstream>
#include <cmath>
#include <optional>
#include <numeric>
#include <chrono>
#include <vector>

class vector {
public:
  static void setQ(unsigned p, std::vector<unsigned> &,
                   std::vector<unsigned> &,
                   std::vector<unsigned> &,
                   std::vector<unsigned> &,
                   std::vector<unsigned> &);
  static unsigned q;
  static std::vector<unsigned> *sums, *products, *differences, *divs;

  vector() = default;
  explicit vector(const unsigned n) : word(n, 0) {}
  vector(const vector &) = default;
  vector &operator=(const vector &) = default;
  vector(vector &&) = default;
  vector &operator=(vector &&) = default;

  void prev();
  [[nodiscard]] auto size() const { return word.size(); }

  bool operator==(const vector &) const = default;
  bool operator!=(const vector &) const = default;
  auto operator<=>(const vector &) const = default;

  unsigned &operator[](size_t idx) { return word[idx]; }
  unsigned operator[](size_t idx) const { return word[idx]; }
  [[nodiscard]] auto begin() const { return word.begin(); }
  [[nodiscard]] auto end() const { return word.end(); }
  auto begin() { return word.begin(); }
  auto end() { return word.end(); }

  vector &operator-=(const vector &);
  vector &operator+=(const vector &);
  vector &operator*=(unsigned);
  vector &operator/=(unsigned);
  friend vector operator+(const vector &l,  const vector &r) { return vector(l) += r; }
  friend vector operator-(const vector &l,  const vector &r) { return vector(l) -= r; }
  friend vector operator*(const unsigned s, const vector &v) { return vector(v) *= s; }
  friend vector operator/(const unsigned s, const vector &v) { return vector(v) /= s; }

private:
  std::vector<uint32_t> word;
};

unsigned vector::q = 2;
std::vector<unsigned> *vector::sums, *vector::products,
                      *vector::differences, *vector::divs;

void vector::setQ(unsigned p, std::vector<unsigned> &inver,
                  std::vector<unsigned> &s,
                  std::vector<unsigned> &pr,
                  std::vector<unsigned> &dif,
                  std::vector<unsigned> &div) {
  vector::sums = &s;
  vector::products = &pr;
  vector::differences = &dif;
  vector::divs = &div;
  vector::q = p;

  inver[1] = 1;
  for (auto i{2u}; i < p; ++i)
    inver[i] = (p - (p / i) * inver[p % i] % p) % p;

  for (auto i{0u}; i < p; i++)
    for (auto j{0u}; j < p; j++) {
      s[i * p + j] = (i + j) % p;
      pr[i * p + j] = (i * j) % p;
      dif[i * p + j] = (i - j) % p;
      div[i * p + j] = pr[i * p + inver[j]];
    }
}

void vector::prev() {
  for (auto i = 0u; i < word.size(); i++) { // NOLINT(modernize-loop-convert)
    if (word[i] != 0) {
      word[i]--;
      break;
    }
    word[i] = q - 1;
  }
}

inline unsigned add(const uint32_t x, const uint32_t y) {
  return (*vector::sums)[x*vector::q+y]; }
vector &vector::operator+=(const vector &other) {
  std::transform(begin(), end(), other.begin(), begin(), add);
  return *this;
}
inline unsigned sub(const uint32_t x, const uint32_t y) {
  return (*vector::differences)[x*vector::q+y]; }
vector &vector::operator-=(const vector &other) {
  std::transform(begin(), end(), other.begin(), begin(), sub);
  return *this;
}
vector &vector::operator*=(const unsigned int s) {
  std::transform(begin(), end(), begin(), [=](const auto &x) {
                     return (*vector::products)[s*vector::q+x]; });
  return *this;
}
vector &vector::operator/=(const unsigned int scalar) {
  std::transform(begin(), end(), begin(), [=](const auto &x) {
                     return (*vector::divs)[x*vector::q+scalar]; });
  return *this;
}

void print(std::vector<vector> &H, std::ofstream &out) {
  auto printElem = [&](const auto &el) { out << el << " "; };
  auto print = [&](const auto &v) {
      std::ranges::for_each(v.begin(), v.end(), printElem);
      out << "\n";
  };
  std::ranges::for_each(H.begin(), H.end(), print);
}

auto gaussElimination(std::vector<vector> &matrix) {
  auto column = 0u;
  auto n = matrix[0].size();
  for (auto it = matrix.begin(); column != n; column++) {
    auto nonzeroRow= std::find_if(it, matrix.end(),
                                  [=](const auto &v) { return v[column] != 0; });
    if (nonzeroRow == matrix.end())
      continue;

    std::iter_swap(it, nonzeroRow);
    (*it) /= (*it)[column];
    std::transform(it + 1, matrix.end(), it + 1,
                   [&](auto &v) { return v -= v[column] * (*it); });
    it++;
  }
  std::erase_if(matrix, [](const auto &v) { return std::transform_reduce(
          v.begin(), v.end(), true,
          std::logical_and<>(),
          [](const auto &el) { return el == 0; }); });
}

bool aLinearCombinationOf(const vector &vec, const std::vector<vector> &vectors) {
  auto matrix = std::vector<vector>{vec};
  matrix.insert(matrix.end(), vectors.begin(), vectors.end());
  auto size = matrix.size();
  gaussElimination(matrix);
  return size != matrix.size();
}

auto binomialCoefficient(const uint32_t n, const uint32_t k) {
  auto res = 1.0;
  for (auto i{1u}; i <= k; ++i)
    res = res * (n - k + i) / i;
  return (int) (res + 0.01);
}

auto satisfiesVarshamovGilbertBorder(const uint32_t n, const uint32_t r,
                                     const uint32_t d, const uint32_t q) {
  uint32_t sum = 0;
  for (auto i{0u}; i < d - 1; i++)
    sum += binomialCoefficient(n - 1, i) *
           (uint32_t) (pow(q - 1, i) + 0.01);
  bool ok = sum < std::pow(q, r);
  if (!ok) std::cout << sum << ">=" << std::pow(q, r) << "\n";
  return ok;
}

auto selectVectors(std::vector<vector> v, const std::vector<bool> &s) {
  return std::transform_reduce(v.begin(), v.end(), s.begin(),
                               std::vector<vector>(),
                               [](std::vector<vector> vec, std::vector<vector> res) {
                                   res.insert(res.end(), vec.begin(), vec.end());
                                   return res;
                               },
                               [](auto &vec, const auto &isSel) -> std::vector<vector> {
                                   if (isSel) return {vec};
                                   return {};
                               });
}

std::optional<std::vector<vector>>
generatePseudoCheckMatrix(const uint32_t n, const uint32_t r,
                          const uint32_t d, const uint32_t q) {
  if (!satisfiesVarshamovGilbertBorder(n, r, d, q)) return {};

  auto result = std::vector<vector>();
  auto minVector = vector(r);
  auto maxVector = vector(r);
  std::fill(maxVector.begin(), maxVector.end(), q - 1);

  for (auto vec{maxVector}; vec != minVector && result.size() != n; vec.prev()) {
    auto selectedPositions = std::vector<bool>(result.size(), false);
    std::fill_n(selectedPositions.begin(), std::min(d - 2, (uint32_t) result.size()), true);
    std::reverse(selectedPositions.begin(), selectedPositions.end());
    auto notLC = true;
    do if (aLinearCombinationOf(vec, selectVectors(result, selectedPositions)))
        notLC = false;
    while (notLC && std::next_permutation(selectedPositions.begin(), selectedPositions.end()));
    if (notLC) {
      result.push_back(vec);
      //std::cout << result.size() << " so far.\n";
    }
  }

  return result;
}

auto transpose(const std::vector<vector> &vectors) {
  auto result = std::vector<vector>(vectors[0].size(), vector((unsigned) vectors.size()));
  for (auto j{0u}; j < vectors.size(); j++)
    for (auto i{0u}; i < vectors[j].size(); i++)
      result[i][j] = vectors[j][i];
  return result;
}

auto findD(unsigned d, const std::vector<vector> &H) {
  auto Ht = transpose(H);
  for (; d < Ht.size(); d++) {
    auto selectedPositions = std::vector<bool>(Ht.size(), false);
    std::fill_n(selectedPositions.begin(), std::min(d, (uint32_t) Ht.size()), true);
    std::reverse(selectedPositions.begin(), selectedPositions.end());
    do {
      auto selected = selectVectors(Ht, selectedPositions);
      auto size = selected.size();
      gaussElimination(selected);
      if (selected.size() != size)
        return d;
    } while (std::next_permutation(selectedPositions.begin(), selectedPositions.end()));
  }
  return d;
}

int main(int argc, char** argv) {
  uint32_t n, r, d, q;
  if (argc <= 1) {
    auto in = std::ifstream("input.txt");
    in >> n >> r >> d >> q;
  } else {
    if (argc != 5) std::cout << "Формат аргументов: n r d q";
    n = std::stoul(argv[1]);
    r = std::stoul(argv[2]);
    d = std::stoul(argv[3]);
    q = std::stoul(argv[4]);
  }
  auto inv = std::vector(q, 0u);
  auto sum = std::vector(q * q, 0u);
  auto prod = std::vector(q * q, 0u);
  auto dif = std::vector(q * q, 0u);
  auto div = std::vector(q * q, 0u);

  auto start = std::chrono::steady_clock::now();
  vector::setQ(q, inv, sum, prod, dif, div);
  auto pseudoCheckMatrix = generatePseudoCheckMatrix(n, r, d, q);
  if (!pseudoCheckMatrix.has_value()) {
    std::cout << n << " " << r << " " << d << " garbage in";
    return 0;
  }
  auto H = transpose(pseudoCheckMatrix.value());
  gaussElimination(H);
  auto actualD = findD(d, H);
  auto end = std::chrono::steady_clock::now();

  auto millis = std::chrono::duration_cast<std::chrono::milliseconds>(end - start).count();

  if (argc <= 1) {
    auto out = std::ofstream("output.txt");
    print(H, out);
    auto paramsOut = std::ofstream("parameters.txt");
    paramsOut << H[0].size() << " " << H[0].size() - H.size() << " " << d << "\n";
    paramsOut << "Time: " << millis<< "ms";
    return 0;
  }
  std::cout << n << " " << r << " " << d << " "
            << H[0].size() << " " << H[0].size() - H.size() << " "
            << actualD << "\n";
  std::cerr << n << " " << r << " " << d << " " << millis << "\n";
}