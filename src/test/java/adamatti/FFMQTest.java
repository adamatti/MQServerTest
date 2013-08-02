package adamatti;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Hashtable;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;

import net.timewalker.ffmq3.FFMQCoreSettings;
import net.timewalker.ffmq3.listeners.ClientListener;
import net.timewalker.ffmq3.listeners.tcp.io.TcpListener;
import net.timewalker.ffmq3.local.FFMQEngine;
import net.timewalker.ffmq3.management.destination.definition.QueueDefinition;
import net.timewalker.ffmq3.utils.Settings;
import org.junit.After;
import org.junit.Before;

/**
 * http://ffmq.sourceforge.net/doc.html#IV_2
 * 
 * @author Marcelo_Adamatti
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
public class FFMQTest extends JMSServerTest  {	
	
	
	private FFMQEngine engine;
	private ClientListener tcpListener;
	@Before
	public void setUp() throws Throwable{
		 Settings settings = createEngineSettings();
		 engine = new FFMQEngine("myLocalEngineName", settings);
		 engine.deploy();
		 
		 logger.debug("Starting listener ...");
         tcpListener = new TcpListener(engine,"0.0.0.0",10002,settings,null);
         tcpListener.start();
		 
		 createQueue();
		 createCtx();
		 createSubscriber();
		 createPublisher();
	}
	@After
	public void tearDown() throws Throwable{
		super.tearDown();
		 
		logger.debug("Stopping listener ...");
        tcpListener.stop();
        
        logger.debug("Undeploying engine ...");
        engine.undeploy();
        
        logger.debug("Done.");
	}
		
	private void createCtx() throws Throwable{
		Hashtable map = new Hashtable();
		map.put(Context.PROVIDER_URL, "tcp://localhost:10002");
		map.put(Context.INITIAL_CONTEXT_FACTORY, "net.timewalker.ffmq3.jndi.FFMQInitialContextFactory");
		ctx =  new InitialContext(map);		
		qcf = (ConnectionFactory) ctx.lookup("factory/ConnectionFactory");
		queue = (Queue ) ctx.lookup("queue/foo");
		conn =  qcf.createConnection();
		conn.start();
		session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		receiver = session.createConsumer(queue);
		sender = session.createProducer(queue);
		sender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
	}	
	private void createQueue() throws Throwable{
		if (!engine.getDestinationDefinitionProvider().hasQueueDefinition("foo")) {
            QueueDefinition queueDef = new QueueDefinition();
            queueDef.setName("foo");
            queueDef.setUseJournal(false);
            queueDef.setMaxNonPersistentMessages(1000);
            queueDef.check();
            engine.createQueue(queueDef);
        }
	}
	private Settings createEngineSettings() {
		Settings settings = new Settings();
		String f = "build/jms";
		new File(f).delete();
		new File(f).mkdirs();
		settings.setStringProperty(FFMQCoreSettings.DESTINATION_DEFINITIONS_DIR, f);
		settings.setStringProperty(FFMQCoreSettings.BRIDGE_DEFINITIONS_DIR, f);
		settings.setStringProperty(FFMQCoreSettings.TEMPLATES_DIR, f);
		settings.setStringProperty(FFMQCoreSettings.DEFAULT_DATA_DIR, f);
		return settings;
	}
	
}
