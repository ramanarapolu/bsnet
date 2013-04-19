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

import net.vz.mongodb.jackson.DBQuery

import com.jda.bsnet.BsnetUtils
import com.jda.bsnet.RoleDef
import com.jda.bsnet.model.MenuMetaData
import com.jda.bsnet.model.Organization
import com.jda.bsnet.model.User
import com.jda.bsnet.uitransfer.LoginResponse
import com.jda.bsnet.uitransfer.MenuUrlPair
import com.jda.bsnet.uitransfer.UserDetails
import com.mongodb.MongoException

@Path("/login")
class LoginResource {




	@GET
	@Path("hello")
	@Produces(TEXT_PLAIN)
	//Example URL:  http://api.jda.com/reco/v1/users/hello
	String getHello(){
		return "Hello"
	}

	@POST
	@Path("logon")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	LoginResponse logOn(@Context HttpServletRequest req ,UserDetails userDetails) {
		LoginResponse lResp = new LoginResponse()
		if (userDetails != null) {
			try {
				User user = BsnetDatabase.getInstance().getJacksonDBCollection(User.class).findOne(DBQuery.is("username",userDetails.username))
				if(user != null) {
					//TODO Hashing of the password
					if(user.password.equals(BsnetUtils.encrypt(userDetails.password))) {
						String role = determineRole(user)
						if(!role.equals(RoleDef.JDA_ADMIN)) {
							boolean orgApproved = isOrgApproved(user)
							//println "orgapproved {1}",orgApproved
							if(!orgApproved) {
								lResp.loginSuccess = false
								lResp.errorString = " Not allowed to login!! Your organization is not yet approved "
								return lResp
							}

						}
						if(role != null) {
							List<MenuUrlPair> menuPairs = getMenusForRole(role)
							lResp.menuList = menuPairs
							lResp.loginSuccess = true

							HttpSession session = req.getSession(true);
							session.setAttribute("orgName", user.orgName)
						}

					}else {
						lResp.loginSuccess = false
						lResp.errorString = "Password is wrong"
					}
				} else {
					lResp.loginSuccess = false
					lResp.errorString = "User Unavailable"
				}

			}catch(MongoException e){
				throw new InternalServerErrorException(e)
			}
		}
		return lResp
	}

	boolean isOrgApproved(User user) {
		boolean result = false
		try{
			Organization org = BsnetDatabase.getInstance().getJacksonDBCollection(Organization.class).findOne(DBQuery.is("orgName", user.orgName))
			if(org.approved) {
				result = true
			}
			return result
		}catch(MongoException e){
			throw new InternalServerErrorException(e)
		}
	}
	String determineRole(User user) {
		String result = null
		try{
			if(user.orgName == null) {
				result = RoleDef.JDA_ADMIN;
			}else {
				Organization org = BsnetDatabase.getInstance().getJacksonDBCollection(Organization.class).findOne(DBQuery.is("orgName", user.orgName))
				if(user.orgAdmin && org.supplier) result = RoleDef.SUPPLIER_ADMIN
				if(!user.orgAdmin && org.supplier) result = RoleDef.SUPPLIER_USER
				if(user.orgAdmin && org.buyer) result = RoleDef.BUYER_ADMIN
				if(!user.orgAdmin && org.buyer) result = RoleDef.BUYER_USER
				if(user.orgAdmin && org.buyer && org.supplier) result = RoleDef.BS_ADMIN
				if(!user.orgAdmin && org.buyer && org.supplier) result = RoleDef.BS_USER
			}
		}catch(MongoException e){
			throw new InternalServerErrorException(e)
		}
		return result
	}

	List<MenuUrlPair> getMenusForRole(String role) {
		List<MenuUrlPair> result = new ArrayList()
		MenuUrlPair mPair = null;
		try{
			BsnetDatabase.getInstance().getJacksonDBCollection(MenuMetaData.class).find().each { MenuMetaData mData ->
				if(mData.getRoleList().contains(role)) {
					mPair = new MenuUrlPair()
					mPair.displayName = mData.menuName
					mPair.menuId = mData.menuId
					mPair.url = mData.menuUrl
					result.add(mPair)
				}
			}

		}catch(MongoException e){
			throw new InternalServerErrorException(e)
		}
		return result
	}

}
