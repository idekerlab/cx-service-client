package org.cytoscape.io.internal;

import org.cytoscape.application.CyApplicationConfiguration;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.io.internal.cxclient.CXServiceClient;
import org.cytoscape.io.internal.cxclient.RemoteAttributeTaskFactory;
import org.cytoscape.io.internal.cxclient.RemoteGraphTaskFactory;
import org.cytoscape.io.internal.cxclient.RemoteLayoutTaskFactory;
import org.cytoscape.io.internal.reader.LoadNetworkStreamTaskFactoryImpl;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskFactory;
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

		final IOFactoryManager ioManager = new IOFactoryManager();

		registerServiceListener(bc, ioManager, "addFactory", "removeFactory",
				CyNetworkViewWriterFactory.class);
		registerServiceListener(bc, ioManager, "addReaderFactory",
				"removeReaderFactory", InputStreamTaskFactory.class);





		// Import dependencies
		final NetworkTaskFactory fitContent = getService(bc, NetworkTaskFactory.class, "(title=Fit Content)");

		final CySwingApplication desktop = getService(bc, CySwingApplication.class);
		final CyApplicationConfiguration config = getService(bc, CyApplicationConfiguration.class);
		final CyApplicationManager appManager = getService(bc, CyApplicationManager.class);
		final CyEventHelper eventHelper = getService(bc, CyEventHelper.class);
		final TaskManager<?, ?> tm = getService(bc, TaskManager.class);

		@SuppressWarnings("unchecked")
		final CyProperty<Properties> cyProp = getService(bc, CyProperty.class, "(cyPropertyName=cytoscape3.props)");


		CyNetworkFactory networkFactory = getService(bc, CyNetworkFactory.class);

		// Create dummy network
		final CyNetwork DUMMY = networkFactory.createNetwork();

		CyNetworkManager netmgr = getService(bc, CyNetworkManager.class);
		CyNetworkViewManager networkViewManager = getService(bc, CyNetworkViewManager.class);
		CyServiceRegistrar serviceRegistrar = getService(bc, CyServiceRegistrar.class);
		CyNetworkNaming cyNetworkNaming = getService(bc, CyNetworkNaming.class);
		VisualMappingManager vmm = getService(bc, VisualMappingManager.class);
		final CyNetworkViewFactory nullNetworkViewFactory = getService(bc, CyNetworkViewFactory.class);

		LoadNetworkStreamTaskFactoryImpl loadNetworkTF = new LoadNetworkStreamTaskFactoryImpl(netmgr, networkViewManager, cyProp,
				cyNetworkNaming, vmm, nullNetworkViewFactory, serviceRegistrar);

		final CXServiceClient client = new CXServiceClient(ioManager, loadNetworkTF, DUMMY);


		Properties remoteLayoutTaskFactoryProps = new Properties();
		remoteLayoutTaskFactoryProps.setProperty(PREFERRED_MENU, "Layout");
		remoteLayoutTaskFactoryProps.setProperty(TITLE, "Call remote layout service...");

		RemoteLayoutTaskFactory remoteLayoutTaskFactory = new RemoteLayoutTaskFactory(client, fitContent);
		registerService(bc, remoteLayoutTaskFactory, NetworkViewTaskFactory.class, remoteLayoutTaskFactoryProps);

		Properties remoteAttributeTaskFactoryProps = new Properties();
		remoteAttributeTaskFactoryProps.setProperty(PREFERRED_MENU, "Tools");
		remoteAttributeTaskFactoryProps.setProperty(TITLE, "Call remote attribute generator service...");

		RemoteAttributeTaskFactory remoteAttributeTaskFactory = new RemoteAttributeTaskFactory(client);
		registerService(bc, remoteAttributeTaskFactory, NetworkTaskFactory.class, remoteAttributeTaskFactoryProps);

		Properties loadTaskFactoryProps = new Properties();
		loadTaskFactoryProps.setProperty(PREFERRED_MENU, "Tools");
		loadTaskFactoryProps.setProperty(TITLE, "Call remote graph generator service...");

		RemoteGraphTaskFactory remoteGraphTaskFactory = new RemoteGraphTaskFactory(client);
		registerService(bc,remoteGraphTaskFactory, TaskFactory.class, loadTaskFactoryProps);

	}

	@Override
	public void shutDown() {
	}
}