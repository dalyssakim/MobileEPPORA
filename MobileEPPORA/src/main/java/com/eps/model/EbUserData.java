package com.eps.model;

import java.util.ArrayList;

public class EbUserData {
	
	private int nmUserId;
	private String firstName;
	private String lastName;
	private int nmPriviledge;
	private String stPriviledge;
	private ArrayList<String> homeList;
	private double nmHoursPerDay = -1;
	
	public String getPriviledgeTypes(int nmPriviledge) {
		StringBuilder abReturn = new StringBuilder(255);

		if ((nmPriviledge & 0x400) != 0)
			abReturn.append(",Ad");
		if ((nmPriviledge & 0x80) != 0)
			abReturn.append(",Ba");
		if ((nmPriviledge & 0x200) != 0)
			abReturn.append(",Ex");
		if ((nmPriviledge & 0x800) != 0)
			abReturn.append(",Su");
		if ((nmPriviledge & 0x40) != 0)
			abReturn.append(",Pm");
		if ((nmPriviledge & 0x20) != 0)
			abReturn.append(",Ppm");
		if ((nmPriviledge & 0x1) != 0)
			abReturn.append(",Ptm");
		String stReturn = abReturn.toString();
		if (stReturn != null && stReturn.length() > 0)
			stReturn = stReturn.substring(1);
		return stReturn;
	}
	
	public int getNmPriviledge() {
		return nmPriviledge;
	}
	public void setNmPriviledge(int nmPriviledge) {
		this.nmPriviledge = nmPriviledge;
		stPriviledge = getPriviledgeTypes(this.nmPriviledge);
	}
	public int getNmUserId() {
		return nmUserId;
	}
	public void setNmUserId(int nmUserId) {
		this.nmUserId = nmUserId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getStPriviledge() {
		return stPriviledge;
	}
	public void setStPriviledge(String stPreviledge) {
		this.stPriviledge = stPreviledge;
	}

	public ArrayList<String> getHomeList() {
		return homeList;
	}

	public void setHomeList(ArrayList<String> homeList) {
		this.homeList = homeList;
	}

	public double getNmHoursPerDay() {
		return nmHoursPerDay;
	}

	public void setNmHoursPerDay(double nmHoursPerDay) {
		this.nmHoursPerDay = nmHoursPerDay;
	}
	
	
	
}
