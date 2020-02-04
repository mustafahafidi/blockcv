package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"fmt"
	"encoding/json"
	"strconv"
)

type BlockCVChaincode struct{}

type OrgSummary struct {
	Index string
	IdOrg string
	Nome string
}

type Organization struct {
	Nome string
	Fondazione string
	Sede string
	Telefono string
	Piva string
	IdOrg string

	Offers []Offer
	Proposals []Proposal
}

type Worker struct {
	Nome string
	Cognome string
	Nascita string
	LuogoNascita string
	Residenza string
	Telefono string
	Sesso string
	CodiceFiscale string
	Email string
	IdWorker string

	WorkingExps []WorkingExperience
	StudyingExps []StudyingExperience
}

var logger = shim.NewLogger("Blockcv-Chaincode-v2")
func main() {
	logger.SetLevel(shim.LogInfo)
	err := shim.Start(new(BlockCVChaincode))
	if err != nil {
		fmt.Printf("Error starting Blockcv-Chaincode-v2: %s", err)
	}
}

func (t *BlockCVChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	logger.Info("Init invocato")
	bytes,err:=stub.GetState("Orgs")
	if err==nil && bytes==nil{
		logger.Info("<Orgs,[]OrgSummary> inizializzato")
		var orgs = []OrgSummary{}
		bytes,_=json.Marshal(orgs)
		stub.PutState("Orgs",bytes)
	}
	logger.Info("Init: return...")
	return shim.Success(nil)
}

func (t *BlockCVChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()
	if function == "changeStatusProposal" {
		return t.changeStatusProposal(stub, args)
	}
	if function == "getWorkers" {
		return t.getWorkers(stub, args)
	}
	if function == "addApplication" {
		return t.addApplication(stub, args)
	}
	if function == "addProposal" {
		return t.addProposal(stub, args)
	}
	if function == "saveOffer" {
		return t.saveOffer(stub, args)
	}
	if function == "updateOffer" {
		return t.updateOffer(stub, args)
	}
	if function == "getOffers" {
		return t.getOffers(stub, args)
	}
	if function == "removeOffer" {
		return t.removeOffer(stub, args)
	}
	if function == "addOffer" {
		return t.addOffer(stub, args)
	}
	if function == "saveExp" {
		return t.saveExp(stub, args)
	}
	if function == "addExp" {
		return t.addExp(stub, args)
	}
	if function == "updateExp" {
		return t.updateExp(stub, args)
	}
	if function == "getExp" {
		return t.getExp(stub, args)
	}
	if function == "removeExp" {
		return t.removeExp(stub, args)
	}
	if function == "changeStatus" {
		return t.changeStatus(stub, args)
	}
	if function == "getOrgsSummaries" {
		return t.getOrgsSummaries(stub, args)
	}
	if function == "saveUser" {
		return t.saveUser(stub, args)
	}
	if function == "addUser" {
		return t.addUser(stub, args)
	}
	if function == "updateInfo" {
		return t.updateInfo(stub, args)
	}
	if function == "resetUser" {
		return t.resetUser(stub, args)
	}
	if function == "getUser" {
		return t.getUser(stub, args)
	} else {return shim.Error("Function with the name " + function + " does not exist.")}
}

//args[0] = id {ricavato tramite algoritmo crittografico MD5}
func (t *BlockCVChaincode) getWorker(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	if len(args)!=1 {return shim.Error("getWorker()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 1")}
	bytes,err:=stub.GetState("w_"+args[0])
	if err!=nil {return shim.Error(err.Error())}
	return shim.Success(bytes)
}

//args[0] = id {ricavato tramite algoritmo crittografico MD5}
func (t *BlockCVChaincode) getOrg(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	if len(args)!=1 {return shim.Error("getOrg()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 1")}
	bytes,err:=stub.GetState("o_"+args[0])
	if err!=nil {return shim.Error(err.Error())}
	return shim.Success(bytes)
}

//args[0] = userType {WORKER,ORGANIZATION} ; args[1] = id {ricavato tramite algoritmo crittografico MD5}
func (t *BlockCVChaincode) getUser(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	if len(args)!=2 {return shim.Error("getUser()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 2")}
	var resp pb.Response
	switch args[0] {
	case "WORKER":
		resp=t.getWorker(stub,[]string{args[1]})
	case "ORGANIZATION":
		resp=t.getOrg(stub,[]string{args[1]})
	default:
		return shim.Error("user type "+args[0]+" does not exist")
	}
	return resp
}

