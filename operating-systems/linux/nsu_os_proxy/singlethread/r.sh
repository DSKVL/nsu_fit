#!/bin/bash
for i in {1..512};
do wget http://www.ccfit.nsu.ru/~rzheutskiy/test_files/500mb.dat --no-http-keep-alive --limit-rate=500 --timeout=3600 -e use_proxy=on -e http_proxy=127.0.0.1:10003 -O /dev/null &
done