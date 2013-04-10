package com.jda.bsnet.model

import net.vz.mongodb.jackson.Id

public class Organization {
	@Id
	String orgName
	boolean buyer
	boolean supplier
	Address address
	boolean approved
}
