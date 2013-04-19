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
import javax.ws.rs.core.Response

import net.vz.mongodb.jackson.DBCursor
import net.vz.mongodb.jackson.DBQuery

import com.jda.bsnet.BsnetUtils;
import com.jda.bsnet.model.BsRelation
import com.jda.bsnet.model.BuyerItem
import com.jda.bsnet.model.Organization
import com.jda.bsnet.model.SupplierItem
import com.jda.bsnet.uitransfer.BSRelationState
import com.mongodb.BasicDBObject
import com.mongodb.MongoException


@Path("/supplierItem")
class SupplierItemResource {

	@POST
	@Path("storeSupplierItems")
	@Produces(APPLICATION_JSON)
	@Consumes(APPLICATION_JSON)
	Response storeSupplierItems(@Context HttpServletRequest req,List<SupplierItem> supItems) {
		try {
			HttpSession session = req.getSession();
			String orgName = session.getAttribute("orgName")
			supItems.each { SupplierItem item ->
				item.orgName = orgName
			}
			BsnetDatabase.getInstance().getJacksonDBCollection(SupplierItem.class).insert(supItems)//(DBQuery.is("approved",false))
		}catch(MongoException e){
			throw new InternalServerErrorException(e)
		}
		return Response.ok().build()
	}

	@GET
	@Path("getBSRelationState")
	@Produces(APPLICATION_JSON)
	List<BSRelationState> getBSRelationState(@Context HttpServletRequest req) {

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
			return bsStateList
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
