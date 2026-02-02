#!/bin/sh

mvn install -f prod-pom.xml -DpoolGroupString='RUDEV' -Dcucumber.filter.tags='@Zul:Pro' --no-transfer-progress
