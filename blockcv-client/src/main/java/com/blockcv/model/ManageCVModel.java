package com.blockcv.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

import com.blockcv.model.data.DataAccess;
import com.blockcv.view.curriculum.OrgSuggestion;
import com.vaadin.server.StreamResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.apache.log4j.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ManageCVModel implements Model {

	private static final Logger log = Logger.getLogger(ManageCVModel.class);
	private final ExpIDGenerator generator = new ExpIDGenerator();
	
	public enum ExperienceType {
		EDUCATIONAL,
		WORKING
	}
	
	private Map<String, Map<String,String>> educationalExps;
	private Map<String, Map<String,String>> workingExps;
	private Map<String,Boolean> visibility;
	private final Pattern dateExportPattern = Pattern.compile("[0-9]+-[0-9]+-[0-9]+");
	
	private List<OrgSuggestion> orgsSuggestions;
	
	public ManageCVModel() {}
	
	public ManageCVModel(Map<String,Map<String,String>> edu, Map<String,Map<String,String>> work, Map<String,Boolean> vis) {
		educationalExps = edu;
		workingExps = work;
		visibility = vis;
	}

	private class TempXmlInfo{
		private Vector<HashMap<String, String>> exp;
		private Vector<HashMap<String, String>> form;

		public TempXmlInfo(File xml){
			exp= new Vector<HashMap<String, String>>();
			form= new Vector<HashMap<String, String>>();
			generateXmlPaths(xml);
			
			xml.delete();
			//xml.deleteOnExit();
		}

		public Vector<HashMap<String, String>> getExperiences(){
			return exp;
		}

		public Vector<HashMap<String, String>> getFormation(){
			return form;
		}

		private void generateXmlPaths(File xml){
			try{
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(xml);
				doc.getDocumentElement().normalize();
				Node n = doc.getElementsByTagName("WorkExperienceList").item(0);
				if(n != null )addPathToExp(n);
				n = doc.getElementsByTagName("EducationList").item(0);
				if(n != null )addPathToForm(n);
			} catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			}
		}

		private void addPathToExp(Node n){
			addPathToExp(n,"",0);
		}

		private void addPathToExp(Node n, String path, int n_el){
			NodeList nList= n.getChildNodes();
			Node node;
			for (int i = 0; i < nList.getLength(); i++) {
				node = nList.item(i);
				if(node.getNodeName().equals("WorkExperience")){
					n_el++;
					exp.add(new HashMap<String, String>());
				}
				if(node.hasChildNodes()) addPathToExp(node, path+"/"+node.getNodeName(),n_el);
				else if(!node.getNodeName().equals("From") && !node.getNodeName().equals("To")) exp.get(n_el-1).put(path, node.getNodeValue());
                    else exp.get(n_el - 1).put(path+"/"+node.getNodeName(), node.getAttributes().getNamedItem("year").getNodeValue() +"-"+ node.getAttributes().getNamedItem("month").getNodeValue().replace("-","") +"-"+ node.getAttributes().getNamedItem("day").getNodeValue().replace("-",""));
            }
		}

		private void addPathToForm(Node n){
			addPathToForm(n,"",0);
		}

		private void addPathToForm(Node n, String path, int n_el){
			NodeList nList= n.getChildNodes();
			Node node;
			for (int i = 0; i < nList.getLength(); i++) {
                node = nList.item(i);
                if (node.getNodeName().equals("Education")) {
                    n_el++;
                    form.add(new HashMap<String, String>());
                }
                if (node.hasChildNodes()) addPathToForm(node, path + "/" + node.getNodeName(), n_el);
                else if (!node.getNodeName().equals("From") && !node.getNodeName().equals("To")) form.get(n_el - 1).put(path, node.getNodeValue());
                    else
                    form.get(n_el - 1).put(path+"/"+node.getNodeName(), node.getAttributes().getNamedItem("year").getNodeValue() +"-"+ node.getAttributes().getNamedItem("month").getNodeValue().replace("-","") +"-"+ node.getAttributes().getNamedItem("day").getNodeValue().replace("-",""));
             }
		}
	}

	public void addExperienceFromXml(File xml){
		TempXmlInfo infoToAdd = new TempXmlInfo(xml);
		Map<String,String> workToAdd;
		for(HashMap<String, String> e : infoToAdd.getExperiences()) {
			workToAdd = new HashMap<String,String>();
			if(e.get("/WorkExperience/Employer/Name")!=null) workToAdd.put("company",e.get("/WorkExperience/Employer/Name"));
			if(e.get("/WorkExperience/Period/From")!=null) workToAdd.put("periodFrom",e.get("/WorkExperience/Period/From"));
			if(e.get("/WorkExperience/Period/To")!=null) workToAdd.put("periodTo",e.get("/WorkExperience/Period/To"));
			else workToAdd.put("periodTo","In Corso");
			if(e.get("/WorkExperience/Employer/ContactInfo/Address/Contact/Municipality")!=null) workToAdd.put("venue",e.get("/WorkExperience/Employer/ContactInfo/Address/Contact/Municipality"));
			if(e.get("/WorkExperience/Position/Label")!=null) workToAdd.put("role",e.get("/WorkExperience/Position/Label"));
            workToAdd.put("visibility","false");
			this.addExperience(ExperienceType.WORKING, workToAdd);
		}
		for(HashMap<String, String> e : infoToAdd.getFormation()) {
			workToAdd = new HashMap<String,String>();
			if(e.get("/Education/Title")!=null) workToAdd.put("studyTitle",e.get("/Education/Title"));
			if(e.get("/Education/Organisation/Name")!=null) workToAdd.put("institute",e.get("/Education/Organisation/Name"));
			if(e.get("/Education/Organisation/ContactInfo/Address/Contact/Municipality")!=null) workToAdd.put("venue",e.get("/Education/Organisation/ContactInfo/Address/Contact/Municipality"));
			if(e.get("/Education/Period/From")!=null) workToAdd.put("periodFrom",e.get("/Education/Period/From"));
			if(e.get("/Education/Period/To")!=null) workToAdd.put("periodTo",e.get("/Education/Period/To"));
			else workToAdd.put("periodTo","In Corso");
            workToAdd.put("visibility","false");

            //Non esiste un campo specializzazione in formato EuroPass
			workToAdd.put("specialization","[COMPILA - Importato da formato EuroPass]");
			this.addExperience(ExperienceType.EDUCATIONAL, workToAdd);
		}
	}

	public boolean loadExperiences(UserModel userModel) {
		orgsSuggestions = DataAccess.getInstance().getOrgSuggestions(userModel);
		return DataAccess.getInstance().loadExperiences(userModel, this);
	}
	
	public void setLastId(int idx ) {
		generator.setLastIdx(idx);
	}
	/*public void setCV(UserModel userModel) throws Exception {
		ManageCVModel m = DataAccess.getInstance().getCV(userModel);
		setData(m);
	}*/
	
	public String addExperience(ExperienceType expType, Map<String,String> experience) {
		String expID = generator.getNextID();
		if(expType.equals(ExperienceType.EDUCATIONAL)) {
			//expID = String.valueOf(educationalExps.size() + 1);
			educationalExps.put(expID, experience);
		}
		else {
			//expID = String.valueOf(workingExps.size() + 1);
			workingExps.put(expID, experience);
		}
		return expID;
	}

	public StreamResource getXmlCV(){
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("SkillsPassport");
			doc.appendChild(rootElement);

			Element lInfo = doc.createElement("LearnerInfo");
			rootElement.appendChild(lInfo);

			Element ident = doc.createElement("Identification");
			lInfo.appendChild(ident);
			Element workExpL = doc.createElement("WorkExperienceList");
			lInfo.appendChild(workExpL);
			Element eduL= doc.createElement("EducationList");
			lInfo.appendChild(eduL);

			Element personame = doc.createElement("PersonName");
			ident.appendChild(personame);
			Element contact = doc.createElement("ContactInfo");
			ident.appendChild(contact);

            Element infoToAdd, sharedEl, period, newEl;
			/*for(HashMap<String,String> e : INFO.entrySet()) {
                //qui andra' la parte che genera i dati dell'utente per l'xml
			}*/
            for(Map.Entry<String,Map<String,String>> e : educationalExps.entrySet()) {
                infoToAdd= doc.createElement("Education");
                sharedEl= doc.createElement("Organisation");
				period= doc.createElement("Period");
                infoToAdd.appendChild(sharedEl);
				infoToAdd.appendChild(period);
                for(Map.Entry<String,String> en : e.getValue().entrySet()) {
                    switch(en.getKey()){
                        case "studyTitle":
                            newEl= doc.createElement("Title");
                            newEl.setTextContent(en.getValue());
                            infoToAdd.appendChild(newEl);
                            break;
                        case "institute":
                            newEl= doc.createElement("Name");
                            newEl.setTextContent(en.getValue());
                            sharedEl.appendChild(newEl);
                            break;
                        case "venue":
                            newEl= doc.createElement("ContactInfo");
                            newEl.appendChild(doc.createElement("Address"));
							newEl.getElementsByTagName("Address").item(0).appendChild(doc.createElement("Contact"));
							newEl.getElementsByTagName("Contact").item(0).appendChild(doc.createElement("Municipality"));
                            newEl.getElementsByTagName("Municipality").item(0).setTextContent(en.getValue());
                            sharedEl.appendChild(newEl);
                            break;
                        case "periodFrom":
							if(dateExportPattern.matcher(en.getValue()).matches()) {
								newEl = doc.createElement("From");
								newEl.setAttribute("day", "---" + en.getValue().split("-")[2]);
								newEl.setAttribute("month", "--" + en.getValue().split("-")[1]);
								newEl.setAttribute("year", en.getValue().split("-")[0]);
								period.appendChild(newEl);
							}
                            //newEl.setTextContent(en.getValue());
                            break;
						case "periodTo":
							if(dateExportPattern.matcher(en.getValue()).matches()) {
								newEl = doc.createElement("To");
								newEl.setAttribute("day", "---" + en.getValue().split("-")[2]);
								newEl.setAttribute("month", "--" + en.getValue().split("-")[1]);
								newEl.setAttribute("year", en.getValue().split("-")[0]);
								period.appendChild(newEl);
								infoToAdd.appendChild(newEl);
							}
							break;
                        default:
                            break;
                    }
                }
                eduL.appendChild(infoToAdd);
            }
			for(Map.Entry<String,Map<String,String>> e : workingExps.entrySet()) {
                infoToAdd= doc.createElement("WorkExperience");
                sharedEl= doc.createElement("Employer");
				period= doc.createElement("Period");
				infoToAdd.appendChild(sharedEl);
				infoToAdd.appendChild(period);
                for(Map.Entry<String,String> en : e.getValue().entrySet()) {
                    switch(en.getKey()){
                        case "company":
                            newEl= doc.createElement("Name");
                            newEl.setTextContent(en.getValue());
                            sharedEl.appendChild(newEl);
                            break;
						case "periodFrom":
							if(dateExportPattern.matcher(en.getValue()).matches()) {
								newEl = doc.createElement("From");
								newEl.setAttribute("day", "---" + en.getValue().split("-")[2]);
								newEl.setAttribute("month", "--" + en.getValue().split("-")[1]);
								newEl.setAttribute("year", en.getValue().split("-")[0]);
								period.appendChild(newEl);
							}
							//newEl.setTextContent(en.getValue());
							break;
						case "periodTo":
							if(dateExportPattern.matcher(en.getValue()).matches()) {
								newEl = doc.createElement("To");
								newEl.setAttribute("day", "---" + en.getValue().split("-")[2]);
								newEl.setAttribute("month", "--" + en.getValue().split("-")[1]);
								newEl.setAttribute("year", en.getValue().split("-")[0]);
								period.appendChild(newEl);
								infoToAdd.appendChild(newEl);
							}
							break;
                        case "venue":
                            newEl= doc.createElement("ContactInfo");
							newEl.appendChild(doc.createElement("Address"));
							newEl.getElementsByTagName("Address").item(0).appendChild(doc.createElement("Contact"));
							newEl.getElementsByTagName("Contact").item(0).appendChild(doc.createElement("Municipality"));
							newEl.getElementsByTagName("Municipality").item(0).setTextContent(en.getValue());
                            sharedEl.appendChild(newEl);
                            break;
                        case "role":
                            newEl= doc.createElement("Position");
                            newEl.appendChild(doc.createElement("Label"));
                            newEl.getElementsByTagName("Label").item(0).setTextContent(en.getValue());
                            infoToAdd.appendChild(newEl);
                            break;
                        default:
                            break;
                    }
                }
                workExpL.appendChild(infoToAdd);
			}
			return generateFile(doc);
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}
		return null;
	}

	private StreamResource generateFile(Document doc){
		String tempfilename = "CV"+new Random().nextInt();
	    File toExport;
		byte[] data;

		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
            toExport = new File(tempfilename+".xml");
            StreamResult result = new StreamResult(toExport);
			transformer.transform(source,result);
			data = Files.readAllBytes(Paths.get(tempfilename+".xml"));
			StreamResource resource = new StreamResource(new StreamResource.StreamSource() {
				public InputStream getStream(){
					return (data == null) ? null : new ByteArrayInputStream(data);
				}
			}, "exportedCV.xml");
			toExport.delete();
            return resource;
		} catch (TransformerException | IOException tfe) {
			tfe.printStackTrace();
            return null;
		}
	}


	public void removeExperience(String expID) {
		educationalExps.remove(expID);
		workingExps.remove(expID);
	}

	public boolean requestExpCertification(UserModel userModel,
										   String idExp,
										   String idOrg,
										   String expType,
										   String comment,
										   String experienceTitle) {
		return DataAccess.getInstance().requestCertifyExperience(userModel,
				new ProposalsModel.Proposal("", userModel.getId(), idExp, idOrg, expType,
						userModel.getPersonalInfo().get("firstname")+" "+userModel.getPersonalInfo().get("lastname"), comment, experienceTitle, ""));
	}

	public boolean saveCurriculum(UserModel userModel, Map<String,Boolean> visibility) {
		setInfoVisibility(visibility);
		return DataAccess.getInstance().saveCurriculum(userModel, this);
	}
	
	/* class related methods */
	public void setData(ManageCVModel m) {
		this.educationalExps = m.educationalExps;
		this.workingExps = m.workingExps;
		this.visibility = m.visibility;
	}
	
	
	public Map<String, String> getExperienceById(String expId, ExperienceType expType) {
		if(expType == ExperienceType.EDUCATIONAL && educationalExps.containsKey(expId))
			return educationalExps.get(expId);
		if(expType == ExperienceType.WORKING && workingExps.containsKey(expId)) 
			return workingExps.get(expId);
		log.info("Cannot find experience from id "+ expId);
		educationalExps.forEach((k,v) -> log.info("K: "+k));
		workingExps.forEach((k,v) -> log.info("K: "+k));
		return new HashMap<String, String>();
	}

	public Map<String, Map<String,String>> getEducationalExps(){
		return educationalExps;
	}

	public Map<String, Map<String,String>> getWorkingExps(){
		return workingExps;
	}
	
	public Map<String, Boolean> getInfoVisibility() {
		return visibility;
	}

	public void setInfoVisibility(Map<String, Boolean> visibility) {
		this.visibility = visibility;
	}
	
	public void setEducationalExps(Map<String, Map<String, String>> educationalExps) {
		this.educationalExps = educationalExps;
	}

	public void setWorkingExps(Map<String, Map<String, String>> workingExps) {
		this.workingExps = workingExps;
	}
	
	
	// UTILITY INNER CLASS
	
	private class ExpIDGenerator {
		
		private int nextID;
		
		public ExpIDGenerator() {
			nextID = 1;
		}
		public void setLastIdx(int idx) {
			nextID = idx;
		}
		public String getNextID() {
			return "exp" + nextID++;
		}
	}

	public void updateVisibility(String expid, ExperienceType exptype, boolean status) {
		getExperienceById(expid, exptype).put("visibility", status ?  "true" : "false");
		log.info("UPDATING VISIBILITY");
		Map<String, String> exp = getExperienceById(expid, exptype);
		exp.forEach((ki, kv) -> System.out.println("'"+ ki +"':'"+ kv +"'"));
		
	}

	public List<OrgSuggestion> getOrgSuggestions() {
		return orgsSuggestions;
	}
}
