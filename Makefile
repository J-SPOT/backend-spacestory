all:
	up

up: 
	./gradlew clean && ./gradlew build && ./gradlew build test

.phony: all up
