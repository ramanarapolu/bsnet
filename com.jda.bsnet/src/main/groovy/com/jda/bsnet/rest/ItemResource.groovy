package com.jda.bsnet.rest;

import static javax.ws.rs.core.MediaType.*
import groovyx.gpars.GParsPool

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.InternalServerErrorException
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

import net.vz.mongodb.jackson.WriteResult

import org.glassfish.jersey.media.multipart.FormDataContentDisposition
import org.glassfish.jersey.media.multipart.FormDataParam

import com.jda.bsnet.CsvUtils
import com.jda.bsnet.csv.CsvBatch
import com.jda.bsnet.csv.CsvBatchTaskCallable
import com.jda.bsnet.model.Item
import com.mongodb.MongoException

@Path("/item")
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
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	Item createItem(Item item) {

		if (item != null)
		{
			try {
				WriteResult<Item, String> result = BsnetDatabase.getInstance().getJacksonDBCollection(Item.class).insert(item)
				return result.getSavedObject();
			}catch(MongoException e){
				throw new InternalServerErrorException(e)
			}
			catch(MongoException e)
			{
				throw new InternalServerErrorException(e)
			}
		}
	}

	@POST
	@Path("/uploadItems")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(APPLICATION_JSON)
	Response createBulkItems(@FormDataParam("file") InputStream uploadedInputStream,
		@FormDataParam("file") FormDataContentDisposition fileDetail) {

		long startTime = System.currentTimeMillis();
		int totalRows = -1;
		int numBatches = -1;
		try {
			//String insertSql = getInsertSql(this.tableName, this.connectionPool);
			int startRow = 0;
			int endRow = 0;
			totalRows = CsvUtils.getRowCount(uploadedInputStream);
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
				CsvBatch csvBatch = new CsvBatch(uploadedInputStream, startRow, endRow,
						CsvUtils.BATCH_SIZE);
				CsvBatchTaskCallable callable = new CsvBatchTaskCallable(
						csvBatch);
				batchList.add(callable)
			}
			GParsPool.withPool(CsvUtils.MAX_THREAD_POOL_SIZE) {
				batchList.eachParallel{CsvBatchTaskCallable callable ->;
					callable.executeBatch()
				}
			}
		} finally {
			// log
		}
		return Response.ok().build()
	}


}
