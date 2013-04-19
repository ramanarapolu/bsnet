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
import com.jda.bsnet.model.Organization
import com.jda.bsnet.model.SupplierItem
import com.mongodb.MongoException


@Path("/buyerItem")

class BuyerItemResource {

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
