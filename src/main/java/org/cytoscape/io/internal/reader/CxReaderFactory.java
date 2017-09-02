package org.cytoscape.io.internal.reader;

import org.cytoscape.io.read.CyNetworkReader;
import org.cytoscape.work.TaskIterator;

public interface CxReaderFactory {
	
	TaskIterator createTaskIterator(final String networkName, final CyNetworkReader reader); 

}
