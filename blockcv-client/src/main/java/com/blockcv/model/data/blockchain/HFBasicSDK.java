package com.blockcv.model.data.blockchain;


import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.Attribute;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.HFCAIdentity;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.IdentityException;

import elemental.json.Json;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.json.JsonObject;

public class HFBasicSDK {

    private static final Logger log = Logger.getLogger(HFBasicSDK.class);
    private final String DOCKERMACHINE = "localhost";//"192.168.56.101";
    private final String ORDERER_ADDRESS = "grpc://"+DOCKERMACHINE+":7050";
    private final String PEER0_ADDRESS = "grpc://"+DOCKERMACHINE+":7051";
    private final String CA_ADDRESS = "http://"+DOCKERMACHINE+":7054";
    private final String EVENTHUB_ADDRESS = "grpc://"+DOCKERMACHINE+":7053";

    private static final String CHANNEL_NAME = "mychannel";
    private static final String CHAINCODE_NAME = "mycc";
    
    private final boolean SERIALIZE = false;
    
    private HFCAClient caClient;
    //private HFClient client;
    
    //private Channel defChannel;
    private MinimalUser admin;
    private MinimalUser appUser;
    
    public static final String EVENTHUB_CONNECTION_WAIT_TIME      = "org.hyperledger.fabric.sdk.eventhub_connection.wait_time";
    private static final String EVENTHUB_CONNECTION_WAIT_TIME_VALUE = "10000";
    
    
    static {
        System.setProperty(EVENTHUB_CONNECTION_WAIT_TIME, EVENTHUB_CONNECTION_WAIT_TIME_VALUE);
    }
    public HFBasicSDK() throws Exception {

    	
    	// create fabric-ca client
    	caClient = getHfCaClient(CA_ADDRESS, null);
    	
        // enroll or load admin
        admin = getAdmin();
        log.info(admin);
/*
        // register and enroll new user
        appUser = getUser("hfuser");
        log.info(appUser);

        // get HFC client instance
        client = getHfClient();
        // set user context
        client.setUserContext(admin);*/

        // get HFC channel using the client
        /*defChannel = getChannel();
        log.info("Channel: " + defChannel.getName());*/
        
        
    }
    

    /**
     * Invoke chaincode mycc
     *
     * @param function The function to invoke in the chaincode
     * @param args Parameters to pass to the function
     * @throws ProposalException
     * @throws InvalidArgumentException
    */ 
    public boolean invokeChaincode(HFClient client, String function, String... args) throws Exception {
    	Channel defChannel = client.getChannel("mychannel");
    	TransactionProposalRequest tpr = client.newTransactionProposalRequest();
    	ChaincodeID cid = ChaincodeID.newBuilder().setName(CHAINCODE_NAME).build();
    	tpr.setChaincodeID(cid);
    	tpr.setFcn(function);
    	tpr.setArgs(args);
    	Collection<ProposalResponse> responses = defChannel.sendTransactionProposal(tpr);
    	List<ProposalResponse> invalid = responses.stream().filter(r -> r.isInvalid()).collect(Collectors.toList());
    	if (!invalid.isEmpty()) {
    	    invalid.forEach(response -> {
    	        log.error(response.getMessage());
    	    });
    	    throw new RuntimeException("invalid response(s) ");
    	}
    	
    	BlockEvent.TransactionEvent event = defChannel.sendTransaction(responses).get(60, TimeUnit.SECONDS);
        if (event.isValid()) {
            log.info("Transaction tx: " + event.getTransactionID() + " is completed.");
            return true;
        } else {
            log.error("Transaction tx: " + event.getTransactionID() + " is invalid.");
            return false;
        }
    }
    
    
    /**
     * Query chaincode mycc
     *
     * @param function The function to invoke in the chaincode
     * @param arg Parameter to pass to the function
     * @throws ProposalException
     * @throws InvalidArgumentException
     */
    public String queryChaincode(HFClient client, String function, String... args) throws ProposalException, InvalidArgumentException {
    	// get channel instance from client
        Channel channel = client.getChannel("mychannel");
        
        // create chaincode request
        QueryByChaincodeRequest qpr = client.newQueryProposalRequest();
        
        // build cc id providing the chaincode name. Version is omitted here.
        ChaincodeID chaincodeId = ChaincodeID.newBuilder().setName(CHAINCODE_NAME).build();
        qpr.setChaincodeID(chaincodeId);
        
        // CC function to be called
        qpr.setFcn(function);
        qpr.setArgs(args);
        Collection<ProposalResponse> res = channel.queryByChaincode(qpr);
        
        // display response
        String stringResponse = "";
        for (ProposalResponse pres : res) {
            stringResponse = new String(pres.getChaincodeActionResponsePayload());
            //log.info(stringResponse);
        }
        return stringResponse; //TODO: get the right response, actually gets last one in the payload
    }

    
    /**
     * Initialize and get HF channel
     *
     * @param client The HFC client
     * @return Initialized channel
     * @throws InvalidArgumentException
     * @throws TransactionException
     * @throws InterruptedException 
     
*/
    public Channel setChannel(HFClient client) throws InvalidArgumentException, TransactionException, InterruptedException {
        // initialize channel
        // peer name and endpoint in fabcar network
        Peer peer = client.newPeer("peer0.org1.example.com", PEER0_ADDRESS);
        // eventhub name and endpoint in fabcar network
        EventHub eventHub = client.newEventHub("eventhub01", EVENTHUB_ADDRESS);
        // orderer name and endpoint in fabcar network
        Orderer orderer = client.newOrderer("orderer.example.com", ORDERER_ADDRESS);
        // channel name in fabcar network
        Channel channel = client.newChannel("mychannel");
        channel.addPeer(peer);
        channel.addEventHub(eventHub);
        channel.addOrderer(orderer);
        //Thread.sleep(10000);
        channel.initialize();
        return channel;
    }
    /**
     * Create new HLF client
     *
     * @return new HLF client instance. Never null.
     * @throws CryptoException
     * @throws InvalidArgumentException
     */
    public HFClient getNewHfClient() throws Exception {
        // initialize default cryptosuite
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        HFClient client = HFClient.createNewInstance();
	    client.setCryptoSuite(cryptoSuite);
        return client;
    }
    
