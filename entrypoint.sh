#!/bin/sh

mvn install -P oneonly,reference-ci -DpoolGroupString=Ref -DskipTests
