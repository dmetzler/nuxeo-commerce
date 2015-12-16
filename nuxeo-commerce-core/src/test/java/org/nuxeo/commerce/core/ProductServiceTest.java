package org.nuxeo.commerce.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.commerce.api.model.Product;
import org.nuxeo.commerce.api.service.ProductService;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@Deploy("nuxeo-commerce-core")
public class ProductServiceTest {

    @Inject
    ProductService ps;

    @Inject
    CoreSession session;

    @Before
    public void doBefore() throws Exception {
        SessionHandler.attach(session);
    }

    @Test
    public void it_can_create_and_retrieve_product() throws Exception {
        Product product = ps.createProduct("a simple product");
        assertThat(product).isNotNull();

        product = ps.createProduct(product);

        Optional<Product> p = ps.getProduct(product.getId());

        assertThat(p.isPresent()).isTrue();
        assertThat(product.getDesignation()).isEqualTo("a simple product");

    }
}
