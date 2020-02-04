package main

import "github.com/hyperledger/fabric/core/chaincode/shim"
import (
	pb "github.com/hyperledger/fabric/protos/peer"
	"encoding/json"
	"strconv"
)

type Offer struct {
	IdOffer string
	Titolo string
	Ambito string
	Mansione string
	Studio string
	Certificazione string
	Scadenza string
	Contratto string
	Salario string
	Descrizione string
	MaxCandidates string
	Azienda string
	IdOrg string

	Applications []Application
}

type Application struct {
	IdWorker string
	Nome string
	Indirizzo string
	Email string
}


type Proposal struct {
	IdProposal string
	IdWorker string
	IdExp string
	ExpType string
	SenderName string
	Comment string
	ExperienceTitle string
	Status string
}

//args[0] = idOrg {MD5}; args[1] = idOffer; args[2] = Titolo; args[3] = Ambito; args[4] = Mansione; args[5] = Studio; args[6] = Certificazione; args[7] = Scadenza;
//args[8] = Contratto; args[9] = Salario; args[10] = Descrizione; args[11] = MaxCandidates;
func (t *BlockCVChaincode) saveOffer(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	//att,found,err:=cid.GetAttributeValue(stub,"user_id")
	//if(found==false) {return shim.Error("saveOffer()> access denied!")}
	//if att!=args[0] {return shim.Error("saveOffer()> access denied!")}
	if len(args)!=12 {return shim.Error("saveOffer()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 12")}
	bytes,err:=stub.GetState("o_"+args[0])
	if err!=nil {return shim.Error("saveOffer()> stub.GetState error")}
	var org Organization
	err=json.Unmarshal(bytes,&org)
	if err!=nil {return shim.Error("saveOffer()> decoding error")}
	var  resp pb.Response
	for _, value := range org.Offers {
		if value.IdOffer==args[1] {
			resp=t.updateOffer(stub,args)
			return resp
		}
	}
	resp=t.addOffer(stub,args)
	return resp
}

//args[0] = idOrg {MD5}; args[1] = idOffer; args[2] = Titolo; args[3] = Ambito; args[4] = Mansione; args[5] = Studio; args[6] = Certificazione; args[7] = Scadenza;
//args[8] = Contratto; args[9] = Salario; args[10] = Descrizione; args[11] = MaxCandidates;
func (t *BlockCVChaincode) addOffer(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	//att,found,err:=cid.GetAttributeValue(stub,"user_id")
	//if(found==false) {return shim.Error("addOffer()> access denied!")}
	//if att!=args[0] {return shim.Error("addOffer()> access denied!")}
	if len(args)!=12 {return shim.Error("addOffer()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 12")}
	bytes,err:=stub.GetState("o_"+args[0])
	if err!=nil {return shim.Error("addOffer()> stub.GetState error")}
	var org Organization
	err=json.Unmarshal(bytes,&org)
	if err!=nil {return shim.Error("addOffer()> decoding error")}
	var offer = Offer{args[0]+args[1],args[2],args[3],args[4],args[5],
		args[6], args[7], args[8], args[9],args[10],args[11],
		org.Nome,args[0],[]Application{}}
	org.Offers=append(org.Offers,offer)
	bytes,err=json.Marshal(org)
	if err!=nil {return shim.Error("addOffer()> encoding error")}
	err=stub.PutState("o_"+args[0],bytes)
	if err!=nil {return shim.Error("addOffer()> stub.PutState error")}
	return shim.Success(nil)
}

