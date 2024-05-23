#!/bin/zsh

set -a
source .env
set +a

java -jar -Denv=dev -Dspring.profiles.active=dev ./build/libs/spacestory-0.0.1-SNAPSHOT.jar