//args[0] = id {MD5}; args[1] = Nome ; args[2] = Cognome; args[3] = Nascita; args[4] = LuogoNascita; args[5] = Residenza;
//args[6] = Telefono; args[7] = Sesso; args[8] = CodiceFiscale; args[9] = Email;
func (t *BlockCVChaincode) addWorker(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	if len(args)!=10 {return shim.Error("addWorker()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 10")}
	//controllo lavoratore già registrato?
	var worker Worker = Worker{args[1],args[2],args[3],args[4],
		args[5],args[6],args[7],args[8],args[9],args[0],[]WorkingExperience{},
		[]StudyingExperience{}}
	bytes,err:=json.Marshal(worker)
	if err!=nil {return shim.Error("addWorker()> encoding error")}
	err=stub.PutState("w_"+args[0],bytes)
	if err!=nil {return shim.Error("addWorker()> stub.PutState error")}
	return shim.Success(nil)
}

//args[0] = id {MD5}; args[1] = Nome; args[2] = Fondazione; args[3] = Sede; args[4] = Telefono; args[5] = Piva;
func (t *BlockCVChaincode) addOrg(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	if len(args)!=6 {return shim.Error("addOrg()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 6")}
	var org Organization = Organization{args[1],args[2],args[3],args[4],args[5],args[0],
	[]Offer{},[]Proposal{}}
	bytes,err:=json.Marshal(org)
	if err!=nil {return shim.Error("addOrg()> encoding error")}
	stub.PutState("o_"+args[0],bytes)
	bytes,err=stub.GetState("Orgs")
	if err!=nil {return shim.Error("addOrg()> stub.GetState")}

	var orgs []OrgSummary
	json.Unmarshal(bytes,&orgs)
	var orgSummary  = OrgSummary{strconv.Itoa(len(orgs)),args[0],args[1]}
	orgs=append(orgs,orgSummary)
	bytes,err=json.Marshal(orgs)
	if err!=nil {return shim.Error("addOrg()> encoding error")}
	err=stub.PutState("Orgs",bytes)
	if err!=nil {return shim.Error("addOrg()> stub.PutState")}
	return shim.Success(nil)
}

//Organization
//args[0] = userType {WORKER,ORGANIZATION}; args[1] = id {MD5}; args[2] = Nome; args[3] = Fondazione; args[4] = Sede; args[5] = Telefono; args[6] = Piva;

//Worker
//args[0] = userType {WORKER,ORGANIZATION} ; args[1] = id {MD5}; args[2] = Nome ; args[3] = Cognome; args[4] = Nascita; args[5] = LuogoNascita; args[6] = Residenza;
//args[7] = Telefono; args[8] = Sesso; args[9] = CodiceFiscale; args[10] = Email;
func (t *BlockCVChaincode) addUser(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	if len(args)<7  {return shim.Error("addUser()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 7 or 11")}
	userType:=args[0]
	args=args[1:len(args)]
	switch userType {
	case "WORKER":
		return t.addWorker(stub,args)
	case "ORGANIZATION":
		return t.addOrg(stub,args)
	default:
		return shim.Error("addUser()> User type "+args[0]+" does not exist")
	}
	return shim.Success(nil)
}

//args[0] = id {MD5}; args[1] = Nome ; args[2] = Cognome; args[3] = Nascita; args[4] = LuogoNascita; args[5] = Residenza;
//args[6] = Telefono; args[7] = Sesso; args[8] = CodiceFiscale; args[9] = Email;
func (t *BlockCVChaincode) updateWorkerInfo(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	if len(args)!=10 {return shim.Error("updateWorkerInfo()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 10")}
	response:=t.getWorker(stub,[]string{args[0]})
	if response.GetMessage()!="" {return response}
	bytes:=response.GetPayload()
	var worker Worker
	json.Unmarshal(bytes,&worker)
	var aux Worker=Worker{args[1],args[2],args[3],args[4],args[5],args[6],
		args[7], args[8],args[9],args[0],worker.WorkingExps,worker.StudyingExps}
	bytes,_=json.Marshal(aux)
	stub.PutState("w_"+args[0],bytes)
	return shim.Success(nil)
}

