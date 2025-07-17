package com.tesla.bikeservices.service;

import com.tesla.bikeservices.dto.ServiceBookingDTO;
import com.tesla.bikeservices.entity.AppointmentSlot;
import com.tesla.bikeservices.entity.Customer;
import com.tesla.bikeservices.entity.ServiceBooking;
import com.tesla.bikeservices.entity.ServiceType;
import com.tesla.bikeservices.entity.SparePart;
import com.tesla.bikeservices.repository.AppointmentSlotRepository;
import com.tesla.bikeservices.repository.CustomerRepository;
import com.tesla.bikeservices.repository.ServiceBookingRepository;
import com.tesla.bikeservices.repository.ServiceTypeRepository;
import com.tesla.bikeservices.repository.SparePartRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceBookingService {

    private static final Logger logger = LogManager.getLogger(ServiceBookingService.class);
    private final ServiceBookingRepository serviceBookingRepository;
    private final CustomerRepository customerRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final SparePartRepository sparePartRepository;
    private final AppointmentSlotRepository appointmentSlotRepository;

    public ServiceBookingService(
            ServiceBookingRepository serviceBookingRepository,
            CustomerRepository customerRepository,
            ServiceTypeRepository serviceTypeRepository,
            SparePartRepository sparePartRepository,
            AppointmentSlotRepository appointmentSlotRepository) {
        this.serviceBookingRepository = serviceBookingRepository;
        this.customerRepository = customerRepository;
        this.serviceTypeRepository = serviceTypeRepository;
        this.sparePartRepository = sparePartRepository;
        this.appointmentSlotRepository = appointmentSlotRepository;
    }

    @Transactional
    public ServiceBooking createBooking(ServiceBookingDTO bookingDTO) {
        logger.info("Creating booking for customer ID: {}", bookingDTO.getCustomerId());

        Customer customer = customerRepository.findById(bookingDTO.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer with ID " + bookingDTO.getCustomerId() + " not found"));

        ServiceType serviceType = serviceTypeRepository.findById(bookingDTO.getServiceTypeId())
                .orElseThrow(() -> new EntityNotFoundException("Service type with ID " + bookingDTO.getServiceTypeId() + " not found"));

        AppointmentSlot appointmentSlot = appointmentSlotRepository.findById(bookingDTO.getAppointmentSlotId())
                .orElseThrow(() -> new EntityNotFoundException("Appointment slot with ID " + bookingDTO.getAppointmentSlotId() + " not found"));

        if (!appointmentSlot.isAvailable()) {
            throw new IllegalStateException("Selected appointment slot is not available");
        }

        List<SparePart> spareParts = bookingDTO.getSparePartIds() != null
                ? sparePartRepository.findAllById(bookingDTO.getSparePartIds())
                : List.of();
        for (SparePart part : spareParts) {
            if (part.getQuantity() <= 0) {
                throw new IllegalStateException("Spare part out of stock: " + part.getPartName());
            }
        }

        ServiceBooking booking = new ServiceBooking();
        booking.setCustomer(customer);
        booking.setServiceType(serviceType);
        booking.setAppointmentSlot(appointmentSlot);
        booking.setStatus(bookingDTO.getStatus());
        booking.setPriority(bookingDTO.getPriority());
        booking.setTechnician(bookingDTO.getTechnician());
        booking.setSpareParts(spareParts);
        booking.setNotes(bookingDTO.getNotes());

        appointmentSlot.setAvailable(false);
        appointmentSlotRepository.save(appointmentSlot);

        for (SparePart part : spareParts) {
            part.setQuantity(part.getQuantity() - 1);
            sparePartRepository.save(part);
        }

        return serviceBookingRepository.save(booking);
    }

    public ServiceBooking getBooking(Long id) {
        logger.debug("Fetching booking with ID: {}", id);
        return findBookingOrThrow(id);
    }

    public Page<ServiceBooking> getAllBookings(Pageable pageable) {
        logger.debug("Fetching all bookings with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return serviceBookingRepository.findAll(pageable);
    }

    public List<ServiceBooking> getBookingsByCustomer(Long customerId) {
        logger.debug("Fetching bookings for customer ID: {}", customerId);
        return serviceBookingRepository.findByCustomerId(customerId);
    }

    public List<ServiceBooking> getBookingsByStatus(String status) {
        logger.debug("Fetching bookings with status: {}", status);
        
        return serviceBookingRepository.findByStatus(status);
    }

    @Transactional
    public ServiceBooking updateBooking(Long id, ServiceBookingDTO bookingDTO) {
        logger.info("Updating booking with ID: {}", id);
        ServiceBooking booking = findBookingOrThrow(id);

        Customer customer = customerRepository.findById(bookingDTO.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer with ID " + bookingDTO.getCustomerId() + " not found"));

        ServiceType serviceType = serviceTypeRepository.findById(bookingDTO.getServiceTypeId())
                .orElseThrow(() -> new EntityNotFoundException("Service type with ID " + bookingDTO.getServiceTypeId() + " not found"));

        AppointmentSlot newSlot = appointmentSlotRepository.findById(bookingDTO.getAppointmentSlotId())
                .orElseThrow(() -> new EntityNotFoundException("Appointment slot with ID " + bookingDTO.getAppointmentSlotId() + " not found"));

        if (!newSlot.isAvailable()) {
            throw new IllegalStateException("Selected appointment slot is not available");
        }

        List<SparePart> spareParts = bookingDTO.getSparePartIds() != null
                ? sparePartRepository.findAllById(bookingDTO.getSparePartIds())
                : List.of();
        for (SparePart part : spareParts) {
            if (part.getQuantity() <= 0) {
                throw new IllegalStateException("Spare part out of stock: " + part.getPartName());
            }
        }

        // Revert stock and availability for old entities
        AppointmentSlot oldSlot = booking.getAppointmentSlot();
        if (oldSlot != null && !oldSlot.getId().equals(newSlot.getId())) {
            oldSlot.setAvailable(true);
            appointmentSlotRepository.save(oldSlot);
        }

        for (SparePart oldPart : booking.getSpareParts()) {
            oldPart.setQuantity(oldPart.getQuantity() + 1);
            sparePartRepository.save(oldPart);
        }

        // Update booking
        booking.setCustomer(customer);
        booking.setServiceType(serviceType);
        booking.setAppointmentSlot(newSlot);
        booking.setStatus(bookingDTO.getStatus());
        booking.setPriority(bookingDTO.getPriority());
        booking.setTechnician(bookingDTO.getTechnician());
        booking.setSpareParts(spareParts);
        booking.setNotes(bookingDTO.getNotes());

        newSlot.setAvailable(false);
        appointmentSlotRepository.save(newSlot);

        for (SparePart part : spareParts) {
            part.setQuantity(part.getQuantity() - 1);
            sparePartRepository.save(part);
        }

        return serviceBookingRepository.save(booking);
    }

    @Transactional
    public void deleteBooking(Long id) {
        logger.info("Deleting booking with ID: {}", id);
        ServiceBooking booking = findBookingOrThrow(id);

        AppointmentSlot slot = booking.getAppointmentSlot();
        if (slot != null) {
            slot.setAvailable(true);
            appointmentSlotRepository.save(slot);
        }

        for (SparePart part : booking.getSpareParts()) {
            part.setQuantity(part.getQuantity() + 1);
            sparePartRepository.save(part);
        }

        serviceBookingRepository.deleteById(id);
    }

    public Page<ServiceBooking> searchBookingsByCriteria(String status, String technicianPrefix, String customerPrefix, Pageable pageable) {
        logger.debug("Searching bookings with status={}, technicianPrefix={}, customerPrefix={}", status, technicianPrefix, customerPrefix);
        return serviceBookingRepository.findByCriteria(status, technicianPrefix, customerPrefix, pageable);
    }

    private ServiceBooking findBookingOrThrow(Long id) {
        return serviceBookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking with ID " + id + " not found"));
    }
}