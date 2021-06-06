package com.es.phoneshop.model.product;

import com.es.phoneshop.model.product.dao.DaoFactory;
import com.es.phoneshop.model.product.dao.ProductDao;
import com.es.phoneshop.model.product.dao.searchparam.SearchParams;
import com.es.phoneshop.model.product.dao.searchparam.SortOrder;
import com.es.phoneshop.model.product.dao.searchparam.SortParam;
import com.es.phoneshop.model.product.exception.ProductNotFoundException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class ArrayListProductDaoTest {
    private static ProductDao productDao;

    @BeforeClass
    public static void setup() {
        productDao = DaoFactory.getInstance().getProductDaoImpl();
    }

    @BeforeClass
    public static void fillProductDao() {
        Currency usd = Currency.getInstance("USD");
        productDao.save(new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
        productDao.save(new Product("iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg"));
        productDao.save(new Product("htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg"));
        productDao.save(new Product("sec901", "Sony Ericsson C901", new BigDecimal(420), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Ericsson%20C901.jpg"));
        productDao.save(new Product("xperiaxz", "Sony Xperia XZ", new BigDecimal(120), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Xperia%20XZ.jpg"));
        productDao.save(new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg"));
        productDao.save(new Product("nokia3310", "Nokia 3310", new BigDecimal(70), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Nokia/Nokia%203310.jpg"));
        productDao.save(new Product("iphone6", "Apple iPhone 6", new BigDecimal(1000), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone%206.jpg"));
        productDao.save(new Product("palmp", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg"));
        productDao.save(new Product("simc56", "Siemens C56", new BigDecimal(70), usd, 20, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C56.jpg"));
        productDao.save(new Product("sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg"));
        productDao.save(new Product("simc61", "Siemens C61", new BigDecimal(80), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C61.jpg"));
        productDao.save(new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg"));
    }

    @Test
    public void testFindProducts() {
        assertFalse(productDao.findProducts().isEmpty());
    }

    @Test
    public void testFindProductsByQuery() {
        String query = "Samsung";
        SearchParams params = new SearchParams(query);

        List<Product> result = productDao.findProducts(params);

        assertEquals(2, result.size());
    }

    @Test
    public void testSortProductByDesc() {
        SearchParams params = new SearchParams(SortOrder.ASCEND, SortParam.DESC);
        List<Product> products = productDao.findProducts(params);
        List<Product> expected = productDao.findProducts();

        expected.sort(Comparator.comparing(Product::getDescription));

        assertEquals(expected, products);
    }

    @Test
    public void testSortProductByPrice() {
        SearchParams params = new SearchParams(SortOrder.DESCEND, SortParam.PRICE);
        List<Product> products = productDao.findProducts(params);
        List<Product> expected = productDao.findProducts();

        expected.sort(Comparator.comparing(Product::getPrice, Comparator.reverseOrder()));

        assertEquals(expected, products);
    }

    @Test
    public void testGetProduct() throws ProductNotFoundException {
        assertNotNull(productDao.getProduct(3L));
    }

    @Test(expected = ProductNotFoundException.class)
    public void testGetProductException() throws ProductNotFoundException {
        productDao.getProduct(null).orElseThrow(() -> new ProductNotFoundException((Long) null));
    }

    @Test
    public void testSaveProduct() throws ProductNotFoundException {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product(null, "test-code", "test-description", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");

        productDao.save(product);

        assertTrue(product.getId() > 0);

        Optional<Product> expected = productDao.getProduct(product.getId());

        assertNotNull(expected);

        assertEquals("test-code", expected.get().getCode());
    }

    @Test
    public void testSaveAndUpdateProduct() throws ProductNotFoundException {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product(null, "sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");

        productDao.save(product);

        Product productUpd = new Product(null, "test-codeUpdate", "test-description", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productUpd.setId(product.getId());

        productDao.save(productUpd);

        assertEquals("test-codeUpdate", productDao.getProduct(product.getId()).get().getCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateProductWithNegativeId() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product(-1L, "sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");

        productDao.save(product);
    }

    @Test(expected = ProductNotFoundException.class)
    public void testDeleteProduct() throws ProductNotFoundException {
        productDao.getProduct(1L);

        productDao.delete(1L);

        productDao.getProduct(1L).orElseThrow(() -> new ProductNotFoundException(1L));
    }
}

