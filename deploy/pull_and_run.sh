#!/bin/bash
set -euo pipefail

REG="shympyo.kr.ncr.ntruss.com"
IMG="account-service"
TAG="${IMAGE_TAG:-latest}"   # 파이프라인에서 SB_BUILD_NUMBER 넘겨받으면 그 값 사용

/usr/bin/docker login "$REG" -u "$ACCESS_KEY" -p "$SECRET_KEY"
/usr/bin/docker pull "$REG/$IMG:$TAG"

/usr/bin/docker run -d --name "$IMG" \
  --restart=unless-stopped \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  "$REG/$IMG:$TAG"
