package rw.aucabus.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import rw.aucabus.model.Bus;
import rw.aucabus.util.HibernateUtil;

import java.io.Serializable;
import java.util.List;

public class BusDao implements Serializable, GenericDao<Bus, Long> {

    private static final long serialVersionUID = 1L;

    @Override
    public void save(Bus bus) {
        executeWrite(session -> session.persist(bus));
    }

    @Override
    public void update(Bus bus) {
        executeWrite(session -> session.merge(bus));
    }

    @Override
    public void delete(Long busId) {
        executeWrite(session -> {
            Bus bus = session.get(Bus.class, busId);
            if (bus != null) {
                session.remove(bus);
            }
        });
    }

    @Override
    public Bus findById(Long busId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Bus.class, busId);
        }
    }

    @Override
    public List<Bus> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Bus b ORDER BY b.busId DESC", Bus.class).getResultList();
        }
    }

    public List<Bus> search(String text) {
        String value = text == null ? "" : text.trim().toLowerCase();
        if (value.isEmpty()) {
            return findAll();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Bus b WHERE lower(b.plateNumber) LIKE :q " +
                                    "OR lower(b.model) LIKE :q " +
                                    "OR lower(b.status) LIKE :q ORDER BY b.busId DESC", Bus.class)
                    .setParameter("q", "%" + value + "%")
                    .getResultList();
        }
    }

    public boolean plateExists(String plateNumber, Long excludedBusId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(b.busId) FROM Bus b WHERE upper(b.plateNumber) = :plate";
            if (excludedBusId != null) {
                hql += " AND b.busId <> :id";
            }
            org.hibernate.query.Query<Long> query = session.createQuery(hql, Long.class)
                    .setParameter("plate", plateNumber.toUpperCase());
            if (excludedBusId != null) {
                query.setParameter("id", excludedBusId);
            }
            return query.getSingleResult() > 0;
        }
    }

    public long count() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT COUNT(b.busId) FROM Bus b", Long.class).getSingleResult();
        }
    }

    public long countByStatus(String status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT COUNT(b.busId) FROM Bus b WHERE b.status = :status", Long.class)
                    .setParameter("status", status)
                    .getSingleResult();
        }
    }

    private void executeWrite(SessionWork work) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            work.accept(session);
            transaction.commit();
        } catch (RuntimeException ex) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw ex;
        }
    }

    @FunctionalInterface
    private interface SessionWork {
        void accept(Session session);
    }
}
