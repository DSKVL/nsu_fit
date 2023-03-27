#!/bin/bash
for n in {2..30}; 
do
  for r in $( seq 2 1 $((n-1)) );
  do
    ./vg $n $r $1 2 1>> distances 2>>times 
  done
done

