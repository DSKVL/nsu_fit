#include <mpi.h>
#include <iostream>
#include <chrono>
#include <fstream> 
#include <algorithm>
#include <functional>
#include <cmath>
#include <limits>
#include <span>
#include <numeric>
#include <vector>

void dump(const double *mtx, const size_t nXArr, const size_t nYArr, const std::string filename) {
  auto out = std::ofstream(filename);
  for (auto jArr = nXArr; jArr < nXArr*(nYArr-1); jArr+=nXArr) {
    for (auto i = 1ul; i < nXArr-1; i++)
      out << mtx[i + jArr] <<  " ";
    out << "\n";  
  }
}
inline void countSlice(double* Phi, double* PhiN, 
     const size_t k, const size_t kStep,
     const size_t iStep, const size_t iLimit,
     const size_t jStep, const size_t jLimit,
     const double xCoef, const double yCoef, const double zCoef, const double a) {
  for (auto j = jStep; j < jLimit; j+=jStep)
    for (auto i = iStep; i < iLimit; i+=iStep) 
      PhiN[i + j + k] =       xCoef*(Phi[i + iStep + j + k] + Phi[i - iStep + j + k])
                            + yCoef*(Phi[i + j + jStep + k] + Phi[i + j - jStep + k])  
                            + zCoef*(Phi[i + j + k + kStep] + Phi[i + j + k - kStep])
                            - (6 - a*Phi[i + j + k]);  
}
inline void countFirstSlice(double* Phi, double* PhiN, double* topSlice, 
     const size_t k, const size_t kStep,
     const size_t iStep, const size_t iLimit,
     const size_t jStep, const size_t jLimit,
     const double xCoef, const double yCoef, const double zCoef, const double a) {
  for (auto j = jStep; j < jLimit; j+=jStep)
    for (auto i = iStep; i < iLimit; i+=iStep) 
      PhiN[i + j + k] =       xCoef*(Phi[i + iStep + j + k] + Phi[i - iStep + j + k])
                            + yCoef*(Phi[i + j + jStep + k] + Phi[i + j - jStep + k])  
                            + zCoef*(Phi[i + j + k + kStep] + topSlice[i + j])
                            - (6 - a*Phi[i + j + k]);  
}
inline void countLastSlice(double* Phi, double* PhiN, double* bottomSlice, 
     const size_t k, const size_t kStep,
     const size_t iStep, const size_t iLimit,
     const size_t jStep, const size_t jLimit,
     const double xCoef, const double yCoef, const double zCoef, const double a) {
  for (auto j = jStep; j < jLimit; j+=jStep)
    for (auto i = iStep; i < iLimit; i+=iStep) 
      PhiN[i + j + k] =       xCoef*(Phi[i + iStep + j + k] + Phi[i - iStep + j + k])
                            + yCoef*(Phi[i + j + jStep + k] + Phi[i + j - jStep + k])  
                            + zCoef*(Phi[i + j + k - kStep] + bottomSlice[i + j])
                            - (6 - a*Phi[i + j + k]);  
}


//возвращает вектор, в котором на итой позиции стоит количество "элементов" для итого процесса
std::vector<int> getSendcounts(int dataSize, int processNumber, int multiplier=1) {
    auto sendcounts = std::vector<int>(); // количество элементов на группу
    auto extraData = dataSize % processNumber; // количество широких столбцов/строк поширше
    auto commonData = dataSize / processNumber; // минимальная ширина/выстоа

    for (int i = 0 ; i < extraData; i++)
        sendcounts.push_back((commonData + 1) * multiplier);
    for (int i = extraData; i < processNumber; i++)
        sendcounts.push_back(commonData * multiplier);
    return sendcounts;
}

//возвращает вектор, в котором на итой позиции стоит сдвижка
std::vector<int> getDisplaces(const std::vector<int> &sendcounts) {
    auto displaces = sendcounts;
    std::exclusive_scan(sendcounts.begin(), sendcounts.end(),
                        displaces.begin(), 0);
    return displaces;
}

bool checkConvergence(double* Phi, double* PhiN, size_t phiSize, double epsilon) {
  double e =   std::transform_reduce(Phi,  Phi+phiSize,
                                     PhiN,
                                     0.0,
                                     [](const auto& a, const auto& b){return std::max(a, b);},
                                     [](const auto& a, const auto& b){return std::abs(a-b);});
  double global; 
  MPI::COMM_WORLD.Allreduce(&e, &global, 1, MPI::DOUBLE, MPI::MAX);
  std::cout << global << "\n";
  return global > epsilon;
}


