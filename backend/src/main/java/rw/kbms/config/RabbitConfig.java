package rw.kbms.config;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.*;
@Configuration public class RabbitConfig {
 public static final String QUEUE="kbms.notifications";
 @Bean Queue notifications(){return QueueBuilder.durable(QUEUE).build();}
}
