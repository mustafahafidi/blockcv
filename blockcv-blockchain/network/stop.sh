#!/bin/bash
#
# SPDX-License-Identifier: Apache-2.0


export PATH=${PWD}/bin:${PWD}:$PATH
export FABRIC_CFG_PATH=${PWD}
export NETWORK_CFG_PATH=${PWD}/network-config
export NETWORK_CFG_GENERATED=${NETWORK_CFG_PATH}/generated
export MONGODB_PATH=../../../../mongodb
export APP_PATH=../../blockcv-app

# Ask user for confirmation to proceed
function askProceed () {
  read -p "Continue? [Y/n] " ans
  case "$ans" in
    y|Y|"" )
      echo "proceeding ..."
    ;;
    n|N )
      echo "exiting..."
      exit 1
    ;;
    * )
      echo "invalid response"
      askProceed
    ;;
  esac
}

# Delete any images that were generated as a part of this setup
# specifically the following images are often left behind:
# TODO list generated image naming patterns
function removeUnwantedImages() {
  DOCKER_IMAGE_IDS=$(docker images | grep "dev\|none\|test-vp\|peer[0-9]-" | awk '{print $3}')
  if [ -z "$DOCKER_IMAGE_IDS" -o "$DOCKER_IMAGE_IDS" == " " ]; then
    echo "---- No images available for deletion ----"
  else
    docker rmi -f $DOCKER_IMAGE_IDS
  fi
}


# Obtain CONTAINER_IDS and remove them
# TODO Might want to make this optional - could clear other containers
function clearContainers () {
  CONTAINER_IDS=$(docker ps -aq)
  if [ -z "$CONTAINER_IDS" -o "$CONTAINER_IDS" == " " ]; then
    echo "---- No containers available for deletion ----"
  else
    docker rm -f $CONTAINER_IDS
  fi
}

# Tear down running network
function networkDown () {
  #docker-compose -f $COMPOSE_FILE down --volumes -t 1
  # Bring down the network, deleting the volumes
  #docker stop $(docker ps -aq) &&  docker rm $(docker ps -aq) &&  docker rmi -f $(docker images | grep dev-* | awk '{print $3}')
  #docker network prune -f
  #Cleanup the chaincode containers
  clearContainers
  #Cleanup images
  removeUnwantedImages
  #docker stop $(docker ps -aq) &&  docker rm $(docker ps -aq) &&  docker rmi -f $(docker images | grep dev-* | awk '{print $3}')
  #docker network prune -f
  # remove orderer block and other channel configuration transactions and certs
  rm -rf $NETWORK_CFG_GENERATED/config/* $NETWORK_CFG_GENERATED/crypto-config docker-compose.yaml $NETWORK_CFG_GENERATED/*.block chaincode
  #rm $APP_PATH/resources/*.jso
  #rm -rf $MONGODB_PATH/data/db/* 
}

EXPMODE="Stopping.."

# use this as the default docker-compose yaml definition
COMPOSE_FILE=docker-compose.yaml

# ask for confirmation to proceed
#askProceed

networkDown
