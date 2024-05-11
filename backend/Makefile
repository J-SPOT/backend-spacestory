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

build-dev:
	./script/build_dev.sh

build-prod:
	./script/build_prod.sh

run-dev:
	./script/run_dev.sh

run-prod:
	./script/run_prod.sh

.phony: all up restart build-dev build-prod run-dev run-prod
