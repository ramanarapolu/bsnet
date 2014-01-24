package com.jda.util;

import net.vz.mongodb.jackson.JacksonDBCollection;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoConnection {

	private DB mongoDB;
	private static MongoConnection instance = new MongoConnection();

	public static MongoConnection getInstance() {
		return instance;
	}

	private MongoConnection() {
             try {
                     MongoClient mClient = new MongoClient("localhost",27017);
                     mongoDB = mClient.getDB("test");
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

	public DB getDB() {
		return mongoDB;
	}

}
