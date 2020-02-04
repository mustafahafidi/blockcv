package com.blockcv.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.blockcv.model.data.DataAccess;

public class SearchCVModel implements Model {
	
	public static class Curriculum {
		
		public static class EducationalExperience {
			private String expID;


			private String idOrg;
			private String studyTitle;
			private String institute;
			private String specialization;
			private String venue;
			private String periodFrom;
			private String periodTo;

			public String getStatus() {
				return status;
			}

			private String status;

			public EducationalExperience(Map<String, String> expInfo) {
				/*expInfo.put("venue", sexp.asJsonObject().getString("Sede"));
				expInfo.put("periodFrom", sexp.asJsonObject().getString("DataInizio"));
				expInfo.put("periodTo", sexp.asJsonObject().getString("DataFine"));
				expInfo.put("idWorker", idWorker);
				expInfo.put("idOrg", sexp.asJsonObject().getString("IdOrg"));
				expInfo.put("type", "Formativa");
				
				expInfo.put("studyTitle", sexp.asJsonObject().getString("Titolo"));
				expInfo.put("institute", sexp.asJsonObject().getString("Istituto"));
				expInfo.put("specialization", sexp.asJsonObject().getString("Specializzazione"));
				expInfo.put("status", sexp.asJsonObject().getString("Status"));
				expInfo.put("visibility", "true");*/
				
				this(expInfo.get("idExp"),
					 expInfo.get("studyTitle"),
					 expInfo.get("institute"),
					 expInfo.get("specialization"),
					 expInfo.get("venue"),
					 expInfo.get("periodFrom"),
					 expInfo.get("periodTo"), expInfo.get("status"));
			}
			
			public EducationalExperience(String id, String st, String i, String sp, String v, String pf, String pt, String status) {
				expID = id;
				studyTitle = st;
				institute = i;
				specialization = sp;
				venue = v;
				periodFrom = pf;
				periodTo = pt;
				this.status = status;
			}
			public String getIdOrg() {
				return idOrg;
			}
			public String getExpID() {
				return expID;
			}
			public String getStudyTitle() {
				return studyTitle;
			}
			public String getInstitute() {
				return institute;
			}
			public String getSpecialization() {
				return specialization;
			}
			public String getVenue() {
				return venue;
			}
			public String getPeriodFrom() {
				return periodFrom;
			}
			public String getPeriodTo() {
				return periodTo;
			}
		}
		
		public static class WorkingExperience {
			private String expID;
			private String company;
			private String periodFrom;
			private String periodTo;
			private String venue;
			private String role;
			
			public WorkingExperience(Map<String, String> expInfo) {
				/*
				 * 
				 * expInfo.put("venue", wexp.asJsonObject().getString("Sede"));
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
				 */
				this(expInfo.get("idExp"),
					 expInfo.get("company"),
					 expInfo.get("periodFrom"),
					 expInfo.get("periodTo"),
					 expInfo.get("venue"),
					 expInfo.get("role")
					 );
			}
			public WorkingExperience(String id, String c, String pf, String pt, String v, String r) {
				expID = id;
				company = c;
				periodFrom = pf;
				periodTo = pt;
				venue = v;
				role = r;
			}
			public String getExpID() {
				return expID;
			}
			public String getCompany() {
				return company;
			}
			public String getPeriodFrom() {
				return periodFrom;
			}
			public String getPeriodTo() {
				return periodTo;
			}
			public String getVenue() {
				return venue;
			}
			public String getRole() {
				return role;
			}
		}
		
		private boolean certifiable;
		private final Map<String,String> personalInfo; 
		private final List<EducationalExperience> educationalExps;
		private final List<WorkingExperience> workingExps;
		
		public Curriculum(boolean certifiable, Map<String,String> info, List<EducationalExperience> ee, List<WorkingExperience> we) {
			this.certifiable = certifiable;
			personalInfo = info;
			educationalExps = ee;
			workingExps = we;
		}
		public boolean isCertifiable() {
			return certifiable;
		}
		public Map<String,String> getPersonalInfo() {
			return personalInfo;
		}
		public List<EducationalExperience> getEducationalExps() {
			return educationalExps;
		}
		public List<WorkingExperience> getWorkingExps() {
			return workingExps;
		}
		public void setCertifiable(boolean certifiable) {
			this.certifiable = certifiable;
		}
	}
	
	private Map<String,Curriculum> cvs;
	
	public void setCvs(Map<String,Curriculum> cvs) {
		this.cvs = cvs;
	}
	
	public Map<String, Curriculum> getCvs() {
		return cvs;
	}
	
	public List<Map<String,String>> getFilteredCVs(UserModel userModel, Map<String,String> filter) {
		setCvs(DataAccess.getInstance().getFilteredCVs(userModel, filter));
		List<Map<String,String>> compactCVs = new ArrayList<>();
		cvs.forEach((id,cv) -> {
			Map<String,String> ccv = new HashMap<>();
			ccv.put("cvID", id);
			ccv.put("firstname", cv.getPersonalInfo().get("firstname"));
			ccv.put("lastname", cv.getPersonalInfo().get("lastname"));
			ccv.put("address", cv.getPersonalInfo().get("address"));
			ccv.put("certifiable", String.valueOf(cv.isCertifiable()));
			compactCVs.add(ccv);
		});
		return compactCVs;
	}
	
	public Curriculum getCV(String id) {
		return cvs.get(id);
	}
	
	public boolean certifyCV(String cvID, String comment) {
		boolean certified = DataAccess.getInstance().certifyCV(cvID, comment);
		if(certified)
			getCV(cvID).setCertifiable(false);
		return certified;
	}
}
