package rw.aucabus.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import rw.aucabus.model.Route;
import rw.aucabus.util.HibernateUtil;

import java.io.Serializable;
import java.util.List;

public class RouteDao implements Serializable, GenericDao<Route, Long> {

    private static final long serialVersionUID = 1L;

    @Override
    public void save(Route route) {
        executeWrite(session -> session.persist(route));
    }

    @Override
    public void update(Route route) {
        executeWrite(session -> session.merge(route));
    }

    @Override
    public void delete(Long routeId) {
        executeWrite(session -> {
            Route route = session.get(Route.class, routeId);
            if (route != null) {
                session.remove(route);
            }
        });
    }

    @Override
    public Route findById(Long routeId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Route.class, routeId);
        }
    }

    @Override
    public List<Route> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Route r ORDER BY r.routeId DESC", Route.class).getResultList();
        }
    }

    public List<Route> search(String text) {
        String value = text == null ? "" : text.trim().toLowerCase();
        if (value.isEmpty()) {
            return findAll();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Route r WHERE lower(r.routeName) LIKE :q " +
                                    "OR lower(r.startPoint) LIKE :q " +
                                    "OR lower(r.endPoint) LIKE :q ORDER BY r.routeId DESC", Route.class)
                    .setParameter("q", "%" + value + "%")
                    .getResultList();
        }
    }

    public long count() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT COUNT(r.routeId) FROM Route r", Long.class).getSingleResult();
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
