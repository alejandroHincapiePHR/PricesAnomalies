package Product.PriceAnomalyDetection.errorHandling;

import Product.PriceAnomalyDetection.commons.CustomResponse;
import Product.PriceAnomalyDetection.constant.MessageConstants;
import Product.PriceAnomalyDetection.errorHandling.exceptions.ProductNotFoundException;
import Product.PriceAnomalyDetection.model.ItemPriceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<ItemPriceResponse> productNotFoundException(ProductNotFoundException exc) {

        CustomResponse customResponse = new CustomResponse();
        customResponse.setMessage(MessageConstants.MSG_RESPONSE_NOT_FOUND);

        ItemPriceResponse itemPriceResponse = new ItemPriceResponse();
        itemPriceResponse.setStatus_code("404");
        itemPriceResponse.setMetadata(customResponse);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(itemPriceResponse);

    }

}