    /**
     * Get new fabic-ca client
     *
     * @param caUrl              The fabric-ca-server endpoint url
     * @param caClientProperties The fabri-ca client properties. Can be null.
     * @return new client instance. never null.
     * @throws Exception
     */
    private HFCAClient getHfCaClient(String caUrl, Properties caClientProperties) throws Exception {
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        HFCAClient caClient = HFCAClient.createNewInstance(caUrl, caClientProperties);
        caClient.setCryptoSuite(cryptoSuite);
        return caClient;
    }

    /**
     * Register and enroll user with userId.
     * If AppUser object with the name already exist on fs it will be loaded and
     * registration and enrollment will be skipped.
     *
     * @param caClient  The fabric-ca client.
     * @param registrar The registrar to be used.
     * @param userId    The user id.
     * @return AppUser instance with userId, affiliation,mspId and enrollment set.
     * @throws Exception
     */
    /*public MinimalUser getUser(/*MinimalUser registrar, String userId) throws Exception {
    	MinimalUser appUser = tryDeserialize(userId);
        if (appUser == null) {
            String enrollmentSecret = registerUser(userId);
            appUser = enrollUser(userId, enrollmentSecret);
            serialize(appUser);
        }
        return appUser; 
    }*/
    
    public String registerUser(String userId, String password, Collection<Attribute> attrs) throws Exception {
    	HFCAIdentity id = caClient.newHFCAIdentity(userId);
    	id.setSecret(password);
    	id.setAffiliation("org1");
    	id.setAttributes(attrs);
    	//id.setType(caClient.HFCA_TYPE_USER);
    	id.setAffiliation("");
    	id.create(admin);
    	
    	
    	/*
    	RegistrationRequest rr = new RegistrationRequest(userId, "org1");
    	rr.setSecret(password);
    	attrs.forEach((attr) -> rr.addAttribute(attr));
        String enrollmentSecret = caClient.register(rr, admin);
        */
        return id.getSecret();
    }
    
    public void updateUser(String userId, String password, Collection<Attribute> attrs) throws IdentityException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException {
    	HFCAIdentity id = caClient.newHFCAIdentity(userId);
    	id.setSecret(password);
    	id.setAffiliation("org1");
    	id.setAttributes(attrs);
    	//id.setType(caClient.HFCA_TYPE_USER);
    	id.setAffiliation("");
    	id.update(admin);
    	
    	
    }
    
    /*public boolean verifyIdentity(String userId, String password) throws IdentityException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException {
    	Collection<HFCAIdentity> identities = caClient.getHFCAIdentities(admin);
    	System.out.println("size: "+identities.size());
    	identities.forEach((id) -> {System.out.println("enrid: "+id.getEnrollmentId()+" pass: "+id.getSecret()+" attrs: "+id.getAttributes().size());
    		id.getAttributes().forEach(att -> System.out.print(att.getName()+"-"+att.getValue()));
    	});
    	return identities.stream().anyMatch((id) -> (id.getEnrollmentId()==userId && id.getSecret()==password));
    }*/
    public HFCAIdentity getUserIdentity(String userId) throws IdentityException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException {
    	Collection<HFCAIdentity> identities = caClient.getHFCAIdentities(admin);
    	/*System.out.println("size: "+identities.size());
    	identities.forEach((id) -> {System.out.println("enrid: "+id.getEnrollmentId()+" pass: "+id.getSecret()+" attrs: "+id.getAttributes().size());
    		id.getAttributes().forEach(att -> System.out.print(att.getName()+"-"+att.getValue()));
    	});*/
    	return identities.stream().filter((id) -> (id.getEnrollmentId().equals(userId))).findAny().get();
    }
    
