package com.jda.bsnet

import org.supercsv.prefs.CsvPreference

import com.jda.bsnet.csv.CountingCsvReader


public class CsvUtils {


	public static final int BATCH_SIZE = 100000;
	public static final int MAX_CONNECTIONS = 50;
	public static final int MAX_THREAD_POOL_SIZE = 10;
	public static final int QUEUE_SIZE = 1000;


	public static int getRowCount(InputStream inStream) {
		int rowCount = 0;
		int csvReaderRowCount = 0;
		BufferedReader reader = null;
		CountingCsvReader csvMapReader = null;
		try {
			reader = new BufferedReader(inStream);
			csvMapReader = new CountingCsvReader(reader,
					CsvPreference.STANDARD_PREFERENCE);
			String[] header = csvMapReader.getHeader(true);
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

}
