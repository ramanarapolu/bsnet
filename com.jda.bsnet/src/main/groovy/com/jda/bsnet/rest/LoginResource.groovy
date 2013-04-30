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

import net.vz.mongodb.jackson.DBQuery

import org.jsoup.nodes.Document

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.jda.bsnet.model.MenuMetaData
import com.jda.bsnet.model.Organization
import com.jda.bsnet.model.User
import com.jda.bsnet.uitransfer.JtableJson
import com.jda.bsnet.uitransfer.LoginResponse
import com.jda.bsnet.uitransfer.MenuUrlPair
import com.jda.bsnet.uitransfer.ResourceMetricTransfer;
import com.jda.bsnet.uitransfer.UserDetails
import com.jda.bsnet.uitransfer.metrics.ResourceMethod
import com.jda.bsnet.uitransfer.metrics.ResourceMetric
import com.jda.bsnet.util.JsoupUtils
import com.jda.bsnet.util.RoleDef
import com.mongodb.MongoException
import com.yammer.metrics.annotation.Timed

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
	@Path("getResourceNames")
	@Produces(APPLICATION_JSON)
	JtableJson getResourceNames(){

		Properties p = BsnetDatabase.getInstance().getBsnetProp()
		String url = "http://"+p.getProperty("bsnet.server.ip")+":"+p.getProperty("bsnet.server.port")+"/bsnet/metrics/metrics?pretty=true"
		println ":"+url
		Document doc = JsoupUtils.getContent(url)

		ObjectMapper objectMapper = new ObjectMapper()
		objectMapper.registerModule(new JodaModule())
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true)
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
		Map<String,ResourceMetric> rm = objectMapper.readValue(doc.getElementsByTag("body").text(), Map.class)

		return new JtableJson("OK", rm.keySet().asList())
	}



	@POST
	@Path("getResourceStatsByName")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	//Example URL:  http://api.jda.com/reco/v1/users/hello
	JtableJson getResourceStatsByName(@Context HttpServletRequest req){

		Properties p = BsnetDatabase.getInstance().getBsnetProp()
		String url = "http://"+p.getProperty("bsnet.server.ip")+":"+p.getProperty("bsnet.server.port")+"/bsnet/metrics/metrics?pretty=true"
		String resourceName = req.getAttribute("resourceName")
		println ":"+resourceName
		Document doc = JsoupUtils.getContent(url)

		ObjectMapper objectMapper = new ObjectMapper()
		objectMapper.registerModule(new JodaModule())
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true)
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
		Map<String,ResourceMetric> rm = objectMapper.readValue(doc.getElementsByTag("body").text(), Map.class)

		ResourceMetric rmByName = rm.get(resourceName)

		Map<String,ResourceMethod> rMap = rmByName.resourceMap

		List<ResourceMetricTransfer> lTrans = new ArrayList<ResourceMetricTransfer>()
		ResourceMetricTransfer ltransEle = null
		for(e in rMap) {
			ltransEle = new ResourceMetricTransfer()
			ltransEle.methodName = e.key
			ltransEle.avgResTime = e.value.duration.mean
			ltransEle.count = e.value.rate.count
			ltransEle.oneMinRate = e.value.rate.m1
			ltransEle.fiveMinRate = e.value.rate.m5
			ltransEle.meanRate = e.value.rate.mean
			lTrans.add(ltransEle)
		}

		return new JtableJson("OK", lTrans)
	}


	@POST
	@Timed
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
					//if(user.password.equals(BsnetUtils.encrypt(userDetails.password))) {
					if(true) {
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
							session.setAttribute("userName", user.username)
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

	@POST
	@Timed
	@Path("logout")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)

	Response logOut(@Context HttpServletRequest req) {

		HttpSession httpSession = req.getSession();
		httpSession.removeAttribute("orgName");
		httpSession.invalidate();
		return Response.ok().build()
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