    public MinimalUser enrollUser(String userId, String enrollmentSecret) throws EnrollmentException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException, IdentityException {
    	//EnrollmentRequest req = new EnrollmentRequest();
    	Enrollment enrollment = caClient.enroll(userId, enrollmentSecret);
    	//caClient.getHFCAIdentities(admin);
        return new MinimalUser(userId, "org1", "Org1MSP", enrollment);
    }
    
    
    /**
     * Enroll admin into fabric-ca using {@code admin/adminpw} credentials.
     * If AppUser object already exist serialized on fs it will be loaded and
     * new enrollment will not be executed.
     *
     * @param caClient The fabric-ca client
     * @return AppUser instance with userid, affiliation, mspId and enrollment set
     * @throws Exception
     */
    private MinimalUser getAdmin() throws Exception {
    	if(SERIALIZE) admin = tryDeserialize("admin");
    	
        if (admin == null) {
            Enrollment adminEnrollment = caClient.enroll("admin", "adminpw");
            
            admin = new MinimalUser("admin", "org1", "Org1MSP", adminEnrollment);
            if(SERIALIZE) serialize(admin);
        }
        return admin; //new MinimalUser("admin", "org1", "Org1MSP", adminEnrollment);
    }

   


    // user serialization and deserialization utility functions
    // files are stored in the base directory

    /**
     * Serialize AppUser object to file
     *
     * @param appUser The object to be serialized
     * @throws IOException
     */
    private void serialize(MinimalUser appUser) throws IOException {
    	//if(!Files.exists(Paths.get(appUser.getName() + ".jso")))
    		
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(
                Paths.get(appUser.getName() + ".jso")))) {
            oos.writeObject(appUser);
        }
    }

    /**
     * Deserialize AppUser object from file
     *
     * @param name The name of the user. Used to build file name ${name}.jso
     * @return
     * @throws Exception
     */
    private MinimalUser tryDeserialize(String name) throws Exception {
        if (Files.exists(Paths.get(name + ".jso"))) {
            return deserialize(name);
        }
        return null;
    }

    private MinimalUser deserialize(String name) throws Exception {
        try (ObjectInputStream decoder = new ObjectInputStream(
                Files.newInputStream(Paths.get(name + ".jso")))) {
            return (MinimalUser) decoder.readObject();
        }
    }
    
    public String getUniqueUserID() {
    	return appUser.getEnrollment().getCert();
    }
    
    public static void main(String[] arg) throws Exception {
    	 HFBasicSDK hfbasicsdk = new HFBasicSDK(); //for testing
         
    	 /* user enrolling and getting new client  */
    	 hfbasicsdk.updateUser("admin", "adminpw", Arrays.asList(new Attribute[] {new Attribute("user_id", "ADMIN EMAIL", true),
    			 																	new Attribute("user_type", "ADMIN Type", true)}));
    	 												
         MinimalUser hfAdmin = hfbasicsdk.enrollUser("admin", "adminpw");
         HFClient client = hfbasicsdk.getNewHfClient();
         client.setUserContext(hfAdmin);
         
         hfbasicsdk.setChannel(client);
         
         Thread.sleep(2000);
         
         String queryResponse; boolean invokeResponse;

         String[] args = new String[15];
         for(int i=0; i<15; i++) args[i] ="3";
         
         
         /* #####  SAVING  WORKER AND GETTING IT*/
         invokeResponse = hfbasicsdk.invokeChaincode(client, "saveUser", "WORKER", "41", args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8]);
         log.info("Saving worker 1: "+invokeResponse);

         queryResponse = hfbasicsdk.queryChaincode(client, "getUser", "WORKER", "41");
         log.info("Getting worker query 1: "+queryResponse);
