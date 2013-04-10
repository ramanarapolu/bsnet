package com.jda.bsnet.rest;
import net.vz.mongodb.jackson.JacksonDBCollection

import com.jda.bsnet.JsonUtils
import com.jda.bsnet.server.config.BsnetServerConfig
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
}