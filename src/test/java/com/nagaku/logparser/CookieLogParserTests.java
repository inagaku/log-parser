package com.nagaku.logparser;


import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

class CookieLogParserTests {

	@Test
	public void testInvalidArgumentCount() {
		String[] args = {"-f", "path/to/file"};
		 assertThrows(ParseException.class, () -> CookieLogParser.main(args));
	}

	@Test
	public void testInvalidFlag() {
		String[] args = {"-x", "path/to/file", "-d", "2020-01-01"};
		assertThrows(ParseException.class, () -> CookieLogParser.main(args));
	}

	@Test
	public void testInvalidFilePath() {
		String[] args = {"-f", "nonexistent/file/path", "-d", "2020-01-01"};
		assertThrows(FileNotFoundException.class, () -> CookieLogParser.main(args));
	}

	@Test
	public void testInvalidDateFormat() throws URISyntaxException {
		URL resource = getClass().getClassLoader().getResource("cookieLog.csv");
		assertNotNull(resource, "Resource file not found");

		// Convert URL to a file path
		String filePath = Paths.get(resource.toURI()).toString();

		String[] args = {"-f", filePath, "-d", "01-01-2020"};
		assertThrows(DateTimeParseException.class, () -> CookieLogParser.main(args));
	}

	@Test
	public void testValidArguments() throws URISyntaxException {
		URL resource = getClass().getClassLoader().getResource("cookieLog.csv");
		assertNotNull(resource, "Resource file not found");

		// Convert URL to a file path
		String filePath = Paths.get(resource.toURI()).toString();

		String[] args = {"-f", filePath, "-d", "2018-12-09"};
		assertDoesNotThrow(() -> CookieLogParser.main(args));

		// If no exception is thrown, the test will pass.
		// There is no check for the right answer as it is printed to output
	}

}
