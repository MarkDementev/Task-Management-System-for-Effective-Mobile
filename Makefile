setup:
	./gradlew wrapper --gradle-version 8.11.1

build:
	./gradlew clean build

start:
	docker compose up

install:
	./gradlew installDist

test:
	./gradlew test


.PHONY: build