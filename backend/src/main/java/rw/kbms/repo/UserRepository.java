package rw.kbms.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import rw.kbms.model.UserAccount;
public interface UserRepository extends JpaRepository<UserAccount,Long> { java.util.Optional<rw.kbms.model.UserAccount> findByEmail(String email); }
