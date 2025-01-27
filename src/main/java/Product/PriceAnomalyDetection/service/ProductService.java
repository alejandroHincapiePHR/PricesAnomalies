package Product.PriceAnomalyDetection.service;

import Product.PriceAnomalyDetection.model.PriceData;
import Product.PriceAnomalyDetection.model.Product;
import Product.PriceAnomalyDetection.repository.IGenericRepo;
import Product.PriceAnomalyDetection.repository.IProductRepo;
import Product.PriceAnomalyDetection.service.serviceUtils.anomalyDetection.AnomalyDetection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
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
