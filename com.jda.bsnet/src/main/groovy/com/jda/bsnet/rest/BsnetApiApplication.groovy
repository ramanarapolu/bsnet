package com.jda.bsnet.rest;

import javax.ws.rs.ApplicationPath
import javax.ws.rs.core.Application

import org.glassfish.jersey.filter.LoggingFilter
import org.glassfish.jersey.jackson.JacksonFeature

import com.sun.jersey.core.impl.provider.entity.MimeMultipartProvider
import com.yammer.metrics.jersey.InstrumentedResourceMethodDispatchAdapter


@ApplicationPath("bsnet")
class BsnetApiApplication extends Application {


	@Override
	public Set<Class<?>> getClasses() {
		final Set<Class<?>> classes = new HashSet<Class<?>>();

		//classes.add(MultiPartReaderServerSide.class)
		//classes.add(MimeMultipartProvider.class);

		//classes.add(MultiPartWriter.class)
		//classes.add(MultiPartFeature.class);
		classes.add(LoginResource.class);
		classes.add(BuyerItemResource.class);
		classes.add(SupplierItemResource.class);
		classes.add(ItemResource.class);
		classes.add(UserResource.class);
		classes.add(BsnetHello.class);
		//classes.add(com.sun.jersey.core.impl.provider.entity.MimeMultipartProvider.class)
		//classes.add(InstrumentedResourceMethodDispatchAdapter.class);
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
		//MetricsRegistry registry = new MetricsRegistry()//
		//instances.add(new com.sun.jersey.core.impl.provider.entity.MimeMultipartProvider())
		instances.add(new MimeMultipartProvider());
		instances.add(new InstrumentedResourceMethodDispatchAdapter());
		instances.add(new JacksonFeature());
		instances.add(new LoggingFilter());
		return instances;
	}
}
