package com.jda.bsnet.rest;

import static javax.ws.rs.core.MediaType.*

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.InternalServerErrorException
import javax.ws.rs.NotAcceptableException
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces

import net.vz.mongodb.jackson.WriteResult

import com.jda.bsnet.model.Organization
import com.jda.bsnet.model.User
import com.jda.bsnet.uitransfer.UserAndOrg
import com.jda.bsnet.uitransfer.UserDetails;
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
		throw new NotAcceptableException(new RuntimeException("Department data not found"))
	}
}
