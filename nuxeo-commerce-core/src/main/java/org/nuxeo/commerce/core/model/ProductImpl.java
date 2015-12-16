package org.nuxeo.commerce.core.model;

import org.nuxeo.commerce.api.model.Product;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.schema.types.QName;

public class ProductImpl implements Product {

    private static final QName DC_TITLE = new QName("title", "dc");

    public static final String DOCTYPE = "Product";

    private DocumentModel doc;

    public ProductImpl(CoreSession session, String designation) {
        doc = session.createDocumentModel(DOCTYPE);
        setDesignation(designation);
    }

    public DocumentModel getDocument() {
        return doc;
    }

    public ProductImpl(DocumentModel doc) {
        this.doc = doc;

    }

    @Override
    public String getId() {
        return doc.getId();
    }

    @Override
    public String getDesignation() {
        return doc.getProperty(DC_TITLE.getPrefixedName())
                  .getValue(String.class);
    }

    @Override
    public void copyFrom(Product product) {
        setDesignation(product.getDesignation());
    }

    private void setDesignation(String designation) {
        doc.setPropertyValue(DC_TITLE.getPrefixedName(), designation);
    }

}
