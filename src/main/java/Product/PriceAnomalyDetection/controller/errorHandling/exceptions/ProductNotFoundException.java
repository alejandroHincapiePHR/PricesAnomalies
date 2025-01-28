package Product.PriceAnomalyDetection.controller.errorHandling.exceptions;

public class ProductNotFoundException extends RuntimeException{

    public ProductNotFoundException(){
        super();
    }

    public ProductNotFoundException(String message){
        super(message);
    }



}
