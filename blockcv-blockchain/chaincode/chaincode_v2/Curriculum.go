package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
	"encoding/json"
	"strconv"
)

type WorkingExperience struct{
	IdWorkingExp string

	Azienda string
	IdOrg string
	DataInizio string
	DataFine string
	Sede string
	Mansione string
	Status string
}

type StudyingExperience struct{
	IdStudyingExp string

	Istituto string
	IdOrg string
	DataInizio string
	DataFine string
	Sede string
	Titolo string
	Specializzazione string
	Status string
}

//WorkingExperience 
// args[0] = ExpType {Lavorativa,Formativa}; args[1] = idWorker {MD5}; args[2] = idExp; args[3] = Azienda; args[4] = idOrg; args[5] = DataInizio; args[6] = DataFine;
//args[7] = Sede; args[8] = Mansione;

//StudyingExperience
//args[0] = ExpType {Lavorativa,Formativa}; args[1] = idWorker {MD5}; args[2] = idExp; args[3] = Istituto; args[4] = idOrg; args[5] = DataInizio; args[6] = DataFine; 
//args[7] = Sede; args[8] = Titolo; args[9] = Specializzazione;
func (t *BlockCVChaincode) addExp(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	if len(args)<9 {return shim.Error("addExp()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 9-10")}
	expType:=args[0]
	var resp pb.Response
	switch expType {
	case "Lavorativa":
		resp=t.addWorkingExp(stub,args[1:len(args)])
		return resp
	case "Formativa":
		resp=t.addStudyingExp(stub,args[1:len(args)])
		return resp
	default:
		return shim.Error("experience type "+expType+" does not exist")
	}
}

//args[0] = idWorker {MD5}; args[1] = idExp; args[2] = Azienda; args[3] = idOrg; args[4] = DataInizio; args[5] = DataFine;
//args[6] = Sede; args[7] = Mansione;
func (t *BlockCVChaincode) addWorkingExp(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	if len(args)!=8 {return shim.Error("addWorkingExp()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 8")}
	bytes,err:=stub.GetState("w_"+args[0])
	if err!=nil {return shim.Error("addWorkingExp()> stub.GetState")}
	var worker Worker
	err=json.Unmarshal(bytes,&worker)
	if err!=nil {return shim.Error("addWorkingExp()> decoding error")}
	var exp WorkingExperience=WorkingExperience{args[1],args[2],args[3],
	args[4],args[5],args[6],args[7],"pending"}
	worker.WorkingExps=append(worker.WorkingExps,exp)
	bytes,err=json.Marshal(worker)
	if err!=nil {return shim.Error("addWorkingExp()> encoding error")}
	err=stub.PutState("w_"+args[0],bytes)
	if err!=nil {return shim.Error("addWorkingExp()> stub.PutState error")}
	return shim.Success(nil)
}

//args[0] = idWorker {MD5}; args[1] = idExp; args[2] = Istituto; args[3] = idOrg; args[4] = DataInizio; args[5] = DataFine; 
//args[6] = Sede; args[7] = Titolo; args[8] = Specializzazione;
func (t *BlockCVChaincode) addStudyingExp(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	if len(args)!=9 {return shim.Error("addStudyingExp()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 9")}
	bytes,err:=stub.GetState("w_"+args[0])
	if err!=nil {return shim.Error("addStudyingExp()> stub.GetState")}
	var worker Worker
	err=json.Unmarshal(bytes,&worker)
	if err!=nil {return shim.Error("addStudyingExp()> decoding error")}
	var exp StudyingExperience=StudyingExperience{args[1],args[2],args[3],
		args[4],args[5],args[6],args[7],args[8],"pending"}
	worker.StudyingExps=append(worker.StudyingExps, exp)
	bytes,err=json.Marshal(worker)
	if err!=nil {return shim.Error("addStudyingExp()> encoding error")}
	err=stub.PutState("w_"+args[0],bytes)
	if err!=nil {return shim.Error("addStudyingExp()> stub.PutState")}
	return shim.Success(nil)
}

