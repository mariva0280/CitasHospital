package com.CitasHospital.Service;

import com.CitasHospital.Controller.Inputs.NursesAppointmentsInput;
import com.CitasHospital.Controller.Inputs.NursesInput;
import com.CitasHospital.Controller.Outputs.NursesAppointmentsOutput;
import com.CitasHospital.Domain.Doctors;
import com.CitasHospital.Domain.Nurses;
import com.CitasHospital.Domain.NursesAppointments;
import com.CitasHospital.Exception.*;
import com.CitasHospital.Repository.DoctorsRepository;
import com.CitasHospital.Repository.NursesAppointmentsRepository;
import com.CitasHospital.Repository.NursesRepository;
import com.CitasHospital.Repository.PatientsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service

public class NursesService {
    @Autowired
    private NursesRepository nursesRepository;
    @Autowired
    private PatientsRepository patientsRepository;
    @Autowired
    private DoctorsRepository doctorsRepository;
    @Autowired
    private NursesAppointmentsRepository nursesAppointmentsRepository;
    @Autowired
    private SchedulesService schedulesService;
    private static final int appointmentInterval = 5;

    public void addNurses(NursesInput nursesInput) throws NursesExistException,InvalidDniException{
        if (patientsRepository.existsById(nursesInput.getDni())) {
            throw new InvalidDniException("DNI is already in use by a patient.");
        }
        if(doctorsRepository.existsById(nursesInput.getDni())){
            throw new InvalidDniException("DNI is already in use by a doctor.");
        }
        if(nursesRepository.existsById(nursesInput.getDni())) {
            throw new NursesExistException("Nurse already exists.");
        }
        Nurses nurse = Nurses.getNurses(nursesInput);
        nursesRepository.save(nurse);
    }
    //metodo para configurar horario enfermeros
    public void addScheduleNurses(String dni, LocalTime startTime, LocalTime endTime) throws NursesDoesntExistsException, InvalidTimeException,NurseScheduleConflictException {
        Nurses nurse = nursesRepository.findById(dni).orElse(null);
        if(nurse == null) {
            throw new NursesDoesntExistsException("The nurse whit dni " + dni + " doesn't exists.");
        }
        if (nurse.getStartTime() != null || nurse.getEndTime() != null) {
            // La enfermera ya tiene un horario de atención asignado
            throw new NurseScheduleConflictException("Nurse with DNI " + dni + " already has a schedule assigned.");
        }
        if (startTime == null || endTime == null) {
            throw new InvalidTimeException("The start time and end time object cannot be null.");
        }
        nurse.addSchedule(startTime, endTime);
        nursesRepository.save(nurse);
    }
    public void updateScheduleNurse(String dni, LocalTime startTime, LocalTime endTime) throws NursesDoesntExistsException,InvalidTimeException,NurseScheduleConflictException{
        Nurses nurse = nursesRepository.findById(dni).orElse(null);
        if(nurse == null){
            throw new NursesDoesntExistsException("The nurse whit dni " + dni + " doesn't exists.");
        }
        if (startTime == null || endTime==null) {
            throw new InvalidTimeException("The start time and end time object cannot be null.");
        }
        if (nurse.getStartTime() == null || nurse.getEndTime() == null) {
            // El doctor no tiene un horario de atención asignado
            throw new NurseScheduleConflictException("Nurse with DNI " + dni + " does not have a schedule assgined yet.");
        }
        nurse.addSchedule(startTime,endTime);
        nursesRepository.save(nurse);
    }

