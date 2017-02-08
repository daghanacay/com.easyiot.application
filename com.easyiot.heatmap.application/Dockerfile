FROM java:8u111-jdk-alpine

COPY com.easyiot.heatmap.jar ./
COPY configuration/ configuration/ 

EXPOSE 8080

CMD ["java", "-jar", "com.easyiot.heatmap.jar"]
