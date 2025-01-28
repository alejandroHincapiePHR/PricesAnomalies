package Product.PriceAnomalyDetection.service.productService;

import Product.PriceAnomalyDetection.model.PriceData;
import Product.PriceAnomalyDetection.model.Product;
import Product.PriceAnomalyDetection.repository.IGenericRepo;
import Product.PriceAnomalyDetection.repository.IProductRepo;
import Product.PriceAnomalyDetection.service.genericService.GenericImp;
import Product.PriceAnomalyDetection.service.serviceUtils.anomalyDetection.AnomalyDetection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class ProductService extends GenericImp<Product, String> implements IProductService {
    private final IProductRepo repo;
    private final AnomalyDetection anomalyDetection;

    @Autowired
    public ProductService(IProductRepo repo, AnomalyDetection anomalyDetection) {
        this.repo = repo;
        this.anomalyDetection = anomalyDetection;
    }

    @Override
    protected IGenericRepo<Product, String> getRepo() {
        return repo;
    }

    /**
     * Detects whether a price is an anomaly for a given product and classifies it accordingly.
     *
     * <p>This method retrieves a product by its {@code id} and uses the anomaly detection service
     * to determine if the provided price is an anomaly. If it is an anomaly, the price is added
     * to the product's outliers list; otherwise, it is added to the non-outliers list.</p>
     *
     * <p>The result of the anomaly detection, along with the corresponding date and price,
     * is saved back to the database.</p>
     *
     * @param id The unique identifier of the product to analyze.
     * @param price The price to evaluate for anomaly detection.
     * @return {@code "true"} if the price is considered an anomaly, {@code "false"} otherwise.
     */
    @Cacheable("isAnomaly")
    @Override
    public String isAnomaly(String id, BigDecimal price) {
        Product product = findById(id);
        Boolean isAnomaly = Boolean.valueOf(anomalyDetection.isAnomaly(product.getNonOutliers(), price));
        Date date = new Date();
        if(isAnomaly){
            product.getOutliers().add(new PriceData(date, price));
        }else {
            product.getNonOutliers().add(new PriceData(date, price));
        }

        save(product);

        return String.valueOf(isAnomaly);
    }


}
