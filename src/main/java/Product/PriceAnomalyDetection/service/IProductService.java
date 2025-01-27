package Product.PriceAnomalyDetection.service;

import Product.PriceAnomalyDetection.model.Product;

import java.math.BigDecimal;

public interface IProductService extends IGenericService<Product, String>{

    String isAnomaly(String id, BigDecimal price);
}
