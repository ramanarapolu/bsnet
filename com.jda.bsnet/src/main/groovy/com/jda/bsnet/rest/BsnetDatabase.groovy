package com.jda.bsnet.rest;
import net.vz.mongodb.jackson.JacksonDBCollection

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.jda.bsnet.model.Item
import com.jda.bsnet.server.config.BsnetServerConfig
import com.mongodb.DB
import com.mongodb.Mongo
import com.mongodb.MongoURI


class BsnetDatabase {
	private DB mongoDB;
	private static BsnetDatabase instance = new BsnetDatabase();
	private BsnetServerConfig bConfig = null
	Properties bsnetProp = new Properties()
	public static BsnetDatabase getInstance() {
		return instance;
	}

	private BsnetDatabase() {
		try {
			//bConfig = JsonUtils.readFromJsonFile("bsnet/bsnetserver.json", BsnetServerConfig.class)
			//MongoURI mongolabUri = new MongoURI(bConfig.host.dbUri)
			InputStream is = BsnetDatabase.class.getResourceAsStream("/bsnetserver.properties")
			bsnetProp.load(is)
			MongoURI mongolabUri = new MongoURI(bsnetProp.get("dbUri"))
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

	public Properties getBsnetServerConfig(){
		return bsnetProp
	}
	public static void main(String[] args) {
		System.out.println("runnig client");

	/*	Address addr = new Address()
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
		user.password = "wglbuyer"
		user.emailId =  "ramana.raps@gmail.com"
		user.mobileNo = "9989861971"
		user.org = org*/

		Item item = new Item()

		item.category = "shoe"
		item.description = "Noke Shoe"
		item.itemName = "Nike shoe"
		item.price = 250.56
		item.imageUrl = "http://argo.com/nike.img"

		ObjectMapper objectMapper = new ObjectMapper()
		objectMapper.registerModule(new JodaModule())
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true)
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
		StringWriter sw = new StringWriter()
		objectMapper.writeValue(sw, item)
		println Long.parseLong("102.34")

		//println "password 1" + BsnetUtils.encrypt("wglbuyer")
		//	println "password 2" + BsnetUtils.encrypt("wglbuyer")


		/*.each { MenuMetaData mData ->
		 println mData.menuName
		 println mData.menuId
		 println mData.menuUrl
		 //printlnadd(mPair)
		 }
		 //BsnetDatabase.getInstance().getJacksonDBCollection(User.class).findOne(DBQuery.is("username","jdauser"))*/
		/*User user = new User()
		user.username = "jdauser"
		user.password = BsnetUtils.encrypt("jdauser")
		user.emailId = "ramana.raps@gmail.com"
		user.mobileNo = "9989861970"
		BsnetDatabase.getInstance().getJacksonDBCollection(User.class).insert(user)*/
	}
}