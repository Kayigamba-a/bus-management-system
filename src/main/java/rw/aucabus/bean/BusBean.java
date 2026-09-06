package rw.aucabus.bean;

import rw.aucabus.dao.BusDao;
import rw.aucabus.model.Bus;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Named("busBean")
@ViewScoped
public class BusBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final BusDao busDao = new BusDao();
    private Bus bus;
    private List<Bus> busList;
    private String searchText;

    @PostConstruct
    public void init() {
        bus = new Bus();
        bus.setStatus("ACTIVE");
        loadForEdit();
        loadBuses();
    }

    public void loadBuses() {
        try {
            busList = busDao.search(searchText);
        } catch (RuntimeException ex) {
            busList = new ArrayList<>();
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Database error",
                    "Unable to load buses. Check PostgreSQL and the Tomcat console.");
        }
    }

    public void clearSearch() {
        searchText = null;
        loadBuses();
    }

    public String newBus() {
        return "/bus-form.xhtml?faces-redirect=true";
    }

    public String editBus(Long busId) {
        return "/bus-form.xhtml?faces-redirect=true&busId=" + busId;
    }

    public void loadForEdit() {
        String idValue = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap().get("busId");

        if (idValue == null || idValue.trim().isEmpty()) {
            bus = new Bus();
            bus.setStatus("ACTIVE");
            return;
        }

        try {
            Long id = Long.valueOf(idValue);
            Bus found = busDao.findById(id);
            if (found == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Not found", "The selected bus no longer exists.");
                bus = new Bus();
                bus.setStatus("ACTIVE");
            } else {
                bus = found;
            }
        } catch (NumberFormatException ex) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Invalid bus", "The bus identifier is invalid.");
        }
    }

    public String saveBus() {
        try {
            normalizeBus();

            if (busDao.plateExists(bus.getPlateNumber(), bus.getBusId())) {
                addMessage(FacesMessage.SEVERITY_ERROR,
                        "Duplicate plate number",
                        "A bus with plate number " + bus.getPlateNumber() + " already exists.");
                return null;
            }

            if (bus.getBusId() == null) {
                busDao.save(bus);
                flashMessage("Bus registered", bus.getPlateNumber() + " was registered successfully.");
            } else {
                busDao.update(bus);
                flashMessage("Bus updated", bus.getPlateNumber() + " was updated successfully.");
            }
            return "/bus-list.xhtml?faces-redirect=true";
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Unable to save bus",
                    "The bus could not be saved. Check the entered data and database connection.");
            return null;
        }
    }

    public void deleteBus(Long busId) {
        try {
            Bus existing = busDao.findById(busId);
            if (existing == null) {
                addMessage(FacesMessage.SEVERITY_WARN, "Already removed", "The bus no longer exists.");
                return;
            }
            busDao.delete(busId);
            addMessage(FacesMessage.SEVERITY_INFO,
                    "Bus deleted",
                    existing.getPlateNumber() + " was deleted successfully.");
            loadBuses();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            addMessage(FacesMessage.SEVERITY_ERROR,
                    "Unable to delete bus",
                    "The bus could not be deleted.");
        }
    }

    private void normalizeBus() {
        if (bus.getPlateNumber() != null) {
            bus.setPlateNumber(bus.getPlateNumber().trim().toUpperCase(Locale.ROOT));
        }
        if (bus.getModel() != null) {
            bus.setModel(bus.getModel().trim());
        }
        if (bus.getStatus() != null) {
            bus.setStatus(bus.getStatus().trim().toUpperCase(Locale.ROOT));
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

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public List<Bus> getBusList() {
        return busList;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}
