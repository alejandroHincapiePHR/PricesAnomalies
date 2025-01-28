package Product.PriceAnomalyDetection.service.genericService;

public interface IGenericService<T,ID> {

    T findById(ID id);

    T save (T t);
}
