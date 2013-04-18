package com.jda.bsnet.model

import com.fasterxml.jackson.annotation.JsonIgnore;
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

	@JsonIgnore
	String getAdminMailId() {
		BasicDBObject andQuery = new BasicDBObject();
		List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
		obj.add(new BasicDBObject("orgName", orgName));
		obj.add(new BasicDBObject("orgAdmin", true));
		andQuery.put('$and', obj);
		User user = BsnetDatabase.getInstance().getJacksonDBCollection(User.class).find(andQuery)
		return user.emailId
	}

}

