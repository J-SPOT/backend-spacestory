#!/bin/zsh

set -a
source .env
set +a

nohup java -jar -Dspring.profiles.active=prod ./build/libs/spacestory-0.0.1-SNAPSHOT.jar
