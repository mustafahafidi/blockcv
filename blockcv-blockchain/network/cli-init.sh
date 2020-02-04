#!/bin/bash

function initNetwork() {
  #set -ev


  # wait for Hyperledger Fabric to start
  # incase of errors when running later commands, issue export FABRIC_START_TIMEOUT=<larger number>
  export FABRIC_START_TIMEOUT=3
  #echo ${FABRIC_START_TIMEOUT}
  sleep ${FABRIC_START_TIMEOUT}

  echo "============Creating channel mychannel ========="
  # Create the channel
  cd network-config/generated/
  peer channel create -o orderer.example.com:7050 -c mychannel -f config/channel.tx
  
  echo "============Joining peer0 to channel mychannel ========="
  # Join peer0.org1.example.com to the channel.
  peer channel join -b mychannel.block
  cd ./../../
}


function installChaincode() {
  # channel name defaults to "mychannel"
  CHANNEL_NAME="mychannel"
  # CHAINCODE NAME
  CHAINCODE_NAME="mycc"
  # CHAINCODE VERSION
  CHAINCODE_VERSION=1
  # CHAINCODE SUBDIR
  CHAINCODE_SUBDIR=chaincode_v2

  echo "============Installing chaincode on peer0 ========="
  peer chaincode install -n $CHAINCODE_NAME -v $CHAINCODE_VERSION -p blockcv/chaincode/$CHAINCODE_SUBDIR -l golang

  echo "============Instantiating chaincode on channel mychannel========="
  peer chaincode instantiate -o orderer.example.com:7050 -C $CHANNEL_NAME -n $CHAINCODE_NAME -l golang -v $CHAINCODE_VERSION -c '{"Args":[""]}' -P "OR('Org1MSP.member')"

  echo "============Chaincode Instantiation finished========="

  sleep 2

  #echo "============INVOKE chaincode========="
  #peer chaincode query -o orderer.example.com:7050 -C mychannel -n mycc -c '{"Args":["query","a"]}'
  #peer chaincode invoke -o orderer.example.com:7050 -C mychannel -n mycc2 -c '{"Args":["QueryOffers"]}'
  
  #echo "============QUERY0 chaincode========="
  #peer chaincode query -o orderer.example.com:7050 -C mychannel -n mycc -c '{"Args":["queryCV","0"]}'
  #echo "============QUERY1 chaincode========="
  #peer chaincode query -o orderer.example.com:7050 -C mychannel -n mycc -c '{"Args":["queryCV","1"]}'

}

echo "Initializing network....."
initNetwork
echo "parametro $1"
if [ "$1" == "-c" ] 
  then 
  installChaincode
fi


echo "Cli waiting for commands....."
sleep 600000