//args[0] = idWorker ; args[1] = idStudyingExp
func (t *BlockCVChaincode) removeStudyingExp(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	if len(args)!=2 {return shim.Error("removeStudyingExp()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 2")}
	bytes,err:=stub.GetState("w_"+args[0])
	if err!=nil {return shim.Error("removeStudyingExp()> Unable to get the worker: "+args[0])}
	var worker Worker
	err=json.Unmarshal(bytes,&worker)
	if err!=nil {return shim.Error("removeStudyingExp()> decoding error")}
	find := false
	for j:=0;j<len(worker.StudyingExps);j++  {
		if worker.StudyingExps[j].IdStudyingExp==args[1] {
			find=true
			worker.StudyingExps=append(worker.StudyingExps[:j],worker.StudyingExps[j+1:]...)
			j=len(worker.StudyingExps)
		}
	}
	if find==false {return shim.Error("removeStudyingExp()> Cannot find exp: "+args[1])}
	bytes,err=json.Marshal(worker)
	err=stub.PutState("w_"+args[0],bytes)
	return shim.Success(nil)
}

//args[0] = idWorker ; args[1] = idWorkingExp
func (t *BlockCVChaincode) removeWorkingExp(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	if len(args)!=2 {return shim.Error("removeWorkingExp()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 2")}
	bytes,err:=stub.GetState("w_"+args[0])
	if err!=nil {return shim.Error("removeWorkingExp()> Unable to get the worker: "+args[0])}
	var worker Worker
	err=json.Unmarshal(bytes,&worker)
	if err!=nil {return shim.Error("removeWorkingExp()> decoding error")}
	find := false
	for j:=0;j<len(worker.WorkingExps);j++  {
		if worker.WorkingExps[j].IdWorkingExp==args[1] {
			find=true
			worker.WorkingExps=append(worker.WorkingExps[:j],worker.WorkingExps[j+1:]...)
			j=len(worker.WorkingExps)
		}
	}
	if find==false {return shim.Error("removeWorkingExp()> Cannot find exp: "+args[1])}
	bytes,err=json.Marshal(worker)
	err=stub.PutState("w_"+args[0],bytes)
	return shim.Success(nil)
}

//args[0] = expType ; args[1] = idWorker ; args[2] = idExp
func (t *BlockCVChaincode) removeExp(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	if len(args)!=3 {return shim.Error("removeExp()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 3")}
	expType:=args[0]
	var resp pb.Response
	switch expType {
	case "Lavorativa":
		resp=t.removeWorkingExp(stub,args[1:len(args)])
		return resp
	case "Formativa":
		resp=t.removeStudyingExp(stub,args[1:len(args)])
		return resp
	default:
		return shim.Error("removeExp Experience type "+expType+" does not exist")
	}
}

//args[0] = expType ; args[1] = idWorker ; args[2] = idExp ; args[3] = status
func (t *BlockCVChaincode) changeStatus(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	//att,found,err:=cid.GetAttributeValue(stub,"user_id")
	//if(found==false) {return shim.Error("getUser()> access denied!")}
	//if att!=args[1] {return shim.Error("getUser()> access denied!")}
	if len(args)!=4 {return shim.Error("changeStatus()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 4")}
	bytes,err:=stub.GetState("w_"+args[1])
	if err!=nil {return shim.Error("changeStatus()> stub.GetState error ")}
	var worker Worker
	err=json.Unmarshal(bytes,&worker)
	if err!=nil {return shim.Error("changeStatus()> decoding error")}
	switch args[0] {
	case "Lavorativa":
		for j:=0;j<len(worker.WorkingExps);j++  {
			if worker.WorkingExps[j].IdWorkingExp==args[2] {
				worker.WorkingExps[j].Status=args[3]
				bytes,_=json.Marshal(worker)
				stub.PutState("w_"+args[1],bytes)
				return shim.Success(nil)
			}
		}
		return shim.Error("changeStatus()> Cannot find work experience with id "+args[2] )
	case "Formativa":
		for j:=0;j<len(worker.WorkingExps);j++  {
			if worker.StudyingExps[j].IdStudyingExp==args[2] {
				worker.StudyingExps[j].Status=args[3]
				bytes,_=json.Marshal(worker)
				stub.PutState("w_"+args[1],bytes)
				return shim.Success(nil)
			}
		}
		return shim.Error("changeStatus()> Cannot find study experience with id "+args[2] )
	default:
		return shim.Error("changeStatus()> Experience type "+args[0]+" does not exist")
	}
	return shim.Success(nil)
}

