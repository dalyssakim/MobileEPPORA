package com.eps.model;

public class EbWorkFlowOfPTM {

	private String prjId;
	private String baseline;
	private String schId;
	private String projectName;
	private int taskId;
	private String projectTeamMember;
	private String startDate;
	private String estimatedHours;
	private String expendedHoursToDate;
	private String expendedHoursToday;
	private String message;
	private String status;
	private String progress;
	private String description;
	private EbProgress ebPg;
	private String pgDescription;
	private String pgPlannedQuantity;
	private String pgIncrementalProgress;
	private String schTitle;
	
	public String getPrjId() {
		return prjId;
	}
	public void setPrjId(String prjId) {
		this.prjId = prjId;
	}
	public String getBaseline() {
		return baseline;
	}
	public void setBaseline(String baseline) {
		this.baseline = baseline;
	}
	public EbProgress getEbPg() {
		return ebPg;
	}
	public void setEbPg(EbProgress ebPg) {
		this.ebPg = ebPg;
	}
	public String getPgDescription() {
		return pgDescription;
	}
	public void setPgDescription(String pgDescription) {
		this.pgDescription = pgDescription;
	}
	public String getPgPlannedQuantity() {
		return pgPlannedQuantity;
	}
	public void setPgPlannedQuantity(String pgPlannedQuantity) {
		this.pgPlannedQuantity = pgPlannedQuantity;
	}
	public String getPgIncrementalProgress() {
		return pgIncrementalProgress;
	}
	public void setPgIncrementalProgress(String pgIncrementalProgress) {
		this.pgIncrementalProgress = pgIncrementalProgress;
	}
	public String getPgAccomplishedToDate() {
		return pgAccomplishedToDate;
	}
	public void setPgAccomplishedToDate(String pgAccomplishedToDate) {
		this.pgAccomplishedToDate = pgAccomplishedToDate;
	}
	private String pgAccomplishedToDate;
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public String getProjectTeamMember() {
		return projectTeamMember;
	}
	public void setProjectTeamMember(String projectTeamMember) {
		this.projectTeamMember = projectTeamMember;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEstimatedHours() {
		return estimatedHours;
	}
	public void setEstimatedHours(String estimatedHours) {
		this.estimatedHours = estimatedHours;
	}
	public String getExpendedHoursToDate() {
		return expendedHoursToDate;
	}
	public void setExpendedHoursToDate(String expendedHoursToDate) {
		this.expendedHoursToDate = expendedHoursToDate;
	}
	public String getExpendedHoursToday() {
		return expendedHoursToday;
	}
	public void setExpendedHoursToday(String expendedHoursToday) {
		this.expendedHoursToday = expendedHoursToday;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getProgress() {
		return progress;
	}
	public void setProgress(String progress) {
		this.progress = progress;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSchId() {
		return schId;
	}
	public void setSchId(String schId) {
		this.schId = schId;
	}
	public String getSchTitle() {
		return schTitle;
	}
	public void setSchTitle(String schTitle) {
		this.schTitle = schTitle;
	}
	
	
}
