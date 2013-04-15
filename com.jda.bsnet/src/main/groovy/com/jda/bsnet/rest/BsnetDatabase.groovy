package com.jda.bsnet.rest;
import net.vz.mongodb.jackson.JacksonDBCollection

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.jda.bsnet.BsnetUtils
import com.jda.bsnet.model.Address
import com.jda.bsnet.model.Organization
import com.jda.bsnet.model.User
import com.jda.bsnet.uitransfer.UserAndOrg;
import com.jda.bsnet.uitransfer.UserDetails;
import com.mongodb.DB
import com.mongodb.Mongo
import com.mongodb.MongoURI


class BsnetDatabase {
	private DB mongoDB;
	private static BsnetDatabase instance = new BsnetDatabase();
	public static BsnetDatabase getInstance() {
		return instance;
	}

	private BsnetDatabase() {
		try {
		//	BsnetServerConfig bConfig = JsonUtils.readFromJsonFile("./src/main/resources/bsnetserver.json", BsnetServerConfig.class)
			MongoURI mongolabUri = new MongoURI("mongodb://localhost:27017/bsnet")
			Mongo m = new Mongo(mongolabUri);
			mongoDB = m.getDB(mongolabUri.getDatabase());
			if ((mongolabUri.getUsername() != null)
			&& (mongolabUri.getPassword() != null)) {
				mongoDB.authenticate(mongolabUri.getUsername(),
						mongolabUri.getPassword());
			}
		} catch (Exception e) {
			throw new RuntimeException(
			"exception while initializing BSNET database", e);
		}
	}

	public <T> JacksonDBCollection<T, String> getJacksonDBCollection(
			Class<T> clazz) {
		return JacksonDBCollection.wrap(
		mongoDB.getCollection(clazz.getSimpleName().toLowerCase()),
		clazz, String.class);
	}
	public static void main(String[] args) {
		System.out.println("runnig client");

		Address addr = new Address()
		addr.city = 'warangal'
		addr.country = 'india'
		addr.postalCode = '500085'
		addr.state = 'ap'
		addr.streetAddress = 'Lake View'


		Organization org = new Organization()
		org.orgName = "flipkart"
		org.address = addr
		org.approved = false
		org.buyer = true
		org.supplier = false

		UserAndOrg user = new UserAndOrg()
		user.username = "wglbuyer"
		user.password = BsnetUtils.encrypt("wglbuyer")
		user.emailId =  "ramana.raps@gmail.com"
		user.mobileNo = "9989861971"
		user.org = org

		ObjectMapper objectMapper = new ObjectMapper()
		objectMapper.registerModule(new JodaModule())
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true)
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
		StringWriter sw = new StringWriter()
		objectMapper.writeValue(sw, user)

		println "password 1" + BsnetUtils.encrypt("wglbuyer")
		println "password 2" + BsnetUtils.encrypt("wglbuyer")

		println sw.toString()
		/*.each { MenuMetaData mData ->

			println mData.menuName
			println mData.menuId
			println mData.menuUrl
			//printlnadd(mPair)
		}
		//BsnetDatabase.getInstance().getJacksonDBCollection(User.class).findOne(DBQuery.is("username","jdauser"))*/
	}
}