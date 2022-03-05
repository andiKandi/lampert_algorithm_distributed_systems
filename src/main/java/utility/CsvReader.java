/**
 * Copyright (C) 2021 Kraus Andreas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package utility;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;

public class CsvReader {

    /**
     * Logger instead of system.print.out
     */
    private static final Logger logger = LogManager.getLogger(CsvReader.class);

    /**
     * Reads the CSV file and returns an ArrayList of csvData
     * @param file
     * @return
     */
    public static ArrayList<CsvData> readCsv(File file) {
        ArrayList<CsvData> csvData = new ArrayList<>();

        try {
            Reader in = new FileReader(file);
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(in);
            for (final CSVRecord record : records) {
                int waitingTime = Integer.parseInt(record.get(0));
                int dataSize = Integer.parseInt(record.get(1));
                csvData.add(new CsvData(waitingTime, dataSize));
            }
        } catch (FileNotFoundException e) {
            logger.error("CSV-File not found at: {}", file);
        } catch (IOException e) {
            logger.error("failed to read csv file", e);
        }
        return csvData;
    }

}
