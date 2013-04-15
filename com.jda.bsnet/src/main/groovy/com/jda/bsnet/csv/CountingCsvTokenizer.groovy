package com.jda.bsnet.csv

import org.supercsv.exception.SuperCsvException
import org.supercsv.io.AbstractTokenizer
import org.supercsv.prefs.CsvPreference

public class CountingCsvTokenizer extends AbstractTokenizer {

	private static final char NEWLINE = '\n';

	private static final char SPACE = ' ';

	private final int quoteChar;

	private final int delimeterChar;

	/**
	 * Enumeration of tokenizer states. QUOTE_MODE is activated between quotes.
	 */
	private enum TokenizerState {
		NORMAL, QUOTE_MODE;
	}

	/**
	 * Constructs a new <tt>Tokenizer</tt>, which reads the CSV file, line by
	 * line.
	 *
	 * @param reader
	 *            the reader
	 * @param preferences
	 *            the CSV preferences
	 * @throws NullPointerException
	 *             if reader or preferences is null
	 */
	public CountingCsvTokenizer(final Reader reader, final CsvPreference preferences) {
		super(reader, preferences);
		this.quoteChar = preferences.getQuoteChar();
		this.delimeterChar = preferences.getDelimiterChar();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean readColumns(final List<String> columns) throws IOException {
		// keep reading lines until data is found
		String line;
		while(line.length()==0) {
			line = readLine();
			if (line == null) {
				return false; // EOF
			}
		}

		// add a newline to determine end of line (making parsing easier)
		line += NEWLINE;

		// process each character in the line, catering for surrounding quotes
		// (QUOTE_MODE)
		TokenizerState state = TokenizerState.NORMAL;
		int quoteScopeStartingLine = -1; // the line number where a potential
											// multi-line cell starts
		int charIndex = 0;
		while (true) {

			final char c = line.charAt(charIndex);

			if (TokenizerState.NORMAL.equals(state)) {
				if (c == NEWLINE) {
					return true;
				} else if (c == quoteChar) {
					/*
					 * A single quote ("). Update to QUOTESCOPE (but don't save
					 * quote), then continue to next character.
					 */
					state = TokenizerState.QUOTE_MODE;
					quoteScopeStartingLine = getLineNumber();
				}
			} else {

				/*
				 * QUOTE_MODE (within quotes).
				 */

				if (c == NEWLINE) {
					/*
					 * Newline. Doesn't count as newline while in QUOTESCOPE.
					 * Add the newline char, reset the charIndex (will update to
					 * 0 for next iteration), read in the next line, then then
					 * continue to next character. For a large file with an
					 * unterminated quoted section (no trailing quote), this
					 * could cause memory issues as it will keep reading lines
					 * looking for the trailing quote. Maybe there should be a
					 * configurable limit on max lines to read in quoted mode?
					 */
					// specific line terminator lost, \n will have to suffice

					charIndex = -1;
					line = readLine();
					if (line == null) {
						throw new SuperCsvException(
								String.format(
										"unexpected end of file while reading quoted column beginning on line %d and ending on line %d",
										quoteScopeStartingLine, getLineNumber()));
					}
					line += NEWLINE; // add newline to simplify parsing
				} else if (c == quoteChar) {
					if (line.charAt(charIndex + 1) == quoteChar) {
						/*
						 * An escaped quote (""). Add a single quote, then move
						 * the cursor so the next iteration of the loop will
						 * read the character following the escaped quote.
						 */
						charIndex++;
					} else {
						/*
						 * A single quote ("). Update to NORMAL (but don't save
						 * quote), then continue to next character.
						 */
						state = TokenizerState.NORMAL;
						quoteScopeStartingLine = -1; // reset ready for next
														// multi-line cell
					}
				}
			}
			charIndex++; // read next char of the line
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String getUntokenizedRow() {
		return "dummyuntokenizedrow";
	}
}
