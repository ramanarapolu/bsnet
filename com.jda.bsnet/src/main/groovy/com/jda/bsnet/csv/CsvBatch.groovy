package com.jda.bsnet.csv

import java.sql.Connection
import java.sql.PreparedStatement

import org.supercsv.cellprocessor.ift.CellProcessor
import org.supercsv.io.CsvMapReader
import org.supercsv.io.ICsvMapReader
import org.supercsv.prefs.CsvPreference

import sun.jdbc.odbc.ee.ConnectionPool

import com.jda.bsnet.model.Item
import com.jda.bsnet.rest.BsnetDatabase;


public class CsvBatch {

	private CsvBatchResult result;
	private ConnectionPool connectionPool;
	private InputStream dataFileLocation;
	private int startRow;
	private int endRow;
	private int commitSize;
	private String insertSql;

	public CsvBatch() {
		super();
	}

	public CsvBatch(InputStream argDataFileLocation, int argStartRow, int argEndRow,
			int argCommitSize) {
		super();
		this.dataFileLocation = argDataFileLocation;
		this.startRow = argStartRow;
		this.endRow = argEndRow;
		this.commitSize = argCommitSize;
		this.insertSql = argInsertSql;
		this.result = new CsvBatchResult();
	}

	public ConnectionPool getConnectionPool() {
		return this.connectionPool;
	}

	public void setConnectionPool(ConnectionPool argConnectionPool) {
		this.connectionPool = argConnectionPool;
	}

	public String getInsertSql() {
		return this.insertSql;
	}

	public void setInsertSql(String argInsertSql) {
		this.insertSql = argInsertSql;
	}

	public String getDataFileLocation() {
		return this.dataFileLocation;
	}

	public void setDataFileLocation(String argDataFileLocation) {
		this.dataFileLocation = argDataFileLocation;
	}

	public int getStartRow() {
		return this.startRow;
	}

	public void setStartRow(int argStartRow) {
		this.startRow = argStartRow;
	}

	public int getEndRow() {
		return this.endRow;
	}

	public void setEndRow(int argEndRow) {
		this.endRow = argEndRow;
	}

	public int getCommitSize() {
		return this.commitSize;
	}

	public void setCommitSize(int argCommitSize) {
		this.commitSize = argCommitSize;
	}

	public CsvBatchResult getResult() {
		return this.result;
	}

	public void setResult(CsvBatchResult argResult) {
		this.result = argResult;
	}

	public void execute() {
		// logger.info("processing batch startRow:" + startRow + " endRow:"
		// + endRow);
		Connection connection = null;
		PreparedStatement ps = null;
		BufferedReader reader = null;
		ICsvMapReader csvMapReader = null;
		Item itemObj = null;
		List<Item> itemList = new ArrayList()
		try {
			reader = new BufferedReader((dataFileLocation));
			csvMapReader = new CsvMapReader(reader,
					CsvPreference.STANDARD_PREFERENCE);
			String[] header = csvMapReader.getHeader(true);
			if (header != null && header.length > 0) {
				CellProcessor[] processors = getProcessors();
				Map<String, String> columnNameVsValueMap;
				int currentBatchSize = 0;
				while ((columnNameVsValueMap = csvMapReader.read(header)) != null) {
					if (this.getEndRow() >= csvMapReader.getRowNumber() - 1) {
						if (this.getStartRow() <= csvMapReader.getRowNumber() - 1) {
							int parameterIndex = 0;
							itemObj = new Item()
							for (String columnName : header) {
								++parameterIndex;
								itemObj.{columnName} = columnNameVsValueMap
										.get(columnName);
							}
							itemList.add(itemObj);
							++currentBatchSize;
							if (currentBatchSize == this.getCommitSize()) {
								BsnetDatabase.getInstance().getJacksonDBCollection(Item.class).insert(itemList)
								// logger.info("Committed batch startRow:"
								// + startRow + " endRow:" + endRow
								// + " counts:" + Arrays.toString(counts));
								currentBatchSize = 0;
								itemList.clear()
							}
						}
					} else {
						break;
					}
				}
				if (currentBatchSize > 0) {
					BsnetDatabase.getInstance().getJacksonDBCollection(Item.class).insert(itemList)
					// logger.info("Committed batch startRow:" + startRow
					// + " endRow:" + endRow + " counts:"
					// + Arrays.toString(counts));
				}
			}
		} catch (Exception e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (Exception ignore) {
					// ignore
				}
			}
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (Exception ignore) {
					// ignore
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception ignore) {
					// ignore
				}
			}
		}
	}

	private CellProcessor[] getProcessors() {
		return new CellProcessor[0];
	}

	/*private CellProcessor[] getProcessors2() {
		final String emailRegex = "[a-z0-9\\._]+@[a-z0-9\\.]+";
		StrRegEx.registerMessage(emailRegex, "must be a valid email address");
		final CellProcessor[] processors = new CellProcessor[] {
				new UniqueHashCode(), // customerNo (must be unique)
				new NotNull(), // firstName
				new NotNull(), // lastName
				new ParseDate("dd/MM/yyyy"), // birthDate
				new NotNull(), // mailingAddress
				new Optional(new ParseBool()), // married
				new Optional(new ParseInt()), // numberOfKids
				new NotNull(), // favouriteQuote
				new StrRegEx(emailRegex), // email
				new LMinMax(0L, LMinMax.MAX_LONG) // loyaltyPoints
		};

		return processors;
	}*/
}