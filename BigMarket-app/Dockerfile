# Base image
FROM openjdk:8-jre-slim

# Author information
#MAINTAINER St-Lin

# Configuration parameters
ENV PARAMS=""

# Set the timezone to Singapore
ENV TZ=Asia/Singapore
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Add the application JAR to the container
ADD target/BigMarket-app.jar /BigMarket-app.jar

# Entry point to run the Java application
ENTRYPOINT ["sh", "-c", "java -jar $JAVA_OPTS /BigMarket-app.jar $PARAMS"]
