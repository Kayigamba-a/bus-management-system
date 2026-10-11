package rw.kbms.config;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import rw.kbms.repo.*;
import rw.kbms.model.*;
import rw.kbms.model.Role;
import java.time.*;
@Configuration public class SeedData {
 @Bean CommandLineRunner seed(UserRepository users,RouteRepository routes,BusRepository buses,TripRepository trips,PasswordEncoder encoder,@Value("${app.seed-admin-email}") String email,@Value("${app.seed-admin-password}") String pass){return args->{
 if(users.findByEmail(email).isEmpty())users.save(new UserAccount("KBMS Administrator",email,encoder.encode(pass),Role.ADMIN));
 if(routes.count()==0){String[][] data={{"Kigali City Centre","Kimironko","KBS-101"},{"Nyabugogo","Remera","KBS-102"},{"Downtown","Kanombe","KBS-103"},{"Kicukiro","Nyanza","KBS-104"}};for(String[] d:data){BusRoute r=new BusRoute();r.origin=d[0];r.destination=d[1];r.routeCode=d[2];r.durationMinutes=45;r.fareRwf=500;routes.save(r);}}
 if(buses.count()==0){for(int i=1;i<=4;i++){Bus b=new Bus();b.plateNumber="RAD "+(100+i)+" A";b.operatorName=i%2==0?"Kigali Transit":"City Express";b.capacity=30;buses.save(b);}}
 if(trips.count()==0){var bs=buses.findAll();var rs=routes.findAll();for(int i=0;i<4;i++){Trip t=new Trip();t.bus=bs.get(i);t.route=rs.get(i);t.departureTime=LocalDateTime.now().plusDays(1).withHour(7+i*2).withMinute(0);t.arrivalTime=t.departureTime.plusMinutes(rs.get(i).durationMinutes);trips.save(t);}}
 };}
}
