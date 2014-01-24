package com.jda.heirarchy;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.JacksonDBCollection;

import com.jda.model.User;
import com.jda.util.MongoConnection;
import com.mongodb.BasicDBObject;

@Path("/heirarchy")
public class HeirarchyResource {

    @GET
    @Path("hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String getHello(){
            return "Heirarchy Hello";
    }
	
    @POST
    @Path("create")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public String createUser(User user) {
    	
    	MongoConnection.getInstance().getJacksonDBCollection(User.class).insert(user);
		return "Done";
    
    }
    
    @GET
    @Path("get/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser(@PathParam("name") String name) {
    	
    	System.out.println("Tring to get details for  "+name);
    	BasicDBObject query = new BasicDBObject("name", name);
    	JacksonDBCollection<User, String> coll = MongoConnection.getInstance().getJacksonDBCollection(User.class);
    	DBCursor<User> cursor = coll.find(query);
    	User user = null;
    	
    	System.out.println("COUNT :"+cursor.count());
    	try {
    	   while(cursor.hasNext()) {
    	       user =  cursor.next();
    	   }
    	} finally {
    	   cursor.close();
    	}
    	return user;
    }
    
    
    
}
