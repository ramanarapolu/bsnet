package com.jda.bsnet

import org.supercsv.prefs.CsvPreference

import com.jda.bsnet.csv.CountingCsvReader


public class CsvUtils {


	public static final int BATCH_SIZE = 100000;
	public static final int MAX_CONNECTIONS = 50;
	public static final int MAX_THREAD_POOL_SIZE = 10;
	public static final int QUEUE_SIZE = 1000;


	public static int getRowCount(String file) {
		int rowCount = 0;
		int csvReaderRowCount = 0;
		BufferedReader reader = null;
		CountingCsvReader csvMapReader = null;
		try {
			println " counting num of lines in csv"
			reader = new BufferedReader(new FileReader(file));
			csvMapReader = new CountingCsvReader(reader,
					CsvPreference.STANDARD_PREFERENCE);
			String[] header = csvMapReader.getHeader(true);
			println "Header in count reader :"+ header.toString()
			for (; csvMapReader.readRow(); ++rowCount) {
				// Do nothing just increment rowCount;
			}
			csvReaderRowCount = csvMapReader.getRowNumber();
		} catch (Exception e) {
		} finally {
			if (csvMapReader != null) {
				try {
					csvMapReader.close();
				} catch (Exception ignore) {
					// ignore
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception ignore) {
					// ignore
				}
			}
		}
		return rowCount;
	}

	public static void main(String[] args) {
		String csvFileLocation = "C:\\tmp\\items.csv";
		long start = System.currentTimeMillis();
		int iterations = 1000;
		//for (int i = 0; i < iterations; ++i) {
			int rowCount = getRowCount(csvFileLocation);
			// logger.info("There are " + rowCount + " rows in file: "
			// + csvFileLocation);
		//}
		System.out.println("Row count :"+rowCount);
		long end = System.currentTimeMillis();
		long timeTaken = end - start;
	}


}