//args[0] = idOrg ; args[1] = idOffer ;
func (t *BlockCVChaincode) removeOffer(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	//att,found,err:=cid.GetAttributeValue(stub,"user_id")
	//if(found==false) {return shim.Error("removeOffer()> access denied!")}
	//if att!=args[0] {return shim.Error("removeOffer()> access denied!")}
	if len(args)!=2 {return shim.Error("removeOffer()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 2")}
	bytes,err:=stub.GetState("o_"+args[0])
	if err!=nil {return shim.Error("removeOffer()> stub.GetState error")}
	var org Organization
	err=json.Unmarshal(bytes,&org)
	if err!=nil {return shim.Error("removeOffer()> decoding error")}
	find :=false
	for j:=0;j<len(org.Offers);j++  {
		if org.Offers[j].IdOffer==args[1]{
			org.Offers=append(org.Offers[:j],org.Offers[j+1:]...)
			logger.Info("removeOffer()> "+strconv.Itoa(len(org.Offers)))
			find=true
			break
			}
	}
	if find==false {return shim.Error("removeOffer()> Cannot find the offer: "+args[1])}
	bytes,err=json.Marshal(org)
	if err!=nil {return shim.Error("removeOffer()> encoding error")}
	err=stub.PutState("o_",bytes)
	if err!=nil {return shim.Error("removeOffer()> stub.PutState error")}
	return shim.Success(nil)
}

//args[0] = idOrg {MD5}; args[1] = idOffer; args[2] = Titolo; args[3] = Ambito; args[4] = Mansione; args[5] = Studio; args[6] = Certificazione; args[7] = Scadenza;
//args[8] = Contratto; args[9] = Salario; args[10] = Descrizione; args[11] = MaxCandidates;
func (t *BlockCVChaincode) updateOffer(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	//att,found,err:=cid.GetAttributeValue(stub,"user_id")
	//if(found==false) {return shim.Error("updateOffer()> access denied!")}
	//if att!=args[0] {return shim.Error("updateOffer()> access denied!")}
	if len(args)!=12 {return shim.Error("updateOffer()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 12")}
	bytes,err:=stub.GetState("o_"+args[0])
	if err!=nil {return shim.Error("updateOffer()> stub.GetState error")}
	var org Organization
	err=json.Unmarshal(bytes,&org)
	if err!=nil {return shim.Error("updateOffer()> Cannot decode: var org Organization")}
	var offer Offer
	for i, value := range org.Offers {
		if value.IdOffer==args[1]{
			offer=Offer{value.IdOffer,args[2],args[3],args[4],args[5],
			args[6],args[7],args[8],args[9],args[10],args[11],
			org.Nome,args[0],value.Applications}
			org.Offers[i]=offer
			break
		}
	}
	bytes,err=json.Marshal(org)
	if err!=nil {return shim.Error("updateOffer()> Cannot encode: var org Organization")}
	err=stub.PutState("o_"+args[0],bytes)
	if err!=nil {return shim.Error("updateOffer()> stub.PutState error")}
	return shim.Success(nil)
}

//args[] vuoto
func (t *BlockCVChaincode) getOffers(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	if len(args)!=0 {return shim.Error("getOffers()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected: 0")}
	bytes,err:=stub.GetState("Orgs")
	if err!=nil {return shim.Error("getOffers()> stub.GetState error")}
	var orgs []OrgSummary
	err=json.Unmarshal(bytes,&orgs)
	logger.Info(len(orgs))
	if err!=nil {return shim.Error("getOffers()> decoding error")}
	var ret=[]Offer{}
	for _, value := range orgs {
		bytes,err=stub.GetState("o_"+value.IdOrg)
		var org Organization
		json.Unmarshal(bytes,&org)
		ret=append(ret,org.Offers...)
	}
	bytes,err=json.Marshal(ret)
	if err!=nil {return shim.Error("getOffers()> encoding error")}
	return shim.Success(bytes)
}

