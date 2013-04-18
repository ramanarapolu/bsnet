package com.jda.bsnet.rest;

import static javax.ws.rs.core.MediaType.*

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.InternalServerErrorException
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response

import net.vz.mongodb.jackson.DBCursor
import net.vz.mongodb.jackson.DBQuery
import net.vz.mongodb.jackson.WriteResult

import com.jda.bsnet.model.Organization
import com.jda.bsnet.model.User
import com.jda.bsnet.uitransfer.UserAndOrg
import com.mongodb.BasicDBObject;
import com.mongodb.MongoException

@Path("/user")
class UserResource {

	@GET
	@Path("hello")
	@Produces(TEXT_PLAIN)
	//Example URL:  http://api.jda.com/reco/v1/users/hello
	String getHello(){
		return "Hello"
	}
	@POST
	@Path("create")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	UserAndOrg createOrgAndUser(UserAndOrg userOrgDetails) {

		Organization org = null
		User user = null
		if (userOrgDetails != null)
		{
			try
			{
				// check whether given org & username are unique accross sys first.

				org = BsnetDatabase.getInstance().getJacksonDBCollection(Organization.class).findOne(DBQuery.is("orgName", userOrgDetails.org.orgName))
				if(org != null) {
					userOrgDetails.success = false
					userOrgDetails.failedReason = " Organization already exists !!!"
					return userOrgDetails
				}

				user = BsnetDatabase.getInstance().getJacksonDBCollection(User.class).findOne(DBQuery.is("username", userOrgDetails.username))

				if(user != null) {
					userOrgDetails.success = false
					userOrgDetails.failedReason = " User already exists !!!"
					return userOrgDetails
				}

				// Saving Organization
				org = userOrgDetails.org
				org.approved = false

				WriteResult<Organization, String> result = BsnetDatabase.getInstance().getJacksonDBCollection(Organization.class).insert(org)
				//return result.getSavedObject();
				user = new User()
				user.emailId = userOrgDetails.emailId
				user.username = userOrgDetails.username
				user.password = userOrgDetails.password
				user.orgAdmin = true
				user.orgName = org.orgName
				user.mobileNo = userOrgDetails.mobileNo

				result = BsnetDatabase.getInstance().getJacksonDBCollection(User.class).insert(user)
				userOrgDetails.success = true
				return userOrgDetails
			}
			catch(MongoException e)
			{
				throw new InternalServerErrorException(e)
			}
		}
	}
	@POST
	@Path("createUser")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	Response createUser(@Context HttpServletRequest req,User userDetails) {
		if (userDetails != null)
		{
			try
			{
				// Get the organization name from the session
				HttpSession session = req.getSession()
				String orgName = session.getAttribute("orgName")
				//return result.getSavedObject();
				User user = new User()
				user.emailId = userDetails.emailId
				user.username = userDetails.username
				user.password = userDetails.password
				user.orgName = orgName
				user.mobileNo = userDetails.mobileNo

				BsnetDatabase.getInstance().getJacksonDBCollection(User.class).insert(user)
			}
			catch(MongoException e)
			{
				throw new InternalServerErrorException(e)
			}
		}
		return Response.ok().build()
	}


	@GET
	@Path("getPendingOrgs")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)

	List<Organization> getPendingOrgs(){
		List<Organization> orgs = null
		Organization org = null
		try {
			DBCursor<Organization> orgCur = BsnetDatabase.getInstance().getJacksonDBCollection(Organization.class).find()//(DBQuery.is("approved",false))
			if(orgCur != null) {
				orgs = new ArrayList()
				while(orgCur.hasNext()){
					org = (Organization) orgCur.next();
					orgs.add(org)
				}
			}
		}catch(MongoException e){
			throw new InternalServerErrorException(e)
		}
		return orgs
	}
	@POST
	@Path("approveOrgs")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	Response updateOrgs(List<Organization> orgs) {
		for(Organization org : orgs) {
			try {
				BasicDBObject source = new BasicDBObject("orgName",org.orgName);
				BasicDBObject newDocument = new BasicDBObject();
				newDocument.append('$set', new BasicDBObject().append("approved", true));
				BsnetDatabase.getInstance().getJacksonDBCollection(Organization.class).update(source,newDocument)
				// Send mail to orgAdmin saying you org approved ???
			}catch(MongoException e){
				throw new InternalServerErrorException(e)
			}
		}
		return Response.ok().build()
	}

	@POST
	@Path("getUserByOrg")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	List<User> getUserByOrgs(@Context HttpServletRequest req) {

		HttpSession session = req.getSession()
		String orgName = session.getAttribute("orgName")
		List<User> users = null
		User user = null
		DBCursor<User> userCur = BsnetDatabase.getInstance().getJacksonDBCollection(User.class).find(DBQuery.is("orgName",orgName))
		if(userCur != null) {
			users = new ArrayList()
			while(userCur.hasNext()){
				user = (User) userCur.next()
				if(!user.orgAdmin)
					users.add(user)
			}
		}
		return users
	}



}
