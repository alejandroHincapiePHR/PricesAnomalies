package Product.PriceAnomalyDetection.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IGenericRepo<T,ID> extends MongoRepository<T,ID> {
}
