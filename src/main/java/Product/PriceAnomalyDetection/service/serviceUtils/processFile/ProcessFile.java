package Product.PriceAnomalyDetection.service.serviceUtils.processFile;

import Product.PriceAnomalyDetection.model.PriceData;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface ProcessFile {
    Map<String, List<PriceData>> processData(MultipartFile file) throws IOException, ParseException;
}
