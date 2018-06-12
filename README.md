# Data Channel Service
**ServiceID**: data-channel-service

Service for managing data channel contracts.
  
## Setup

Run `mvn clean install` in the root of the project for building the application.

### Docker

The file `docker-compose.yml` sets up a composition of necessary services (e.g. GOST), which can be used for local development.

Run

```
./util.sh local
```

for building the application and start necessa.ry services via Docker.

**Depencencies**:

* **Docker**: Version 18.03.0-ce
* **Docker-compose**: Version 1.20
* **Apache Maven**: Version 3.5.3

### Important Endpoints

* **Swagger Documentation**: [http://localhost:9099/swagger-ui.html#/](http://localhost:9099/swagger-ui.html#/)
* **GOST Dashboard**: [http://localhost:8081/#/things](http://localhost:8081/#/things)

