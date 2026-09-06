package rw.aucabus.bean;

import rw.aucabus.dao.RouteDao;
import rw.aucabus.model.Route;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("routeBean")
@ViewScoped
public class RouteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final RouteDao routeDao = new RouteDao();
    private Route route;
    private List<Route> routeList;
    private String searchText;

    @PostConstruct
    public void init() {
        route = new Route();
        loadForEdit();
        loadRoutes();
    }

    public void loadRoutes() {
        try {
            routeList = routeDao.search(searchText);
        } catch (RuntimeException ex) {
            routeList = new ArrayList<>();
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Database error",
                    "Unable to load routes. Check PostgreSQL and the Tomcat console.");
        }
    }

    public void clearSearch() {
        searchText = null;
        loadRoutes();
    }

    public String newRoute() {
        return "/route-form.xhtml?faces-redirect=true";
    }

    public String editRoute(Long routeId) {
        return "/route-form.xhtml?faces-redirect=true&routeId=" + routeId;
    }

    public void loadForEdit() {
        String idValue = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap().get("routeId");

        if (idValue == null || idValue.trim().isEmpty()) {
            route = new Route();
            return;
        }

        try {
            Long id = Long.valueOf(idValue);
            Route found = routeDao.findById(id);
            if (found == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Not found", "The selected route no longer exists.");
                route = new Route();
            } else {
                route = found;
            }
        } catch (NumberFormatException ex) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Invalid route", "The route identifier is invalid.");
        }
    }

    public String saveRoute() {
        try {
            normalizeRoute();

            if (route.getStartPoint() != null && route.getEndPoint() != null
                    && route.getStartPoint().equalsIgnoreCase(route.getEndPoint())) {
                addMessage(FacesMessage.SEVERITY_ERROR,
                        "Invalid route",
                        "Start point and end point must be different.");
                return null;
            }

            if (route.getRouteId() == null) {
                routeDao.save(route);
                flashMessage("Route created", route.getRouteName() + " was created successfully.");
            } else {
                routeDao.update(route);
                flashMessage("Route updated", route.getRouteName() + " was updated successfully.");
            }
            return "/route-list.xhtml?faces-redirect=true";
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Unable to save route",
                    "The route could not be saved. Check the entered data and database connection.");
            return null;
        }
    }

    public void deleteRoute(Long routeId) {
        try {
            Route existing = routeDao.findById(routeId);
            if (existing == null) {
                addMessage(FacesMessage.SEVERITY_WARN, "Already removed", "The route no longer exists.");
                return;
            }
            routeDao.delete(routeId);
            addMessage(FacesMessage.SEVERITY_INFO,
                    "Route deleted",
                    existing.getRouteName() + " was deleted successfully.");
            loadRoutes();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Unable to delete route",
                    "The route could not be deleted.");
        }
    }

    private void normalizeRoute() {
        if (route.getRouteName() != null) {
            route.setRouteName(route.getRouteName().trim());
        }
        if (route.getStartPoint() != null) {
            route.setStartPoint(route.getStartPoint().trim());
        }
        if (route.getEndPoint() != null) {
            route.setEndPoint(route.getEndPoint().trim());
        }
        if (route.getEstimatedDuration() != null) {
            route.setEstimatedDuration(route.getEstimatedDuration().trim());
        }
    }

    private void flashMessage(String summary, String detail) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public List<Route> getRouteList() {
        return routeList;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}
