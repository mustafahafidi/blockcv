package com.blockcv.model.data;


import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.blockcv.model.ManageOffersModel.Offer.Candidate;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import com.blockcv.view.curriculum.OrgSuggestion;
import com.vaadin.ui.UniqueSerializable;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric_ca.sdk.Attribute;

import com.blockcv.model.ManageCVModel;
import com.blockcv.model.ManageCVModel.ExperienceType;
import com.blockcv.model.ManageOffersModel;
import com.blockcv.model.ManageOffersModel.Offer;
import com.blockcv.model.ProposalsModel.Proposal;
import com.blockcv.model.SearchCVModel.Curriculum;
import com.blockcv.model.SearchCVModel.Curriculum.EducationalExperience;
import com.blockcv.model.SearchCVModel.Curriculum.WorkingExperience;
import com.blockcv.model.SearchOffersModel.WorkOffer;
import com.blockcv.model.UserModel;
import com.blockcv.model.UserModel.UserType;
import com.blockcv.model.data.blockchain.HFBasicSDK;
import com.blockcv.model.data.blockchain.MinimalUser;
import com.blockcv.model.data.mongodb.MDBasicSDK;
import com.blockcv.presenter.MainPagePresenter;
import com.blockcv.view.curriculum.EducationalExpView;
import com.mongodb.client.FindIterable;
import com.vaadin.ui.Notification.Type;


public class DataAccess {
	
    private static final Logger log = Logger.getLogger(DataAccess.class);
    
	private HFBasicSDK hfsdk;
	private MDBasicSDK mdbsdk;
	
	private static DataAccess singleInstance;
	
	private DataAccess() {
		try {
			hfsdk = new HFBasicSDK();
		} catch (Exception e) {
			MainPagePresenter.showNotification("Impossibile connettersi alla blockchain..", Type.ERROR_MESSAGE);
			log.error("Error instantiating hfbasicsdk" + e.getMessage());
		}
		try {
			mdbsdk = new MDBasicSDK();
		} catch (Exception e) {
			MainPagePresenter.showNotification("Impossibile connettersi a Mongodb..", Type.ERROR_MESSAGE);
		}
		
	}
	
	
	public DataAccess(String x) {}
	
	public static void setInstance(DataAccess da) {
		singleInstance = da;
	}
	
	public static DataAccess getInstance() {
		if(singleInstance == null)
			singleInstance = new DataAccess();
		return singleInstance;
	}
	
