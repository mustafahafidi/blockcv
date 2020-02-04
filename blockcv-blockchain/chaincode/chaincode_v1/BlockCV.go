package main

import (
	"fmt"
  	"encoding/json"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

type BlockCVChaincode struct{}

type Organization struct {
	idOrg string `json:"idOrg"`
	nome string `json:"nome"`
	fondazione string `json:"fondazione"`
	sede string `json:"sede"`
	telefono string `json:"telefono"`
	piva string `json:"piva"`
}

type Worker struct {
	idWorker string `json:"idWorker"`

	nome string `json:"nome"`
	cognome string `json:"cognome"`
	nascita string `json:"nascita"`
	residenza string `json:"residenza"`
	telefono string `json:"telefono"`
	sesso string `json:"sesso"`
	codiceFiscale  string `json:"codiceFiscale"`
}

func (t *BlockCVChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {

	var organizations []Organization
	var bytes , err = json.Marshal(organizations)
	if err != nil {
		return shim.Error("encoding error")
	}
	stub.PutState("organizations",bytes)

	var offers []Offer
	bytes , err = json.Marshal(offers)
	if err != nil {
		return shim.Error("encoding error")
	}
	stub.PutState("offers",bytes)

	var applications []Application
	bytes , err = json.Marshal(applications)
	if err != nil {
		return shim.Error("encoding error")
	}
	stub.PutState("applications",bytes)

	var curricula []Curriculum
	bytes , err = json.Marshal(curricula)
	if err != nil {
		return shim.Error("encoding error")
	}
	stub.PutState("curricula",bytes)

	var wExp []WorkingExperience
	bytes , err = json.Marshal(wExp)
	if err != nil {
		return shim.Error("encoding error")
	}
	stub.PutState("wExp",bytes)

	var sExp []WorkingExperience
	bytes , err = json.Marshal(sExp)
	if err != nil {
		return shim.Error("encoding error")
	}
	stub.PutState("sExp",bytes)

	return shim.Success(nil)
}

func (t *BlockCVChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()
	if function == "QueryOffers" {
		return t.QueryAllOffers(stub, args)
	}
	if function == "candidateToOffer" {
		return t.candidateToOffer(stub, args)
	}
	if function == "addOffer" {
		return t.addOffer(stub, args)
	}
	if function == "addWorkingExp" {
		return t.addWorkingExp(stub, args)
	}
	if function == "addStudyingExp" {
		return t.addStudyingExp(stub, args)
	}
	if function == "deleteWorkingExp" {
		return t.deleteWorkingExp(stub, args)
	}
	if function == "deleteStudyingExp" {
		return t.deleteStudyingExp(stub, args)
	}
	if function == "getWorkingExps" {
		return t.getWorkingExps(stub, args)
	}
	if function == "getStudyingExps" {
		return t.getStudyingExps(stub, args)
	} else {return shim.Error("Invalid invoke function name")}
}

func main() {
	err := shim.Start(new(BlockCVChaincode))
	if err != nil {
		fmt.Printf("Error starting Simple chaincode: %s", err)
	}
}