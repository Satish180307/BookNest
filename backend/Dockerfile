FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/booknest-backend-1.0.0.jar app.jar

EXPOSE 10000

# Network test: check if we can reach Aiven before starting the app
RUN echo '#!/bin/bash\n\
echo "=== DNS resolution ==="\n\
getent hosts booknest-db-booknest-db.e.aivencloud.com\n\
echo "=== Testing connection to Aiven ==="\n\
timeout 5 bash -c "cat < /dev/null > /dev/tcp/booknest-db-booknest-db.e.aivencloud.com/14605" \
&& echo "TCP CONNECTION: SUCCESS" \
|| echo "TCP CONNECTION: FAILED"\n\
echo "=== Starting app ==="\n\
exec java -jar app.jar' > /app/start.sh && chmod +x /app/start.sh

ENTRYPOINT ["/app/start.sh"]
