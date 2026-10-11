package rw.kbms.config;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
@Configuration public class BookingSchemaUpgrade {
 @Bean ApplicationRunner upgradeBookingConstraints(JdbcTemplate jdbc){return args->jdbc.execute("""
 DO $$ DECLARE c_name text; BEGIN
 FOR c_name IN
 SELECT c.conname FROM pg_constraint c JOIN pg_class t ON t.oid=c.conrelid
 WHERE t.relname='bookings' AND c.contype='u' AND
 (SELECT array_agg(a.attname::text ORDER BY a.attname::text) FROM unnest(c.conkey) k(n)
 JOIN pg_attribute a ON a.attrelid=t.oid AND a.attnum=k.n) = ARRAY['passenger_id','trip_id']::text[]
 LOOP EXECUTE format('ALTER TABLE bookings DROP CONSTRAINT %I',c_name); END LOOP;
 END $$;
 ALTER TABLE bookings ALTER COLUMN passenger_id DROP NOT NULL;
 """);}
}
