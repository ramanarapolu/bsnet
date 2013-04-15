package com.jda.bsnet.rest;

import static javax.ws.rs.core.MediaType.*

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.InternalServerErrorException
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces

import net.vz.mongodb.jackson.DBQuery
import net.vz.mongodb.jackson.WriteResult

import com.jda.bsnet.BsnetUtils;
import com.jda.bsnet.model.Organization
import com.jda.bsnet.model.User
import com.jda.bsnet.uitransfer.UserAndOrg
import com.mongodb.DBCursor
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

		if (userOrgDetails != null)
		{
			try
			{
				// Saving Organization
				Organization org = userOrgDetails.org
				org.approved = false

				WriteResult<Organization, String> result = BsnetDatabase.getInstance().getJacksonDBCollection(Organization.class).insert(org)
				println "organization successfully created ..."
				//return result.getSavedObject();
				User user = new User()
				user.emailId = userOrgDetails.emailId
				user.username = userOrgDetails.username
				user.password = BsnetUtils.encrypt(userOrgDetails.password)
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

	@GET
	@Path("getPendingOrgs")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)

	List<Organization> getPendingOrgs(){
		List<Organization> orgs = null
		Organization org = null
		try {
			DBCursor<Organization> orgCur = BsnetDatabase.getInstance().getJacksonDBCollection(Organization.class).findOne(DBQuery.is("approved",false))
			if(orgCur != null) {
				//TODO Hashing of the password
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
	void updateOrgs(List<Organization> orgs) {
		orgs.each { Organization org ->
			try {
				BsnetDatabase.getInstance().getJacksonDBCollection(Organization.class).save(org)
			}catch(MongoException e){
				throw new InternalServerErrorException(e)
			}
		}
	}
}
