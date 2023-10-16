package com.example.trainstationtemp.service;

import com.example.trainstationtemp.dto.*;
import com.example.trainstationtemp.entity.User;
import com.example.trainstationtemp.entity.UserAuthority;
import com.example.trainstationtemp.entity.domain.*;
import com.example.trainstationtemp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class DtoMappings {
    @Autowired private BrigadeRepository brigadeRepository;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private DriverRepository driverRepository;
    @Autowired private FlightInspectionRepository flightInspectionRepository;
    @Autowired private FlightRepository flightRepository;
    @Autowired private FlightStopRepository flightStopRepository;
    @Autowired private LocomotiveBrigadeRepository locomotiveBrigadeRepository;
    @Autowired private LocomotiveRepository locomotiveRepository;
    @Autowired private PassengerRepository passengerRepository;
    @Autowired private RepairmenBrigadeRepository repairmenBrigadeRepository;
    @Autowired private RouteRepository routeRepository;
    @Autowired private TicketRepository ticketRepository;
    @Autowired private WorkerRepository workerRepository;

    public WorkerDTO mapToDto(Worker worker) {
        return new WorkerDTO(
                worker.getId(),
                worker.getFirstName(),
                worker.getLastName(),

                worker.getBirthdate(),
                worker.getGender(),
                worker.getBrigade().getId(),
                worker.getEmploymentDate(),
                worker.getSalary(),
                worker.getHasKids(),
                worker.getKidsCounter()
        );
    }

    public Worker mapToEntity(WorkerDTO dto) throws NoSuchElementException {
        return Worker.builder()
                .id(dto.id())
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .birthdate(dto.birthdate())
                .gender(dto.gender())
                .brigade(brigadeRepository.findById(dto.brigadeId()).orElseThrow())
                .employmentDate(dto.employmentDate())
                .salary(dto.salary())
                .hasKids(dto.hasKids())
                .kidsCounter(dto.kidsCounter())
                .build();
    }

    public BrigadeDTO mapToDto(Brigade brigade) {
        return new BrigadeDTO(
                brigade.getId(),
                brigade.getBrigadier().getId(),
                brigade.getDepartment().getId()
        );
    }

    public Brigade mapToEntity(BrigadeDTO dto) throws NoSuchElementException {
        return Brigade.builder()
                .id(dto.id())
                .brigadier(workerRepository.findById(dto.brigadierId()).orElseThrow())
                .department(departmentRepository.findById(dto.departmentId()).orElseThrow())
                .build();
    }

    public DepartmentDTO mapToDto(Department department) {
        return new DepartmentDTO(
                department.getId(),
                department.getChief().getId(),
                department.getName());
    }

    public Department mapToEntity(DepartmentDTO dto) throws NoSuchElementException {
        return Department.builder()
                .id(dto.id())
                .chief(workerRepository.findById(dto.chiefId()).orElseThrow())
                .name(dto.name())
                .build();
    }

    public DriverDTO mapToDto(Driver driver) {
        return new DriverDTO(
                driver.getId(),
                driver.getFirstName(),
                driver.getLastName(),

                driver.getBirthdate(),
                driver.getGender(),
                driver.getBrigade().getId(),
                driver.getEmploymentDate(),
                driver.getSalary(),
                driver.getHasKids(),
                driver.getKidsCounter(),
                driver.getLocomotiveBrigade().getId()
        );
    }

    public Driver mapToEntity(DriverDTO dto) {
        return Driver.builder()
                .id(dto.id())
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .birthdate(dto.birthdate())
                .gender(dto.gender())
                .brigade(brigadeRepository.findById(dto.brigadeId()).orElseThrow())
                .employmentDate(dto.employmentDate())
                .salary(dto.salary())
                .hasKids(dto.hasKids())
                .kidsCounter(dto.kidsCounter())
                .locomotiveBrigade(locomotiveBrigadeRepository.findById(dto.locomotiveBrigadeId()).orElseThrow())
                .build();
    }

    public FlightDTO mapToDto(Flight flight) {
        return new FlightDTO(
                flight.getId(),
                flight.getRoute().getId(),
                flight.getLocomotive().getId()
        );
    }

    public Flight mapToEntity(FlightDTO dto) {
        return Flight.builder()
                .id(dto.id())
                .route(routeRepository.findById(dto.routeId()).orElseThrow())
                .locomotive(locomotiveRepository.findById(dto.locomotiveId()).orElseThrow())
                .build();
    }

    public FlightDelayDTO mapToDto(FlightDelay flight) {
        return new FlightDelayDTO(
                flight.getId(),
                flight.getFlightStop().getId(),
                flight.getReason()
        );
    }

    public FlightDelay mapToEntity(FlightDelayDTO dto) {
        return FlightDelay.builder()
                .id(dto.id())
                .flightStop(flightStopRepository.findById(dto.flightStopId()).orElseThrow())
                .reason(dto.reason())
                .build();
    }

    public FlightInspectionDTO mapToDto(FlightInspection entity) {
        return new FlightInspectionDTO(
                entity.getId(),
                entity.getLocomotive().getId(),
                entity.getBrigade().getId(),
                entity.getDate(),
                entity.getFlight().getId()
        );
    }

    public FlightInspection mapToEntity(FlightInspectionDTO dto) {
        return FlightInspection.builder()
                .id(dto.id())
                .locomotive(locomotiveRepository.findById(dto.locomotiveId()).orElseThrow())
                .brigade(repairmenBrigadeRepository.findById(dto.repairmenBrigadeId()).orElseThrow())
                .date(dto.date())
                .flight(flightRepository.findById(dto.id()).orElseThrow())
                .build();
    }

    public FlightStopDTO mapToDto(FlightStop entity) {
        return new FlightStopDTO(
                entity.getId(),
                entity.getRoute().getId(),
                entity.getPlannedArrivalTime(),
                entity.getArrivalTime(),
                entity.getPlannedDepartureTime(),
                entity.getDepartureTime()
        );
    }

    public FlightStop mapToEntity(FlightStopDTO dto) {
        return FlightStop.builder()
                .id(dto.id())
                .plannedArrivalTime(dto.plannedArrivalTime())
                .arrivalTime(dto.arrivalTime())
                .plannedDepartureTime(dto.plannedDepartureTime())
                .departureTime(dto.departureTime())
                .build();
    }

    public LocomotiveBrigadeDTO mapToDto(LocomotiveBrigade entity) {
        return new LocomotiveBrigadeDTO(
                entity.getId(),
                entity.getLocomotive().getId(),
                entity.getDriver().getId()
        );
    }

    public LocomotiveBrigade mapToEntity(LocomotiveBrigadeDTO dto) {
        return LocomotiveBrigade.builder()
                .id(dto.id())
                .locomotive(locomotiveRepository.findById(dto.locomotiveId()).orElseThrow())
                .driver(driverRepository.findById(dto.driverId()).orElseThrow())
                .build();
    }

    public LocomotiveDTO mapToDto(Locomotive entity) {
        return new LocomotiveDTO(
                entity.getId(),
                entity.getManufacturingDate(),
                entity.getLocomotiveBrigade().getId(),
                entity.getRoute().getId(),
                entity.getCapacity()
        );
    }

    public Locomotive mapToEntity(LocomotiveDTO dto) {
        return Locomotive.builder()
                .id(dto.id())
                .manufacturingDate(dto.manufacturingDate())
                .locomotiveBrigade(locomotiveBrigadeRepository.findById(dto.locomotiveBrigadeId()).orElseThrow())
                .route(routeRepository.findById(dto.routeId()).orElseThrow())
                .capacity(dto.capacity())
                .build();
    }

    public LocomotiveInspectionDTO mapToDto(LocomotiveInspection entity) {
        return new LocomotiveInspectionDTO(
                entity.getId(),
                entity.getLocomotive().getId(),
                entity.getBrigade().getId(),
                entity.getDate(),
                entity.getNeedsRepair()
        );
    }

    public LocomotiveInspection mapToEntity(LocomotiveInspectionDTO dto) {
        return LocomotiveInspection.builder()
                .id(dto.id())
                .locomotive(locomotiveRepository.findById(dto.locomotiveId()).orElseThrow())
                .brigade(repairmenBrigadeRepository.findById(dto.repairmenBrigadeId()).orElseThrow())
                .date(dto.date())
                .needsRepair(dto.needsRepair())
                .build();
    }

    public MedicalExaminationDTO mapToDto(MedicalExamination entity) {
        return new MedicalExaminationDTO(
                entity.getId(),
                entity.getDriver().getId(),
                entity.getDate()
        );
    }

    public MedicalExamination mapToEntity(MedicalExaminationDTO dto) {
        return MedicalExamination.builder()
                .id(dto.id())
                .driver(driverRepository.findById(dto.id()).orElseThrow())
                .date(dto.date())
                .build();
    }

    public PassengerDTO mapToDto(Passenger entity) {
        return new PassengerDTO(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getBirthdate(),
                entity.getGender()
        );
    }

    public Passenger mapToEntity(PassengerDTO dto) {
        return Passenger.builder()
                .id(dto.id())
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .birthdate(dto.birthdate())
                .gender(dto.gender())
                .build();
    }

    public RepairDTO mapToDto(Repair entity) {
        return new RepairDTO(
                entity.getId(),
                entity.getInspection().getId(),
                entity.getDate()
                );
    }

    public Repair mapToEntity(RepairDTO dto) {
        return Repair.builder()
                .id(dto.id())
                .inspection(flightInspectionRepository.findById(dto.inspectionId()).orElseThrow())
                .date(dto.date())
                .build();
    }

    public RepairmanDTO mapToDto(Repairman entity) {
        return new RepairmanDTO(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getBirthdate(),
                entity.getGender(),
                entity.getBrigade().getId(),
                entity.getEmploymentDate(),
                entity.getSalary(),
                entity.getHasKids(),
                entity.getKidsCounter(),
                entity.getRepairmenBrigade().getId()
                );
    }

    public Repairman mapToEntity(RepairmanDTO dto) {
        return Repairman.builder()
                .id(dto.id())
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .birthdate(dto.birthdate())
                .gender(dto.gender())
                .brigade(brigadeRepository.findById(dto.brigadeId()).orElseThrow())
                .employmentDate(dto.employmentDate())
                .salary(dto.salary())
                .hasKids(dto.hasKids())
                .kidsCounter(dto.kidsCounter())
                .repairmenBrigade(repairmenBrigadeRepository.findById(dto.repairmenBrigadeId()).orElseThrow())
                .build();
    }

    public RepairmenBrigadeDTO mapToDto(RepairmenBrigade entity) {
        return new RepairmenBrigadeDTO(
                entity.getId()
                );
    }

    public RepairmenBrigade mapToEntity(RepairmenBrigadeDTO dto) {
        return RepairmenBrigade.builder()
                .id(dto.id())
                .build();
    }

    public RouteDTO mapToDto(Route entity) {
        return new RouteDTO(
                entity.getId(),
                entity.getType(),
                entity.getDestinationType()
                );
    }

    public Route mapToEntity(RouteDTO dto) {
        return Route.builder()
                .id(dto.id())
                .type(dto.type())
                .destinationType(dto.destinationType())
                .build();
    }

    public RouteStopDTO mapToDto(RouteStop entity) {
        return new RouteStopDTO(
                entity.getId(),
                entity.getStationName()
                );
    }

    public RouteStop mapToEntity(RouteStopDTO dto) {
        return RouteStop.builder()
                .id(dto.id())
                .stationName(dto.stationName())
                .build();
    }

    public TicketDTO mapToDto(Ticket entity) {
        return new TicketDTO(
                entity.getId(),
                entity.getPrice(),
                entity.getDeparture().getId(),
                entity.getDestination().getId(),
                entity.getPlace(),
                entity.getPassenger().getId(),
                entity.getLagguageIncluded()
                );
    }

    public Ticket mapToEntity(TicketDTO dto) {
        return Ticket.builder()
                .id(dto.id())
                .price(dto.price())
                .departure(flightStopRepository.findById(dto.departureId()).orElseThrow())
                .destination(flightStopRepository.findById(dto.destinationId()).orElseThrow())
                .place(dto.place())
                .passenger(passengerRepository.findById(dto.passengerId()).orElseThrow())
                .lagguageIncluded(dto.lagguageIncluded())
                .build();
    }

    public TicketRefundDTO mapToDto(TicketRefund entity) {
        return new TicketRefundDTO(
                entity.getId(),
                entity.getTicket().getId(),
                entity.getDate()
                );
    }

    public TicketRefund mapToEntity(TicketRefundDTO dto) {
        return TicketRefund.builder()
                .id(dto.id())
                .ticket(ticketRepository.findById(dto.id()).orElseThrow())
                .date(dto.date())
                .build();
    }

    public UserDto mapToDto(User entity) {
        return new UserDto(
            entity.getUsername(),
            entity.getPassword(),
            entity.getAuthorities().stream().map(UserAuthority::getAuthority).toList()
        );
    }
}
