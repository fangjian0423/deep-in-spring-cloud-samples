#!/usr/bin/env bash
while true
do
    echo `curl -s -XGET http://localhost:8080/springcloud/status/500`
done