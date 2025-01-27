package Product.PriceAnomalyDetection.service;

import Product.PriceAnomalyDetection.errorHandling.exceptions.ProductNotFoundException;
import Product.PriceAnomalyDetection.repository.IGenericRepo;

import java.util.NoSuchElementException;

public abstract class GenericImp<T, ID> implements IGenericService<T, ID> {

    protected abstract IGenericRepo<T, ID> getRepo();

    @Override
    public T findById(ID id) {
        return getRepo().findById(id).orElseThrow(ProductNotFoundException::new);
    }

    @Override
    public T save(T t){
        return getRepo().save(t);
    }


}
