package adamatti;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Test;

public abstract class JMSServerTest implements MessageListener{
	protected final Logger logger = Logger.getLogger(this.getClass());
	protected Context ctx;
	protected ConnectionFactory qcf;
	protected Queue queue;
	protected Connection conn;
	protected Session session;
	protected MessageConsumer receiver;
	protected MessageProducer sender = null;
	protected Thread publisher = null;

	@After
	public void tearDown() throws Throwable{
		publisher.stop();
		publisher.interrupt();
		receiver.close();
		if (sender!=null) 
			sender.close();
		session.close();
		conn.close();
		ctx.close();
	}
	protected void createSubscriber() throws Throwable{
		receiver.setMessageListener(this);
		logger.debug("Subscriber created!");
	}
	protected void createPublisher() throws Throwable{			
		publisher = new Thread(){
			@Override public void run() {
				while (true){
					try {
						String txt = "<xml>" + System.currentTimeMillis() + "</xml>";
						Message msg = session.createTextMessage(txt);
						sender.send(msg);
						logger.debug("Msg sent: " + txt);						
						Thread.sleep(1000);
					} catch (ThreadDeath t){
						//DO NOTHING
						break;
					} catch (Throwable t){
						logger.error("Error: " + t.getMessage(),t);
						break;
					}
					
				}
			}			
		};
		publisher.start();
		//t.join();
		logger.debug("Publisher created");
	}
	@Test
	public void test() throws Throwable {		
		IOUtils.waitEnter();		
	}
	@Override
	public void onMessage(Message msg) {		
		try {
			TextMessage txt = (TextMessage) msg;
			logger.debug("Msg received: " + txt.getText());
		} catch (JMSException t) {
			logger.error("Error: " + t.getMessage(),t);
		}
	}
}
