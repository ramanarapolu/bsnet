package com.jda.bsnet.model


public class Organization {
	String _id
	String orgName
	boolean buyer
	boolean supplier
	Address address
	boolean approved

	boolean isBuyer() {
		return buyer
	}
	boolean isSupplier() {
		return supplier
	}
	boolean isApproved() {
		return approved
	}
}

