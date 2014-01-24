package com.jda.product;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/product")
public class ProductResource {

	@GET
	@Path("hello")
	@Produces(MediaType.APPLICATION_JSON)
	public String getHello() {
		return "product hello";
	}
	
}
