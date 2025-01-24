package Product.PriceAnomalyDetection.service;

import Product.PriceAnomalyDetection.controller.ProductController;
import Product.PriceAnomalyDetection.repository.IProductRepo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Data
public class UploadDataServiceImp implements IUploadDataService {

    private final IProductRepo repo;
    private static final Logger logger = LoggerFactory.getLogger(UploadDataServiceImp.class);
    @Autowired
    public UploadDataServiceImp(IProductRepo repo) {
        this.repo = repo;
    }


    @Override
    public void processFile(MultipartFile file) throws IOException, IllegalArgumentException {
        Map<String, List<BigDecimal>> data = new HashMap<>();

        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : csvParser) {
                try {
                    String itemId = record.get("ITEM_ID");
                    BigDecimal price = BigDecimal.valueOf(Long.parseLong(record.get("PRICE")));
                    if (!data.containsKey(itemId)) {
                        List<BigDecimal> pricesList = new ArrayList<>();
                        pricesList.add(price);
                        data.put(itemId, pricesList);
                    } else {
                        data.get(itemId).add(price);
                    }
                } catch (IllegalArgumentException e) {
                    throw e;
                }

            }
        } catch (IOException e) {
            throw e;
        }


        logger.info(data.toString());

    }


}
