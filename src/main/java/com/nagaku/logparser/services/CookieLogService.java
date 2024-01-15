package com.nagaku.logparser.services;

import com.nagaku.logparser.exceptions.CookieLogException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CookieLogService {
    private final String DELIMITER = ",";

    public List<String> getMostActiveCookies(String filePath, OffsetDateTime dateOfDay) throws IOException {
        Map<String, Integer> cookieCountMap = new HashMap<>();
        int maxCount = -1;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line = reader.readLine();
            int lineCounter = 1;

            if (line == null) {
                throw new CookieLogException("File is empty");
            }
            line = reader.readLine();

            while (line != null) {
                String[] logLine = line.split(DELIMITER);

                if (logLine.length != 2) {
                    throw new CookieLogException("Wrong data in the file at the line " + lineCounter);
                }

                OffsetDateTime cookieDate = OffsetDateTime.parse(logLine[1], DateTimeFormatter.ISO_OFFSET_DATE_TIME);

                // If we process the log on the day before required that means that have we read all the lines for specified day
                if (cookieDate.truncatedTo(ChronoUnit.DAYS).isBefore(dateOfDay)) {
                    break;
                }

                // We do not need to process logs for another day
                if (cookieDate.truncatedTo(ChronoUnit.DAYS).isAfter(dateOfDay)) {
                    line = reader.readLine();
                    continue;
                } 
                line = reader.readLine();

                // Map <cookie, count>
                cookieCountMap.put(logLine[0], cookieCountMap.getOrDefault(logLine[0], 0) + 1);
                maxCount = Math.max(maxCount, cookieCountMap.get(logLine[0]));
            }

        }

        int finalMaxCount = maxCount;
        return cookieCountMap.entrySet().stream()
                .filter(entry -> entry.getValue() == finalMaxCount)
                .map(Map.Entry::getKey)
                .toList();
    }
}
