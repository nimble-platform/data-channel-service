#!/bin/bash

if [[ "$1" = "gost" ]]; then
	export HOSTNAME=`hostname`
	docker-compose --project-name sensorthingsserver up --build --force-recreate
fi