FROM eclipse-temurin:17-alpine
COPY target/links-downloader.jar /links-downloader.jar
# This is the port that your javalin application will listen on
EXPOSE 7070
ENTRYPOINT ["java", "-jar", "/links-downloader.jar", "/home/links"]