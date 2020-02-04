package com.blockcv.model.data.blockchain;

import java.io.Serializable;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

public class MinimalUser  implements User, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String affiliation;
	private String msp;
	private Enrollment enrollment;

	MinimalUser(String name, String affiliation, String msp, Enrollment enrollment) {
		this.name = name;
		this.affiliation = affiliation;
		this.msp = msp;
		this.enrollment = enrollment;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Set<String> getRoles() {
		return Collections.emptySet();
	}

	@Override
	public String getAccount() {
		return name;
	}

	@Override
	public String getAffiliation() {
		return affiliation;
	}

	@Override
	public Enrollment getEnrollment() {
		return enrollment;
	}

	@Override
	public String getMspId() {
		return msp;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MinimalUser minUser = (MinimalUser) o;
		return Objects.equals(name, minUser.name) &&
				Objects.equals(msp, minUser.msp) &&
				Objects.equals(enrollment, minUser.enrollment);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, msp, enrollment);
	}

	@Override
	public String toString() {
		return "MinimalUser{" +
				"name='" + name + '\'' +
				", msp='" + msp + '\'' +
				", enrollment=" + enrollment +
				'}';
	}
	
}
