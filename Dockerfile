FROM amazoncorretto:17-alpine

RUN apk update && apk add --no-cache curl

COPY target/app.jar /app.jar

EXPOSE 7070

HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
  CMD curl --fail http://localhost:7070/api/v1/health || exit 1

CMD ["java", "-jar", "/app.jar"]
