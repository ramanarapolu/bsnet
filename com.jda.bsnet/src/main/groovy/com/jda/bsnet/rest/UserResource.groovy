package com.jda.bsnet.rest;

import static javax.ws.rs.core.MediaType.*

import java.util.concurrent.TimeUnit

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
import com.jda.bsnet.uitransfer.JtableJson
import com.jda.bsnet.uitransfer.JtableResponse
import com.jda.bsnet.uitransfer.UserAndOrg
import com.jda.bsnet.util.BsnetUtils
import com.mongodb.BasicDBObject
import com.mongodb.MongoException
import com.yammer.metrics.annotation.Timed

@Path("/user")
class UserResource {

	@GET
	@Path("hello")
	@Timed(name = "getHello", rateUnit = TimeUnit.SECONDS, durationUnit = TimeUnit.MICROSECONDS)

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
				user.password = BsnetUtils.encrypt(userOrgDetails.password)
				user.orgAdmin = true
				user.orgName = org.orgName
				user.mobileNo = userOrgDetails.mobileNo

				result = BsnetDatabase.getInstance().getJacksonDBCollection(User.class).insert(user)
				userOrgDetails.success = true

				//send mail to admin that Org created need to be approved.

				Properties bsNetProp = BsnetDatabase.getInstance().getBsnetServerConfig()
				String body=bsNetProp.get("email.orgcrea.body").toString();
				body=body.replace("ORGNAME", org.orgName)
				BsnetUtils.sendMail("ramana.rapolu@jda.com", bsNetProp.get("email.orgcrea.subject").toString(), body.toString())
				return userOrgDetails


			}
			catch(MongoException e)
			{
				e.printStackTrace()
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
				user.password = BsnetUtils.encrypt(userDetails.password)
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


	@POST
	@Path("getPendingOrgs")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)

	JtableJson getPendingOrgs(){
		List<Organization> orgs = null
		Organization org = null
		try {
			//DBCursor<Organization> orgCur = BsnetDatabase.getInstance().getJacksonDBCollection(Organization.class).find(DBQuery.is("approved",false))
			DBCursor<Organization> orgCur = BsnetDatabase.getInstance().getJacksonDBCollection(Organization.class).find()
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
		return  new JtableJson("OK", orgs)
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

				String orgAdminId = BsnetUtils.getAdminMailId(org.orgName)
				// Send mail to orgAdmin saying you org approved ???

				Properties bsNetProp = BsnetDatabase.getInstance().getBsnetServerConfig()
				String body=bsNetProp.get("email.orgapp.body").toString();
				body=body.replace("USER", org.orgName)
				BsnetUtils.sendMail(orgAdminId, bsNetProp.get("email.orgapp.subject").toString(), body.toString())

			}catch(MongoException e){
				throw new InternalServerErrorException(e)
			}
		}
		return Response.ok().build()
	}

	@POST
	@Path("approveOrgs1")
	@Produces(APPLICATION_JSON)
	JtableResponse updateOrgs1(@Context HttpServletRequest req) {
		println "enterd updateOrgs1" + req.getParameter("orgName")
		try {
			BasicDBObject source = new BasicDBObject("orgName",req.getParameter("orgName"));
			BasicDBObject newDocument = new BasicDBObject();
			newDocument.append('$set', new BasicDBObject().append("approved", true));
			BsnetDatabase.getInstance().getJacksonDBCollection(Organization.class).update(source,newDocument)
		}catch(MongoException e){

			throw new InternalServerErrorException(e)
		}

		return  new JtableResponse("OK")
	}


	@GET
	@Path("getUserByOrg")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	List<User> getUserByOrgs(@Context HttpServletRequest req) {

		HttpSession session = req.getSession()
		String orgName = session.getAttribute("orgName")
		System.out.println("organization name :"+orgName);
		List<User> users = null
		User user = null
		DBCursor<User> userCur = BsnetDatabase.getInstance().getJacksonDBCollection(User.class).find(DBQuery.is("orgName",orgName))
		if(userCur != null) {
			users = new ArrayList()
			while(userCur.hasNext()){
				user = (User) userCur.next()
				if(!user.orgAdmin) {
					users.add(user)
				}
			}
		}
		return users
	}



}