//args[0] = id {MD5}; args[1] = Nome; args[2] = Fondazione; args[3] = Sede; args[4] = Telefono; args[5] = Piva;
func (t *BlockCVChaincode) updateOrgInfo(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	if len(args)!=6 {return shim.Error("updateOrgInfo()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 6")}
	response:=t.getOrg(stub,[]string{args[0]})
	if response.GetMessage()!="" {return response}
	bytes:=response.GetPayload()
	var org Organization
	json.Unmarshal(bytes,&org)
	var aux Organization=Organization{args[1],args[2],args[3],args[4],args[5],args[0],
	org.Offers,org.Proposals}
	bytes,_=json.Marshal(aux)
	stub.PutState("o_"+args[0],bytes)
	return shim.Success(nil)
}

//Organization
//args[0] = userType {WORKER,ORGANIZATION}; args[1] = id {MD5}; args[2] = Nome; args[3] = Fondazione; args[4] = Sede; args[5] = Telefono; args[6] = Piva;

//Worker
//args[0] = userType {WORKER,ORGANIZATION} ; args[1] = id {MD5}; args[2] = Nome ; args[3] = Cognome; args[4] = Nascita; args[5] = LuogoNascita; args[6] = Residenza;
//args[7] = Telefono; args[8] = Sesso; args[9] = CodiceFiscale; args[10] = Email;
func (t *BlockCVChaincode) updateInfo(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	if len(args)<7 {return shim.Error("updateInfo()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 7 or 11")}
	userType:=args[0]
	args=args[1:len(args)]
	switch userType {
	case "WORKER":
		t.updateWorkerInfo(stub,args)
	case "ORGANIZATION":
		t.updateOrgInfo(stub,args)
	default:
		return shim.Error("updateInfo()> User type "+args[0]+" does not exist")
	}
	return shim.Success(nil)
}

//args[0] = userType ; args[1] = id
func (t *BlockCVChaincode) resetUser(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	if len(args)!=2 {return shim.Error("resetUser()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 2")}
	switch args[0] {
	case "WORKER":
		var worker Worker = Worker{"","","","",
			"","","","","",args[1],[]WorkingExperience{},[]StudyingExperience{}}
		bytes,_:=json.Marshal(worker)
		stub.PutState("w_"+args[1],bytes)
	case "ORGANIZATION":
		var org Organization = Organization{"","","","","",args[1] ,
		[]Offer{},[]Proposal{}}
		bytes,_:=json.Marshal(org)
		stub.PutState("o_"+args[1],bytes)
	default:
		return shim.Error("resetUser()> User type "+args[0]+" does not exist")
	}
	return shim.Success(nil)
}

//Organization
//args[0] = userType {WORKER,ORGANIZATION}; args[1] = id {MD5}; args[2] = Nome; args[3] = Fondazione; args[4] = Sede; args[5] = Telefono; args[6] = Piva;

//Worker
//args[0] = userType {WORKER,ORGANIZATION} ; args[1] = id {MD5}; args[2] = Nome ; args[3] = Cognome; args[4] = Nascita; args[5] = LuogoNascita; args[6] = Residenza;
//args[7] = Telefono; args[8] = Sesso; args[9] = CodiceFiscale; args[10] = Email;
func (t *BlockCVChaincode) saveUser(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	if len(args)<7 {return shim.Error("saveUser()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 7 or 11")}
	response:=t.getUser(stub,args[0:2])
	if response.GetStatus() == 500 { return shim.Error(response.GetMessage())}

	if response.GetPayload()!=nil {
		response=t.updateInfo(stub,args)
		if response.GetStatus() == 500 { return shim.Error(response.GetMessage())}
		return response
	}
	response=t.addUser(stub,args)
	return response
}

//args[] vuoto
func (t *BlockCVChaincode) getOrgsSummaries(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	if len(args)!=0 {return shim.Error("getOrgsSummaries()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 0")}
	bytes,err:=stub.GetState("Orgs")
	if err!= nil {return shim.Error("getOrgsSummaries()> stub.GetState error")}
	return shim.Success(bytes)
}

//args[] vuoto
func (t *BlockCVChaincode) getWorkers(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	if len(args)!=0 {return shim.Error("getWorkers()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 0")}
	it, _ := stub.GetStateByRange("w_", "")
	var worker Worker
	workers:=[]Worker{}
	for it.HasNext() {
		key,_:= it.Next()
		json.Unmarshal(key.Value,&worker)
		workers=append(workers,worker)
	}
	bytes,_:=json.Marshal(workers)
	return shim.Success(bytes)
}