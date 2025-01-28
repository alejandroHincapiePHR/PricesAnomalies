package Product.PriceAnomalyDetection.service.uploadService;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;

public interface IUploadDataService {

    void processFile(MultipartFile file) throws IOException, ParseException;
}