int main(int argc, char** argv) {
  using namespace MPI;
  int size, rank, prev, next;
  Init();
  size = COMM_WORLD.Get_size();
  rank = COMM_WORLD.Get_rank();

  if (argc != 4) {
    std::cout << "Wrong arguments.\n";
    return 0;
  }

  const auto nX = std::stoul(std::string(argv[1]));
  const auto nY = std::stoul(std::string(argv[2]));
  const auto nZ = std::stoul(std::string(argv[3]));
  const auto iStep = 1ul;
  const auto jStep = nX + 2;
  const auto nYArr = nY + 2;
  const auto nZArr = nY + 2;
  const auto kStep = jStep*nYArr; 
  const auto arrSize = jStep*nYArr*nZArr;
  const auto jArrLimit = kStep - jStep;
  auto sendcounts = getSendcounts(nZ, size, kStep);
  auto displaces = getDisplaces(sendcounts);
  next = (rank + 1) % size;
  prev = (rank - 1) >= 0 ? rank - 1 : size - 1;
  auto phiSize = sendcounts[rank];
  const auto kArrLimit = phiSize - kStep;
  
  constexpr auto A = -1.0f;
  constexpr auto B =  1.0f;
  
  const double hX = (B - A)/(nX - 1);
  const double hY = (B - A)/(nY - 1);
  const double hZ = (B - A)/(nZ - 1);

  const double a = 100000.0f;
  const double epsilon = 0.000001;
  const auto commonMultiplier = 1/(2/(hX*hX) + 2/(hY*hY) + 2/(hZ*hZ) + a); 
  const auto xCoef = commonMultiplier/(hX*hX);
  const auto yCoef = commonMultiplier/(hY*hY);
  const auto zCoef = commonMultiplier/(hZ*hZ);
 
  
  Request reqNextRecv, reqPrevRecv, reqNextSend, reqPrevSend;
  auto Phi = new double[phiSize];
  auto PhiN = new double[phiSize];
  auto prevSlice = new double[kStep];
  auto nextSlice = new double[kStep];

  auto localnZ = phiSize/kStep;
  for (auto k = 0; k < localnZ; k++)
    for (auto j = 0; j < nY+2; j++)
      for (auto i = 0; i < nX+2; i++) 
        if (  k == 0 && rank == 0
          ||  k == localnZ-1 && rank == size-1
          ||  i == 0 || i == nX-1 || j == 0 || j == nY-1) {
          auto kvalue = (k-1+displaces[rank])*hZ;
          auto value = ((i-1)*hX)*((i-1)*hX) + ((j-1)*hY)*((j-1)*hY) + kvalue*kvalue; 
          Phi[i+j*jStep+k*kStep] = value;
        }
  if (rank == 0) dump(Phi, nX+2, nYArr, "check");
    
  if (rank != 0){
    reqPrevSend = COMM_WORLD.Isend(PhiN, kStep, DOUBLE, prev, 0);
    reqPrevRecv = COMM_WORLD.Irecv(prevSlice, kStep, DOUBLE, prev, 0);
  }
  if (rank != size-1) {
    reqNextRecv = COMM_WORLD.Irecv(nextSlice, kStep, DOUBLE, next, 0);
    reqNextSend = COMM_WORLD.Isend(PhiN + kArrLimit-kStep, kStep, DOUBLE, next, 0);
  }
  
  while (checkConvergence(Phi, PhiN, phiSize, epsilon)) {
    auto k = 0ul;
    if (rank != 0){
      reqPrevRecv.Wait();
      countFirstSlice(Phi, PhiN, prevSlice, kStep, kStep, iStep, nX + 1, jStep, jArrLimit, xCoef, yCoef, zCoef, a);
      reqPrevSend = COMM_WORLD.Isend(PhiN, kStep, DOUBLE, prev, 0);
      reqPrevRecv = COMM_WORLD.Irecv(prevSlice, kStep, DOUBLE, prev, 0);
    }
    k+=kStep;
    for (; k < kArrLimit - kStep; k+=kStep)
      countSlice(Phi, PhiN, k, kStep, iStep, nX + 1, jStep, jArrLimit, xCoef, yCoef, zCoef, a);

    if (rank != size-1) {
      reqNextRecv.Wait();
      countLastSlice(Phi, PhiN, nextSlice, k, kStep, iStep, nX + 1, jStep, jArrLimit, xCoef, yCoef, zCoef, a);
      reqNextRecv = COMM_WORLD.Irecv(nextSlice, kStep, DOUBLE, next, 0);
      reqNextSend = COMM_WORLD.Isend(PhiN + k, kStep, DOUBLE, next, 0);
    }
    std::swap(PhiN, Phi);
  }

  Finalize();
  
  delete[] Phi;
  delete[] PhiN;
  delete[] prevSlice;
  delete[] nextSlice;    
  
  return 0;
}
