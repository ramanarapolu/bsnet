package com.jda.bsnet.rest

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

import net.vz.mongodb.jackson.DBCursor
import net.vz.mongodb.jackson.DBQuery
import net.vz.mongodb.jackson.WriteResult

import com.jda.bsnet.model.BsRelation
import com.jda.bsnet.model.BuyerItem
import com.jda.bsnet.model.Item
import com.jda.bsnet.model.Organization
import com.jda.bsnet.model.SupplierItem
import com.jda.bsnet.uitransfer.BSRelationState
import com.jda.bsnet.uitransfer.ItemForSup
import com.jda.bsnet.uitransfer.JtableAddResponse
import com.jda.bsnet.uitransfer.JtableJson
import com.jda.bsnet.uitransfer.JtableResponse
import com.jda.bsnet.util.BsnetUtils
import com.mongodb.BasicDBObject
import com.mongodb.MongoException


@Path("/supplierItem")
class SupplierItemResource {

	@POST
	@Path("create")
	@Produces(APPLICATION_JSON)
	JtableAddResponse create(@Context HttpServletRequest req) {
		List<SupplierItem> storeList = new ArrayList()
		try {
			HttpSession session = req.getSession();
			String orgName = session.getAttribute("orgName")
			SupplierItem supItem = null
			supItem = new SupplierItem()
			supItem.item = req.getParameter("itemName")
			supItem.orgName = orgName
			supItem.deliveryWindow = req.getParameter("deliveryWindow")
			supItem.promoPrice = req.getParameter("promoPrice")
			storeList.add(supItem)
			WriteResult<Item, String> result = BsnetDatabase.getInstance().getJacksonDBCollection(SupplierItem.class).insert(storeList)//(DBQuery.is("approved",false))
			return  new JtableAddResponse("OK", supItem)

		}catch(MongoException e){
			throw new InternalServerErrorException(e)
		}
	}


	@POST
	@Path("update")
	@Produces(APPLICATION_JSON)
	JtableResponse updateItem(@Context HttpServletRequest req) {

		HttpSession session = req.getSession();
		String orgName = session.getAttribute("orgName")
		//item._id = req.getParameter("_id")
		try {
			BasicDBObject source = new BasicDBObject()
			source.put("item",req.getParameter("itemName"))
			BasicDBObject newDocument = new BasicDBObject();
			newDocument.append("orgName", orgName)
			newDocument.append("itemName", req.getParameter("itemName"))
			newDocument.append("deliveryWindow", req.getParameter("deliveryWindow"))
			newDocument.append("promoPrice", req.getParameter("promoPrice"))
			//BasicDBObject updatedDoc = new BasicDBObject('$set', newDocument);
			BsnetDatabase.getInstance().getJacksonDBCollection(Item.class).update(source,new BasicDBObject('$set', newDocument))

		}catch(MongoException e){

			throw new InternalServerErrorException(e)
		}

		return  new JtableResponse("OK")
	}

	@POST
	@Path("delete")
	@Produces(APPLICATION_JSON)
	JtableResponse deleteItem (@Context HttpServletRequest req){
		//deleting from database
		try{


			BasicDBObject andQuery = new BasicDBObject();
			List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
			obj.add(new BasicDBObject("orgName", req.getParameter("orgName")));
			obj.add(new BasicDBObject("item", req.getParameter("itemName")));
			andQuery.put('$and', obj);

			BsnetDatabase.getInstance().getJacksonDBCollection(Item.class).remove(andQuery)

		}catch(MongoException e){

			throw new InternalServerErrorException(e)
		}

		return  new JtableResponse("OK")

	}



	@GET
	@Path("optionsList")
	@Produces(APPLICATION_JSON)
	JtableJson optionsList(@Context HttpServletRequest req) {

		List<ItemForSup> result = new ArrayList()
		try {
			HttpSession session = req.getSession();
			String orgName = session.getAttribute("orgName")
			SupplierItem supItem = null;
			DBCursor<SupplierItem> supCur = BsnetDatabase.getInstance().getJacksonDBCollection(SupplierItem.class).find(DBQuery.is("orgName",orgName))
			List<String> supItems = new ArrayList()
			if(supCur != null) {
				while(supCur.hasNext()){
					supItem = (SupplierItem) supCur.next()
					supItems.add(supItem.item)
				}
			}

			DBCursor<Item> itemCur = BsnetDatabase.getInstance().getJacksonDBCollection(Item.class).find()
			ItemForSup itemForSup = null;
			Item item = null
			while(itemCur?.hasNext()){
				item  = (Item) supCur.next()
				if(!supItems.contains(item.itemName)) {
					itemForSup = new ItemForSup()
					itemForSup.itemName = item.itemName
					itemForSup.category = item.category
					itemForSup.listprice = item.price
					itemForSup.description = item.description
					itemForSup.imageUrl = item.imageUrl
					itemForSup.promoPrice = null
					itemForSup.deliveryWindow = ""
					result.add(itemForSup)
				}
			}

		}catch(MongoException e){
			throw new InternalServerErrorException(e)
		}
		return new JtableJson("OK", result)
	}


