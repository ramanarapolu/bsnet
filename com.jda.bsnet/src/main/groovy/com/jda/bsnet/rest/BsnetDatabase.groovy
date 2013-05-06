package com.jda.bsnet.rest;
import net.vz.mongodb.jackson.JacksonDBCollection

import com.jda.bsnet.server.config.BsnetServerConfig
import com.jda.bsnet.util.JsonUtils
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
			/*bConfig = JsonUtils.readFromJsonFile(jsonPath, BsnetServerConfig.class)
			MongoURI mongolabUri = new MongoURI(bConfig.host.dbUri)*/
			InputStream is = BsnetDatabase.class.getResourceAsStream("bsnetserver.properties")
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

	public BsnetServerConfig getBsnetServerJsonConfig(){
		return bConfig
	}

}