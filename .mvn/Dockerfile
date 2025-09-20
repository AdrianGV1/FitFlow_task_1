# ========================
# Etapa de build (Maven)
# ========================
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos el POM y resolvemos dependencias en caché
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests dependency:go-offline

# Copiamos el código
COPY src ./src

# Evita fallo del plugin graphqlcodegen si no existe la carpeta/archivos
# Crea la carpeta y un schema mínimo SOLO si falta.
RUN test -d src/main/resources/graphql-client || mkdir -p src/main/resources/graphql-client
RUN if [ -z "$(ls -A src/main/resources/graphql-client 2>/dev/null)" ]; then \
      printf 'type Query { _ping: String }\n' > src/main/resources/graphql-client/schema.graphqls; \
    fi

# Compila el proyecto
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests clean package

# ========================
# Etapa de runtime (JRE)
# ========================
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

# Usuario no root
RUN useradd -ms /bin/bash appuser
USER appuser

# Copiamos el JAR desde la etapa de build
# Ajusta el nombre si tu jar final cambia
ARG JAR_FILE=target/*.jar
COPY --from=build /app/${JAR_FILE} app.jar

# Puerto de Spring Boot
EXPOSE 8080

# Arranque
ENTRYPOINT ["java","-jar","/app/app.jar"]
