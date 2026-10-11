package rw.kbms.audit;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
@Document(collection="audit_events") public class AuditEvent {
 @Id public String id;
 public String action;
 public String actor;
 public String details;
 public Instant createdAt=Instant.now();
 public AuditEvent(){}
 public AuditEvent(String action,String actor,String details){this.action=action;this.actor=actor;this.details=details;}
}
