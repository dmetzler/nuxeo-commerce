package org.nuxeo.commerce.core;

import java.text.Normalizer;
import java.util.Optional;

import org.nuxeo.commerce.api.model.Product;
import org.nuxeo.commerce.api.service.ProductService;
import org.nuxeo.commerce.core.model.ProductImpl;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;

public class ProductServiceImpl implements ProductService {

    @Override
    public Optional<Product> getProduct(String id) {
        CoreSession session = SessionHandler.getSession();
        IdRef docRef = new IdRef(id);
        if (session.exists(docRef)) {
            return Optional.ofNullable(session.getDocument(docRef)
                                              .getAdapter(Product.class));
        } else {
            return Optional.empty();
        }

    }

    @Override
    public Product createProduct(String designation) {
        return new ProductImpl(SessionHandler.getSession(), designation);
    }

    @Override
    public Product createProduct(Product product) {
        CoreSession session = SessionHandler.getSession();
        if (product instanceof ProductImpl) {
            ProductImpl p = (ProductImpl) product;
            p.getDocument().setPathInfo(getProductsRoot().getPathAsString(), normalizeName(product.getDesignation()));
            DocumentModel doc = session.createDocument(p.getDocument());
            return doc.getAdapter(Product.class);
        } else {
            throw new RuntimeException(
                    String.format("Unable to persist other product that %s", ProductImpl.class.getCanonicalName()));
        }

    }

    private DocumentModel getProductsRoot() {
        CoreSession session = SessionHandler.getSession();
        PathRef rootRef = new PathRef("/products");
        if (!session.exists(rootRef)) {
            return session.createDocument(session.createDocumentModel("/", "products", "ProductRoot"));
        } else {
            return session.getDocument(rootRef);
        }

    }

    private static String normalizeName(String name) {
        name = Normalizer.normalize(name, Normalizer.Form.NFD);
        return name.replaceAll("[^\\p{ASCII}]", "");
    }

}
