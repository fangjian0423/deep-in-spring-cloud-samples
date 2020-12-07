#!/usr/bin/env bash
curl -v -H "LANG:zh-cn" http://localhost:8080/httpbin/status/500
#*   Trying ::1...
#* TCP_NODELAY set
#* Connected to localhost (::1) port 8080 (#0)
#> GET /httpbin/status/500 HTTP/1.1
#> Host: localhost:8080
#> User-Agent: curl/7.54.0
#> Accept: */*
#> LANG:zh-cn
#>
#< HTTP/1.1 429 Too Many Requests
#< Content-Type: application/json;charset=UTF-8
#< Content-Length: 64
#<
#* Connection #0 to host localhost left intact
#{"code":429,"message":"Blocked by Sentinel: ParamFlowException"}
curl -v -H "LANG1:zh-cn" http://localhost:8080/httpbin/status/500
#*   Trying ::1...
#* TCP_NODELAY set
#* Connected to localhost (::1) port 8080 (#0)
#> GET /httpbin/status/500 HTTP/1.1
#> Host: localhost:8080
#> User-Agent: curl/7.54.0
#> Accept: */*
#> LANG1:zh-cn
#>
#< HTTP/1.1 500 Internal Server Error
#< Date: Wed, 26 Feb 2020 16:45:37 GMT
#< Content-Type: text/html; charset=utf-8
#< Content-Length: 0
#< Server: gunicorn/19.9.0
#< Access-Control-Allow-Origin: *
#< Access-Control-Allow-Credentials: true
