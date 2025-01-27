package Product.PriceAnomalyDetection.service;


import Product.PriceAnomalyDetection.model.PriceData;
import Product.PriceAnomalyDetection.model.Product;
import Product.PriceAnomalyDetection.repository.IProductRepo;
import Product.PriceAnomalyDetection.service.serviceUtils.dataTransformer.DataTransformer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Data
public class UploadDataServiceImp extends GenericImp<Product, String> implements IUploadDataService {

    private final IProductRepo repo;
    private final DataTransformer dataTransformer;
    private static final Logger logger = LoggerFactory.getLogger(UploadDataServiceImp.class);

    @Autowired
    public UploadDataServiceImp(IProductRepo repo, DataTransformer dataTransformer) {
        this.repo = repo;
        this.dataTransformer = dataTransformer;
    }


    @Override
    public void processFile(MultipartFile file) throws IOException, IllegalArgumentException, ParseException {
        Map<String, List<PriceData>> data = new HashMap<>();

        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : csvParser) {
                try {
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
                } catch (IllegalArgumentException e) {
                    throw e;
                }

            }
        } catch (IOException e) {
            throw e;
        }

        data.forEach((key, value) -> {
            Product product = dataTransformer.transformToProduct(key, value);
            save(product);
        });

    }


}
