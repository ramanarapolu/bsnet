package com.jda.bsnet;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;



@ApplicationPath("bsnet")
class BsnetApiApplication extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		final Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(BsnetHello.class);
		//classes.add(InstrumentedResourceMethodDispatchAdapter.class);
		return classes;
	}

	@Override
	public Set<Object> getSingletons() {
		final Set<Object> instances = new HashSet<Object>();
		instances.add(new JacksonFeature());
		instances.add(new LoggingFilter());
		return instances;
	}
}