//args[0] = idOrg {MD5}; args[1] = idOffer ; args[2] = idWorker {MD5}
func (t *BlockCVChaincode) addApplication(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	if len(args)!=3 {return shim.Error("addApplication()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected:3 ")}
	bytes,err:=stub.GetState("o_"+args[0])
	if err!=nil {return shim.Error("addApplication()> stub.GetState error")}
	var org Organization
	err=json.Unmarshal(bytes,&org)
	if err!=nil {return shim.Error("addApplication()> Cannot decode: var org Organization")}
	for i, value := range org.Offers {
		if value.IdOffer==args[1] {
			logger.Info("addApplication()> trovato.")
			for _, x := range value.Applications { //controllo se si è già candidato
				if x.IdWorker==args[2] {
					return shim.Error("addApplication()> Duplicated application!")
				}
			}
			w,_:=stub.GetState("w_"+args[2])
			var worker Worker
			json.Unmarshal(w,&worker)
			value.Applications=append(value.Applications,Application{args[2],worker.Nome,worker.Residenza,worker.Email})
			org.Offers[i].Applications=value.Applications
			logger.Info("addApplication()> len: "+strconv.Itoa(len(value.Applications)))
		}
	}
	bytes,err=json.Marshal(org)
	if err!=nil {return shim.Error("addApplication()> Cannot encode: var org Organization")}
	err=stub.PutState("o_"+args[0],bytes)
	if err!=nil {return shim.Error("addApplication()> stub.PutState error")}
	return shim.Success(nil)
}

//args[0] = idOrg {MD5} ; args[1] = idWorker {MD5} ;args[2] = idExp ; args[3] = ExpType {Lavorativa,Formativa}; args[4] = SenderName;
//args[5] = Comment; args[6] = ExperienceTitle
func (t *BlockCVChaincode) addProposal(stub shim.ChaincodeStubInterface,args []string) pb.Response {
	if len(args)!=7 {return shim.Error("addProposal()> Invalid # of arguments, received: "+strconv.Itoa(len(args))+" expected:7 ")}
	bytes,err:=stub.GetState("o_"+args[0])
	if err!=nil {return shim.Error("addProposal()> stub.GetState error")}
	var org Organization
	err=json.Unmarshal(bytes,&org)
	if err!=nil {return shim.Error("addProposal()> Cannot decode: var org Organization")}
	var proposal Proposal = Proposal{strconv.Itoa(len(org.Proposals)),args[1],args[2],
	args[3],args[4],args[5],args[6],"pending"}
	org.Proposals=append(org.Proposals, proposal)
	bytes,err=json.Marshal(org)
	if err!=nil {return shim.Error("addProposal()> Cannot encode: var org Organization")}
	err=stub.PutState("o_"+args[0],bytes)
	if err!=nil {return shim.Error("addProposal()> stub.PutState error")}
	return shim.Success(nil)
}

//args[0] = idOrg; args[1] = idProposal; args[2] = status
func (t *BlockCVChaincode) changeStatusProposal(stub shim.ChaincodeStubInterface,args []string) pb.Response{
	//att,found,err:=cid.GetAttributeValue(stub,"user_id")
	//if(found==false) {return shim.Error("changeStatusProposal()> access denied!")}
	//if att!=args[0] {return shim.Error("changeStatusProposal()> access denied!")}
	bytes,err:=stub.GetState("o_"+args[0])
	if err!=nil {return shim.Error("changeStatusProposal()> stub.GetState error")}
	var org Organization
	err=json.Unmarshal(bytes,&org)
	if err!=nil {return shim.Error("changeStatusProposal()> Cannot decode: var org Organization")}
	for i, value := range org.Proposals {
		if value.IdProposal==args[1] {
			org.Proposals[i].Status=args[2]
			t.changeStatus(stub,[]string{org.Proposals[i].ExpType,org.Proposals[i].IdWorker,org.Proposals[i].IdExp,args[2]})
		}
	}
	bytes,err=json.Marshal(org)
	if err!=nil {return shim.Error("changeStatusProposal()> Cannot encode: var org Organization")}
	err=stub.PutState("o_"+args[0],bytes)
	if err!=nil {return shim.Error("changeStatusProposal()> stub.PutState error")}
	return shim.Success(nil)
}
