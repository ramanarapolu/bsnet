package com.jda.bsnet.rest

import static javax.ws.rs.core.MediaType.*

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import javax.ws.rs.InternalServerErrorException
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context

import net.vz.mongodb.jackson.DBCursor
import net.vz.mongodb.jackson.DBQuery
import net.vz.mongodb.jackson.WriteResult

import org.bson.types.ObjectId

import com.jda.bsnet.model.BsRelation
import com.jda.bsnet.model.BuyerItem
import com.jda.bsnet.model.Item
import com.jda.bsnet.model.Organization
import com.jda.bsnet.model.SupplierItem
import com.jda.bsnet.uitransfer.BSRelationState
import com.jda.bsnet.uitransfer.ItemForSup
import com.jda.bsnet.uitransfer.JtableAddResponse
import com.jda.bsnet.uitransfer.JtableJson
import com.jda.bsnet.uitransfer.JtableOptions
import com.jda.bsnet.uitransfer.JtableOptionsResponse
import com.jda.bsnet.uitransfer.JtableResponse
import com.jda.bsnet.util.BsnetUtils
import com.jda.bsnet.util.MetricsUtils
import com.mongodb.BasicDBObject
import com.mongodb.MongoException
import com.yammer.metrics.annotation.Timed
import com.yammer.metrics.core.TimerContext


@Path("/supplierItem")
class SupplierItemResource {

	@POST
	@Timed
	@Path("create")
	@Produces(APPLICATION_JSON)
	JtableAddResponse create(@Context HttpServletRequest req) {

		TimerContext tc = MetricsUtils.startTimer(MetricsUtils.createSupItemCounter)
		List<SupplierItem> storeList = new ArrayList()
		try {
			HttpSession session = req.getSession();
			String orgName = session.getAttribute("orgName")
			SupplierItem supItem = null
			supItem = new SupplierItem()
			supItem.item = req.getParameter("item")
			supItem.orgName = orgName
			supItem.deliveryWindow = req.getParameter("deliveryWindow")
			if(req.getParameter("listprice") != null && req.getParameter("listprice")!= "")
				supItem.promoPrice = Double.parseDouble(req.getParameter("listprice"))
			Item item  = BsnetDatabase.getInstance().getJacksonDBCollection(Item.class).findOne(DBQuery.is("itemName",supItem.item))
			supItem.category = item.category;
			supItem.description = item.description; 

			WriteResult<Item, String> result = BsnetDatabase.getInstance().getJacksonDBCollection(SupplierItem.class).insert(supItem)

			DBCursor<Item> supCur = BsnetDatabase.getInstance().getJacksonDBCollection(SupplierItem.class).find(DBQuery.is("item",supItem.item))
			supItem = (SupplierItem) supCur.next();

			return  new JtableAddResponse("OK", supItem)
		} catch(MongoException e){
			throw new InternalServerErrorException(e)
		}finally {
			MetricsUtils.stopTimer(tc)
		}
	}


	@POST
	@Timed
	@Path("update")
	@Produces(APPLICATION_JSON)
	JtableResponse updateItem(@Context HttpServletRequest req) {

		TimerContext tc = MetricsUtils.startTimer(MetricsUtils.updateSupItemCounter)
		try {

			BasicDBObject source = new BasicDBObject("_id",new ObjectId(req.getParameter("_id")));
			BasicDBObject newDocument = new BasicDBObject();
			newDocument.append('$set', new BasicDBObject().append("deliveryWindow", req.getParameter("deliveryWindow")));
			newDocument.append('$set', new BasicDBObject().append("promoPrice", req.getParameter("listprice")));


			BsnetDatabase.getInstance().getJacksonDBCollection(SupplierItem.class).update(source, newDocument)
		}catch(MongoException e){
			throw new InternalServerErrorException(e)
		}
		return  new JtableResponse("OK")
	}

	@POST
	@Timed
	@Path("delete")
	@Produces(APPLICATION_JSON)
	JtableResponse deleteItem (@Context HttpServletRequest req){
		//deleting from database
		TimerContext tc = MetricsUtils.startTimer(MetricsUtils.deleteSupItemCounter)
		try{
			/*	BasicDBObject andQuery = new BasicDBObject();
			 List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
			 obj.add(new BasicDBObject("orgName", req.getParameter("orgName")));
			 obj.add(new BasicDBObject("item", req.getParameter("itemName")));
			 andQuery.put('$and', obj);*/

			ObjectId objId= new ObjectId(req.getParameter("_id"));
			BasicDBObject document = new BasicDBObject();
			document.put("_id", objId);
			BsnetDatabase.getInstance().getJacksonDBCollection(SupplierItem.class).remove(document)

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

		TimerContext tc = MetricsUtils.startTimer(MetricsUtils.optionListCounter)
		List<JtableOptions> result = new ArrayList()
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
			//ItemForSup itemForSup = null;
			Item item = null
			JtableOptions options = null
			//int count = 1
			while(itemCur.hasNext()){
				item  = (Item) itemCur.next()
				if(!supItems.contains(item.itemName)) {
					options = new JtableOptions()
					options.displayText = item.itemName
					options.value = item.itemName
					//	count++
					result.add(options)
				}
			}

		}catch(MongoException e){
			throw new InternalServerErrorException(e)
		}finally {
			MetricsUtils.stopTimer(tc)
		}

		return new JtableOptionsResponse("OK", result)
	}


