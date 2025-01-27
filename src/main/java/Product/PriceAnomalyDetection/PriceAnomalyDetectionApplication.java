package Product.PriceAnomalyDetection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class PriceAnomalyDetectionApplication {

	public static void main(String[] args) {
		SpringApplication.run(PriceAnomalyDetectionApplication.class, args);
	}

}
