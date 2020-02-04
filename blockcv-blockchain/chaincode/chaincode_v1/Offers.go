package main

import (
	"encoding/json"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

type Offer struct {
	idOffer string `json:"idOffer"`
	idOrg string `json:"idOrg"`

	ambito string `json:"ambito"`
	mansione string `json:"mansione"`
	studio string `json:"studio"`
	certificazione string `json:"certificazione"`
	scadenza string `json:"scadenza"`
	contratto string `json:"contratto"`
	salario string `json:"salario"`
	descrizione string `json:"descrizione"`
	luogo string `json:"luogo"`
}

type Application struct {
	idOffer string `json:"idOffer"`
	idWorker string `json:"idWorker"`
}

//len(args)==0
func (t *BlockCVChaincode) QueryAllOffers(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args)!=0 {return shim.Error("incorrect # of arguments")}
	bytes,err := stub.GetState("offers")
	if err!=nil {
		if len(bytes)>0 {
			return shim.Success(bytes) //almeno un'offerta
		}
		return shim.Success(nil) //vuoto
	}
	return shim.Error("missing []offers") //errore
}

//args[0] = idOffer ; args[1] = id worker
func (t *BlockCVChaincode) candidateToOffer(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args)!=2 {
		return shim.Error("incorrect # of arguments")
	}
	bytes,err := stub.GetState("applications")
	if err != nil {
		return shim.Error("missing []application")
	}
	var applications []Application
	json.Unmarshal(bytes,&applications)
	for j:=0; j<len(applications); j++  {
		if applications[j].idOffer==args[0] && applications[j].idWorker==args[1] {return shim.Error("application already existing")}
	}
	applications=append(applications, Application{args[0],args[1]})
	return shim.Success(nil)
}

//args[0]=idOffer ; args[1]=idOrg ecc... vedi la struct Offer
func (t *BlockCVChaincode) addOffer(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args)!=11 {return shim.Error("incorrect # of arguments")}
	bytes,err := stub.GetState("offers")
	if err!=nil {
		return shim.Error("missing []offers")
	}
	var offer Offer=Offer{
		args[0],
		args[1],
		args[2],
		args[3],
		args[4],
		args[5],
		args[6],
		args[7],
		args[8],
		args[9],
		args[10],
	}
	o,_ := json.Marshal(offer)
	bytes=append(bytes,o...)
	stub.PutState("offers",bytes)
	return shim.Success(nil)
}

//args[0] = idOffer
func (t *BlockCVChaincode) QueryApplications(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args)!=1 {return shim.Error("incorrect # of arguments")}
	bytes,err := stub.GetState("applications")
	if err!=nil {return shim.Error("missing []Applications")}
	if len(bytes)==0 {return shim.Success(nil)}
	var applications []Application
	json.Unmarshal(bytes,applications)
	var aux []Application
	for j:=0;j<len(applications);j++  {
		if applications[j].idOffer==args[0] {aux= append(aux, applications[j])}
	}
	for j:=0;j<len(aux);j++  {
		//TO BE CONTINUED
	}
	return shim.Success(nil)
}

