package org.nuxeo.commerce.api.model;


/**
 * A product is something that one can purchase in our store.
 *
 */
public interface Product {

    /**
     * Return the primary key of the product.
     * @return
     */
    String getId();

    /**
     * Return the label of the product.
     * @return
     */
    String getDesignation();

    void copyFrom(Product product);


}
