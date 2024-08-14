FROM maven:3.6.3-openjdk-17-slim

COPY --chown=user:group . /testsuite
COPY entrypoint.sh /usr/local/bin/

WORKDIR /testsuite

RUN ["chmod", "+x", "/usr/local/bin/entrypoint.sh"]
ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
