package com.nagaku.logparser;

import com.nagaku.logparser.services.CookieLogService;
import lombok.SneakyThrows;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@SpringBootApplication
public class CookieLogParser {

	private static CookieLogService service = new CookieLogService();

	@SneakyThrows
	public static void main(String[] args) {
		// Create options for command line arguments
		Options options = new Options();
		options.addOption(OptionBuilder.withLongOpt("file").withDescription("The path to the file with logs").hasArg().withArgName("FILE").isRequired().create('f'));
		options.addOption(OptionBuilder.withLongOpt("date").withDescription("The date of a day").hasArg().withArgName("DATE").isRequired().create('d'));

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			// Parse the command line arguments
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			formatter.printHelp("CookieLogParser", options);
			throw e;
		}

		String filePath = cmd.getOptionValue("file");
		String dateString = cmd.getOptionValue("date");

		try {
			// It's expected that date will be provided in the format 'yyyy-MM-dd' at UTC time
			LocalDate localDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
			List<String> mostFrequentCookies = service.getMostActiveCookies(filePath, localDate.atStartOfDay().atOffset(ZoneOffset.UTC));
			for (String cookie : mostFrequentCookies) {
				System.out.println(cookie);
			}
		} catch (DateTimeParseException e) {
			System.err.println("Failed to parse date: " + dateString);
			throw e;
		} catch (FileNotFoundException e) {
			System.err.println("Wrong file path: " + filePath);
			throw e;
		} catch (IOException e) {
			System.err.println("Cannot read the file: " + filePath);
			throw e;
		}
	}
}
