package org.nuxeo.commerce.api.service;

import java.util.Optional;

import org.nuxeo.commerce.api.model.Product;

/**
 * This service is responsible of creating/retrieving products.
 *
 */
public interface ProductService {

    /**
     * Returns a product given its id.
     * @param id
     * @return
     */
    Optional<Product> getProduct(String id);


    /**
     * Creates a bare Product model suitable for further creation.
     * see {@link #createProduct(Product)}
     * @param designation
     * @return
     */
    Product createProduct(String designation);

    /**
     * Materialize a product into database. After calling this
     * method, only the returned Product object should be used.
     * @param productModel
     * @return
     */
    Product createProduct(Product productMpdel);


}
