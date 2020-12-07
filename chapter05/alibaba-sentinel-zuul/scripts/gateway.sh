#!/usr/bin/env bash
curl -H "LANG:zh-cn" http://localhost:8080/springcloud
# {"code":403, "message":"Provider2 Block", "route":"my-provider2"}
curl http://localhost:8080/dubbo
# {"code":403, "message":"Provider1 Block", "route":"my-provider1"}
curl http://localhost:8080/s-c-alibaba\?name\=2
# {"code":403, "message":"Sentinel Block", "route":"my-provider3"}