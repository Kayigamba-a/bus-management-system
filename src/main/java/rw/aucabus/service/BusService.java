package rw.aucabus.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.aucabus.exception.BusinessValidationException;
import rw.aucabus.exception.ResourceNotFoundException;
import rw.aucabus.model.Bus;
import rw.aucabus.repository.BusRepository;
import rw.aucabus.repository.ScheduleRepository;
import java.util.List;

@Service
public class BusService {
    private final BusRepository busRepository;
    private final ScheduleRepository scheduleRepository;

    public BusService(BusRepository busRepository, ScheduleRepository scheduleRepository) {
        this.busRepository = busRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional
    public Bus create(Bus bus) {
        normalize(bus);
        if (busRepository.existsByPlateNumberIgnoreCase(bus.getPlateNumber())) {
            throw new BusinessValidationException("A bus with this plate number already exists.");
        }
        return busRepository.save(bus);
    }

    public List<Bus> findAll() { return busRepository.findAll(); }

    public Bus findById(Long id) {
        return busRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bus not found with ID " + id));
    }

    @Transactional
    public Bus update(Long id, Bus input) {
        Bus bus = findById(id);
        normalize(input);
        if (busRepository.existsByPlateNumberIgnoreCaseAndBusIdNot(input.getPlateNumber(), id)) {
            throw new BusinessValidationException("A bus with this plate number already exists.");
        }
        bus.setPlateNumber(input.getPlateNumber());
        bus.setModel(input.getModel());
        bus.setCapacity(input.getCapacity());
        bus.setStatus(input.getStatus());
        return busRepository.save(bus);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        if (scheduleRepository.existsByBusBusId(id)) {
            throw new BusinessValidationException("This bus cannot be deleted because it is assigned to a schedule.");
        }
        busRepository.deleteById(id);
    }

    private void normalize(Bus bus) {
        if (bus.getPlateNumber() != null) bus.setPlateNumber(bus.getPlateNumber().trim().toUpperCase());
        if (bus.getModel() != null) bus.setModel(bus.getModel().trim());
        if (bus.getStatus() != null) bus.setStatus(bus.getStatus().trim().toUpperCase());
    }
}
