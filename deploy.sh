#!/bin/bash

TOMEE_PATH="/Users/Santi/apache-tomee-plus-1.7.0"
WAR_PATH="/Users/Santi/arkaserver.war"

echo "Stopping tomcat . . ."
sudo $TOMEE_PATH/bin/shutdown.sh > /dev/null 2>/dev/null
sleep 5
echo "Undeploying arkaserver . . ."
sudo rm $TOMEE_PATH/apps/arkaserver.war 
sudo rm -rf $TOMEE_PATH/apps/arkaserver
echo "Starting tomcat . . ."
sudo $TOMEE_PATH/bin/startup.sh > /dev/null 2>/dev/null
sleep 10
echo "Deploying arkaserver . . ."
sudo $TOMEE_PATH/bin/tomee.sh deploy $WAR_PATH > /dev/null 2>/dev/null