/*
         invokeResponse = hfbasicsdk.invokeChaincode(client, "saveUser", "WORKER", "41", "B","A","N","A","N","A",args[7],args[8]);
         log.info("Saving worker  2: "+invokeResponse);

         queryResponse = hfbasicsdk.queryChaincode(client, "getUser", "WORKER", "41");
         log.info("Getting worker query 2: "+queryResponse);

         invokeResponse = hfbasicsdk.invokeChaincode(client, "resetUser", "WORKER", "41");
         log.info("Resetting worker 1: "+invokeResponse);

         queryResponse = hfbasicsdk.queryChaincode(client, "getUser", "WORKER", "41");
         log.info("Getting worker query 3: "+queryResponse);
          */
         /* #####  SAVING  ORGANIZATION AND GETTING IT 
         invokeResponse = hfbasicsdk.invokeChaincode(client, "saveUser", "ORGANIZATION", "14", args[1],args[2],args[3],args[4],args[5]);
         log.info("Saving organization 1: "+invokeResponse);

         invokeResponse = hfbasicsdk.invokeChaincode(client, "saveUser", "ORGANIZATION", "24", args[1],args[2],args[3],args[4],args[5]);
         log.info("Saving organization 2: "+invokeResponse);

         invokeResponse = hfbasicsdk.invokeChaincode(client, "saveUser", "ORGANIZATION", "34", args[1],args[2],args[3],args[4],args[5]);
         log.info("Saving organization 3: "+invokeResponse);
         
         
         queryResponse = hfbasicsdk.queryChaincode(client, "getOrgsSummaries");
         log.info("Getting all organization query 1: "+queryResponse);
*/
         //-------------------------------------------------------------------------------
         
         /* #####  SAVING  STUDY EXPERIENCE AND GETTING IT 

         invokeResponse = hfbasicsdk.invokeChaincode(client, "saveExp", "Formativa", "41", "0",args[2],args[3],args[4],args[5],args[6],args[7],args[8]);
         log.info("Saving STUDY Experience 1: "+invokeResponse);
         
         queryResponse = hfbasicsdk.queryChaincode(client, "getExp", "Formativa", "41", "0");
         log.info("Getting STUDY Experience query 1: "+queryResponse); 
         */
         /* #####  SAVING  WORK EXPERIENCE AND GETTING IT 
         invokeResponse = hfbasicsdk.invokeChaincode(client, "saveExp", "Lavorativa", "41", "0",args[2],args[3],args[4],args[5],args[6],args[7]);
         log.info("Saving WORK Experience 1: "+invokeResponse);

         invokeResponse = hfbasicsdk.invokeChaincode(client, "saveExp", "Lavorativa", "41", "1",args[2],args[3],args[4],args[5],args[6],args[7]);
         log.info("Saving WORK Experience 2: "+invokeResponse);
         
         queryResponse = hfbasicsdk.queryChaincode(client, "getExp", "Lavorativa", "41", "0");
         log.info("Getting WORK Experience query 1: "+queryResponse); 
         */
         /* #####  CHANGING STATUS OF A  WORK EXPERIENCE AND GETTING IT
         invokeResponse = hfbasicsdk.invokeChaincode(client, "changeStatus", "Lavorativa", "41", "0","approved");
         log.info("Changing status WORK Experience 1: "+invokeResponse);
         
         queryResponse = hfbasicsdk.queryChaincode(client, "getExp", "Lavorativa", "41", "0");
         log.info("Getting WORK Experience query 2: "+queryResponse);
 */
         /* #####  REMOVING A WORK EXPERIENCE AND GETTING IT 
         invokeResponse = hfbasicsdk.invokeChaincode(client, "removeExp", "Lavorativa", "41", "0");
         log.info("Removing WORK Experience 1: "+invokeResponse);
*/
         // queryResponse = hfbasicsdk.queryChaincode(client, "getExp", "WORK", "41", "0");
         // log.info("Getting WORK Experience 3: "+queryResponse);
         /* #####  SAVING AN OFFER AND GETTING IT 
         invokeResponse = hfbasicsdk.invokeChaincode(client, "saveOffer", "24", "1",args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9],args[10]);
         log.info("Saving Offer 1: "+invokeResponse);

         invokeResponse = hfbasicsdk.invokeChaincode(client, "saveOffer", "34", "1",args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9],args[10]);
         log.info("Saving Offer 2: "+invokeResponse);

         invokeResponse = hfbasicsdk.invokeChaincode(client, "saveOffer", "34", "2",args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9],args[10]);
         log.info("Saving Offer 3: "+invokeResponse);

         queryResponse = hfbasicsdk.queryChaincode(client, "getOffers");
         log.info("Getting all Offers 1: "+queryResponse);

         invokeResponse = hfbasicsdk.invokeChaincode(client, "removeOffer", "34", "1");
         log.info("Removing Offer 1: "+invokeResponse);

         invokeResponse = hfbasicsdk.invokeChaincode(client, "addApplication", "34", "2","41");
         log.info("Saving application 1: "+invokeResponse);

         invokeResponse = hfbasicsdk.invokeChaincode(client, "addProposal", "24", "41","1","Lavorativa");
         log.info("Saving proposal 1: "+invokeResponse);*/
    }
      
}