package com.jda.bsnet.model

import net.vz.mongodb.jackson.Id

public class Organization {
	String _id
	String orgName
	boolean buyer
	boolean supplier
	Address address
	boolean approved
}
