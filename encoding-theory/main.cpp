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
  res.append("\\end{array}\n\\right)$$\n");
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
  std::set<std::vector<bool>> result;
  std::copy_if(code.begin(), code.end(),
               std::inserter(result, result.end()),
               [](const auto& word) { 
                  return std::accumulate(word.begin(), word.end(),
                                         true, std::bit_xor<>());});
  return result;
}

auto shorten(std::set<std::vector<bool>> code, int pos, bool val) {
  std::set<std::vector<bool>> result;
  std::copy_if(code.begin(), code.end(),
               std::inserter(result, result.end()),
               [=](const auto& word) { return word.at(pos) == val;});
  return pinOut(result, pos);
}

auto add(std::vector<bool> vec1, std::vector<bool> vec2) {
  std::transform(vec1.begin(), vec1.end(),
                 vec2.begin(), vec1.begin(),
                 std::bit_xor<>());
  return vec1;
}

auto plotkin(std::set<std::vector<bool>> code1, std::set<std::vector<bool>> code2) {
  std::set<std::vector<bool>> result;
  for (const auto& word2 : code2)
    for (auto word1 : code1) {
      auto sum = add(word1, word2);
      word1.insert(word1.end(), sum.begin(), sum.end());
      result.insert(word1);
    }
  return result;
}

auto generateEnBasis(int n) {
  std::set<std::vector<bool>> result;
  std::vector<bool> basisVector(n, false);
  basisVector[0] = true;
  for (int i = 0; i < n; i++) {
    result.insert(basisVector);
    std::rotate(basisVector.begin(), basisVector.begin()+1, basisVector.end());
  }
  return result;
}

auto generateLinearSpan(std::set<std::vector<bool>> linearSpan) {
  for (const auto& a : linearSpan)
    for (auto b : linearSpan) {
      std::transform(b.begin(), b.end(), 
                     a.begin(), b.begin(),
                     std::bit_xor<>());
      linearSpan.insert(b);
    }
  return linearSpan;
}

auto addParityBit(std::set<std::vector<bool>> code) {
  std::for_each(code.begin(), code.end(),[](auto word){ 
    word.insert(word.end(), 
                std::accumulate(word.begin(), word.end(), false, std::bit_xor<>()));
    });
  return code;
}

int main() {
  std::vector<std::vector<bool>> hammingCodeVec{
    {1, 0, 0, 0, 0, 1, 1},
    {0, 1, 0, 0, 1, 0, 1},
    {0, 0, 1, 0, 1, 1, 0},
    {0, 0, 0, 1, 1, 1, 1}
  };

  auto hammingCodeBasis = std::set<std::vector<bool>>(hammingCodeVec.begin(), hammingCodeVec.end());
  auto EnBasis = generateEnBasis(8);  
  
  std::cout << formatCode(plotkin(EnBasis, addParityBit(hammingCodeBasis)));
}
