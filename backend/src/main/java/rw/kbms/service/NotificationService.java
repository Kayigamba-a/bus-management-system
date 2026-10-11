package rw.kbms.service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import rw.kbms.config.RabbitConfig;
@Service public class NotificationService {
 private final RabbitTemplate rabbit;
 public NotificationService(RabbitTemplate rabbit){this.rabbit=rabbit;}
 public void send(String message){rabbit.convertAndSend(RabbitConfig.QUEUE,message);}
 @RabbitListener(queues=RabbitConfig.QUEUE)
 public void handle(String message){System.out.println("KBMS notification queued (development email/SMS simulation): "+message);}
}