//args[0] = expType ; args[1] = idWorker ; args[2] = idExp
func (t *BlockCVChaincode) getExp(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	if len(args)!=3 {return shim.Error("getExp()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 3")}
	bytes,err:=stub.GetState("w_"+args[1])
	if err!=nil {return shim.Error("getExp()> stub.GetState error ")}
	var worker Worker
	err=json.Unmarshal(bytes,&worker)
	if err!=nil {return shim.Error("getExp()> decoding error")}
	switch args[0] {
	case "Lavorativa":
		for j:=0;j<len(worker.WorkingExps);j++  {
			if worker.WorkingExps[j].IdWorkingExp==args[2] {
				ret,_:=json.Marshal(worker.WorkingExps[j])
				return shim.Success(ret)
			}
		}
		return shim.Error("getExp()> Unable to find experience: "+args[2])
	case "Formativa":
		for j:=0;j<len(worker.StudyingExps);j++  {
			if worker.StudyingExps[j].IdStudyingExp==args[2] {
				ret,_:=json.Marshal(worker.StudyingExps[j])
				return shim.Success(ret)
			}
		}
		return shim.Error("getExp()> Unable to find experience: "+args[2])
	default:
		return shim.Error("getExp()> Experience type "+args[0]+" does not exist")
	}
}

//WorkingExperience 
// args[0] = ExpType {Lavorativa,Formativa}; args[1] = idWorker {MD5}; args[2] = idExp; args[3] = Azienda; args[4] = idOrg; args[5] = DataInizio; args[6] = DataFine;
//args[7] = Sede; args[8] = Mansione;

//StudyingExperience
//args[0] = ExpType {Lavorativa,Formativa}; args[1] = idWorker {MD5}; args[2] = idExp; args[3] = Istituto; args[4] = idOrg; args[5] = DataInizio; args[6] = DataFine; 
//args[7] = Sede; args[8] = Titolo; args[9] = Specializzazione;
func (t *BlockCVChaincode) updateExp(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	if len(args)<9 {return shim.Error("updateExp()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 9-10")}
	bytes,err:=stub.GetState("w_"+args[1])
	if err!=nil {return shim.Error("updateExp()> stub.GetState")}
	var worker Worker
	err=json.Unmarshal(bytes,&worker)
	if err!=nil {return shim.Error("updateExp()> decoding error")}
	switch args[0] {
	case "Lavorativa":
		for j:=0;j<len(worker.WorkingExps);j++  {
			if worker.WorkingExps[j].IdWorkingExp==args[2] {
				var exp WorkingExperience=WorkingExperience{args[2],args[3],args[4],
					args[5],args[6],args[7],args[8],"pending"}
				worker.WorkingExps[j]=exp
				bytes,err=json.Marshal(worker)
				if err!=nil {return shim.Error("updateExp()> encoding error - WORK")}
				err=stub.PutState("w_"+args[1],bytes)
				if err!=nil {return shim.Error("updateExp()> stub.PutState - WORK")}
				break
			}
		}
		return shim.Success(nil)
	case "Formativa":
		for j:=0;j<len(worker.WorkingExps);j++  {
			if worker.StudyingExps[j].IdStudyingExp==args[2] {
				var exp StudyingExperience=StudyingExperience{args[2],args[3],args[4],
					args[5],args[6],args[7],args[8],args[9],"pending"}
				worker.StudyingExps[j]=exp
				bytes,err=json.Marshal(worker)
				if err!=nil {return shim.Error("updateExp()> encoding error - STUDY")}
				err=stub.PutState("w_"+args[1],bytes)
				if err!=nil {return shim.Error("updateExp()> stub.PutState - STUDY")}
				break
			}
		}
		return shim.Success(nil)
	default:
		return shim.Error("updateExp()> Experience type "+args[0]+" does not exist")
	}
}

//WorkingExperience 
// args[0] = ExpType {Lavorativa,Formativa}; args[1] = idWorker {MD5}; args[2] = idExp; args[3] = Azienda; args[4] = idOrg; args[5] = DataInizio; args[6] = DataFine;
//args[7] = Sede; args[8] = Mansione;

//StudyingExperience
//args[0] = ExpType {Lavorativa,Formativa}; args[1] = idWorker {MD5}; args[2] = idExp; args[3] = Istituto; args[4] = idOrg; args[5] = DataInizio; args[6] = DataFine; 
//args[7] = Sede; args[8] = Titolo; args[9] = Specializzazione;
func (t *BlockCVChaincode) saveExp(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	if len(args)<9 {return shim.Error("saveExp()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 9-10")}
	resp:=t.getExp(stub,args[:3]) //fino ad args[2]
	if resp.Payload!=nil {
		resp=t.updateExp(stub,args)
		}else {resp=t.addExp(stub,args)}
	return resp
}
