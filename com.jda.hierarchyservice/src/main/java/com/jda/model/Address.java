package com.jda.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


public class Address {

	public Integer getHouseNo() {
		return houseNo;
	}
	public void setHouseNo(Integer houseNo) {
		this.houseNo = houseNo;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public int[] getPincode() {
		return pincode;
	}
	public void setPincode(int[] pincode) {
		this.pincode = pincode;
	}
	private Integer houseNo;
	private String city;
	private String street;
	private int[] pincode;
	private String _id;
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
}
