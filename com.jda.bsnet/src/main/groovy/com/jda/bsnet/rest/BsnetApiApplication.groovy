package com.jda.bsnet.rest;

import javax.annotation.PostConstruct
import javax.servlet.ServletConfig
import javax.servlet.ServletContext
import javax.ws.rs.ApplicationPath
import javax.ws.rs.core.Application
import javax.ws.rs.core.Context

import org.glassfish.jersey.filter.LoggingFilter
import org.glassfish.jersey.jackson.JacksonFeature
import org.glassfish.jersey.media.multipart.MultiPartFeature

@ApplicationPath("bsnet")
class BsnetApiApplication extends Application {


	@Override
	public Set<Class<?>> getClasses() {
		final Set<Class<?>> classes = new HashSet<Class<?>>();

		classes.add(MultiPartFeature.class);
		classes.add(LoginResource.class);
		classes.add(BuyerItemResource.class);
		classes.add(SupplierItemResource.class);
		classes.add(ItemResource.class);
		classes.add(UserResource.class);
		classes.add(BsnetHello.class);
		return classes;
	}

	/*@Context
	public void setServletContext(ServletConfig context) {
		System.out.println("servlet context set here" + context.getServletName());
		System.out.println("init parameter value :"+context.getInitParameter("appconfig"));
	}

	@PostConstruct
	public void readInitParams() {
		//System.out.println(sc.getInitParameter("appconfig"));
	}*/

	@Override
	public Set<Object> getSingletons() {
		final Set<Object> instances = new HashSet<Object>();
		instances.add(new JacksonFeature());
		instances.add(new LoggingFilter());
		return instances;
	}
}
