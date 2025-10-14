# Use a Tomcat image with Java 17+
FROM tomcat:10.1-jdk17-temurin

# Remove the default ROOT application
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Copy your WAR file to the Tomcat webapps folder
COPY target/realtime-app.war /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
