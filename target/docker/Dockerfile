FROM java
EXPOSE 8080
ADD /app/libs /app/libs
ADD 13/docker-inject_2.10-0.1.0.jar /app/docker-inject_2.10-0.1.0.jar
CMD ["java", "-cp", "\/app\/libs\/*:\/app\/docker-inject_2.10-0.1.0.jar", "com.example.WebApp"]