package com.jda.bsnet.csv


public class CsvBatchTaskCallable {
	private CsvBatch csvBatch;

	public CsvBatchTaskCallable(CsvBatch argCsvBatch) {
		super();
		this.csvBatch = argCsvBatch;
	}

	public CsvBatchResult executeBatch() throws Exception {
		CsvBatchResult result = null;
		try {
			csvBatch.execute();
			result = csvBatch.getResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}