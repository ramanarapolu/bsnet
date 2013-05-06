package com.jda.bsnet.rest

import static javax.ws.rs.core.MediaType.*
import groovy.util.logging.Slf4j

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

import org.bson.types.ObjectId

import com.jda.bsnet.model.BsRelation
import com.jda.bsnet.model.BuyerItem
import com.jda.bsnet.model.Item
import com.jda.bsnet.model.Organization
import com.jda.bsnet.model.SupplierItem
import com.jda.bsnet.model.User
import com.jda.bsnet.uitransfer.JtableAddResponse
import com.jda.bsnet.uitransfer.JtableJson
import com.jda.bsnet.uitransfer.JtableOptions
import com.jda.bsnet.uitransfer.JtableOptionsResponse
import com.jda.bsnet.uitransfer.JtableResponse
import com.mongodb.BasicDBObject
import com.mongodb.MongoException
import com.yammer.metrics.annotation.Timed


@Path("/buyerItem")
@Slf4j
class BuyerItemResource {


	@POST
	@Timed
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
	@Timed
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
	@Timed
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
				e.printStackTrace()
				throw new InternalServerErrorException(e)
			}

		return  new JtableResponse("OK")
	}

	@POST
	@Timed
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
	@Timed
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
	@Timed
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
	@Timed
	@Path("approve")
	@Produces(APPLICATION_JSON)
	Response approveRelation(@Context HttpServletRequest req) {

		String itemName = req.getParameter("item")
		String buyerOrg = req.getParameter("buyerOrg")
		String buyerOrg_id = req.getParameter("id")
		String supplierOrg = req.getParameter("supplierOrg")

		Organization org = BsnetDatabase.getInstance().getJacksonDBCollection(Organization.class).findOne(DBQuery.is("_id",new ObjectId(buyerOrg_id)))
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
			e.printStackTrace()
			throw new InternalServerErrorException(e)
		}
		return Response.ok().build()
	}

	@POST
	@Timed
	@Path("buyerItemlistAll")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)

	JtableJson buyerItemlistAll(@Context HttpServletRequest req){
		List<User> buyers = null
		BuyerItem buyerItem = null
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
			DBCursor<BuyerItem> buyerCur = BsnetDatabase.getInstance().getJacksonDBCollection(BuyerItem.class).find(DBQuery.is("orgName",orgName)).skip(jtStartIndex).limit(jtPageSize)
			if(buyerCur != null) {
				buyers = new ArrayList()
				while(buyerCur.hasNext()){
					buyerItem = (BuyerItem) buyerCur.next();
					buyers.add(buyerItem)
				}
			}
		}catch(MongoException e){
			throw new InternalServerErrorException(e)
		}

	 return  new JtableJson("OK", buyers,buyers.size())
	}



		@POST
		@Timed
		@Path("buyerItemCreate")
		@Produces(APPLICATION_JSON)
		JtableAddResponse buyerItemCreate(@Context HttpServletRequest req) {

			try {
				HttpSession session = req.getSession();
				String orgName = session.getAttribute("orgName")
				BuyerItem buyerItem = null
				buyerItem = new BuyerItem()
				buyerItem.item = req.getParameter("itemName")
				buyerItem.orgName = orgName

				WriteResult<BuyerItem, String> result = BsnetDatabase.getInstance().getJacksonDBCollection(BuyerItem.class).insert(buyerItem)

				BasicDBObject andQuery = new BasicDBObject();
				List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
				obj.add(new BasicDBObject("item", buyerItem.item));
				obj.add(new BasicDBObject("orgName", buyerItem.orgName));
				andQuery.put('$and', obj);

				buyerItem = BsnetDatabase.getInstance().getJacksonDBCollection(BuyerItem.class).findOne(andQuery)

				return  new JtableAddResponse("OK", buyerItem)

			}catch(MongoException e){
				throw new InternalServerErrorException(e)

			}
		}

		@POST
		@Timed
		@Path("buyerItemDelete")
		@Produces(APPLICATION_JSON)
		JtableResponse buyerItemDelete (@Context HttpServletRequest req){
			//deleting from database
			try{


				ObjectId objId= new ObjectId(req.getParameter("_id"));
				BasicDBObject document = new BasicDBObject();
				document.put("_id", objId);
				BsnetDatabase.getInstance().getJacksonDBCollection(BuyerItem.class).remove(document)

			}catch(MongoException e){
				throw new InternalServerErrorException(e)
			}
			return  new JtableResponse("OK")
		}



		@POST
		@Timed
		@Path("optionsList")
		@Produces(APPLICATION_JSON)
		JtableOptionsResponse optionsList(@Context HttpServletRequest req) {

			List<JtableOptions> result = new ArrayList()
			try {
				HttpSession session = req.getSession();
				String orgName = session.getAttribute("orgName")
				BuyerItem buyerItem = null;
				DBCursor<BuyerItem> buyerCur = BsnetDatabase.getInstance().getJacksonDBCollection(BuyerItem.class).find(DBQuery.is("orgName",orgName))
				List<String> buyerItems = new ArrayList()
				if(buyerCur != null) {
					while(buyerCur.hasNext()){
						buyerItem = (BuyerItem) buyerCur.next()
						buyerItems.add(buyerItem.item)
					}
				}

				DBCursor<Item> itemCur = BsnetDatabase.getInstance().getJacksonDBCollection(Item.class).find()
				//ItemForSup itemForSup = null;
				Item item = null
				JtableOptions options = null
				//int count = 1
				while(itemCur.hasNext()){
					item  = (Item) itemCur.next()
					if(!buyerItems.contains(item.itemName)) {
						options = new JtableOptions()
						options.displayText = item.itemName
						options.value = item.itemName
						//	count++
						result.add(options)
					}
				}

			}catch(MongoException e){
				throw new InternalServerErrorException(e)
			}
			return new JtableOptionsResponse("OK", result)
		}


		@POST
		@Timed
		@Path("buyerItemSupplierList")
		@Consumes(APPLICATION_JSON)
		@Produces(APPLICATION_JSON)

		JtableJson buyerItemSupplierList(@Context HttpServletRequest req){
			List<BsRelation> suppliers = null
			BsRelation bsRelation = null
			String orgName =  req.getSession().getAttribute("orgName")


			BasicDBObject andQuery = new BasicDBObject();
			List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
			obj.add(new BasicDBObject("item", req.getParameter("item")	));
			obj.add(new BasicDBObject("buyer", orgName));
			andQuery.put('$and', obj);
			try {

				DBCursor<BsRelation> suppCur = BsnetDatabase.getInstance().getJacksonDBCollection(BsRelation.class).find(andQuery)
				if(suppCur != null) {
					suppliers = new ArrayList()
					while(suppCur.hasNext()){
						bsRelation = (BsRelation) suppCur.next();
						suppliers.add(bsRelation)
					}
				}
			}catch(MongoException e){
				throw new InternalServerErrorException(e)
			}

		 return  new JtableJson("OK", suppliers)
		}


		@POST
		@Timed
		@Path("buyerItemSupplierCreate")
		@Produces(APPLICATION_JSON)
		JtableAddResponse buyerItemSupplierCreate(@Context HttpServletRequest req) {

			try {
				HttpSession session = req.getSession();
				String orgName = session.getAttribute("orgName")
				BsRelation bsRelation = null
				bsRelation = new BsRelation()

				String supplier
				String buyer
				String item

				bsRelation.supplier = req.getParameter("supplierAdd")
				bsRelation.item = req.getParameter("item")
				bsRelation.buyer = orgName

				WriteResult<BsRelation, String> result = BsnetDatabase.getInstance().getJacksonDBCollection(BsRelation.class).insert(bsRelation)

				BasicDBObject andQuery = new BasicDBObject();
				List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
				obj.add(new BasicDBObject("item", bsRelation.item));
				obj.add(new BasicDBObject("supplier", bsRelation.supplier));
				obj.add(new BasicDBObject("buyer", bsRelation.buyer));
				andQuery.put('$and', obj);

				bsRelation = BsnetDatabase.getInstance().getJacksonDBCollection(BsRelation.class).findOne(andQuery)

				return  new JtableAddResponse("OK", bsRelation)

			}catch(MongoException e){
				throw new InternalServerErrorException(e)

			}
		}




		@POST
		@Timed
		@Path("buyerItemSupplierDelete")
		@Produces(APPLICATION_JSON)
		JtableResponse buyerItemSupplierDelete (@Context HttpServletRequest req){
			//deleting from database
			try{


				ObjectId objId= new ObjectId(req.getParameter("_id"));
				BasicDBObject document = new BasicDBObject();
				document.put("_id", objId);
				BsnetDatabase.getInstance().getJacksonDBCollection(BsRelation.class).remove(document)

			}catch(MongoException e){
				throw new InternalServerErrorException(e)
			}
			return  new JtableResponse("OK")
		}



	//  Not completed
		@POST
		@Timed
		@Path("optionsListSupplier")
		@Produces(APPLICATION_JSON)
		JtableOptionsResponse optionsListSupplier(@Context HttpServletRequest req) {

			List<JtableOptions> result = new ArrayList()
			try {
				HttpSession session = req.getSession();
				String orgName = session.getAttribute("orgName")
				String item = req.getParameter("item")

				// Getting buyer-supp relations
				BasicDBObject andQuery = new BasicDBObject();
				List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
				obj.add(new BasicDBObject("item", item	));
				obj.add(new BasicDBObject("buyer", orgName));
				andQuery.put('$and', obj);
				BsRelation bsr = null
				List<String> bsrList = new ArrayList();

					DBCursor<BsRelation> bsrCur = BsnetDatabase.getInstance().getJacksonDBCollection(BsRelation.class).find(andQuery)
					if(bsrCur != null) {
						while(bsrCur.hasNext()){
							bsr = (BsRelation) bsrCur.next();
							bsrList.add(bsr.supplier)
						}
					}

				// Getting suppliers list
					List<SupplierItem> suppliersList = new ArrayList();
					SupplierItem suppItem = null;
					JtableOptions options = null
					DBCursor<SupplierItem> suppCur = BsnetDatabase.getInstance().getJacksonDBCollection(SupplierItem.class).find(DBQuery.is("item",item))
					if(suppCur!=null){
					while(suppCur.hasNext()){
						suppItem = (SupplierItem) suppCur.next()
						suppliersList.add(suppItem)
						if(!bsrList.contains(suppItem.orgName)) {
							options = new JtableOptions()
							options.displayText = suppItem.orgName
							options.value = suppItem.orgName
							//	count++
							result.add(options)
						}
					}
				}

			}catch(MongoException e){
				throw new InternalServerErrorException(e)
			}
			return new JtableOptionsResponse("OK", result)
		}


}
