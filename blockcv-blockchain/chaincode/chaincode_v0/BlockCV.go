package main


import (
	"fmt"
  	"encoding/json"
	//"strconv"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

// SimpleChaincode example simple Chaincode implementation
type SimpleChaincode struct {
}

type Curriculum struct {
  Id       string `json:"id"`
  First    string `json:"first"`
  Last     string `json:"last"`
  FavPosition string `json:"favposition"`
}


func (t *SimpleChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("BlockCV_Chaincode Is Starting Up")
	cvs := []Curriculum{}
	var cv Curriculum

	cv.Id="0"
	cv.First="Alessio"
	cv.Last="Gobbo"
	cv.FavPosition="Soft.Dev"

	cvs = append(cvs,cv)/**/
	bytes, err := json.Marshal( cvs )

	if err != nil {
		return shim.Error("Error initializing cvslist.")
	}

	err = stub.PutState("cvlist", bytes)
	if err != nil {
		return shim.Error("Error saving state cvslist.")
	}

	return shim.Success(nil)
}

func (t *SimpleChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("ex02 Invoke")
	function, args := stub.GetFunctionAndParameters()
	if function == "recordCV" {
		return t.recordCV(stub, args)
	} else if function == "queryCV" {
		return t.queryCV(stub, args)
	}

	return shim.Error("Invalid invoke function name. Expecting \"recordCV\" \"queryCV\"")
}

// Transaction makes payment of X units from A to B
func (t *SimpleChaincode) recordCV(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	bytes, err := stub.GetState( "cvlist" )

	if err != nil {
		return shim.Error( "Unable to get cvs." )
	}

	var cv Curriculum
	cv.Id=args[0]
	cv.First=args[1]
	cv.Last=args[2]
	cv.FavPosition=args[3]

	var cvs []Curriculum
	err = json.Unmarshal( bytes, &cvs )
	if err != nil {
		return shim.Error(err.Error())
	}
	cvs = append( cvs, cv )

	// Encode as JSON
	// Put back on the block
	bytes, err = json.Marshal( cvs )
	err = stub.PutState( "cvlist", bytes )
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(nil)
}

// query callback representing the query of a chaincode
func (t *SimpleChaincode) queryCV(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	bytes, err := stub.GetState( "cvlist" )
	if err != nil {
		return shim.Error( "Unable to get cvlist." +err.Error())
	}
	if bytes == nil {
		return shim.Error("cvlist value is nil")
	}

	var cvs []Curriculum

	// From JSON to data structure
	err = json.Unmarshal( bytes, &cvs )
	if err != nil {
		return shim.Error( "Unable unmarshal cvlist." +err.Error())
	}
	found := false

	// Look for match
	for _, cv := range cvs {
		// Match
		if cv.Id == args[0] {
		  // JSON encode
		  bytes, err = json.Marshal( cv )
		  found = true
		  break
		}
	}

	// Nope
	if found != true {
		//bytes, err = json.Marshal( nil )
		bytes = []byte("CV NOT FOUND")//stub.GetState("prova")
	}
	//fmt.Printf("Query Response:%s\n", bytes)
	return shim.Success(bytes)
}

func main() {
	err := shim.Start(new(SimpleChaincode))
	if err != nil {
		fmt.Printf("Error starting Simple chaincode: %s", err)
	}
}
