package Product.PriceAnomalyDetection.controller;

import Product.PriceAnomalyDetection.commons.CustomResponse;
import Product.PriceAnomalyDetection.constant.MessageConstants;
import Product.PriceAnomalyDetection.errorHandling.exceptions.ProductNotFoundException;
import Product.PriceAnomalyDetection.model.ItemPriceRequest;
import Product.PriceAnomalyDetection.model.ItemPriceResponse;
import Product.PriceAnomalyDetection.model.Product;
import Product.PriceAnomalyDetection.service.IProductService;
import Product.PriceAnomalyDetection.service.IUploadDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

import static Product.PriceAnomalyDetection.constant.ApiGlobalConstant.*;

@RestController
@RequestMapping(API_PRODUCT)
public class ProductController {

    private final IProductService productService;
    private final IUploadDataService uploadDataService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    public ProductController(IProductService productService, IUploadDataService uploadDataService) {
        this.productService = productService;
        this.uploadDataService = uploadDataService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable("id") String id) {

        logger.info(productService.findById(id).toString());

        Product product = productService.findById(id);


        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(product);
    }


    @PostMapping("/")
    public ResponseEntity<ItemPriceResponse> isAnomaly(@RequestBody ItemPriceRequest request) throws ProductNotFoundException {

        String itemId = request.getItem_id();
        BigDecimal newPrice = request.getPrice();
        Boolean isAnomaly = productService.isAnomaly(itemId, newPrice);

        CustomResponse customResponse = new CustomResponse();
        customResponse.setMessage(MessageConstants.MSG_RESPONSE_OK);

        ItemPriceResponse itemPriceResponse = new ItemPriceResponse();
        itemPriceResponse.setItem_id(itemId);
        itemPriceResponse.setPrice(newPrice);
        itemPriceResponse.setAnomaly(isAnomaly);
        itemPriceResponse.setMetadata(customResponse);
        itemPriceResponse.setStatus_code(String.valueOf(HttpStatus.OK.value()));


        HttpHeaders headers = new HttpHeaders();
        headers.add("API-Method", API_METHOD_IS_ANOMALY);
        headers.add("API-Version", API_VERSION);
        headers.add("Content-Type", API_CONTENT_TYPE);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .body(itemPriceResponse);
    }


    @PostMapping("/upload")
    public ResponseEntity<String> uploadData(@RequestParam("file") MultipartFile file) throws IOException {
        uploadDataService.processFile(file);

        return ResponseEntity.status(HttpStatus.OK).body("Uploaded");

    }


}
