package rw.kbms.audit;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface AuditRepository extends MongoRepository<AuditEvent,String>{}
