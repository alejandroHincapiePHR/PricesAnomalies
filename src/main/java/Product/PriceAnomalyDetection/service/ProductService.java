package Product.PriceAnomalyDetection.service;

import Product.PriceAnomalyDetection.model.Product;
import Product.PriceAnomalyDetection.repository.IGenericRepo;
import Product.PriceAnomalyDetection.repository.IProductRepo;
import Product.PriceAnomalyDetection.service.anomalyDetection.AnomalyDetection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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

    @Override
    public Boolean isAnomaly(String id, BigDecimal price) {
        Product product = findById(id);
        return anomalyDetection.isAnomaly(product.getNonOutliers(), price);
    }


}
