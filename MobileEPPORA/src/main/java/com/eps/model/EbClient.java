package com.eps.model;

public class EbClient {

	public Integer recId;
	private String stEmail;
	private String stPwd;
	private String stAuth;
	private Integer nmPriviledge;
	private int nmApproveUserID;
	private String stProjects;
	private String stSchedules;
	private boolean session = false;
	private long nmLastLoginTime = 0;
	private long nmSuccessLoginTime = 0;
	public Integer getRecId() {
		return recId;
	}
	public void setRecId(Integer recId) {
		this.recId = recId;
	}
	public String getStEmail() {
		return stEmail;
	}
	public void setStEmail(String stEmail) {
		this.stEmail = stEmail;
	}
	public String getStAuth() {
		return stAuth;
	}
	public void setStAuth(String stAuth) {
		this.stAuth = stAuth;
	}
	public Integer getNmPriviledge() {
		return nmPriviledge;
	}
	public void setNmPriviledge(Integer nmPriviledge) {
		this.nmPriviledge = nmPriviledge;
	}
	public int getNmApproveUserID() {
		return nmApproveUserID;
	}
	public void setNmApproveUserID(int nmApprovedUserID) {
		this.nmApproveUserID = nmApprovedUserID;
	}
	public String getStProjects() {
		return stProjects;
	}
	public void setStProjects(String stProjects) {
		this.stProjects = stProjects;
	}
	public String getStSchedules() {
		return stSchedules;
	}
	public void setStSchedules(String stSchedules) {
		this.stSchedules = stSchedules;
	}
	public boolean isSession() {
		return session;
	}
	public void setSession(boolean session) {
		this.session = session;
	}
	public String getStPwd() {
		return stPwd;
	}
	public void setStPwd(String stPwd) {
		this.stPwd = stPwd;
	}
	public long getNmLastLoginTime() {
		return nmLastLoginTime;
	}
	public void setNmLastLoginTime(long nmLastLoginTime) {
		this.nmLastLoginTime = nmLastLoginTime;
	}
	public long getNmSuccessLoginTime() {
		return nmSuccessLoginTime;
	}
	public void setNmSuccessLoginTime(long nmSuccessLoginTime) {
		this.nmSuccessLoginTime = nmSuccessLoginTime;
	}
}
