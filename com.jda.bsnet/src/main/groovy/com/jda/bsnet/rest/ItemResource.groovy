package com.jda.bsnet.rest;

import static javax.ws.rs.core.MediaType.*
import groovy.util.logging.Slf4j
import groovyx.gpars.GParsPool

import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.InternalServerErrorException
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

import net.vz.mongodb.jackson.DBCursor
import net.vz.mongodb.jackson.DBQuery
import net.vz.mongodb.jackson.WriteResult

import org.bson.types.ObjectId
import org.glassfish.jersey.media.multipart.FormDataContentDisposition
import org.glassfish.jersey.media.multipart.FormDataParam

import com.jda.bsnet.csv.CsvBatch
import com.jda.bsnet.csv.CsvBatchTaskCallable
import com.jda.bsnet.model.Item
import com.jda.bsnet.model.SupplierItem
import com.jda.bsnet.uitransfer.JtableAddResponse
import com.jda.bsnet.uitransfer.JtableJson
import com.jda.bsnet.uitransfer.JtableResponse
import com.jda.bsnet.util.CsvUtils
import com.mongodb.BasicDBObject
import com.mongodb.MongoException

@Path("/item")
@Slf4j
class ItemResource {




	@GET
	@Path("hello")
	@Produces(TEXT_PLAIN)
	//Example URL:  http://api.jda.com/reco/v1/users/hello
	String getHello(){
		return "Hello"
	}

	@POST
	@Path("create")
	@Produces(APPLICATION_JSON)
	JtableAddResponse createItem(@Context HttpServletRequest req) {
		Item item = new Item();
		item.itemName = req.getParameter("itemName")
		item.description = req.getParameter("description")
		if(req.getParameter("price") != null && req.getParameter("price")!= ""){
		item.price = Double.parseDouble(req.getParameter("price"));
		}
		item.category = req.getParameter("category")
		if (item != null)
		{
			try {
				WriteResult<Item, String> result = BsnetDatabase.getInstance().getJacksonDBCollection(Item.class).insert(item)

				//DBCursor<Item> itemCur = BsnetDatabase.getInstance().getJacksonDBCollection(Item.class).find(DBQuery.is("itemName",item.itemName))
				//item = (Item) itemCur.next();
				//return  new JtableResponse("OK")

				return  new JtableAddResponse("OK", item)
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
	@Path("update")
	@Produces(APPLICATION_JSON)
	JtableResponse updateItem(@Context HttpServletRequest req) {

		Item item = new Item();
		item.itemName = req.getParameter("itemName")
		item.description = req.getParameter("description")
		item.price = Double.parseDouble(req.getParameter("price"));
		item.category = req.getParameter("category")


			try {
				/*BasicDBObject source = new BasicDBObject("itemName",req.getParameter("itemName"));
				BasicDBObject newDocument = new BasicDBObject();
				newDocument.append('$set', new BasicDBObject().append("approved", true));
				BsnetDatabase.getInstance().getJacksonDBCollection(Item.class).update(source,newDocument)*/
			}catch(MongoException e){

				throw new InternalServerErrorException(e)
			}

		return  new JtableResponse("OK")
	}


	@POST
	@Path("delete")
	@Produces(APPLICATION_JSON)
	JtableResponse deleteItem (@Context HttpServletRequest req){
		Item item = new Item();
		item._id = req.getParameter("_id")
		println item._id

		//deleting from database
		try{
			ObjectId objId= new ObjectId(item._id);

			BasicDBObject document = new BasicDBObject();
			document.put("_id", objId);
			 BsnetDatabase.getInstance().getJacksonDBCollection(Item.class).remove(document)

		}catch(MongoException e){

				throw new InternalServerErrorException(e)
			}

		return  new JtableResponse("OK")

	}


	@POST
	@Path("listAll")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)

	JtableJson getAllItems(){
		List<Item> items = null
		Item item = null
		try {
			//DBCursor<Organization> orgCur = BsnetDatabase.getInstance().getJacksonDBCollection(Organization.class).find(DBQuery.is("approved",false))
			DBCursor<Item> itemCur = BsnetDatabase.getInstance().getJacksonDBCollection(Item.class).find()
			if(itemCur != null) {
				items = new ArrayList()
				while(itemCur.hasNext()){
					item = (Item) itemCur.next();
					items.add(item)
				}
			}
		}catch(MongoException e){
			throw new InternalServerErrorException(e)
		}

	 return  new JtableJson("OK", items)
	}


	@POST
	@Path("/uploadItems")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(APPLICATION_JSON)
	Response createBulkItems(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		String uploadedFileLocation = BsnetDatabase.getInstance().getBsnetProp().getProperty("bsnet.itemfile.loc") + fileDetail.getFileName();
		saveToFile(uploadedInputStream, uploadedFileLocation);
		long startTime = System.currentTimeMillis();
		int totalRows = -1;
		int numBatches = -1;
		try {
			int startRow = 0;
			int endRow = 0;
			totalRows = CsvUtils.getRowCount(uploadedFileLocation);
			numBatches = totalRows % CsvUtils.BATCH_SIZE == 0 ? (totalRows
					/ CsvUtils.BATCH_SIZE)
					: (totalRows / CsvUtils.BATCH_SIZE + 1);
			List<CsvBatchTaskCallable> batchList = new ArrayList()
			for (int i = 1; i <= numBatches; ++i) {
				startRow = (i - 1) * CsvUtils.BATCH_SIZE + 1;
				endRow = i * CsvUtils.BATCH_SIZE;
				if (endRow > totalRows) {
					endRow = totalRows;
				}
				CsvBatch csvBatch = new CsvBatch(uploadedFileLocation, startRow, endRow,
						CsvUtils.BATCH_SIZE);
				CsvBatchTaskCallable callable = new CsvBatchTaskCallable(
						csvBatch);
				batchList.add(callable)
			}
			GParsPool.withPool(CsvUtils.MAX_THREAD_POOL_SIZE) {
				batchList.eachParallel{CsvBatchTaskCallable callable ->
					callable.executeBatch()
				}
			}
		} finally {
			new File(uploadedFileLocation).delete()
		}
		return Response.ok().build()
	}


	@POST
	@Path("getSuppliers")
	@Produces(APPLICATION_JSON)
	@Consumes(APPLICATION_JSON)
	List<SupplierItem> getSuppliers(String itemName) {
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

	// save uploaded file to new location
	private void saveToFile(InputStream uploadedInputStream,
		String uploadedFileLocation) {

		try {
			OutputStream out = null;
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}
}
