package com.jda.bsnet.rest;

import groovy.util.logging.Slf4j;

import javax.ws.rs.GET;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.uri.internal.JerseyUriBuilder;

import com.yammer.metrics.annotation.Timed;
// Plain old Java Object it does not extend as class or implements
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation.
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML.

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello

@Path("/hello")
@Slf4j
public class BsnetHello {

	// This method is called if TEXT_PLAIN is request

	@GET
	@Timed
	@Path("plain")
	@Produces(MediaType.TEXT_PLAIN)
	public String sayPlainTextHello() {
		return "Hello Jersey";
	}

	// This method is called if XML is request
	@GET
	@Timed
	@Path("xml")
	@Produces(MediaType.TEXT_XML)
	public String sayXMLHello() {
		println "saying XML hello..."
		log.info "entered XML say hello.."
		return "<?xml version=\"1.0\"?>" + "<hello> Hello Jersey" + "</hello>";
	}

	// This method is called if HTML is request
	@GET
	@Timed
	@Path("html")
	@Produces(MediaType.TEXT_HTML)
	public String sayHtmlHello() {
		return "<html> " + "<title>" + "Hello Jersey" + "</title>"
				+ "<body><h1>" + "Hello Jersey" + "</body></h1>" + "</html> ";
	}

}