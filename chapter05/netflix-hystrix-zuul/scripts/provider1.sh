#!/usr/bin/env bash
while true
do
    echo `curl -s -XGET http://localhost:8080/dubbo/123`
done