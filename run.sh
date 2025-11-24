#!/bin/bash

# ==========================================
# Auto Deploy Script for CONVERT_FILE (Mac/Linux)
# ==========================================

# ------------------------------------------
# CONFIGURATION - PLEASE UPDATE THIS PATH
# ------------------------------------------
# NOTE: You must update this path to point to your local Tomcat installation
TOMCAT_HOME="/opt/homebrew/opt/tomcat/libexec" 
# ------------------------------------------

PROJECT_DIR="$(pwd)"
WAR_FILE="$PROJECT_DIR/target/CONVERT_FILE.war"
DEPLOY_DIR="$TOMCAT_HOME/webapps"

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}   AUTO DEPLOY - CONVERT_FILE           ${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# Check Tomcat Home
if [ ! -d "$TOMCAT_HOME" ]; then
    echo -e "${RED}ERROR: Tomcat directory not found at: $TOMCAT_HOME${NC}"
    echo "Please edit this script (run.sh) and update the TOMCAT_HOME variable."
    echo "Current value: $TOMCAT_HOME"
    exit 1
fi

echo "[1/6] Building project with Maven..."
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo -e "${RED}ERROR: Maven build failed!${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Build successful!${NC}"
echo ""

echo "[2/6] Stopping Tomcat..."
if [ -f "$TOMCAT_HOME/bin/shutdown.sh" ]; then
    "$TOMCAT_HOME/bin/shutdown.sh"
    # Wait for Tomcat to fully stop
    sleep 5
else
    echo -e "${RED}WARNING: shutdown.sh not found at $TOMCAT_HOME/bin/shutdown.sh${NC}"
fi
echo -e "${GREEN}✓ Tomcat stopped${NC}"
echo ""

echo "[3/6] Removing old deployment..."
if [ -d "$DEPLOY_DIR/CONVERT_FILE" ]; then
    rm -rf "$DEPLOY_DIR/CONVERT_FILE"
    echo -e "${GREEN}✓ Old folder removed${NC}"
else
    echo "- No old folder found"
fi

if [ -f "$DEPLOY_DIR/CONVERT_FILE.war" ]; then
    rm -f "$DEPLOY_DIR/CONVERT_FILE.war"
    echo -e "${GREEN}✓ Old WAR removed${NC}"
fi
echo ""

echo "[4/6] Copying new WAR file..."
if [ -f "$WAR_FILE" ]; then
    cp "$WAR_FILE" "$DEPLOY_DIR/CONVERT_FILE.war"
    if [ $? -ne 0 ]; then
        echo -e "${RED}ERROR: Failed to copy WAR file!${NC}"
        exit 1
    fi
    echo -e "${GREEN}✓ WAR file copied${NC}"
else
    echo -e "${RED}ERROR: WAR file not found at $WAR_FILE${NC}"
    exit 1
fi
echo ""

echo "[5/6] Starting Tomcat..."
if [ -f "$TOMCAT_HOME/bin/startup.sh" ]; then
    "$TOMCAT_HOME/bin/startup.sh"
    sleep 3
else
    echo -e "${RED}ERROR: startup.sh not found at $TOMCAT_HOME/bin/startup.sh${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Tomcat started${NC}"
echo ""

echo "[6/6] Deployment complete!"
echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}   DEPLOYMENT SUCCESSFUL!               ${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo "Application URL: http://localhost:8080/CONVERT_FILE/"
echo ""
echo "Opening browser..."
open "http://localhost:8080/CONVERT_FILE/"

# Function to stop Tomcat when script exits
cleanup() {
    echo ""
    echo -e "${GREEN}Stopping Tomcat (Auto-shutdown)...${NC}"
    if [ -f "$TOMCAT_HOME/bin/shutdown.sh" ]; then
        "$TOMCAT_HOME/bin/shutdown.sh"
    fi
}

# Trap EXIT signal (happens when you Ctrl+C or script finishes)
trap cleanup EXIT

echo "Tailing logs (Press Ctrl+C to stop server)..."
if [ -f "$TOMCAT_HOME/logs/catalina.out" ]; then
    tail -f "$TOMCAT_HOME/logs/catalina.out"
else
    echo "Log file not found at $TOMCAT_HOME/logs/catalina.out"
    echo "You can check other log files in $TOMCAT_HOME/logs/"
    # Keep script running to maintain the trap if logs aren't found
    wait
fi
