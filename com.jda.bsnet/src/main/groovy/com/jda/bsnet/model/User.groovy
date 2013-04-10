package com.jda.bsnet.model

import net.vz.mongodb.jackson.Id;

public class User {
	@Id
	String username
	String password
	String emailId
	String mobilNo
	String orgName
	boolean orgAdmin
}
