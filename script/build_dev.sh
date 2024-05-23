#!/bin/zsh
set -a
source .env
set +a

./gradlew clean && ./gradlew build -Penv=dev
