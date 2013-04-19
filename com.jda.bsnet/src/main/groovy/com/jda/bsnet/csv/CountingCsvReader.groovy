package com.jda.bsnet.csv

import org.supercsv.io.AbstractCsvReader
import org.supercsv.prefs.CsvPreference
import java.io.IOException;
import java.io.Reader;

public class CountingCsvReader extends AbstractCsvReader {

	public CountingCsvReader(Reader argReader, CsvPreference argPreferences) {
		super(new CountingCsvTokenizer(argReader, argPreferences), argPreferences);
	}

	@Override
	public boolean readRow() throws IOException {
		return super.readRow();
	}
}