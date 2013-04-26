package com.jda.bsnet.rest

import static javax.ws.rs.core.MediaType.*

import javax.servlet.http.HttpServletRequest
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

import com.jda.bsnet.model.BsRelation
import com.jda.bsnet.model.User
import com.jda.bsnet.model.Organization
import com.jda.bsnet.model.SupplierItem
import com.jda.bsnet.uitransfer.JtableAddResponse;
import com.jda.bsnet.uitransfer.JtableJson;
import com.jda.bsnet.uitransfer.JtableResponse;
import com.mongodb.MongoException
import net.vz.mongodb.jackson.WriteResult
import com.mongodb.BasicDBObject
import org.bson.types.ObjectId


@Path("/buyerItem")

class BuyerItemResource {
	
	
	@POST
	@Path("createBuyerUser")
	@Produces(APPLICATION_JSON)
	JtableAddResponse createBuyerUser(@Context HttpServletRequest req) {
		User user = new User();
		
		user.username = req.getParameter("username")
		user.password = req.getParameter("password")
		user.emailId = req.getParameter("emailId")
		user.mobileNo = req.getParameter("mobileNo")
		user.orgName = req.getSession().getAttribute("orgName")
		user.orgAdmin = false
		
		
		if (user != null)
		{
			try {
				WriteResult<User, String> result = BsnetDatabase.getInstance().getJacksonDBCollection(User.class).insert(user)

				DBCursor<User> userCur = BsnetDatabase.getInstance().getJacksonDBCollection(User.class).find(DBQuery.is("username",user.username))
				user = (User) userCur.next();
				//return  new JtableResponse("OK")

				return  new JtableAddResponse("OK", user)
			}catch(MongoException e){
				e.printStackTrace()
				throw new InternalServerErrorException(e)
			}
			catch(MongoException e)
			{
				throw new InternalServerErrorException(e)
			}
		}
	}

	@POST
	@Path("buyerUserlistAll")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)

	JtableJson buyerUserlistAll(@Context HttpServletRequest req){
		List<User> users = null
		User user = null
		String orgName =  req.getSession().getAttribute("orgName")
		int jtStartIndex=0
		int jtPageSize=10
		int TotalRecordCount=10

		if(req.getParameter("jtStartIndex") != null && req.getParameter("jtStartIndex") != "")
		jtStartIndex =  Integer.parseInt (req.getParameter("jtStartIndex"))

		if(req.getParameter("jtPageSize") != null && req.getParameter("jtPageSize") != "")
		 jtPageSize = Integer.parseInt (req.getParameter("jtPageSize"))


		println jtStartIndex+"and"+jtPageSize
		try {
			//DBCursor<Organization> orgCur = BsnetDatabase.getInstance().getJacksonDBCollection(Organization.class).find(DBQuery.is("approved",false))			 
			DBCursor<User> userCur = BsnetDatabase.getInstance().getJacksonDBCollection(User.class).find(DBQuery.is("orgName",orgName)).skip(jtStartIndex).limit(jtPageSize)
			if(userCur != null) {
				users = new ArrayList()
				while(userCur.hasNext()){
					user = (User) userCur.next();
					users.add(user)
				}
			}
		}catch(MongoException e){
			throw new InternalServerErrorException(e)
		}

	 return  new JtableJson("OK", users,users.size())
	}

	@POST
	@Path("buyerUserUpdate")
	@Produces(APPLICATION_JSON)
	JtableResponse buyerUserUpdate(@Context HttpServletRequest req) {

		User user = new User();
		user._id = req.getParameter("_id")
		user.username = req.getParameter("username")
		user.password = req.getParameter("password")
		user.emailId = req.getParameter("emailId")
		user.mobileNo = req.getParameter("mobileNo")
	


			try {
				BasicDBObject source = new BasicDBObject()
				source.put("_id", new ObjectId(user._id))
				BasicDBObject newDocument = new BasicDBObject();
				newDocument.append("username", user.username)
				newDocument.append("password", user.password)
				newDocument.append("emailId", user.emailId)
				newDocument.append("mobileNo", user.mobileNo)
				//BasicDBObject updatedDoc = new BasicDBObject('$set', newDocument);
				BsnetDatabase.getInstance().getJacksonDBCollection(User.class).update(source,new BasicDBObject('$set', newDocument))

			}catch(MongoException e){

				throw new InternalServerErrorException(e)
			}

		return  new JtableResponse("OK")
	}
	
	@POST
	@Path("buyerUserDelete")
	@Produces(APPLICATION_JSON)
	JtableResponse buyerUserDelete (@Context HttpServletRequest req){
		User user = new User();
		user._id = req.getParameter("_id")
		

		//deleting from database
		try{
			ObjectId objId= new ObjectId(user._id);

			BasicDBObject document = new BasicDBObject();
			document.put("_id", objId);
			 BsnetDatabase.getInstance().getJacksonDBCollection(User.class).remove(document)

		}catch(MongoException e){

				throw new InternalServerErrorException(e)
			}

		return  new JtableResponse("OK")

	}

	
	
	@POST
	@Path("storeBuyerItems")
	@Produces(APPLICATION_JSON)
	@Consumes(APPLICATION_JSON)
	Response storeByerItems() {
		try {
			BsnetDatabase.getInstance().getJacksonDBCollection(SupplierItem.class).insert(supItems)//(DBQuery.is("approved",false))
		}catch(MongoException e){
			throw new InternalServerErrorException(e)
		}
		return Response.ok().build()
	}

	@GET
	@Path("getBuyerItems")
	@Produces(APPLICATION_JSON)
	List<SupplierItem> getBuyerItems() {
		List<SupplierItem> suppliers = null
		SupplierItem supplier = null
		try {
			DBCursor<SupplierItem> supCur = BsnetDatabase.getInstance().getJacksonDBCollection(SupplierItem.class).find(DBQuery.is("item",itemName))
			if(supCur != null) {
				suppliers = new ArrayList()
				while(supCur.hasNext()){
					supplier = (SupplierItem) supCur.next()
					suppliers.add(supplier)
				}
			}
		}catch(MongoException e){
			throw new InternalServerErrorException(e)
		}
		return suppliers
	}

	@GET
	@Path("approve")
	@Produces(APPLICATION_JSON)
	Response approveRelation(@Context HttpServletRequest req) {


		String itemName = req.getParameter("item")
		String buyerOrg = req.getParameter("buyerOrg")
		String buyerOrg_id = req.getParameter("id")
		String supplierOrg = req.getParameter("supplierOrg")

		Organization org = BsnetDatabase.getInstance().getJacksonDBCollection(Organization.class).findOne(DBQuery.is("_id",buyerOrg_id))

		if(org == null) {
			return Response.serverError().build()
		}
		BsRelation bsRelation = null
		try {

			bsRelation = new BsRelation()
			bsRelation.supplier = supplierOrg
			bsRelation.buyer = buyerOrg
			bsRelation.item = itemName
			BsnetDatabase.getInstance().getJacksonDBCollection(BsRelation.class).insert(bsRelation)
		}catch(MongoException e){
			throw new InternalServerErrorException(e)
		}
		return Response.ok().build()
	}

}