	private boolean saveUser(UserModel userModel, Map<String, Boolean> visibilities) {
		log.info("saveUser: received userModel: "+userModel.toString());
		boolean result = false;
		Map<String, String> personalInfo = userModel.getPersonalInfo();
		
		if(visibilities == null || visibilities.isEmpty()) {
			log.info("Resetting visibilities");
			visibilities = new HashMap<String, Boolean>();
			visibilities.put("dateOfBirth", false);
			visibilities.put("placeOfBirth", false);
			visibilities.put("gender", false);
			visibilities.put("phoneNumber", false);
			visibilities.put("fiscalCode", false);
			visibilities.put("curriculum", true);
		}
		try {
			
			if(userModel.getUserType() == UserType.WORKER) {
				//1. save user personal infos, only visible
				
				if(visibilities.get("curriculum")) 
					result = hfsdk.invokeChaincode(userModel.getHfClient(), "saveUser", userModel.getUserType().name(), 
																					userModel.getId(),  
																					personalInfo.get("firstname"),  
																					personalInfo.get("lastname"),
																					visibilities.get("dateOfBirth") ? personalInfo.get("dateOfBirth") : "",
																					visibilities.get("placeOfBirth") ? personalInfo.get("placeOfBirth") : "",
																					personalInfo.get("address"),
																					visibilities.get("phoneNumber") ? personalInfo.get("phoneNumber") : "",
																					visibilities.get("gender") ? personalInfo.get("gender") : "",
																					visibilities.get("fiscalCode") ? personalInfo.get("fiscalCode") : "",
																					userModel.getEmail());
				
				
				mdbsdk.insertUser(userModel.getId(), personalInfo, visibilities);
				
				
			} else {
				result = hfsdk.invokeChaincode(userModel.getHfClient(), "saveUser", 
																				userModel.getUserType().name(), 
																				userModel.getId(),  
																				personalInfo.get("orgName"),
																				 personalInfo.get("foundationYear"),
																				 personalInfo.get("venue"),  
																				 personalInfo.get("orgPhone"),
																				 personalInfo.get("vatNumber"),
																				 userModel.getEmail()
																						 );
				
				mdbsdk.insertOrg(userModel.getId(), personalInfo);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error saving user error message: "+e.getMessage());
		}
		return result;
	}
	
	private Map<String, String> getPersonalInfoForUser(HFClient hfclient, String userId, UserType userType) {
		log.info("getPeronalInfoForUser: received userId: "+userId+" & userType: " + userType.name());
		String response = "";
		try {
			response = hfsdk.queryChaincode(hfclient, "getUser", userType.name(), userId);

			log.info("Got response from bl: "+response);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new HashMap<String, String>();
		}
		
		if(response.isEmpty()) {
			log.error("Error loading user info: response empty "+response);
			return new HashMap<String, String>();
		}
		
		return parsePersonalInfo(response, userType);
	}
	
	private Map<String, String> parsePersonalInfo(String chaincodeResponse, UserType userType) {
		
		JsonObject jobj = Json.createReader(new StringReader(chaincodeResponse)).readObject();
		Map<String,String> personalInfo = new HashMap<String, String>();
		if(userType == UserType.WORKER) {
			String userId = jobj.getString("IdWorker");
			personalInfo = mdbsdk.getAllPersonalInfo(userId, true);
			Map<String, Boolean> visibilities = mdbsdk.getPersonalVisibility(userId);
			if(visibilities.get("curriculum")) {
				if(visibilities.get("dateOfBirth")) personalInfo.put("dateOfBirth", jobj.getString("Nascita"));
				if(visibilities.get("placeOfBirth")) personalInfo.put("placeOfBirth", jobj.getString("LuogoNascita"));
				if(visibilities.get("phoneNumber")) personalInfo.put("phoneNumber", jobj.getString("Telefono"));
				if(visibilities.get("gender")) personalInfo.put("gender", jobj.getString("Sesso"));
				if(visibilities.get("fiscalCode")) personalInfo.put("fiscalCode", jobj.getString("CodiceFiscale"));
			}
			
		} else {
			String userId = jobj.getString("IdOrg");
			personalInfo = mdbsdk.getAllOrganizationInfo(userId, true);
			Map<String, Boolean> visibilities = mdbsdk.getOrganizationVisibility(userId);
			if(visibilities.isEmpty()) {
				log.error("Could not retreive visibilities from mongodb size:"+visibilities.size());
				return new HashMap<String, String>();
			}
			personalInfo.put("foundationYear", jobj.getString("Fondazione"));
			personalInfo.put("venue", jobj.getString("Sede"));
			personalInfo.put("orgPhone", jobj.getString("Telefono"));
			personalInfo.put("vatNumber", jobj.getString("Piva"));
			//personalInfo.put("email", jobj.getString("Email"));
		}
		return personalInfo;
	}
	
	private boolean getUserExperiences(UserModel userModel, Map<String, Map<String,String>> educationalExps, Map<String, Map<String, String>> workingExps) {
	
		String response = "";
		try {
			response = hfsdk.queryChaincode(userModel.getHfClient(), "getUser", userModel.getUserType().name(), userModel.getId());
			log.info("Got response from bl: "+response);
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
		
		return getUserExperiences(response, educationalExps, workingExps);
	}
	
	private boolean getUserExperiences(String chaincodeResponse, Map<String, Map<String,String>> educationalExps, Map<String, Map<String, String>> workingExps) {
		try {
			JsonObject jobj = Json.createReader(new StringReader(chaincodeResponse)).readObject();
			String idWorker = jobj.getString("IdWorker");
			jobj.getJsonArray("WorkingExps").forEach((wexp) -> {
				Map<String, String> expInfo = new HashMap<String, String>();
				expInfo.put("venue", wexp.asJsonObject().getString("Sede"));
				expInfo.put("periodFrom", wexp.asJsonObject().getString("DataInizio"));
				expInfo.put("periodTo", wexp.asJsonObject().getString("DataFine"));
				expInfo.put("idWorker", idWorker);
				expInfo.put("idOrg", wexp.asJsonObject().getString("IdOrg"));
				expInfo.put("type", "Lavorativa");
				expInfo.put("role", wexp.asJsonObject().getString("Mansione"));
				expInfo.put("company", wexp.asJsonObject().getString("Azienda"));
				expInfo.put("status", wexp.asJsonObject().getString("Status"));
				expInfo.put("idExp", wexp.asJsonObject().getString("IdWorkingExp"));
				expInfo.put("visibility", "true");
				
				workingExps.put(wexp.asJsonObject().getString("IdWorkingExp"), expInfo);
			});
			
			
			jobj.getJsonArray("StudyingExps").forEach((sexp) -> {
				Map<String, String> expInfo = new HashMap<String, String>();
				expInfo.put("venue", sexp.asJsonObject().getString("Sede"));
				expInfo.put("periodFrom", sexp.asJsonObject().getString("DataInizio"));
				expInfo.put("periodTo", sexp.asJsonObject().getString("DataFine"));
				expInfo.put("idWorker", idWorker);
				expInfo.put("idOrg", sexp.asJsonObject().getString("IdOrg"));
				expInfo.put("type", "Formativa");
				
				expInfo.put("studyTitle", sexp.asJsonObject().getString("Titolo"));
				expInfo.put("institute", sexp.asJsonObject().getString("Istituto"));
				expInfo.put("specialization", sexp.asJsonObject().getString("Specializzazione"));
				expInfo.put("status", sexp.asJsonObject().getString("Status"));
				expInfo.put("visibility", "true");
	
				expInfo.put("idExp", sexp.asJsonObject().getString("IdStudyingExp"));
				
				educationalExps.put(sexp.asJsonObject().getString("IdStudyingExp"), expInfo);
			});
			
			
			// Getting exps from mdb
			mdbsdk.getExps(idWorker).forEach((expid, expinfo) -> {
				expinfo.put("visibility", "false");
				if(expinfo.get("type").equals("Lavorativa")) 
					workingExps.put(expid, expinfo);
				else 
					educationalExps.put(expid, expinfo);
				
				
			});
		} catch(Exception e) {
			log.error("getUserExperiences by parsing response error");
			return false;
		}
		
		return true;
	}
	
	private boolean saveExperience(UserModel userModel, Map<String, String> expInfo) {
		log.info("==================SaveExperience: received this: =================== ");
		expInfo.forEach((ki, kv) -> System.out.println("'"+ ki +"':'"+ kv +"'"));
		if(expInfo.get("visibility").equals("true")) {
			try {
				if(expInfo.get("type").equals("Lavorativa"))
					hfsdk.invokeChaincode(userModel.getHfClient(), "saveExp",   expInfo.get("type"),
																			userModel.getId(),
																			expInfo.get("idExp"),
																			expInfo.get("company"),
																			expInfo.get("idOrg"),
																			expInfo.get("periodFrom"),
																			expInfo.get("periodTo"),
																			expInfo.get("venue"),
																			expInfo.get("role")
																			);
				else
					hfsdk.invokeChaincode(userModel.getHfClient(), "saveExp",   expInfo.get("type"),
																			userModel.getId(),
																			expInfo.get("idExp"),
																			expInfo.get("institute"),
																			expInfo.get("idOrg"),
																			expInfo.get("periodFrom"),
																			expInfo.get("periodTo"),
																			expInfo.get("venue"),
																			expInfo.get("studyTitle"),
																			expInfo.get("specialization")
																			);	
			} catch (Exception e) {
				log.error(e.getMessage());
				return false;
			}
		} else {
			// saving on mongodb
			log.info("===================saving on mongodb>:=============");
			expInfo.forEach((ki, kv) -> System.out.println("'"+ ki +"':'"+ kv +"'"));
			mdbsdk.saveExp(expInfo);
		}
		
		return true;
	}
	
	
	
	
	
	
	
	
	/* Authentication Data Persistance */
	public UserModel tryLogin(String user, String pass) {
		// register and enroll new user
        HFClient hfclient;
        MinimalUser hfUser;
        Collection<Attribute> userAttrs;
        UserModel result;
		
        try {
			hfUser = hfsdk.enrollUser(user, pass);
	        hfclient = hfsdk.getNewHfClient();
	        hfclient.setUserContext(hfUser); 
	        hfsdk.setChannel(hfclient);
	        userAttrs = hfsdk.getUserIdentity(user).getAttributes();
	        Attribute userIdAttr = userAttrs.stream().filter((attr) -> attr.getName().equals("user_id")).findAny().get();
			Attribute userTypeAttr=userAttrs.stream().filter((attr) -> attr.getName().equals("user_type")).findAny().get();
			Map<String, String> personalInfo = getPersonalInfoForUser(hfclient, userIdAttr.getValue(), UserType.valueOf(userTypeAttr.getValue()));
	        if(personalInfo.isEmpty()){
	        	log.error("Cannot load userPersonal Info");
	        	return new UserModel();
	        }
	        result = new UserModel(userIdAttr.getValue(), user, pass, UserType.valueOf(userTypeAttr.getValue()), personalInfo, hfclient);
		} catch (Exception e) {
			e.printStackTrace();
        	log.error("Exception while login user: "+ e.getMessage());
			return new UserModel();
		}
		return result; 
	}
	
	public boolean trySignup(String user, String pass, Map<String,String> info, UserType userType) {
		String userId = UserModel.generateUserId(user);
		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
		attrs.add(new Attribute("user_id", userId,true));
		attrs.add(new Attribute("user_type", userType.toString(),true));
		
		try {
			log.info("Registring user to the CA...");
			hfsdk.registerUser(user, pass, attrs);
			HFClient client = hfsdk.getNewHfClient();
			client.setUserContext(hfsdk.enrollUser(user, pass));
	        hfsdk.setChannel(client);
	        /*
	        boolean result;
	        if(visibilities.get("foundationYearv")) personalInfo.put("foundationYear", jobj.getString("Fondazione"));
			if(visibilities.get("venuev")) personalInfo.put("venue", jobj.getString("Sede"));
			if(visibilities.get("orgPhonev")) personalInfo.put("orgPhone", jobj.getString("Telefono"));
			if(visibilities.get("vatNumberv")) personalInfo.put("vatNumber", jobj.getString("Piva"));
			if(visibilities.get("emailv")) personalInfo.put("email", jobj.getString("Email"));
	         
	        if(userType == UserType.WORKER)
				result = hfsdk.invokeChaincode(client, "saveUser", userType.name(), userId,  "",  "", "", "", "", "", "", "");
	        else 
				result = hfsdk.invokeChaincode(client, "saveUser", userType.name(), userId,  info.get("email"),
																							 info.get("orgName"), 
																							 info.get("foundationYear"),
																							 info.get("venue"), 
																							 info.get("orgPhone"), 
																						 info.get("vatNumber"));
				
	        if(!result) log.error("Could not save user ID on the blockchain");
	        */
	        UserModel userModel = new UserModel(userId, user, pass, userType, info, client);
	        saveUser(userModel, null);
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
		
		return true;
	}
	
	
	/* CV Data Persistance */
	public boolean loadExperiences(UserModel userModel, ManageCVModel manageCVModel) {
		
		if(!userModel.isLoggedIn()) {
			log.error("User not logged in, cannot get its data");
			return false;
		}
	
		Map<String, Map<String, String>> edExps = new HashMap<String, Map<String, String>>();
		Map<String, Map<String, String>> wExps = new HashMap<String, Map<String, String>>();
		getUserExperiences(userModel, edExps, wExps);
		
		log.info("loadExperiences: es"+edExps.size()+" ws"+wExps.size());
		manageCVModel.setEducationalExps(edExps);
		manageCVModel.setWorkingExps(wExps);
		manageCVModel.setLastId(wExps.size()+edExps.size()+1);
		Map<String, Boolean> vis = mdbsdk.getPersonalVisibility(userModel.getId());
		manageCVModel.setInfoVisibility(vis);
		
		/*pi.forEach((ki, kv) -> System.out.println("'"+ ki +"':'"+ kv +"'"));
		System.out.println(vis.size());
		vis.forEach((ki, kv) -> System.out.println("'"+ ki +"':'"+ kv +"'"));*/
		return true;
	}

	
	public boolean saveCurriculum(UserModel userModel, ManageCVModel manageCVModel) {
		//TODO ############## save private data on mongodb
		Map<String, Boolean> visibilities = manageCVModel.getInfoVisibility();
		Map<String, String> personalInfo = userModel.getPersonalInfo();
		
		if(visibilities.get("curriculum")) {
			
			log.info("Saving public personal info on blockchain");
			boolean result = saveUser(userModel, visibilities);
			if(!result) log.error("saveCurriculum: could not saveUserInfo");
			
			manageCVModel.getEducationalExps().forEach((kexp, expInfo) -> {
				log.info("saveCurriculum.idexp"+kexp+" vis:"+ expInfo.get("visibility"));
				expInfo.put("idExp", kexp);
				saveExperience(userModel, expInfo);
			});
			manageCVModel.getWorkingExps().forEach((kexp, expInfo) -> {
				expInfo.put("idExp", kexp);
				saveExperience(userModel, expInfo);
			});
			
			
			
		} else { 
			// the cv is not public anymore we need to update worldstate
			log.info("Cv is not public anymore, resetting user data on blockchain");
	
			//transfer public experiences back to mongodb
			log.info("transferring all public experiences back to mongodb");
			Map<String, Map<String, String>> edExps = manageCVModel.getEducationalExps();
			Map<String, Map<String, String>> wExps = manageCVModel.getWorkingExps();
			edExps.forEach((expid, expinfo) -> {
				if(expinfo.get("visibility").equals("true")) {
					expinfo.put("visibility", "false");
					saveExperience(userModel, expinfo);
				} else {
					expinfo.put("idWorker", userModel.getId());
					mdbsdk.saveExp(expinfo);
				}
			});
			wExps.forEach((expid, expinfo) -> {
				if(expinfo.get("visibility").equals("true")) {
					expinfo.put("visibility", "false");
					saveExperience(userModel, expinfo);
				} else {
					expinfo.put("idWorker", userModel.getId());
					mdbsdk.saveExp(expinfo);
				}
			});
			
			try {
				hfsdk.invokeChaincode(userModel.getHfClient(), "resetUser", userModel.getUserType().name(), userModel.getId());
			} catch (Exception e) {
				log.error(e.getMessage());
				return false;
			}		
		}
		return true;
	}
	
	public boolean  requestCertifyExperience(UserModel userModel, Proposal proposal) {

		try {
			///args[0] = idOrg ; args[1] = idWorker ;args[2] = idExp ; args[3] = ExpType
			hfsdk.invokeChaincode(userModel.getHfClient(), "addProposal", 
																 proposal.getIdOrg(),
																 userModel.getId(),
																 proposal.getIdExp(),
																 proposal.getExpType(),
																 proposal.getSenderName(),
																 proposal.getComment(),
																 proposal.getExperienceTitle());
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}	
		
		return true;
	}

	public boolean certifyExperience(UserModel userModel, String expID) {
		ArrayList<String> args = new ArrayList<String>();
		args.add(userModel.getId());
		args.add(expID);
		try {

			//args[0] = expType ; args[1] = idWorker ; args[2] = idExp ; args[3] = status	
			hfsdk.invokeChaincode(userModel.getHfClient(), "changeStatus", 
															 "Lavorativa",
															 userModel.getId(),
															 expID,
															 "approved");
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}	
		
		return true;
	}
	
	public boolean certifyCV(String cvID, String comment) {
		/*unused
		 * ArrayList<String> args = new ArrayList<String>();
		args.add(userModel.getId());
		args.add(expID);
		try {
			hfsdk.invokeChaincode(userModel.getHfClient(), "certify", (String[]) args.toArray());
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}	
		*/
		return true;
	}
	
	public Map<String,Offer> getOrgOffers(UserModel userModel) {
		log.info("GetOrgOffers invocato");
		if(!userModel.isLoggedIn()) { 
			log.error("User not logged in, cannot get its data");
			return new HashMap<String,Offer>();
		}

		
		String response = "";
		try {
			
			response = hfsdk.queryChaincode(userModel.getHfClient(), "getUser",
																	 userModel.getUserType().name(),
																	 userModel.getId());

			log.info("Got data from bl: "+response);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new HashMap<String, Offer>();
		}
		
		if(response == null || response.isEmpty()) {
			log.error("Response getOffers empty");
			return new HashMap<String, Offer>();
		}
		
		Map<String, Offer> result = new HashMap<String, Offer>();
		JsonObject jobj = Json.createReader(new StringReader(response)).readObject();
		
		
		jobj.getJsonArray("Offers").forEach((jv) -> {
		
			List<Candidate> candidates = new ArrayList<Candidate>();
			
			jv.asJsonObject().getJsonArray("Applications").forEach((jApp) -> 
				candidates.add(new Candidate(jApp.asJsonObject().getString("Nome"),
											 jApp.asJsonObject().getString("Indirizzo"),
											 jApp.asJsonObject().getString("Email")))
			);
			
			result.put(jv.asJsonObject().getString("IdOffer"), new Offer(jv.asJsonObject().getString("IdOffer"), 
																		userModel.getId(),
																		jv.asJsonObject().getString("Azienda"),
																		jv.asJsonObject().getString("Titolo"),
																		Integer.parseInt(jv.asJsonObject().getString("MaxCandidates")), 
																			jv.asJsonObject().getString("Ambito"),
																			jv.asJsonObject().getString("Mansione"),
																			jv.asJsonObject().getString("Studio"),
																			jv.asJsonObject().getString("Certificazione"),
																			jv.asJsonObject().getString("Scadenza"),
																			jv.asJsonObject().getString("Contratto"),
																			jv.asJsonObject().getString("Salario"),
																			jv.asJsonObject().getString("Descrizione"),
																			candidates)
																);
		});
		
		return result;
	}

	public Map<String, Offer> getAllOffers(UserModel userModel) {
		log.info("GetOrgOffers invocato");
		if(!userModel.isLoggedIn()) {
			log.error("User not logged in, cannot get its data");
			return new HashMap<String,Offer>();
		}


		String response = "";
		try {

			response = hfsdk.queryChaincode(userModel.getHfClient(), "getOffers");

			log.info("Got data from bl: "+response);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new HashMap<String, Offer>();
		}

		if(response == null || response.isEmpty()) {
			log.error("Response getOffers empty");
			return new HashMap<String, Offer>();
		}

		Map<String, Offer> result = new HashMap<String, Offer>();
		JsonArray jobj = Json.createReader(new StringReader(response)).readArray();

		jobj.forEach((jv) -> {

			result.put(jv.asJsonObject().getString("IdOffer"), new Offer(jv.asJsonObject().getString("IdOffer"),
					jv.asJsonObject().getString("IdOrg"),
					jv.asJsonObject().getString("Azienda"),
					jv.asJsonObject().getString("Titolo"),
					Integer.parseInt(jv.asJsonObject().getString("MaxCandidates")),
					jv.asJsonObject().getString("Ambito"),
					jv.asJsonObject().getString("Mansione"),
					jv.asJsonObject().getString("Studio"),
					jv.asJsonObject().getString("Certificazione"),
					jv.asJsonObject().getString("Scadenza"),
					jv.asJsonObject().getString("Contratto"),
					jv.asJsonObject().getString("Salario"),
					jv.asJsonObject().getString("Descrizione"),
					null)
			);
		});

		return result;
	}
	
	public boolean saveOffers(UserModel userModel, ManageOffersModel manageOffersModel) {
		ArrayList<String> args = new ArrayList<String>();
		args.add(userModel.getId());
		
		manageOffersModel.getOffers().forEach((idOff, vOff) -> {
			try {
				hfsdk.invokeChaincode(userModel.getHfClient(), "saveOffer", userModel.getId(),
																			vOff.getId(),
																			vOff.getTitle(),
																			vOff.getEmploymentSector(),
																			vOff.getWorkFunction(),
																			vOff.getRequiredStudyTitle(),
																			vOff.getRequiredCert(),
																			vOff.getExpirationDate(),
																			vOff.getContractType(),
																			vOff.getSalaryRange(),
																			vOff.getDescription(),
																			vOff.getMaxCandidates().toString()
																			);
			} catch (Exception e) {
				log.error(e.getMessage());
			}	
		});
		
		return true;
	}

	public boolean candidateToOffer(UserModel userModel, WorkOffer wOffer) {
		
		try {
			hfsdk.invokeChaincode(userModel.getHfClient(), "addApplication", wOffer.getIdOrg(),
																		wOffer.getId(),
																		userModel.getId()
																			 );
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}	
		return true;
	}

	
	public Map<String, WorkOffer> getFilteredOffers(UserModel userModel, Map<String,String> filter) {
	
		Map<String, WorkOffer> wOffers = new HashMap<String, WorkOffer>();
		Map<String, Offer> offers = getAllOffers(userModel);
		offers.forEach((idOffer, infOffer) -> {
			boolean include = true;
			String keyword = filter.get("keywords");
			String offerSummary = infOffer.getId()+
					infOffer.getIdOrg()+
					infOffer.getCompany()+
					infOffer.getTitle()+
					infOffer.getEmploymentSector()+
					infOffer.getWorkFunction()+
					infOffer.getRequiredStudyTitle()+
					infOffer.getRequiredCert()+
					infOffer.getExpirationDate()+
					infOffer.getContractType()+
					infOffer.getSalaryRange()+
					infOffer.getDescription();
			/*
			if(!offerSummary.contains(filter.get("keywords"))) include=false;
			if(!offerSummary.contains(filter.get("scope")) && (filter.get("scope")!=null || !filter.get("scope").isEmpty())) include=false;
			if(!offerSummary.contains(filter.get("job")) && (filter.get("job")!=null || !filter.get("job").isEmpty())) include=false;
			if(!offerSummary.contains(filter.get("contractType")) && (filter.get("contractType")!=null || !filter.get("contractType").isEmpty())) include=false;
			if(include)*/
				wOffers.put(idOffer, new WorkOffer(infOffer.getId(), 
												   infOffer.getIdOrg(), 
												   infOffer.getCompany(),
												   infOffer.getTitle(), 
												   infOffer.getEmploymentSector(), 
												   infOffer.getWorkFunction(), 
												   infOffer.getRequiredStudyTitle(), 
												   infOffer.getRequiredCert(), 
												   infOffer.getExpirationDate(), 
												   infOffer.getContractType(),
												   infOffer.getSalaryRange(), 
												   infOffer.getDescription(), 
												   false));
		});
		return wOffers;
	}

	public Map<String,Curriculum> getFilteredCVs(UserModel userModel, Map<String,String> filter) {
		
		
		if(!userModel.isLoggedIn()) { 
			log.error("User not logged in");
			return new HashMap<String,Curriculum>();
		}

		
		String response = "";
		try {
			response = hfsdk.queryChaincode(userModel.getHfClient(), "getWorkers");
			log.info("Got data from bl: "+response);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new HashMap<String, Curriculum>();
		}
		
		if(response == null || response.isEmpty() || !response.startsWith("[")) {
			log.error("Response getCurriculum empty or invalid");
			return new HashMap<String, Curriculum>();
		}


		Map<String, Curriculum> result = new HashMap<String, Curriculum>();
		JsonArray jobj = Json.createReader(new StringReader(response)).readArray();
		
		jobj.forEach((jv) -> {
			
			Map<String,Map<String, String>> edExps = new HashMap<String, Map<String, String>>();
			Map<String,Map<String, String>> wExps = new HashMap<String, Map<String, String>>();
			getUserExperiences(jv.toString(), edExps, wExps);

			List<EducationalExperience> edMExps = new ArrayList<EducationalExperience>();
			List<WorkingExperience> wMExps = new ArrayList<WorkingExperience>();

			if(!edExps.isEmpty())
				edMExps  = edExps.values().stream().map(expInfo -> new EducationalExperience(expInfo)).collect(Collectors.toList());
			if(!wExps.isEmpty())
				wMExps = edExps.values().stream().map(expInfo -> new WorkingExperience(expInfo)).collect(Collectors.toList());

			result.put(jv.asJsonObject().getString("IdWorker"), new Curriculum(true, parsePersonalInfo(jv.toString(), UserType.WORKER), edMExps,wMExps));
		});
		
		/*
		List<EducationalExperience> eduExps = Arrays.asList(new EducationalExperience("e001", "Laurea triennale", "Università di Padova", "Informatica", "Torre Archimede", "2016", "2018"));
		List<WorkingExperience> workExps = Arrays.asList(new WorkingExperience("e002", "Ifin Sistemi", "Ottobre 2017", "", "Via tettarella 5 Padova", "Progettista"), new WorkingExperience("e003", "CRAI", "Luglio 2016", "", "Venegazzù", "Magazziniere"));
		
		Map<String,String> info1 = new HashMap<>();
		info1.put("firstname", "Luca");
		info1.put("lastname", "Ballan");
		info1.put("address", "Volpago del M.llo");
		info1.put("dateOfBirth", "26/08/1996");
		
		Map<String,String> info2 = new HashMap<>();
		info2.put("firstname", "Alessio");
		info2.put("lastname", "Gobbo");
		info2.put("address", "Padova");
		
		Map<String,Curriculum> cvs = new HashMap<>();
		cvs.put("cv001", new Curriculum(true, info1, eduExps, workExps));
		cvs.put("cv002", new Curriculum(false, info2, new ArrayList<>(), new ArrayList<>()));
		
		Map<String,Curriculum> filteredCVs = new HashMap<>();
		cvs.forEach((id,cv) -> {
			String loc = filter.get("location").toLowerCase();
			boolean cert = Boolean.parseBoolean(filter.get("certifiableExps"));
			if((loc.equals("") || (!loc.equals("") && cv.getPersonalInfo().get("address").toLowerCase().contains(filter.get("location")))) && (cert==false || (cert==true && cv.isCertifiable())))
				filteredCVs.put(id,cv);
		});*/
		return result;//filteredCVs;
	}
	
	public Map<String,Proposal> getProposals(UserModel userModel) {
		
		if(!userModel.isLoggedIn()) { 
			log.error("User not logged in, cannot get its data");
			return new HashMap<String,Proposal>();
		}

		
		String response = "";
		try {
			
			response = hfsdk.queryChaincode(userModel.getHfClient(), "getUser",
																	 userModel.getUserType().name(),
																	 userModel.getId());

			log.info("Got data from bl: "+response);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new HashMap<String, Proposal>();
		}
		
		if(response == null || response.isEmpty()) {
			log.error("Response getProposals empty");
			return new HashMap<String, Proposal>();
		}

		Map<String,Proposal> result = new HashMap<>();
		JsonObject jobj = Json.createReader(new StringReader(response)).readObject();
		jobj.getJsonArray("Proposals").forEach((jv) -> {
			result.put(jv.asJsonObject().getString("IdProposal"), 
					new Proposal(jv.asJsonObject().getString("IdProposal"),
								jv.asJsonObject().getString("IdWorker"),
								jv.asJsonObject().getString("IdExp"),
								userModel.getId(),
								jv.asJsonObject().getString("ExpType"),
								jv.asJsonObject().getString("SenderName"),
								 jv.asJsonObject().getString("Comment"),
								 jv.asJsonObject().getString("ExperienceTitle"),
								 jv.asJsonObject().getString("Status"))
					);
		});
		/*	*/
		//result.put("pr001", new Proposal("0","0","0",ExperienceType.EDUCATIONAL,"Mustafa Hafidi", "Titolo esperienza", "dfgfdgfdg","pending"));
		/*props.put("pr002", new Proposal("Biblioteca Comunale di Volpago del M.llo", "L'esperienza si è rivelata un fallimento totale", "Impiego estivo come spazzino"));
		props.put("pr003", new Proposal("Alessio Gobbo Idraulica", "Scroto Scroto Scroto Sroto Scroto", "Esperienza Formativa casuale animale"));*/
		return result;
	}
	
	
	public boolean acceptProposal(UserModel userModel, String idProp) {
		try {
			hfsdk.invokeChaincode(userModel.getHfClient(), "changeStatusProposal", userModel.getId(),
																			 idProp,
																			 "approved");
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}	
		return true;
	}
	
	public boolean rejectProposal(UserModel userModel, String idProp, String message) {
		try {
			hfsdk.invokeChaincode(userModel.getHfClient(), "changeStatusProposal", userModel.getId(),
																			 idProp,
																			 "rejected");
																			 //message);
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}	
		return true;
	}

	/*public boolean addProposal(UserModel userModel, String idProp, String idOrg, String idExp, ExperienceType expType) {
		try {
			hfsdk.invokeChaincode(userModel.getHfClient(), "addProposal", userModel.getId(),
																						idProp,
																						"rejected");
			//message);
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
		return true;
	}*/

	public List<OrgSuggestion> getOrgSuggestions(UserModel userModel) {
		ArrayList<OrgSuggestion>  result = new ArrayList<OrgSuggestion>();
		try {
			String response = hfsdk.queryChaincode(userModel.getHfClient(), "getOrgsSummaries");
			log.info("getOrgSuggestions: received from bc "+response);
			if(response.isEmpty()) {
				log.error("Error loading orgsSuggestions info: response empty "+response);
				return new ArrayList<OrgSuggestion>();
			}
			
			JsonArray jobj = Json.createReader(new StringReader(response)).readArray();
			jobj.asJsonArray().forEach(jv -> {
				result.add(new OrgSuggestion(jv.asJsonObject().getString("IdOrg"), jv.asJsonObject().getString("Nome")));
			});
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return result;
	}

	public static void main(String[] args) {
		/* data access testing */
		String emailTest="email"+((new Date()).getTime());
		String passwordTest = "password";
		/*
		
		Map<String, String> infos = new HashMap<String, String>();
		infos.put("firstname", "firstname- value1");
		infos.put("lastname", "lastname- value1");
		infos.put("dateOfBirth", "dateOfBirth value");
		infos.put("placeOfBirth", "placeOfBirth value");
		infos.put("address", "address value");
		infos.put("phoneNumber", "phoneNumber value");
		infos.put("gender", "gender value");
		infos.put("fiscalCode", "fiscalCode value");
		
		infos.put("orgName", "orgName value");
		infos.put("foundationYear", "foundationYear value");
		infos.put("venue", "venue value");
		infos.put("orgPhone", "orgPhone value");
		infos.put("vatNumber", "vatNumber value");
		infos.put("email", "email value");
		
		/* SIGNING UP WORKER AND GETTING IT */
		/*boolean result = DataAccess.getInstance().trySignup(emailTest+"w", passwordTest, infos, UserType.WORKER);
		if(!result) log.error("Could not signup test worker ");

		UserModel resuser = DataAccess.getInstance().tryLogin(emailTest+"w", passwordTest);
		if(!resuser.isLoggedIn()) log.error("Could not login test user ");
		/*log.info("personal info size: "+resuser.getPersonalInfo().size());
		resuser.getPersonalInfo().forEach((ki, kv) -> log.info("'"+ ki +"':'"+ kv +"'"));*/
		
		/* SIGNING UP ORGANIZATION AND GETTING IT */
		/*result = DataAccess.getInstance().trySignup(emailTest+"o", passwordTest, infos, UserType.ORGANIZATION);
		if(!result) log.error("Could not signup test organization");

		resuser = DataAccess.getInstance().tryLogin(emailTest+"o", passwordTest);
		if(!resuser.isLoggedIn()) log.error("Could not login test organization ");
		
		log.info("personal info size: "+resuser.getPersonalInfo().size());
		resuser.getPersonalInfo().forEach((ki, kv) -> log.info("'"+ ki +"':'"+ kv +"'"));
		
		
		/* LOADING EXPERIENCES */
	/*	resuser = DataAccess.getInstance().tryLogin(emailTest+"w", passwordTest);
		if(!resuser.isLoggedIn()) log.error("Could not login test worker");
		
		ManageCVModel manageCVModel = new ManageCVModel();
		Map<String, String> fields = new HashMap<String,String>();
		fields.put("studyTitle", "studyTitle value");
		fields.put("institute", "institute value");
		fields.put("specialization", "specialization value");
		fields.put("venue", "venue value");
		fields.put("periodFrom", "periodFrom value");
		fields.put("periodTo", "periodTo value");
		
		Map<String, Boolean> visibility = new HashMap<String, Boolean>();
		/*if(visibilities.get("dateOfBirth")) userInfoArgs.add(personalInfo.get("dateOfBirth"));
			if(visibilities.get("placeOfBirth")) userInfoArgs.add(personalInfo.get("placeOfBirth"));
			if(visibilities.get("phoneNumber")) userInfoArgs.add(personalInfo.get("phoneNumber"));
			if(visibilities.get("gender")) userInfoArgs.add(personalInfo.get("gender"));
			if(visibilities.get("fiscalCode"))*/

	/*	visibility.put("dateOfBirth", true);
		visibility.put("placeOfBirth", true);
		visibility.put("gender", true);
		visibility.put("phoneNumber", true);
		visibility.put("fiscalCode", true);
		visibility.put("curriculum", true);
		manageCVModel.setInfoVisibility(visibility);
		result = manageCVModel.loadExperiences(resuser);
		if(!result) log.error("could not load experiences for test worker");
		
		log.info("saving a new exprience through savecurriculum()");
		manageCVModel.addExperience(ExperienceType.EDUCATIONAL, fields);
		manageCVModel.saveCurriculum(resuser, visibility);
		
		log.info("Experiences educational loaded: "+manageCVModel.getEducationalExps().size());
		manageCVModel.getEducationalExps().forEach((ki, kv) -> log.info("'"+ ki +"':'"+ kv +"'"));

		log.info("Working educational loaded: "+manageCVModel.getWorkingExps().size());
		manageCVModel.getWorkingExps().forEach((ki, kv) -> log.info("'"+ ki +"':'"+ kv +"'"));*/
	}

	
}
