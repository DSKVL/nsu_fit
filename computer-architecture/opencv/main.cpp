#include <iostream>
#include <opencv2/opencv.hpp>

int main() {
    cv::Mat frame, input_frame, output_frame;
    cv::VideoCapture capture;

    cv::Point processing_time(100,14);
    cv::Point capture_time(0,14);
    cv::Point previous_frame_display_time(0, 28);
    cv::Point fps_pos(100, 28);

    capture.open(0, cv::CAP_ANY);
    if (!capture.isOpened())
        return 1;

    capture.read(frame);

    int depth = frame.depth();

    cv::Mat kernel = cv::Mat::ones( 5, 5, CV_32F )/ (float)(5*5);

    auto v = static_cast<double>(cv::getTickCount());
    auto fps_ticks_1 = static_cast<double>(cv::getTickCount());

    while(1)
    {
        auto fps_ticks_2 = fps_ticks_1;
        fps_ticks_1 = static_cast<double>(cv::getTickCount());
        auto fps = cv::getTickFrequency()/(fps_ticks_1 - fps_ticks_2);

        auto t = static_cast<double>(cv::getTickCount());
        capture.read(frame);
        if (frame.empty())
            return 1;
        t = (static_cast<double>(cv::getTickCount()) - t)/cv::getTickFrequency();

        input_frame = frame.clone();

        auto u = static_cast<double>(cv::getTickCount());
//      cv::fastNlMeansDenoisingColored(input_frame, output_frame);
//      cv::blur(input_frame, output_frame, cv::Size(11,11));
        cv::filter2D(input_frame, output_frame, depth, kernel);
        u = (static_cast<double>(cv::getTickCount()) - u)/cv::getTickFrequency();

        cv::putText(output_frame, std::to_string(u), processing_time, cv::FONT_HERSHEY_COMPLEX, 0.5, cv::Scalar(170, 170, 170));
        cv::putText(output_frame, std::to_string(t), capture_time, cv::FONT_HERSHEY_COMPLEX, 0.5, cv::Scalar(170, 170, 170));
        cv::putText(output_frame, std::to_string(v), previous_frame_display_time, cv::FONT_HERSHEY_COMPLEX, 0.5, cv::Scalar(170, 170, 170));
        cv::putText(output_frame, std::to_string(fps), fps_pos, cv::FONT_HERSHEY_COMPLEX, 0.5, cv::Scalar(170, 170, 170));

        v = static_cast<double>(cv::getTickCount());
        cv::imshow("processed",output_frame);
        v = (static_cast<double>(cv::getTickCount()) - v)/cv::getTickFrequency();

        if(cv::waitKey(1) >= 0)
             break;
    }

    return 0;
}
