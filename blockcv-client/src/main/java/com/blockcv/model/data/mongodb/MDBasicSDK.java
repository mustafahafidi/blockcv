package com.blockcv.model.data.mongodb;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.blockcv.model.data.DataAccess;
import com.mongodb.BasicDBObject;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MDBasicSDK {
    private static final Logger log = Logger.getLogger(MDBasicSDK.class);
    
	private final MongoClient client;
	private final MongoDatabase database;
	private final MongoCollection<Document> exp;//experience
	private final MongoCollection<Document> orginfo;//organizationInfo
	private final MongoCollection<Document> perinfo;//personalInfo
	
	//TEST: Ok
	//effettua la connessione ad atlas
	public MDBasicSDK() {
		MongoClientURI uri = new MongoClientURI("mongodb+srv://finitysys_map:map123456@blockcv-qu92c.mongodb.net/admin");

		client = new MongoClient(uri);
		database = client.getDatabase("BlockCV");
		exp = database.getCollection("Experience");
		orginfo = database.getCollection("organizationInfo");
		perinfo = database.getCollection("personalInfo");
	}
	
	//FUNZIONI x PERSONALINFO
	
	//TEST: Ok
	//ritorna i dati personali resi visibili
	public List<String> getPersonalInfo(String idWorker) {
		List<String> listR = new ArrayList<String>();
		
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("idWorker", idWorker);

		FindIterable<Document> cursor = perinfo.find(searchQuery);
		Document doc=cursor.first();
		if(doc.isEmpty()) {
			return listR;
		}
		listR.add((String)doc.get("firstname"));
		listR.add((String)doc.get("lastname"));
		listR.add((String)doc.get("address"));
		if(doc.getBoolean("dateOfBirthv")) {
			listR.add((String)doc.get("dateOfBirth"));
		}
		if(doc.getBoolean("placeOfBirthv")) {
			listR.add((String)doc.get("placeOfBirth"));
		}
		if(doc.getBoolean("phoneNumberv")) {
			listR.add((String)doc.get("phoneNumber"));
		}
		if(doc.getBoolean("genderv")) {
			listR.add((String)doc.get("gender"));
		}
		if(doc.getBoolean("fiscalCodev")) {
			listR.add((String)doc.get("fiscalCode"));
		}
		return listR;
	}
	
	//TEST: Ok
	//ritorna la visibilità dei dati personali
	public Map<String,Boolean> getPersonalVisibility(String idWorker){
		Map<String,Boolean> mapr = new HashMap<String,Boolean>();
		
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("idWorker", idWorker);

		FindIterable<Document> cursor = perinfo.find(searchQuery);
		Document doc=cursor.first();
		if(doc == null || doc.isEmpty()) {
			return mapr;
		}
		mapr.put("dateOfBirth", doc.getBoolean("dateOfBirthv"));
		mapr.put("placeOfBirth", doc.getBoolean("placeOfBirthv"));
		mapr.put("phoneNumber", doc.getBoolean("phoneNumberv"));
		mapr.put("gender", doc.getBoolean("genderv"));
		mapr.put("fiscalCode", doc.getBoolean("fiscalCodev"));
		mapr.put("curriculum", doc.getBoolean("curriculum"));
		return mapr;
	}
	
	//TEST: Ok
	//ritorna tutti i dati personali sia quelli visibili se v è false, ritorna tutti i dati se v è true
	public Map<String,String> getAllPersonalInfo(String idWorker, Boolean v){
		Map<String,String> mapr = new HashMap<String,String>();
		
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("idWorker", idWorker);

		FindIterable<Document> cursor = perinfo.find(searchQuery);
		try {
		Document doc=cursor.first();
		if(doc.isEmpty()) {
			return mapr;
		}
		mapr.put("firstname", (String)doc.get("firstname"));
		mapr.put("lastname", (String)doc.get("lastname"));
		mapr.put("address", (String)doc.get("address"));
		
		if(!doc.getBoolean("dateOfBirthv") || v) {
			mapr.put("dateOfBirth", (String)doc.get("dateOfBirth"));
		}
		if(!doc.getBoolean("placeOfBirthv") || v) {
			mapr.put("placeOfBirth", (String)doc.get("placeOfBirth"));
		}
		if(!doc.getBoolean("phoneNumberv") || v) {
			mapr.put("phoneNumber", (String)doc.get("phoneNumber"));
		}
		if(!doc.getBoolean("genderv") || v) {
			mapr.put("gender", (String)doc.get("gender"));
		}
		if(!doc.getBoolean("fiscalCodev") || v) {
			mapr.put("fiscalCode", (String)doc.get("fiscalCode"));
		}
		
		return mapr;
		}catch(NullPointerException e) {
			return mapr;
		}
	}
	
	//TEST: Ok
	//ritorna tutti i dati personali sia quelli visibili sia quelli non visibili
	public Map<String,String> getAllPersonalInfo(String idWorker){
		Map<String,String> mapr = new HashMap<String,String>();
		
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("idWorker", idWorker);

		FindIterable<Document> cursor = perinfo.find(searchQuery);
		try {
			Document doc=cursor.first();
		
		mapr.put("firstname", (String)doc.get("firstname"));
		mapr.put("lastname", (String)doc.get("lastname"));
		mapr.put("dateOfBirth", (String)doc.get("dateOfBirth"));
		mapr.put("placeOfBirth", (String)doc.get("placeOfBirth"));
		mapr.put("address", (String)doc.get("address"));
		mapr.put("phoneNumber", (String)doc.get("phoneNumber"));
		mapr.put("gender", (String)doc.get("gender"));
		mapr.put("fiscalCode", (String)doc.get("fiscalCode"));
		return mapr;
		}catch(NullPointerException e) {
			return mapr;
		}
		
	}
	
	//TEST: Ok
	//ritorna tutti i dati visibili delle persone che hanno location contenuta nella residenza
	public Map<String, List<String>> getInfos(String location){
		Map<String, List<String>> maplist= new HashMap<String,List<String>>();
		
		FindIterable<Document> cursor = perinfo.find();
		for(Document doc : cursor) {
			if((doc.getString("address").toLowerCase().contains(location.toLowerCase()))) {
				maplist.put(doc.getString("idWorker"), this.getPersonalInfo(doc.getString("idWorker")));
			}
		}
		return maplist;
	}
	
	
	
	//FUNZIONI x EXPERIENCE
	///////////////////////LETTURA
	
	//TEST: 0k
	//funzionalià ricerca per parole chiave
	public List<String> getUsers(String keyword){
		List<String> listr=new ArrayList<String>();
		
		FindIterable<Document> cursor = exp.find();
		
		for(Document doc : cursor) {
			if(doc.getBoolean("visibility")) {
				if(doc.getString("type").equals("Lavorativa") && (doc.getString("role").toLowerCase().contains(keyword.toLowerCase()) || doc.getString("company").toLowerCase().contains(keyword.toLowerCase())) ) {
					if(!listr.contains(doc.getString("idWorker"))) {
						listr.add(doc.getString("idWorker"));
					}
				}
				if(doc.getString("type").equals("Formativa") && (doc.getString("specialization").toLowerCase().contains(keyword.toLowerCase()) || doc.getString("studyTitle").toLowerCase().contains(keyword.toLowerCase())) ) {
					if(!listr.contains(doc.getString("idWorker"))) {
						listr.add(doc.getString("idWorker"));
					}
				}
			}//if visibility
					
		}//for
		
		return listr;
	}
	
	
	//TEST: Ok
	//ritorna tutte le esperienze di un utente
	public Map<String, Map<String,String>> getExps(String PersonalFabricId){
		Map<String, Map<String, String>> listmap= new HashMap<String, Map<String,String>>();
		
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("idWorker", PersonalFabricId);

		FindIterable<Document> cursor = exp.find(searchQuery);
		
		for(Document doc : cursor) {
			String id =  doc.get("_id").toString();
			listmap.put(id, this.getExp(id));
		}
		
		return listmap;
	}
	
	//TEST: Ok
	//ritorna un'esperienza anche se non visibile
	public Map<String,String> getExp(String expID){
		Map<String,String> mapr = new HashMap<String,String>();

		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("_id", new ObjectId(expID));

		FindIterable<Document> cursor = exp.find(searchQuery);
		Document doc=cursor.first();
		if(doc.isEmpty()) {
			return mapr;
		}
		
		if(((String)doc.get("type")).equals("Formativa")) {
			mapr.put("studyTitle",(String)doc.get("studyTitle"));
			mapr.put("institute",(String)doc.get("institute"));
			mapr.put("specialization",(String)doc.get("specialization"));
		}
		if(((String)doc.get("type")).equals("Lavorativa")) {
			mapr.put("company",(String)doc.get("company"));
			mapr.put("role",(String)doc.get("role"));
		}
		
		mapr.put("venue",(String)doc.get("venue"));
		mapr.put("periodFrom",(String)doc.get("periodFrom"));
		mapr.put("periodTo",(String)doc.get("periodTo"));
		mapr.put("idWorker",(String)doc.get("idWorker"));
		mapr.put("idOrg",(String)doc.get("idOrg"));
		mapr.put("type",(String)doc.get("type"));
		if(doc.getBoolean("visibility")) {
			mapr.put("visibility","true");
		} else {
			mapr.put("visibility","false");	
		}
		return mapr;
	}
	
	//TEST ok
	public String saveExp(Map<String,String> mapexp) {
		String r="";
		List<String> ls= new ArrayList<String>();
		String idfp=mapexp.get("idWorker");
		if(idfp.equals("")) {
			return r;
		}
		
		Document document = new Document();
		
		document.put("venue", mapexp.get("venue"));
		document.put("periodFrom", mapexp.get("periodFrom"));
		document.put("periodTo", mapexp.get("periodTo"));
		document.put("idWorker", mapexp.get("idWorker"));
		document.put("idOrg", mapexp.get("idOrg"));
		document.put("idExp", mapexp.get("idExp"));
		document.put("type", mapexp.get("type"));
		document.put("visibility", false);
		
		if(mapexp.get("type").equals("Formativa")) {
			document.put("studyTitle", mapexp.get("studyTitle"));
			document.put("institute", mapexp.get("institute"));
			document.put("specialization", mapexp.get("specialization"));
		
		} else if(mapexp.get("type").equals("Lavorativa")) {
			document.put("role", mapexp.get("role"));
			document.put("company", mapexp.get("company"));
		}
		
		BasicDBObject searchQuery1 = new BasicDBObject();
		searchQuery1.put("idExp", mapexp.get("idExp"));
		FindIterable<Document> cursor3 = exp.find(searchQuery1);
		for(Document doc : cursor3) {
			if(!ls.contains(doc.get("_id").toString())) {
				return doc.get("_id").toString();
			}
		}
		
		Document exExp = exp.find(eq("idExp", mapexp.get("idExp"))).first();
		
		if(exExp !=null) {
			log.info("mdbsdk: replacing");
			exp.replaceOne(eq("idExp", mapexp.get("idExp")), document);
		} else {
			log.info("mdbsdk: inserting");
			exp.insertOne(document);
		}

		
		return r;
	}
	
	//TEST ok
	public void deleteExp(String IdExp) {
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("_id", new ObjectId(IdExp));
		exp.deleteOne(searchQuery);
	}
		
	//TEST ok
	public void insertUser(String idpf, Map<String,String> m, Map<String, Boolean> vis) {
		
		if(!idpf.equals("")) {
			Boolean t=true;
			FindIterable<Document> cursor = perinfo.find();
			for(Document doc : cursor) {
				if(doc.get("idWorker").toString().equals(idpf)) {
					t=false;
					
				}
			}
			
				
			Document document = new Document();
			document.put("idWorker", idpf);
			document.put("firstname", m.get("firstname"));
			document.put("lastname", m.get("lastname"));
			document.put("dateOfBirth", m.get("dateOfBirth"));
			document.put("placeOfBirth", m.get("placeOfBirth"));
			document.put("address", m.get("address"));
			document.put("phoneNumber", m.get("phoneNumber"));
			document.put("gender", m.get("gender"));
			document.put("fiscalCode", m.get("fiscalCode"));
			
			/*
			  
			visibilities.put("dateOfBirth", true);
			visibilities.put("placeOfBirth", true);
			visibilities.put("gender", true);
			visibilities.put("phoneNumber", true);
			visibilities.put("fiscalCode", true);
			visibilities.put("curriculum", true);
			
			 */
			
			
			
			document.put("dateOfBirthv", vis.get("dateOfBirth"));
			document.put("placeOfBirthv", vis.get("placeOfBirth"));
			document.put("phoneNumberv", vis.get("phoneNumber"));
			document.put("genderv", vis.get("gender"));
			document.put("fiscalCodev", vis.get("fiscalCode"));
			document.put("curriculum", vis.get("curriculum"));
			if(t) {
				perinfo.insertOne(document);
			} else {
				BasicDBObject searchQuery = new BasicDBObject();
				searchQuery.put("idWorker", idpf);
				perinfo.replaceOne(searchQuery, document);
			}
		}
	}

	//TEST ok
	public void insertOrg(String idpf, Map<String,String> m) {
		
		if(!idpf.equals("")) {
			Boolean t=true;
			FindIterable<Document> cursor = orginfo.find();
			for(Document doc : cursor) {
				if(doc.get("idWorker").toString().equals(idpf)) {
					t=false;
				}
			}
			if(t) {	
				Document document = new Document();
				document.put("idWorker", idpf);
				document.put("orgName", m.get("orgName"));
				document.put("foundationYear", m.get("foundationYear"));
				document.put("venue", m.get("venue"));
				document.put("orgPhone", m.get("orgPhone"));
				document.put("vatNumber", m.get("vatNumber"));
				document.put("email", m.get("email"));
				document.put("foundationYearv", false);
				document.put("venuev", false);
				document.put("orgPhonev", false);
				document.put("vatNumberv", false);
				document.put("emailv", false);
				orginfo.insertOne(document);
			}
		}
	}

	//TEST ok
	public Map<String,String> getAllOrganizationInfo(String idWorker, Boolean v){
		Map<String,String> mapr = new HashMap<String,String>();
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("idWorker", idWorker);
		FindIterable<Document> cursor = orginfo.find(searchQuery);
		try {
			Document doc=cursor.first();
			mapr.put("orgName", (String)doc.get("orgName"));
			mapr.put("venue", (String)doc.get("venue"));
	
			if(v || !doc.getBoolean("foundationYearv")) {
				mapr.put("foundationYear", (String)doc.get("foundationYear"));
			}
			if(v || !doc.getBoolean("venuev")) {
				mapr.put("venue", (String)doc.get("venue"));
			}
			if(v || !doc.getBoolean("orgPhonev")) {
				mapr.put("orgPhone", (String)doc.get("orgPhone"));
			}
			if(v || !doc.getBoolean("vatNumberv")) {
				mapr.put("vatNumber", (String)doc.get("vatNumber"));
			}
			if(v || !doc.getBoolean("emailv")) {
				mapr.put("email", (String)doc.get("email"));
			}
	
			return mapr;
	
		}catch(NullPointerException e) {
			return mapr;
		}
}
	
	//TEST ok
	public Map<String,Boolean> getOrganizationVisibility(String idWorker){
		Map<String,Boolean> mapr = new HashMap<String,Boolean>();
		
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("idWorker", idWorker);

		FindIterable<Document> cursor = orginfo.find(searchQuery);
		Document doc=cursor.first();
		log.info("vis. size: "+doc.size());
		try {
			mapr.put("foundationYearv", doc.getBoolean("foundationYearv"));
			mapr.put("venuev", doc.getBoolean("venuev"));
			mapr.put("orgPhonev", doc.getBoolean("orgPhonev"));
			mapr.put("vatNumberv", doc.getBoolean("vatNumberv"));
			mapr.put("emailv", doc.getBoolean("emailv"));
		}catch(NullPointerException e) {
			log.error(e.getMessage());
			return mapr;
		}
		return mapr;
	}


}