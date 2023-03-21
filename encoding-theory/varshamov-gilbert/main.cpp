#include <functional>
#include <vector>
#include <algorithm>
#include <iostream>
#include <fstream>
#include <cmath>
#include <optional>
#include <numeric>
#include <ranges>
#include <chrono>

class vector {
public:
  static void setQ(unsigned p);
  static unsigned q;

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

  friend vector operator+(const vector &, const vector &);
  friend vector operator-(const vector &, const vector &);
  friend vector operator*(unsigned, const vector &);
  friend vector operator/(unsigned, const vector &);
private:
  std::vector<uint32_t> word;
  static std::vector<unsigned> inverses;
  static std::vector<std::vector<unsigned>> products;
  static std::vector<std::vector<unsigned>> sums;
  static std::vector<std::vector<unsigned>> differences;

};

unsigned vector::q = 2;
std::vector<unsigned> vector::inverses{0, 1};
std::vector<std::vector<unsigned>> vector::products{{0, 0}, {0, 1}};
std::vector<std::vector<unsigned>> vector::sums{{0, 1}, {1, 0}};
std::vector<std::vector<unsigned>> vector::differences{{0, 1}, {1, 0}};

void vector::setQ(unsigned p) {
  vector::q = p;
  for (auto j = 0u; j < q; j++)
    for (auto i = 0u; i < q; i++){
      vector::products[j][i] = (i*j)%q;
      vector::sums[j][i]     = (i+j)%q;
      vector::differences[j][i] = (j-i)%q;
    }

  inverses[1] = 1;
  for (auto i{2u}; i < p; ++i)
    inverses[i] = (p - (p / i) * inverses[p % i] % p) % p;
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

vector operator+(const vector &left, const vector &right) {
  auto result = left;
  std::ranges::transform(left, right, result.begin(),
                         [](const auto &x, const auto &y) { return vector::sums[x][y]; });
  return result;
}

vector operator-(const vector &left, const vector &right) {
  auto result = left;
  std::ranges::transform(left, right, result.begin(),
                         [](const auto &x, const auto &y) { return vector::differences[x][y]; });
  return result;
}

vector operator*(const unsigned int scalar, const vector &vec) {
  auto result = vec;
  std::ranges::transform(result, result.begin(),
                         [=](const auto &x) { return vector::products[scalar][x]; });
  return result;
}

vector operator/(unsigned scalar, const vector &v) {
  auto result = v;
  return result /= vector::inverses[scalar];
}

vector &vector::operator+=(const vector &other) {
  std::ranges::transform(*this, other, begin(),
                         [=](const auto &x, const auto &y) { return vector::sums[x][y]; });
  return *this;
}

vector &vector::operator-=(const vector &other) {
  std::ranges::transform(*this, other, begin(),
                         [=](const auto &x, const auto &y) { return vector::differences[x][y]; });
  return *this;
}

vector &vector::operator*=(const unsigned int scalar) {
  std::ranges::transform(*this, begin(),
                         [=](const auto &x) { return vector::products[scalar][x]; });
  return *this;
}

vector &vector::operator/=(const unsigned int scalar) {
  return *this *= inverses[scalar];
}

auto gaussElimination(std::vector<vector> &matrix) {
  auto column = 0u;
  auto n = matrix[0].size();
  for (auto it = matrix.begin(); it != matrix.end() && column != n; it++, column++) {
    auto nonzeroRowIter = std::find_if(it, matrix.end(),
                                       [=](const auto &v) { return v[column] != 0; });
    if (nonzeroRowIter == matrix.end())
      continue;

    std::iter_swap(it, nonzeroRowIter);
    (*it) /= (*it)[column];
    std::transform(it + 1, matrix.end(), it + 1,
                   [=](auto &v) { return v -= v[column] * (*it); });

  }
  (void) std::remove_if(matrix.begin(), matrix.end(),
                        [](const auto &v) {
                            return std::transform_reduce(
                                    v.begin(), v.end(), true,
                                    std::logical_and<>(),
                                    [](const auto &el) { return el == 0; });
                        });
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
  for (auto i{0u}; i < d - 2; i++) sum += binomialCoefficient(n - 1, i);
  return sum < std::pow(q, r);
}

auto selectVectors(std::vector<vector> vectors, const std::vector<bool> &selected) {
//  zip(vectors, selected) | filter([](auto pair){ return pair.second; })
//                         | transform([](auto pair){ return pair.first; });
//  return vectors;
  return std::transform_reduce(vectors.begin(), vectors.end(),
                               selected.begin(),
                               std::vector<vector>(),
                               [](std::vector<vector> vec, std::vector<vector> result) {
                                   result.insert(result.end(), vec.begin(), vec.end());
                                   return result;
                               },
                               [](auto &vec, const auto &isSelected) -> std::vector<vector> {
                                   if (isSelected) return {vec};
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
    std::fill_n(selectedPositions.begin(), std::min(d - 1, (uint32_t) result.size()), true);
    std::reverse(selectedPositions.begin(), selectedPositions.end());
    auto notLC = true;
    do
      if (aLinearCombinationOf(vec, selectVectors(result, selectedPositions)))
        notLC = false;
    while (notLC && std::next_permutation(selectedPositions.begin(), selectedPositions.end()));
    if (notLC)
      result.push_back(vec);
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


int main() {
  auto in = std::ifstream("input.txt");
  uint32_t n, r, d, q;
  in >> n >> r >> d >> q;
  auto start = std::chrono::steady_clock::now();
  vector::setQ(q);
  auto pseudoCheckMatrix = generatePseudoCheckMatrix(n, r, d, q);
  if (!pseudoCheckMatrix.has_value())
    return 0;
  auto H = transpose(pseudoCheckMatrix.value());
  gaussElimination(H);
  d = findD(d, H);
  auto end = std::chrono::steady_clock::now();

  auto out = std::ofstream("output.txt");
  auto parameters = std::ofstream("parameters.txt");

  auto printElem = [&](const auto &el) { out << el << " "; };
  auto print = [&](const auto &v) {
      std::ranges::for_each(v, printElem);
      out << "\n";
  };
  std::ranges::for_each(H, print);

  parameters << H[0].size() + H.size() << " " << H[0].size() << " " << d << "\n";
  parameters << "Time: " << std::chrono::duration_cast<std::chrono::milliseconds>(end - start).count() << "ms";
}