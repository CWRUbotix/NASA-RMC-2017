import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;


public class MotorLayer {
 
  private final static String QUEUE_NAME = "motorhighlevel";

  public static void main (String[] args) 
  throws java.io.IOException, java.lang.InterruptedException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.sethost("localhost");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    System.out.println("Waiting for message. CTRL+C to exit");

    Consumer consumer = new DefaultConsumer(channel) {
    @Override
    public void handleDelivery (String consumerTag, Envelope envelope, 
    AMQP.BasicProperties properties, byte[] body) throws IOException {
      String message = new String (body, "UTF-8");
}
