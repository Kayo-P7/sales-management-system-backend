# estagio 1: construcao da aplicacao com maven e jdk 21
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# copia pom para download de dependencias e cache de camadas
COPY pom.xml .
RUN mvn dependency:go-offline -B

# copia o codigo fonte e gera o pacote jar executavel
COPY src ./src
RUN mvn clean package -DskipTests

# estagio 2: imagem enxuta de execucao com jre 21 alpine
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# cria usuario sem privilegios de root para execucao segura
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# copia o jar gerado do estagio de build
COPY --from=build /app/target/*.jar app.jar

# ajusta permissoes do artefato
RUN chown -R appuser:appgroup /app

# executa como usuario nao-root
USER appuser:appgroup

# expoem a porta padrao do servico
EXPOSE 8089

# inicia o servico com configuracao de memoria otimizada para containers
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
