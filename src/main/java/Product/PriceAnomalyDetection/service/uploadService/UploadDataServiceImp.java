package Product.PriceAnomalyDetection.service.uploadService;


import Product.PriceAnomalyDetection.model.PriceData;
import Product.PriceAnomalyDetection.model.Product;
import Product.PriceAnomalyDetection.repository.IGenericRepo;
import Product.PriceAnomalyDetection.repository.IProductRepo;
import Product.PriceAnomalyDetection.service.genericService.GenericImp;
import Product.PriceAnomalyDetection.service.serviceUtils.dataTransformer.DataTransformer;
import Product.PriceAnomalyDetection.service.serviceUtils.processFile.ProcessFile;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@Service
@Data
public class UploadDataServiceImp extends GenericImp<Product, String> implements IUploadDataService {

    private final IProductRepo repo;
    private final DataTransformer dataTransformer;
    private final ProcessFile processFile;

    @Autowired
    public UploadDataServiceImp(IProductRepo repo, DataTransformer dataTransformer, ProcessFile processFile) {
        this.repo = repo;
        this.dataTransformer = dataTransformer;
        this.processFile = processFile;
    }

    @Override
    protected IGenericRepo<Product, String> getRepo() {
        return repo;
    }

    /**
     * Processes the provided file and transforms its data into product objects, which are then saved.
     *
     * <p>This method accepts a file, processes its data to extract relevant information, and then
     * transforms the extracted data into {@link Product} objects. For each product, the corresponding
     * data is used to create a list of {@link PriceData}. The product is then saved to the database.</p>
     *
     * <p>The method performs the following steps:
     * <ol>
     *     <li>Processes the file to extract data into a map, where the key is the product identifier
     *         and the value is a list of price data.</li>
     *     <li>For each product, the data is transformed into a {@link Product} object.</li>
     *     <li>The transformed product is saved to the database.</li>
     * </ol></p>
     *
     * @param file The file containing the data to be processed.
     * @throws IOException If an I/O error occurs while processing the file.
     * @throws IllegalArgumentException If the file content is invalid or cannot be parsed correctly.
     * @throws ParseException If there is an error while parsing the data from the file.
     */
    @Override
    public void processFile(MultipartFile file) throws IOException, IllegalArgumentException, ParseException {
        Map<String, List<PriceData>> data = processFile.processData(file);
        data.forEach((key, value) -> {
            Product product = dataTransformer.transformToProduct(key, value);
            save(product);
        });

    }


}