    public void addNurseAppointment(NursesAppointmentsInput nurseAppointmentsInput) throws PatientsDoesntExistsException, NursesDoesntExistsException, InvalidTimeException, AppointmentExistsException,NurseScheduleConflictException,InvalidAppointmentIntervalException,
            WorkersScheduleNotSetException{

        if(!patientsRepository.existsById(nurseAppointmentsInput.getDniPatients())){
            throw new PatientsDoesntExistsException("Patient doesn't exist");
        }
        Nurses nurse = nursesRepository.findById(nurseAppointmentsInput.getDniNurses()).orElse(null);
        if(nurse == null){
            throw new NursesDoesntExistsException("Nurses doesn't exist");
        }
        if(nurse.getStartTime() == null || nurse.getEndTime() == null){
            throw new WorkersScheduleNotSetException("Nurse's schedule is not set yet. Please set the nurse's schedule before adding appointments. ");
        }

        if(!schedulesService.getTimeWindow(LocalDate.now()).contains(nurseAppointmentsInput.getDays())){
            throw new InvalidTimeException("The appointment must be scheduled for the next week.");
        }
        if(nurseAppointmentsInput.getHours().isBefore(nurse.getStartTime()) || nurseAppointmentsInput.getHours().isAfter(nurse.getEndTime())){
            throw new NurseScheduleConflictException("The appointment is outside the nurse's working hours");
        }
        if (nursesAppointmentsRepository.existsByDniNursesAndDaysAndHours(nurseAppointmentsInput.getDniNurses(),
                nurseAppointmentsInput.getDays(), nurseAppointmentsInput.getHours())){
            throw new AppointmentExistsException("There is already an appointment scheduled at the requested time.");
        }

        if(nursesAppointmentsRepository.existsByDaysAndHoursBetween(nurseAppointmentsInput.getDays(),
                nurseAppointmentsInput.getHours().minusMinutes(appointmentInterval), nurseAppointmentsInput.getHours().plusMinutes(appointmentInterval))) {
            throw new InvalidAppointmentIntervalException("There is already an appointment scheduled within 10 minutes of the requested time.");
        }


        NursesAppointments appointment = NursesAppointments.getAppointmentsNurses(nurseAppointmentsInput);
        nursesAppointmentsRepository.save(appointment);

    }
    public List<Map<String,Object>> getAvailableSlotsForNurse(Nurses nurse, LocalDate startDate) throws InvalidTimeException{
        List<Map<String,Object>> availableSlotsPerDayList = new ArrayList<>();

        // Validar si la fecha está dentro del "time window" válido
        List<LocalDate> timeWindow = schedulesService.getTimeWindow(LocalDate.now());
        if (!timeWindow.contains(startDate)) {
            throw new InvalidTimeException("The consult is outside the time window.");
        }

        LocalTime startTime = nurse.getStartTime();
        LocalTime endTime = nurse.getEndTime();

        LocalDate endDate = startDate.plusDays(4); // Obtener la fecha de finalización (5 días después)

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                // Ignorar los fines de semana
                List<LocalTime> hoursOfDay = schedulesService.getHoursList(startTime, endTime);

                // Obtener las citas de la enfermera por día
                List<NursesAppointments> appointmentsForDay = nursesAppointmentsRepository
                        .findByDniNursesAndDaysOrderByHours(nurse.getDni(), date);

                for (NursesAppointments appointment : appointmentsForDay) {
                    hoursOfDay.remove(appointment.getHours());
                }

                Map<String, Object> availableSlotsPerDay = new HashMap<>();
                availableSlotsPerDay.put("day", date);
                availableSlotsPerDay.put("availableSlots", hoursOfDay);

                availableSlotsPerDayList.add(availableSlotsPerDay);
            }
        }

        return availableSlotsPerDayList;
    }
    public List<NursesAppointmentsOutput> getAppointmentsForPatientOnDayNurses(String dniPatients, LocalDate days) throws PatientsDoesntExistsException, EmptyListException,InvalidTimeException {
        if(!patientsRepository.existsById(dniPatients)) throw new PatientsDoesntExistsException("Patient doesn't exist");

        List<LocalDate>timeWindow = schedulesService.getTimeWindow(LocalDate.now());
        if(!timeWindow.contains(days)){
            throw new InvalidTimeException("Invalid day for the consult.");
        }

        List<NursesAppointments> listAppointmentsForPatientOnDay = nursesAppointmentsRepository.findByDniPatientsAndDaysOrderByHours(dniPatients, days);

        if(listAppointmentsForPatientOnDay.isEmpty()) throw new EmptyListException("The appointments list for this patient is empty");
        List<NursesAppointmentsOutput> appointmentsOutputDayNurseList = new ArrayList<>();
        for(NursesAppointments appointment : listAppointmentsForPatientOnDay){
            appointmentsOutputDayNurseList.add(NursesAppointmentsOutput.getAppointmentsNurses(appointment));
        }
        return appointmentsOutputDayNurseList;
    }

}
