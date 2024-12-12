build:
	./gradlew clean build

start:
	docker compose up

test:
	./gradlew test


.PHONY: build