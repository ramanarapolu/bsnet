package com.jda.application;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("hub")
public class HubApplication extends ResourceConfig{
	public HubApplication() {
		register(new JacksonFeature());
		packages("com.jda.heirarchy;com.jda.product");
	}
}
