package Product.PriceAnomalyDetection.service;

public interface IGenericService<T,ID> {

    T findById(ID id);
}
