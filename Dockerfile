FROM maven:3.9.15-eclipse-temurin-21-noble

COPY --chown=user:group . /testsuite
COPY entrypoint.sh /usr/local/bin/

WORKDIR /testsuite

RUN ["chmod", "+x", "/usr/local/bin/entrypoint.sh"]
ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
