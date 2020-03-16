# Data Channel Service
**ServiceID**: data-channel-service

Service for managing data channel negotiations and security for internal datachannel.
  
## Setup

Run `mvn clean install` in the root of the project for building the application.

### Docker

The file `docker-compose.yml` sets up a composition of necessary services (e.g. GOST), which can be used for local development.

Run

```
./util.sh local
```

for building the application and start necessa.ry services via Docker.

