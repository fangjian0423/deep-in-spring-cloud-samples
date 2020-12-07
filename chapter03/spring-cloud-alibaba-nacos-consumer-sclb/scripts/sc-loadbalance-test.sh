#!/usr/bin/env bash
while true
do
    echo `curl -s -XGET http://localhost:8084/echo`
done