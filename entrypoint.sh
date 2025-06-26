#!/bin/sh

mvn install -P prod -DpoolGroupString='RUDEV' -Dcucumber.filter.tags='@Zul:Pro' --no-transfer-progress
