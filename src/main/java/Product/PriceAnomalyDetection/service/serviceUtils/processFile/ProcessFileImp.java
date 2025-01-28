package Product.PriceAnomalyDetection.service.serviceUtils.processFile;

import Product.PriceAnomalyDetection.model.PriceData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
@Component
public class ProcessFileImp implements ProcessFile {


    /**
     * Processes a CSV file containing price data and organizes it into a map by item ID.
     *
     * <p>This method reads a CSV file, extracts the item ID, price, and date for each record,
     * and stores the price data associated with each item ID in a map. Each item ID is mapped
     * to a list of {@link PriceData} objects that contain the corresponding price and date.</p>
     *
     * <p>The method performs the following steps:
     * <ol>
     *     <li>Reads the input CSV file using a {@link CSVParser} with the first record as a header.</li>
     *     <li>For each record in the CSV, extracts the "ITEM_ID", "PRICE", and "ORD_CLOSED_DT" fields.</li>
     *     <li>Converts the price field to a {@link BigDecimal} and the date field to a {@link Date} using a {@link SimpleDateFormat}.</li>
     *     <li>Creates a {@link PriceData} object for each record and adds it to the appropriate list in the map.</li>
     *     <li>If an item ID is encountered for the first time, a new list of price data is created; otherwise, the new data is added to the existing list.</li>
     * </ol></p>
     *
     * @param file The CSV file containing the price data to be processed.
     * @return A map where each key is an item ID and each value is a list of {@link PriceData} objects associated with that item.
     * @throws IOException If an I/O error occurs while reading the file.
     * @throws ParseException If the date format in the file cannot be parsed correctly.
     */
    @Override
    public Map<String, List<PriceData>> processData(MultipartFile file) throws IOException, ParseException {

        Map<String, List<PriceData>> data = new HashMap<>();

        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : csvParser) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String itemId = record.get("ITEM_ID");
                BigDecimal price = BigDecimal.valueOf(Double.valueOf(record.get("PRICE")));
                Date date = format.parse(record.get("ORD_CLOSED_DT"));
                PriceData priceData = new PriceData(date, price);
                if (!data.containsKey(itemId)) {
                    List<PriceData> pricesList = new ArrayList<>();
                    pricesList.add(priceData);
                    data.put(itemId, pricesList);
                } else {
                    data.get(itemId).add(priceData);
                }
            }
        }
        return data;
    }
}