package rw.aucabus.bean;

import rw.aucabus.dao.BusDao;
import rw.aucabus.dao.RouteDao;
import rw.aucabus.util.HibernateUtil;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named("dashboardBean")
@RequestScoped
public class DashboardBean {

    private final BusDao busDao = new BusDao();
    private final RouteDao routeDao = new RouteDao();

    private long totalBuses;
    private long activeBuses;
    private long maintenanceBuses;
    private long inactiveBuses;
    private long totalRoutes;
    private boolean databaseConnected;

    @PostConstruct
    public void init() {
        try {
            databaseConnected = HibernateUtil.testConnection();
            if (databaseConnected) {
                totalBuses = busDao.count();
                activeBuses = busDao.countByStatus("ACTIVE");
                maintenanceBuses = busDao.countByStatus("MAINTENANCE");
                inactiveBuses = busDao.countByStatus("INACTIVE");
                totalRoutes = routeDao.count();
            }
        } catch (RuntimeException ex) {
            databaseConnected = false;
            ex.printStackTrace();
        }
    }

    public long getTotalBuses() {
        return totalBuses;
    }

    public long getActiveBuses() {
        return activeBuses;
    }

    public long getMaintenanceBuses() {
        return maintenanceBuses;
    }

    public long getInactiveBuses() {
        return inactiveBuses;
    }

    public long getTotalRoutes() {
        return totalRoutes;
    }

    public boolean isDatabaseConnected() {
        return databaseConnected;
    }
}
