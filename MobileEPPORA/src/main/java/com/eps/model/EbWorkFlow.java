package com.eps.model;

public class EbWorkFlow {

	private String prjId;
	private String baseline;
	private String schId;
	private String projectName;
	private int taskId;
	private String startDate;
	private String expectedHoursToDate;
	private String estimatedHours;
	private String expendedHoursToday;
	private String status;
	private String description;
	private String message;
	private EbProgress ebPg;
	private String pgDescription;
	private String pgPlannedQuantity;
	private String pgIncrementalProgress;
	private String pgAccomplishedToDate;
	private String schTitle;
	
	
	public String getSchTitle() {
		return schTitle;
	}
	public void setSchTitle(String schTitle) {
		this.schTitle = schTitle;
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
	public String getSchId() {
		return schId;
	}
	public void setSchId(String schId) {
		this.schId = schId;
	}
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
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getExpectedHoursToDate() {
		return expectedHoursToDate;
	}
	public void setExpectedHoursToDate(String expectedHoursToDate) {
		this.expectedHoursToDate = expectedHoursToDate;
	}
	public String getEstimatedHours() {
		return estimatedHours;
	}
	public void setEstimatedHours(String estimatedHours) {
		this.estimatedHours = estimatedHours;
	}
	public String getExpendedHoursToday() {
		return expendedHoursToday;
	}
	public void setExpendedHoursToday(String expendedHoursToday) {
		this.expendedHoursToday = expendedHoursToday;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public EbProgress getEbPg() {
		return ebPg;
	}
	public void setEbPg(EbProgress ebPg) {
		this.ebPg = ebPg;
	}
}
