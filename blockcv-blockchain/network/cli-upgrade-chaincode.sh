#!/bin/bash
peer chaincode install -n "$1" -v "$2" -p blockcv/chaincode/chaincode_v2 -l golang
peer chaincode upgrade -C "mychannel" -n "$1" -v "$2" -p blockcv/chaincode/chaincode_v2 -l golang -c '{"Args":[""]}' -P "OR('Org1MSP.member')"
#peer chaincode instantiate -o orderer.example.com:7050 -C mychannel -n "$1" -l golang -v "$2" -c '{"Args":[""]}' -P "OR('Org1MSP.member')"
