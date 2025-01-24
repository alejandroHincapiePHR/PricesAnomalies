package Product.PriceAnomalyDetection.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IUploadDataService {

    void processFile(MultipartFile file) throws IOException;
}
