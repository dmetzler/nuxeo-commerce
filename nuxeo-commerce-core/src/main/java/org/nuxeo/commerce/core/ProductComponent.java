package org.nuxeo.commerce.core;

import org.nuxeo.commerce.api.service.ProductService;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;

public class ProductComponent extends DefaultComponent {

    private ProductServiceImpl productService;

    @Override
    public void activate(ComponentContext context) {
        productService = new ProductServiceImpl();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAdapter(Class<T> adapter) {
        if (adapter.equals(ProductService.class)) {
            return (T) productService;
        }
        return super.getAdapter(adapter);
    }
}
