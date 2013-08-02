package adamatti;

import static org.junit.Assert.*;
import java.util.Hashtable;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.activemq.broker.BrokerService;
import org.junit.After;
import org.junit.Before;
/**
 * 
 * @author Marcelo_Adamatti
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
public class ActiveMQTest extends JMSServerTest{
	private BrokerService broker;
	@Before
	public void setUp() throws Throwable {
		broker = new BrokerService();		 
		// configure the broker
		broker.addConnector("tcp://localhost:61616");		
		broker.start();
		createCtx();
		createSubscriber();
		createPublisher();
	}
	@After
	public void tearDown() throws Throwable{
		super.tearDown();
		broker.stop();
	}
	
	private void createCtx() throws Throwable{
		Hashtable map = new Hashtable();
		map.put(Context.PROVIDER_URL, "tcp://localhost:61616");
		map.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		ctx =  new InitialContext(map);	
		qcf = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		
		conn =  qcf.createConnection();
		conn.start();
		session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		queue = session.createQueue("foo");
		queue = (Queue) ctx.lookup("dynamicQueues/foo"); //NOT REQUIRED, JUST TO TEST THE LOOKUP
		receiver = session.createConsumer(queue);
		sender = session.createProducer(queue);
		sender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);		
	}
}
