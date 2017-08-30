package org.cytoscape.io.internal;

import org.cytoscape.application.CyApplicationConfiguration;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.io.internal.org.cytoscape.io.internal.cxclient.CXServiceClient;
import org.cytoscape.io.internal.org.cytoscape.io.internal.cxclient.RemoteLayoutTaskFactory;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.property.CyProperty;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.work.TaskManager;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;


public class CyActivator extends AbstractCyActivator {
	
	// Logger for this activator
	private static final Logger logger = LoggerFactory.getLogger(CyActivator.class);

	public void start(BundleContext bc) {

		final ViewWriterFactoryManager viewWriterManager = new ViewWriterFactoryManager();

		registerServiceListener(bc, viewWriterManager, "addFactory", "removeFactory",
				CyNetworkViewWriterFactory.class);


		final CXServiceClient client = new CXServiceClient(viewWriterManager);


		// Import dependencies
		final CySwingApplication desktop = getService(bc, CySwingApplication.class);
		final CyApplicationConfiguration config = getService(bc, CyApplicationConfiguration.class);
		final CyApplicationManager appManager = getService(bc, CyApplicationManager.class);
		final CyEventHelper eventHelper = getService(bc, CyEventHelper.class);
		final TaskManager<?, ?> tm = getService(bc, TaskManager.class);

		@SuppressWarnings("unchecked")
		final CyProperty<Properties> cyProp = getService(bc, CyProperty.class, "(cyPropertyName=cytoscape3.props)");


		Properties remoteLayoutTaskFactoryProps = new Properties();
		remoteLayoutTaskFactoryProps.setProperty(PREFERRED_MENU, "Layout");
		remoteLayoutTaskFactoryProps.setProperty(TITLE, "Remote layout");

		RemoteLayoutTaskFactory remoteLayoutTaskFactory = new RemoteLayoutTaskFactory(client);
		registerService(bc, remoteLayoutTaskFactory, NetworkViewTaskFactory.class, remoteLayoutTaskFactoryProps);

	}

	@Override
	public void shutDown() {
	}



}