	@GET
	@Path("listAll")
	@Produces(APPLICATION_JSON)
	JtableJson listAll(@Context HttpServletRequest req) {

		List<ItemForSup> result = new ArrayList()
		try {
			HttpSession session = req.getSession();
			String orgName = session.getAttribute("orgName")
			SupplierItem supItem = null;
			ItemForSup itemForSup = null;
			DBCursor<SupplierItem> supCur = BsnetDatabase.getInstance().getJacksonDBCollection(SupplierItem.class).find(DBQuery.is("orgName",orgName))
			if(supCur != null) {
				while(supCur.hasNext()){
					itemForSup = new ItemForSup()
					supItem = (SupplierItem) supCur.next()
					Item item = BsnetDatabase.getInstance().getJacksonDBCollection(Item.class).findOne(DBQuery.is("itemName",supItem.item))
					itemForSup.itemName = supItem.item
					itemForSup.category = item.category
					itemForSup.listprice = item.price
					itemForSup.description = item.description
					itemForSup.imageUrl = item.imageUrl
					itemForSup.promoPrice = supItem.promoPrice
					itemForSup.deliveryWindow = supItem.deliveryWindow
					result.add(itemForSup)
				}
			}

		}catch(MongoException e){
			throw new InternalServerErrorException(e)
		}
		return new JtableJson("OK", result)
	}

	@GET
	@Path("getBSRelationState")
	@Produces(APPLICATION_JSON)
	JtableJson getBSRelationState(@Context HttpServletRequest req) {

		List<BSRelationState> bsStateList = new ArrayList()
		BSRelationState bsState = null;
		SupplierItem supItem = null
		BuyerItem buyItem = null
		try {
			HttpSession session = req.getSession();
			String orgName = session.getAttribute("orgName")
			// get the items supplied by the org
			DBCursor<SupplierItem> supCur = BsnetDatabase.getInstance().getJacksonDBCollection(SupplierItem.class).find(DBQuery.is("orgName",orgName))
			if(supCur != null) {
				while(supCur.hasNext()){

					supItem = (SupplierItem) supCur.next()
					// get the buyer name who are intrested in items supplied by the org.
					DBCursor<BuyerItem> buyCur = BsnetDatabase.getInstance().getJacksonDBCollection(BuyerItem.class).find(DBQuery.is("item",supItem.item))

					while(buyCur.hasNext()) {
						buyItem = (BuyerItem)buyCur.next()
						bsState = new BSRelationState()

						BasicDBObject andQuery = new BasicDBObject();
						List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
						obj.add(new BasicDBObject("supplier", orgName));
						obj.add(new BasicDBObject("buyer", buyItem.orgName));
						obj.add(new BasicDBObject("item", buyItem.item));
						andQuery.put('$and', obj);

						BsRelation bsRelation = BsnetDatabase.getInstance().getJacksonDBCollection(BsRelation.class).find(andQuery)
						bsState.buyerName = buyItem.orgName
						bsState.item = buyItem.item
						if(bsRelation != null) {
							bsState.isSubscribed = true
						}else {
							bsState.isSubscribed = false
						}
						bsStateList.add(bsState)
					}
				}
			}
			return new JtableJson("OK", bsStateList)
		}catch(MongoException e) {
			throw new InternalServerErrorException(e)
		}
	}

	@POST
	@Path("requestBuyers")
	@Produces(APPLICATION_JSON)
	@Consumes(APPLICATION_JSON)
	List<BSRelationState> requestBuyersForRelation(@Context HttpServletRequest req,List<BSRelationState> bsRelationList) {

		HttpSession session = req.getSession();
		String orgName = session.getAttribute("orgName")
		bsRelationList.each { BSRelationState bs ->
			Organization buyerOrg = BsnetDatabase.getInstance().getJacksonDBCollection(Organization.class).findOne(DBQuery.is("orgName",bs.buyerName))
			String adminMailId = BsnetUtils.getAdminMailId(buyerOrg.orgName)
			Properties bsNetProp = BsnetDatabase.getInstance().getBsnetServerConfig()
			String body=bsNetProp.get("email.reqbuyer.body").toString();
			String approveLink = bsNetProp.get("bsnet.server.url").toString();
			approveLink=approveLink.replace("ITEM",bs.item).replace("ID",buyerOrg._id).replace("SUPORG", orgName).replace("BUYORG", buyerOrg.orgName)
			String link="http://"+bsNetProp.getProperty("cimix.server.ip")+":"+bsNetProp.getProperty("cimix.server.port")+approveLink
			body=body.replace("ITEM", bs.item)+approveLink
			body=body+bsNetProp.get("email.reqbuyer.signature");
			BsnetUtils.sendMail(adminMailId, bsNetProp.get("email.reqbuyer.subject").toString(), body.toString())
		}

	}

}
