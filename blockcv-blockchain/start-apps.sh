#!/bin/bash

export MONGODB_PATH=../../mongodb

trap ctrl_c INT

function ctrl_c() {
  echo "Stopping Mongodb...."
  jobs
  kill %1
  sleep 3
}


function startMongoDB() {
  CURRENT_DIR=$PWD
  cd $MONGODB_PATH/bin
  if [ ! -d "../data/db" ]; then
    echo "Directory data/db not found, creating..."
    mkdir -p ../data/db
  fi
  ./mongod --dbpath=../data/db &
  cd "$CURRENT_DIR"
}

function startExplorer() {
  CURRENT_DIR=$PWD
  cd network/blockchain-explorer
  ./reset.sh && ./start.sh 
  cd "$CURRENT_DIR"
}

function startNetwork() {
  CURRENT_DIR=$PWD
  cd network
  ./restart.sh
  cd "$CURRENT_DIR"
}

#echo "Starting Mongodb...."
#startMongoDB
#startNetwork
echo "Starting Hyperledger Explorer..."
startExplorer



