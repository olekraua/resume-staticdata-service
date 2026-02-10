# staticdata-service

Standalone repository for the staticdata-service microservice.

## Local build

```bash
./mvnw -pl microservices/backend/services/staticdata-service -am -Dmaven.test.skip=true package
```

## Local run

```bash
./mvnw -pl microservices/backend/services/staticdata-service -am spring-boot:run
```

## Included modules

- shared
- staticdata
- profile
- notification
- auth
- media
- web
- microservices/backend/services/staticdata-service

