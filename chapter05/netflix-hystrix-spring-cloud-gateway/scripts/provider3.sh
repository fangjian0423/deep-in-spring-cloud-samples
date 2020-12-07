#!/usr/bin/env bash
while true
do
    echo `curl -s -XGET http://localhost:8080/s-c-alibaba/status/500`
done