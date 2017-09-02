package org.cytoscape.io.internal;

import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * Simple OSGi service manager for importing CX Writer object.
 *
 */
public class IOFactoryManager {

    // ID of the CX writer service
    private static final String CX_WRITER_ID = "cxNetworkWriterFactory";
    private static final String CX_READER_ID = "cytoscapeCxNetworkReaderFactory";

    private static final String ID_TAG = "id";

    private final Map<String, CyNetworkViewWriterFactory> factories;

    private InputStreamTaskFactory readerFactory;



    public IOFactoryManager() {
        factories = new HashMap<>();
    }

    public CyNetworkViewWriterFactory getCxFactory() {
        return factories.get(CX_WRITER_ID);
    }

    @SuppressWarnings("rawtypes")
    public void addFactory(final CyNetworkViewWriterFactory factory, final Map properties) {
        final String id = (String) properties.get(ID_TAG);
        if (id != null) {
            factories.put(id, factory);
        }
    }

    @SuppressWarnings("rawtypes")
    public void removeFactory(final CyNetworkViewWriterFactory factory, Map properties) {
        final String id = (String) properties.get(ID_TAG);

        if (id != null) {
            properties.remove(id);
        }
    }

    @SuppressWarnings("rawtypes")
    public void addReaderFactory(final InputStreamTaskFactory factory, final Map properties) {
        final String id = (String) properties.get(ID_TAG);
        if (id != null && id.equals(CX_READER_ID)) {
            readerFactory = factory;
        }
    }

    @SuppressWarnings("rawtypes")
    public void removeReaderFactory(final InputStreamTaskFactory factory, Map properties) {
        final String id = (String) properties.get(ID_TAG);

        if (id != null && id.equals(CX_READER_ID)) {
            readerFactory = null;
        }
    }

    public InputStreamTaskFactory getReaderFactory() {
        if(readerFactory == null) {
            throw new IllegalStateException("CX Reader Factory is not available!");
        }

        return readerFactory;
    }
}
