FROM openjdk:11

RUN mkdir -p /app/api/vkube-health

COPY build/libs/vkube-health-1.0-SNAPSHOT-all.jar /app/api/vkube-health/

EXPOSE 22100

WORKDIR "/app/api/vkube-health"

CMD ["java", "-jar", "vkube-health-1.0-SNAPSHOT-all.jar"]
