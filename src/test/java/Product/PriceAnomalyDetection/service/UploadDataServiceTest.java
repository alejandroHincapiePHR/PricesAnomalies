package Product.PriceAnomalyDetection.service;

import Product.PriceAnomalyDetection.model.Product;
import Product.PriceAnomalyDetection.repository.IProductRepo;
import Product.PriceAnomalyDetection.service.serviceUtils.dataTransformer.DataTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UploadDataServiceTest {

    @Mock
    private IProductRepo repo;

    @Mock
    private DataTransformer dataTransformer;

    private UploadDataServiceImp uploadDataService;

    @BeforeEach
    public void setUp() {
        uploadDataService = new UploadDataServiceImp(repo, dataTransformer);
    }

    @Test
    public void testProcessFileWithValidCSV() throws IOException, ParseException {

        String csvContent = "ITEM_ID,PRICE,ORD_CLOSED_DT\n1,10.5,2023-10-01";
        MultipartFile file = new MockMultipartFile("file.csv", csvContent.getBytes());

        Product mockProduct = new Product();
        when(dataTransformer.transformToProduct(anyString(), anyList())).thenReturn(mockProduct);

        uploadDataService.processFile(file);

        verify(repo, times(1)).save(mockProduct);
    }

    @Test
    public void testProcessFileWithLargeCSV() throws IOException, ParseException {

        StringBuilder csvContent = new StringBuilder("ITEM_ID,PRICE,ORD_CLOSED_DT\n");
        for (int i = 1; i <= 1000; i++) {
            csvContent.append(i).append(",").append(i * 10).append(",2023-10-01\n");
        }
        MultipartFile file = new MockMultipartFile("file.csv", csvContent.toString().getBytes());
        when(dataTransformer.transformToProduct(anyString(), anyList())).thenReturn(new Product());
        uploadDataService.processFile(file);
        verify(repo, times(1000)).save(any(Product.class));
    }


    @Test
    public void testProcessFileWithNullValues() throws IOException, ParseException {

        String csvContent = "ITEM_ID,PRICE,ORD_CLOSED_DT\n1,,2023-10-01";
        MultipartFile file = new MockMultipartFile("file.csv", csvContent.getBytes());
        assertThrows(IllegalArgumentException.class, () -> {
            uploadDataService.processFile(file);
        });


    }

    @Test
    public void testProcessFileWithMissingValues() throws IOException, ParseException {
        String csvContent = "ITEM_ID,PRICE\n1,10.5";
        MultipartFile file = new MockMultipartFile("file.csv", csvContent.getBytes());
        assertThrows(IllegalArgumentException.class, () -> {
            uploadDataService.processFile(file);
        });
    }

}
