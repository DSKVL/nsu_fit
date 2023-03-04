#include <iostream>
#include <algorithm>
#include <set>
#include <bitset>
#include <vector>
#include <algorithm>
#include <numeric>
#include <functional>
#include <ranges>

std::string formatCode(std::set<std::vector<bool>> code) {
  auto res = std::string("$$\\left(\n\\begin{array}{ccccccc}\n");
  for (auto word : code) {  
    for (auto c : word) 
      res.append(" " + (c ? std::string("1") : std::string("0")) + " &");
    
    res.back() = '\\';
    res.append("\\\n");
  }
  res.append("\\end{array}\n\\right)$$");
  return res;
}

auto pinOut(std::set<std::vector<bool>> code, int pos) {
  std::set<std::vector<bool>> result;
  for (auto word : code) {
    word.erase(word.begin() + pos);
    result.insert(word);  
  }
  return result;
}

auto throwOut(std::set<std::vector<bool>> code) {
  code | std::ranges::views::filter([](std::vector<bool> a){ return a % 2 == 0; })

}

int main() {
  std::vector<std::vector<bool>> hammingCodeVec{
    {1, 0, 0, 0, 0, 1, 1},
    {0, 1, 0, 0, 1, 0, 1},
    {0, 0, 1, 0, 1, 1, 0},
    {0, 0, 0, 1, 1, 1, 1}
  };

  std::set<std::vector<bool>> hammingCode(hammingCodeVec.begin(), hammingCodeVec.end());

  for (const auto& a : hammingCode)
    for (auto b : hammingCode) {
      std::transform(b.begin(), b.end(), 
                     a.begin(), b.begin(),
                     std::bit_xor<>());
      hammingCode.insert(b);
    }

  for (auto i{0}; i < 7; i++)
    std::cout << formatCode(pinOut(hammingCode, i));
}
