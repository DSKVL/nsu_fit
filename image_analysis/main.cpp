#include <iostream>
#include <concepts>
#include <filesystem>
#include <set>
#include <functional>
#include <algorithm>
#include <ranges>
#include <opencv4/opencv2/imgcodecs.hpp>
#include <opencv4/opencv2/core.hpp> 
#include <opencv4/opencv2/imgproc.hpp>
#include <opencv4/opencv2/highgui.hpp>
#include <opencv4/opencv2/core/types.hpp>

void removeVignette() {
  
}

template<std::regular_invocable<const cv::Mat&, cv::Mat&> op>
void apply(const cv::Mat& source, cv::Mat& destination, std::vector<op> operators) {
  if (operators.empty()) {
    destination = source;
    return;
  }
  
  cv::Mat intermediate = source;
  auto opIt = operators.begin();
  cv::Mat *d = &destination, *i = &intermediate;
  if (operators.size() >= 1) {
    std::invoke((*opIt), source, destination);
    opIt++;
  }
  for (;opIt != operators.end(); ++opIt) {
    std::invoke((*opIt), *d, *i);
    std::swap(d, i);
  }  
  destination = *d;
}

int main(int argc, char** argv) {
  using namespace std::filesystem;
  using cv::Mat, cv::MORPH_ELLIPSE, cv::Size, cv::IMREAD_GRAYSCALE, cv::Point, cv::waitKey;
  if (argc < 1) {
    std::cout << "Specify data_source";
    return 0;
  }

  auto dataDirectory = path(argv[1]);
  auto outputDirectory = path("output");
  Mat img, first, proc;
  auto ker =  getStructuringElement(MORPH_ELLIPSE, Size(5, 5));
  std::vector<path> imagesPaths;

  for (auto &entry : directory_iterator(dataDirectory))
    imagesPaths.push_back(entry.path());
  std::ranges::sort(imagesPaths);

  auto iter = imagesPaths.begin();
  first = imread(*iter, IMREAD_GRAYSCALE);

  std::function<void(const Mat&, Mat&)> 
    blur     =  [](const Mat& s, Mat& d){ GaussianBlur(s, d, Size(7, 7), 2, 2); },
    erode    = [&](const Mat& s, Mat& d){ cv::erode(s, d, ker, Point(-1, -1), 2, 1, 1); },
    dilate   = [&](const Mat& s, Mat& d){ cv::dilate(s,d,ker, Point(-1, -1), 2, 1, 1); },
    subFirst = [&](const Mat& s, Mat& d){ cv::addWeighted(s, -1.0, first, 1.0, 0, d); },
    canny    = [] (const Mat& s, Mat& d){ cv::Canny(s, d, 200, 100); };
  
  for (;iter != imagesPaths.end(); ++iter) {
    std::cout << *iter << '\n';
    img = imread(*iter, IMREAD_GRAYSCALE);
    apply(img, proc, std::vector{
      blur, canny
    });
    imshow("original", img);
    imshow("processed", proc);
    waitKey(0);
  }
}