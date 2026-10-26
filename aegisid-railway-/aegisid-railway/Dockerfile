FROM tomcat:10.1-jdk17

RUN rm -rf /usr/local/tomcat/webapps/*

# Driver MySQL
ADD https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.4.0/mysql-connector-j-8.4.0.jar /usr/local/tomcat/webapps/ROOT/WEB-INF/lib/mysql-connector-j-8.4.0.jar

COPY webapp/ /usr/local/tomcat/webapps/ROOT/

# Compilar servlets y clases Java (Tomcat 10 usa jakarta.servlet)
COPY src/main/java /tmp/src
RUN mkdir -p /usr/local/tomcat/webapps/ROOT/WEB-INF/classes \
 && find /tmp/src -name "*.java" > /tmp/sources.txt \
 && javac -encoding UTF-8 --release 17 \
      -cp "/usr/local/tomcat/lib/*" \
      -d /usr/local/tomcat/webapps/ROOT/WEB-INF/classes @/tmp/sources.txt \
 && rm -rf /tmp/src /tmp/sources.txt

EXPOSE 8080
CMD ["catalina.sh", "run"]
