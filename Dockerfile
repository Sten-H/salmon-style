FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/salmon-style.jar /salmon-style/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/salmon-style/app.jar"]
