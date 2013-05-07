package com.jda.bsnet.rest
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.POST
import javax.ws.rs.Path

import static javax.ws.rs.core.MediaType.*

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import javax.ws.rs.GET
import javax.ws.rs.InternalServerErrorException
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context

import net.vz.mongodb.jackson.DBCursor
import net.vz.mongodb.jackson.DBQuery

import com.jda.bsnet.model.BuyerItem
import com.jda.bsnet.model.Item
import com.jda.bsnet.model.SupplierItem
import com.jda.bsnet.util.MetricsUtils
import com.yammer.metrics.annotation.Timed
import com.yammer.metrics.core.TimerContext

@Path("/marketPlace")
class MarketplaceResource {

	@GET
	@Timed
	@Path("itemDetails")
	@Produces(APPLICATION_JSON)

	Item  itemDetails(@Context HttpServletRequest req){

		TimerContext tc = MetricsUtils.startTimer(MetricsUtils.itemDetailsCounter)
		Item item = null
		try {
			item = BsnetDatabase.getInstance().getJacksonDBCollection(Item.class).findOne(DBQuery.is("item", req.getParameter("item")))
		}catch(Exception e){
			throw new InternalServerErrorException(e)
		}finally {
			MetricsUtils.stopTimer(tc)
		}
		return  item
	}


	@GET
	@Timed
	@Path("categoryList")
	@Produces(APPLICATION_JSON)

	List categoryList(@Context HttpServletRequest req){

		TimerContext tc = MetricsUtils.startTimer(MetricsUtils.categoryListCounter)
		List categoryList = new ArrayList();

		try {
			categoryList = BsnetDatabase.getInstance().getJacksonDBCollection(Item.class).distinct("category")
		}catch(Exception e){
			throw new InternalServerErrorException(e)
		}finally {
			MetricsUtils.stopTimer(tc)
		}
		return  categoryList
	}



	@POST
	@Timed
	@Path("getItems")
	@Produces(APPLICATION_JSON)

	List getItems(@Context HttpServletRequest req){

		TimerContext tc = MetricsUtils.startTimer(MetricsUtils.getItemsCounter)
		List<String> BuyerItemsList = null
		BuyerItem buyerItem = new BuyerItem()
		HttpSession session = req.getSession()
		String orgName = session.getAttribute("orgName")

		List<SupplierItem> suppItemList = null
		List<SupplierItem> suppItemListFiltered = null
		SupplierItem suppItem = new SupplierItem()

		try {
			// Get All buyer items and store them into BuyerItemsList
			DBCursor<BuyerItem> buyerItemCur  = BsnetDatabase.getInstance().getJacksonDBCollection(BuyerItem.class).find(DBQuery.is("orgName", orgName))
			if(buyerItemCur != null) {
				BuyerItemsList = new ArrayList()
				while(buyerItemCur.hasNext()){
					buyerItem = (BuyerItem) buyerItemCur.next();
					BuyerItemsList.add(buyerItem.item)
				}
			}

			// Get all supplier items
			DBCursor<SupplierItem> suppItemCur  = BsnetDatabase.getInstance().getJacksonDBCollection(SupplierItem.class).find()
			if(suppItemCur != null) {
				suppItemList = new ArrayList()
				while(suppItemCur.hasNext()){
					suppItem = (SupplierItem) suppItemCur.next();
					suppItemList.add(suppItem)
				}
			}

			// Remove items from supplier items which are not buyer items
			suppItemListFiltered = new ArrayList()
			for (SupplierItem x : suppItemList ){

				if (BuyerItemsList.contains(x.item)){
					suppItemListFiltered.add(x)
				}
			}
		}catch(Exception e){
			throw new InternalServerErrorException(e)
		}finally {
			MetricsUtils.stopTimer(tc)
		}
		return  suppItemListFiltered
	}







}
