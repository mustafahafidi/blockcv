package main

import (
	"encoding/json"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

type Curriculum struct {
	idCurriculum string `json:"idCurriculum"`
	idWorker string `json:"idWorker"`
}

type WorkingExperience struct{
	idCurriculum string `json:"idCurriculum"`
	idExp string `json:"idExp"`

	azienda string `json:"azienda"` //idOrg?
	periodo string `json:"periodo"`
	indirizzo string `json:"indirizzo"`
	mansione string `json:"mansione"`
}

type StudyingExperience struct{
	idCurriculum string `json:"idCurriculum"`
	idExp string `json:"idExp"`

	titolo string `json:"titolo"`
	istituto string `json:"titolo"` //idOrg?
	specializzazione string `json:"specializzazione"`
	indirizzo string `json:"indirizzo"`
	periodo string `json:"periodo"`
}

//args[0] = idCurriculum
func (t *BlockCVChaincode) getWorkingExps(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args)!=1 {return shim.Error("incorrect # of arguments")}
	bytes,err := stub.GetState("wExp")
	if err != nil {
		return shim.Error("missing []application")
	}
	var wExps []WorkingExperience
	json.Unmarshal(bytes,wExps)
	var result []byte
	for j:=0;j<len(wExps);j++  {
		if wExps[j].idCurriculum==args[0] {
			exp,_ :=json.Marshal(wExps[j])
			result=append(result,exp...)
		}
	}
	return shim.Success(result)
}

//args[0] = idCurriculum
func (t *BlockCVChaincode) getStudyingExps(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args)!=1 {return shim.Error("incorrect # of arguments")}
	bytes,err := stub.GetState("sExp")
	if err != nil {
		return shim.Error("missing []application")
	}
	var sExps []WorkingExperience
	json.Unmarshal(bytes,sExps)
	var result []byte
	for j:=0;j<len(sExps);j++  {
		if sExps[j].idCurriculum==args[0] {
			exp,_ :=json.Marshal(sExps[j])
			result=append(result,exp...)
		}
	}
	return shim.Success(result)
}

//args[0] = idCurriculum ; args[2] = idExp ecc... vedi WorkingExperience struct
func (t *BlockCVChaincode) addWorkingExp(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args)!=6 {return shim.Error("invalid # of arguments")}
	bytes,err := stub.GetState("wExp")
	if err!=nil {return shim.Error("missing []WorkingExperience")}
	exp := WorkingExperience{args[0],args[1],args[2],args[3],args[4],args[6]}
	result,err := json.Marshal(exp)
	if err!=nil {return shim.Error("encoding failed")}
	bytes=append(bytes,result...)
	stub.PutState("wExp",bytes)
	return shim.Success(nil)
}

//args[0] = idCurriculum ; args[2] = titolo ecc... vedi StudyingExperience struct
func (t *BlockCVChaincode) addStudyingExp(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args)!=7 {return shim.Error("invalid # of arguments")}
	bytes,err := stub.GetState("sExp")
	if err!=nil {return shim.Error("missing []StydingExpperience")}
	exp := StudyingExperience{args[0],args[1],args[2],args[3],args[4],
	args[6],args[7]}
	result,err := json.Marshal(exp)
	if err!=nil {return shim.Error("encoding failed")}
	bytes=append(bytes,result...)
	stub.PutState("sExp",bytes)
	return shim.Success(nil)
}

//args[0] = idExp
func (t *BlockCVChaincode) deleteWorkingExp(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args)!=1 {return shim.Error("incorrect # of arguments")}
	bytes,err := stub.GetState("wExp")
	if err != nil {
		return shim.Error("missing []workingExperience")
	}
	var wExps []WorkingExperience
	json.Unmarshal(bytes,wExps)
	s := make([]WorkingExperience,len(wExps))
	copy(wExps,s)
	for j:=0;j<len(s);j++  {
		if s[j].idExp==args[0] {
			l:=s[0:j]
			r:=s[j+1:len(s)]
			l= append(l,r...)
			bytes,err=json.Marshal(l)
			stub.PutState("wExp",bytes)
			return shim.Success(nil)
		}
	}
	return shim.Error("no matched idExp")
}

//args[0] = idExp
func (t *BlockCVChaincode) deleteStudyingExp(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args)!=1 {return shim.Error("incorrect # of arguments")}
	bytes,err := stub.GetState("sExp")
	if err != nil {
		return shim.Error("missing []StudyingExperience")
	}
	var sExps []StudyingExperience
	json.Unmarshal(bytes,sExps)
	s := make([]StudyingExperience,len(sExps))
	copy(sExps,s)
	for j:=0;j<len(s);j++  {
		if s[j].idExp==args[0] {
			l:=s[0:j]
			r:=s[j+1:len(s)]
			l= append(l,r...)
			bytes,err=json.Marshal(l)
			stub.PutState("sExp",bytes)
			return shim.Success(nil)
		}
	}
	return shim.Error("no matched idExp")
}