	/*	@POST
	 @Path("optionsListAll")
	 @Produces(APPLICATION_JSON)
	 JtableOptionsResponse optionsListAll(@Context HttpServletRequest req) {
	 List<JtableOptions> result = new ArrayList()
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
	 //ItemForSup itemForSup = null;
	 Item item = null
	 JtableOptions options = null
	 //int count = 1
	 while(itemCur.hasNext()){
	 item  = (Item) itemCur.next()
	 //if(!supItems.contains(item.itemName)) {
	 if(true) {
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
	 }*/

	@POST
	@Timed
	@Path("listAll")
	@Produces(APPLICATION_JSON)
	JtableJson listAll(@Context HttpServletRequest req) {

		TimerContext tc = MetricsUtils.startTimer(MetricsUtils.listAllCounter)
		List<ItemForSup> result = new ArrayList()
		try {
			HttpSession session = req.getSession();
			String orgName = session.getAttribute("orgName")
			println orgName
			SupplierItem supItem = null;
			ItemForSup itemForSup = null;
			DBCursor<SupplierItem> supCur = BsnetDatabase.getInstance().getJacksonDBCollection(SupplierItem.class).find(DBQuery.is("orgName",orgName))
			if(supCur != null) {
				while(supCur.hasNext()){
					itemForSup = new ItemForSup()
					supItem = (SupplierItem) supCur.next()
					Item item = BsnetDatabase.getInstance().getJacksonDBCollection(Item.class).findOne(DBQuery.is("itemName",supItem.item))
					itemForSup._id = supItem._id
					itemForSup.itemName = supItem.item
					itemForSup.category = item.category
					itemForSup.listprice = supItem.promoPrice
					itemForSup.description = item.description
					itemForSup.imageUrl = item.imageUrl
					itemForSup.promoPrice = supItem.promoPrice
					itemForSup.deliveryWindow = supItem.deliveryWindow
					result.add(itemForSup)
				}
			}
		}catch(MongoException e){
			throw new InternalServerErrorException(e)
		}finally {

			MetricsUtils.stopTimer(tc)
		}
		return new JtableJson("OK", result, result.size())
	}

	@POST
	@Timed
	@Path("getBSRelationState")
	@Produces(APPLICATION_JSON)
	JtableJson getBSRelationState(@Context HttpServletRequest req) {

		TimerContext tc = MetricsUtils.startTimer(MetricsUtils.getBSRelationStateCounter)
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

						BsRelation bsRelation = BsnetDatabase.getInstance().getJacksonDBCollection(BsRelation.class).findOne(andQuery)
						bsState.buyerName = buyItem.orgName
						bsState.item = buyItem.item
						if(bsRelation != null) {
							bsState.subscribed = "Subscribed"
						}else {
							bsState.subscribed = "Not subscribed"
						}
						bsStateList.add(bsState)
					}
				}
			}
			return new JtableJson("OK", bsStateList)
		}catch(MongoException e) {
			throw new InternalServerErrorException(e)
		}finally {
			MetricsUtils.stopTimer(tc)
		}
	}
	@POST
	@Timed
	@Path("requestBuyers")
	@Produces(APPLICATION_JSON)
	List<BSRelationState> requestBuyersForRelation(@Context HttpServletRequest req,List<BSRelationState> bsRelationList) {

		TimerContext tc = MetricsUtils.startTimer(MetricsUtils.requestBuyersCounter)
		HttpSession session = req.getSession();
		String orgName = session.getAttribute("orgName")

		bsRelationList.each { BSRelationState bs ->
			Organization buyerOrg = BsnetDatabase.getInstance().getJacksonDBCollection(Organization.class).findOne(DBQuery.is("orgName",bs.buyerName))
			String adminMailId = BsnetUtils.getAdminMailId(buyerOrg.orgName)
			Properties bsNetProp = BsnetDatabase.getInstance().getBsnetServerConfig()
			String body=bsNetProp.get("email.reqbuyer.body").toString();
			String approveLink = bsNetProp.get("bsnet.server.url").toString();
			approveLink=approveLink.replace("ITEM",bs.item).replace("ID",buyerOrg._id).replace("SUPORG", orgName).replace("BUYORG", buyerOrg.orgName)
			String link="http://"+bsNetProp.getProperty("bsnet.server.ip")+":"+bsNetProp.getProperty("bsnet.server.port")+approveLink
			body=body.replace("ITEM", bs.item)+link
			body=body+bsNetProp.get("email.reqbuyer.signature");
			BsnetUtils.sendMail(adminMailId, bsNetProp.get("email.reqbuyer.subject").toString(), body.toString())
		}
		MetricsUtils.stopTimer(tc)
	}
}
