#!/usr/bin/env bash
n=1
while [ $n -le 2500 ]
do
    echo `curl -s http://localhost:8085/echo`
    let n++
done
