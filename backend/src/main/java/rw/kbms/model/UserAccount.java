package rw.kbms.model;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity @Table(name="users",uniqueConstraints=@UniqueConstraint(columnNames="email"))
public class UserAccount {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false) public String name;
 @Column(nullable=false,unique=true) public String email;
 @JsonIgnore @Column(nullable=false) public String passwordHash;
 @Enumerated(EnumType.STRING) @Column(nullable=false) public Role role=Role.PASSENGER;
 public UserAccount(){}
 public UserAccount(String name,String email,String hash,Role role){this.name=name;this.email=email;this.passwordHash=hash;this.role=role;}
}
