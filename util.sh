#!/bin/bash

set -e    # Exits immediately if a command exits with a non-zero status.

if [[ "$1" = "local" ]]; then
	export HOSTNAME=`hostname`
	docker-compose --project-name data-channel-setup up --build --force-recreate

elif [ "$1" == "build" ]; then

    mvn clean install -DskipTests

elif [ "$1" == "docker-build" ]; then

    mvn -f data-channel-service/pom.xml docker:build

elif [ "$1" == "docker-push" ]; then

    mvn -f data-channel-service/pom.xml docker:push

elif [ "$1" == "stage" ]; then

    mvn clean install -DskipTests
    mvn -f data-channel-service/pom.xml docker:build -DdockerImageTag=staging
    mvn -f data-channel-service/pom.xml docker:push -DdockerImageTag=staging
fi