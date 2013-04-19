package com.jda.bsnet.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.jda.bsnet.rest.BsnetDatabase
import com.mongodb.BasicDBObject

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

