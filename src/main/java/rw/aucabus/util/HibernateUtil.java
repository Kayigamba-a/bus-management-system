package rw.aucabus.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Creates one shared Hibernate SessionFactory for the application.
 *
 * Database defaults:
 *   database: bus_management_db
 *   host: localhost
 *   port: 5432
 *   user: postgres
 *
 * The database password is intentionally NOT stored in source control.
 * Supply it at runtime using either:
 *   -Ddb.password=yourPassword
 * or environment variable BUS_DB_PASSWORD.
 */
public final class HibernateUtil {

    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");

            String password = System.getProperty("db.password");
            if (password == null) {
                password = System.getenv("BUS_DB_PASSWORD");
            }

            if (password == null) {
                throw new IllegalStateException(
                        "Database password is not configured. " +
                        "Set -Ddb.password in the Tomcat VM options or BUS_DB_PASSWORD in the environment."
                );
            }

            configuration.setProperty("hibernate.connection.password", password);

            System.out.println("====================================================");
            System.out.println("Connecting Hibernate to PostgreSQL:");
            System.out.println("jdbc:postgresql://localhost:5432/bus_management_db");
            System.out.println("User: postgres");
            System.out.println("====================================================");

            SessionFactory sessionFactory = configuration.buildSessionFactory();
            System.out.println("Hibernate SessionFactory started successfully.");
            return sessionFactory;
        } catch (Throwable ex) {
            System.err.println("====================================================");
            System.err.println("HIBERNATE FAILED TO START");
            System.err.println("Check PostgreSQL is running and the runtime database password is configured.");
            System.err.println("====================================================");
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static boolean testConnection() {
        try (Session session = SESSION_FACTORY.openSession()) {
            Object value = session.createNativeQuery("SELECT 1").getSingleResult();
            return value != null;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static void shutdown() {
        if (SESSION_FACTORY != null && !SESSION_FACTORY.isClosed()) {
            SESSION_FACTORY.close();
        }
    }
}
