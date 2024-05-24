all:
	up

up: 
	docker compose --file ./docker-compose.yml up --detach --build

stop:
	docker compose --file ./docker-compose.yml stop

down:
	docker compose --file ./docker-compose.yml down --remove-orphans	

restart:
	docker restart $$(docker ps -q)

build:
	./gradlew clean && ./gradlew build

run-dev:
	java -jar -Dspring.profiles.active=dev ./build/libs/spacestory-0.0.1-SNAPSHOT.jar

run-prod:
	nohup java -jar -Dspring.profiles.active=prod ./build/libs/spacestory-0.0.1-SNAPSHOT.jar

.phony: all up restart build run-dev run-prod
