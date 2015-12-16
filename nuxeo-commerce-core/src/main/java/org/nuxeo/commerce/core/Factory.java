package org.nuxeo.commerce.core;

import org.nuxeo.commerce.api.model.Product;
import org.nuxeo.commerce.core.model.ProductImpl;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

public class Factory implements DocumentAdapterFactory{

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> itf) {
        if(Product.class.equals(itf) && ProductImpl.DOCTYPE.equals(doc.getType())) {
            return new ProductImpl(doc);
        }
        return null;
    }

}
