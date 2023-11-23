package com.CitasHospital.Service;

import com.CitasHospital.Controller.Inputs.DoctorsAppointmentsInput;
import com.CitasHospital.Controller.Inputs.DoctorsInput;
import com.CitasHospital.Controller.Outputs.DoctorsAppointmentsOutput;
import com.CitasHospital.Domain.Doctors;
import com.CitasHospital.Domain.DoctorsAppointments;
import com.CitasHospital.Exception.*;
import com.CitasHospital.Repository.DoctorsAppointmentsRepository;
import com.CitasHospital.Repository.DoctorsRepository;
import com.CitasHospital.Repository.NursesRepository;
import com.CitasHospital.Repository.PatientsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service

public class DoctorsService {
    @Autowired
    private DoctorsRepository doctorsRepository;
    @Autowired
    private PatientsRepository patientsRepository;
    @Autowired
    private NursesRepository nursesRepository;
    @Autowired
    private DoctorsAppointmentsRepository doctorsAppointmentsRepository;
    @Autowired
    private SchedulesService schedulesService;

    private static final  int appointmentInterval = 5;
    //metodo para añadir doctors, comprobando si el dni ya existe en la base de datos como paciente,nurse or doctor, para evitar duplicidades.
    public void addDoctors(DoctorsInput doctorsInput) throws DoctorsExistException,InvalidDniException{

        if (patientsRepository.existsById(doctorsInput.getDni())) {
            throw new InvalidDniException("DNI is already in use by a patient.");
        }
        if(nursesRepository.existsById(doctorsInput.getDni())){
            throw new InvalidDniException("DNI is already in use by a nurse.");
        }
        if(doctorsRepository.existsById(doctorsInput.getDni())) {
            throw new DoctorsExistException("Doctor already exists.");
        }
        Doctors doctor = Doctors.getDoctors(doctorsInput);
        doctorsRepository.save(doctor);
    }
    /*
    metodo para añadir horario de atención doctors, comprobando si el doctor ya está dado de alta, si ya tiene un horario asignado, no deja tener horario nulo.
     */
    public void addScheduleDoctors(String dni, LocalTime startTime, LocalTime endTime) throws DoctorsDoesntExistsExcpetion,DoctorScheduleConflictException {
        Doctors doctor = doctorsRepository.findById(dni).orElse(null);
        if(doctor == null) {
            throw new DoctorsDoesntExistsExcpetion ("The doctor whit dni " + dni + " doesn't exists.");
        }
        if (doctor.getStartTime() != null || doctor.getEndTime() != null) {
            // El doctor ya tiene un horario de atención asignado
            throw new DoctorScheduleConflictException("Doctor with DNI " + dni + " already has a schedule assigned.");
        }

        doctor.addSchedule(startTime,endTime);
        doctorsRepository.save(doctor);
    }
    /*metodo para actualizar horario de atencion cuando cambiamos de time window,como no he podido manejar el time window en el metodo anterior he creado este metodo
    para cuando cambiemos de semana pueda actualizar el horario de atención.
     */
    public void updateScheduleDoctor(String dni, LocalTime startTime, LocalTime endTime) throws DoctorsDoesntExistsExcpetion,DoctorScheduleConflictException{
        Doctors doctor = doctorsRepository.findById(dni).orElse(null);
        if(doctor == null){
            throw new DoctorsDoesntExistsExcpetion("The doctor whit dni " + dni + " doesn't exists.");
        }
        if (doctor.getStartTime() == null || doctor.getEndTime() == null) {
            // El doctor no tiene un horario de atención asignado
            throw new DoctorScheduleConflictException("Doctor with DNI " + dni + " does not have a schedule assigned yet.");
        }
        doctor.addSchedule(startTime,endTime);
        doctorsRepository.save(doctor);
    }
    /*
    metodo para crear una cita con el doctor,comprobando si el paciente existe, si el doctor existe, comprueba si el horario de atención del doctor ya esta añadido
    comprueba que estamos en la ventana de tiempo correcta, comprueba que la hora elegida está dentro del horario de atención,comprueba si la cita ya existe para
    ese dia y hora, comprueba el intervalo entre citas,y por ultimo crea la cita.
     */
    public void addDoctorAppointment(DoctorsAppointmentsInput doctorAppointmentsInput) throws DoctorsDoesntExistsExcpetion, PatientsDoesntExistsException, AppointmentExistsException, InvalidTimeException,DoctorScheduleConflictException,InvalidAppointmentIntervalException,
            WorkersScheduleNotSetException {

        if(!patientsRepository.existsById(doctorAppointmentsInput.getDniPatients())){
            throw new PatientsDoesntExistsException("Patient doesn't exist");
        }
        Doctors doctor = doctorsRepository.findById(doctorAppointmentsInput.getDniDoctors()).orElse(null);
        if(doctor == null){
            throw new DoctorsDoesntExistsExcpetion("Doctor doesn't exist");
        }
        //comprueba si el horario del doctor está añadido si no se ha añadido el horario salta la excepcion
        if(doctor.getStartTime() == null || doctor.getEndTime() == null){
            throw new WorkersScheduleNotSetException("Doctor's schedule is not set yet. Please set the doctor's schedule before adding appointments. ");
        }
        //comprueba si está en la ventana de tiempo
        if(!schedulesService.getTimeWindow(LocalDate.now()).contains(doctorAppointmentsInput.getDays())){
            throw new InvalidTimeException("The appointment must be scheduled for the next week.");
        }
        //comprueba si la hora está dentro del horario del doctor
        if(doctorAppointmentsInput.getHours().isBefore(doctor.getStartTime()) || doctorAppointmentsInput.getHours().isAfter(doctor.getEndTime())){
            throw new DoctorScheduleConflictException("The appointment is outside the doctor's working hours");
        }
        // comprueba si la cita existe para ese dia y hora
        if (doctorsAppointmentsRepository.existsByDniDoctorsAndDaysAndHours(doctorAppointmentsInput.getDniDoctors(),
                doctorAppointmentsInput.getDays(), doctorAppointmentsInput.getHours())){
            throw new AppointmentExistsException("There is already an appointment scheduled at the requested time with this doctor.");
        }

        //comprueba el intervalo de tiempo que yo le he puesto de 10 minutos
        if(doctorsAppointmentsRepository.existsByDaysAndHoursBetween(doctorAppointmentsInput.getDays(),
                doctorAppointmentsInput.getHours().minusMinutes(appointmentInterval), doctorAppointmentsInput.getHours().plusMinutes(appointmentInterval))) {
            throw new InvalidAppointmentIntervalException("There is already an appointment scheduled within 10 minutes of the requested time.");
        }

        DoctorsAppointments appointment = DoctorsAppointments.getAppointmentsDoctors(doctorAppointmentsInput);
        doctorsAppointmentsRepository.save(appointment);
    }
    /*
    consulta huecos libres en la agenda del doctor para la ventana temporal.
     */
    public List<Map<String,Object>> getAvailableSlotsForDoctor(Doctors doctor, LocalDate startDate) throws InvalidTimeException{

        List<Map<String,Object>> availableSlotsPerDayList = new ArrayList<>();
        //obtener las horas de trabajo del medico
        // Validar si la fecha está dentro del "time window" válido
        List<LocalDate> timeWindow = schedulesService.getTimeWindow(LocalDate.now());
        if (!timeWindow.contains(startDate)) {
            throw new InvalidTimeException("The consult is outside the time window.");
        }

        LocalTime startTime = doctor.getStartTime();
        LocalTime endTime = doctor.getEndTime();

        LocalDate endDate = startDate.plusDays(4); // Obtener la fecha de finalización (5 días después)

        //bucle para que recorra la semana desde startDate
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                // Ignorar los fines de semana
                List<LocalTime> hoursOfDay = schedulesService.getHoursList(startTime, endTime);

                // Obtener las citas del doctor por día
                List<DoctorsAppointments> appointmentsForDay = doctorsAppointmentsRepository
                        .findByDniDoctorsAndDaysOrderByHours(doctor.getDni(), date);

                for (DoctorsAppointments appointment : appointmentsForDay) {
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
    /*
    consulta las citas que tiene un paciente con el doctor para un dia determinado por horas,yo lo he puesto que compruebe las citas dentro de la venta temporal
    en la que se pueden pedir las citas puesto que entiendo que se quiere consultar las citas futuras y no las citas pasadas.Comprueba tambien si el paciente no
    tiene citas.
     */
    public List<DoctorsAppointmentsOutput> getAppointmentsForPatientOnDayDoctors(String dniPatients, LocalDate days) throws PatientsDoesntExistsException, EmptyListException,InvalidTimeException {
        if(!patientsRepository.existsById(dniPatients)) throw new PatientsDoesntExistsException("Patient doesn't exist");
        List<LocalDate>timeWindow = schedulesService.getTimeWindow(LocalDate.now());
        if(!timeWindow.contains(days)){
            throw new InvalidTimeException("Invalid day for the consult.");
        }
        List<DoctorsAppointments> listAppointmentsForPatientOnDay = doctorsAppointmentsRepository.findByDniPatientsAndDaysOrderByHours(dniPatients, days);
        if(listAppointmentsForPatientOnDay.isEmpty()){
            throw new EmptyListException("The appointments list for this patient is empty");
        }
        List<DoctorsAppointmentsOutput> appointmentsOutputDayDoctorList = new ArrayList<>();
        for(DoctorsAppointments appointment : listAppointmentsForPatientOnDay){
            appointmentsOutputDayDoctorList.add(DoctorsAppointmentsOutput.getAppointmentsDoctors(appointment));
        }
        return appointmentsOutputDayDoctorList;
    }
    /*
    consulta las citas que tiene un doctor para toda la semana ordenado por dia y horas, comprobando si el doctor consultado existe, si la lista está vacia o no,
    y solo consulta las citas dentro de la ventana de tiempo.
     */
    public List<DoctorsAppointmentsOutput> getAppointmentsForDoctorsByWeek(String dniDoctors) throws DoctorsDoesntExistsExcpetion,EmptyListException {
        if (!doctorsRepository.existsById(dniDoctors)) {
            throw new DoctorsDoesntExistsExcpetion("Doctor doesn't exist.");
        }

        // Obtener el rango de tiempo válido (la "semana" o "timeWindow")
        List<LocalDate> timeWindow = schedulesService.getTimeWindow(LocalDate.now());

        // Obtener las citas dentro de la semana actual (o el rango de tiempo deseado)
        List<DoctorsAppointments> listAppointmentsForDoctorsByWeek = new ArrayList<>();
        for (LocalDate day : timeWindow) {
            List<DoctorsAppointments> appointmentsForDay = doctorsAppointmentsRepository
                    .findByDniDoctorsAndDaysOrderByHours(dniDoctors, day);
            listAppointmentsForDoctorsByWeek.addAll(appointmentsForDay);
        }

        if (listAppointmentsForDoctorsByWeek.isEmpty()) {
            throw new EmptyListException("The appointments list for this doctor is empty.");
        }

        List<DoctorsAppointmentsOutput> appointmentOutputDoctorWeekList = new ArrayList<>();
        for (DoctorsAppointments appointment : listAppointmentsForDoctorsByWeek) {
            appointmentOutputDoctorWeekList.add(DoctorsAppointmentsOutput.getAppointmentsDoctors(appointment));
        }

        return appointmentOutputDoctorWeekList;
    }
    /*
    consulta los medicos más ocupados en la ventana temporal.
     */

    public List<Map<String,Object>> getMostOccupiedDoctorsInTimeWindow(LocalDate startDate) throws InvalidTimeException {
        List<LocalDate> timeWindow = schedulesService.getTimeWindow(LocalDate.now());

        if (!timeWindow.contains(startDate)) {
            throw new InvalidTimeException("Requested start date is outside the current time window.");
        }

        Map<String, Integer> doctorAppointmentsCount = new HashMap<>();

        for (LocalDate date : timeWindow) {
            List<Doctors> allDoctors = doctorsRepository.findAll();

            for (Doctors doctor : allDoctors) {
                List<DoctorsAppointments> appointments = doctorsAppointmentsRepository
                        .findByDniDoctorsAndDaysOrderByHours(doctor.getDni(), date);

                int appointmentCount = appointments.size();

                doctorAppointmentsCount.put(doctor.getName(),doctorAppointmentsCount.getOrDefault(doctor.getName(),0)+appointmentCount);
            }
        }
        // Convertir el Map en una lista ordenada
        List<Map.Entry<String, Integer>> sortedDoctorsList = doctorAppointmentsCount.entrySet()
                .stream()
                .sorted(Map.Entry.<String,Integer>comparingByValue().reversed())
                .collect(Collectors.toList());
        List<Map<String,Object>>result = new ArrayList<>();
        for(Map.Entry<String,Integer> entry : sortedDoctorsList){
            Map<String,Object> doctorInfo = new HashMap<>();
            doctorInfo.put("name",entry.getKey());
            doctorInfo.put("appointmentCount",entry.getValue());
            result.add(doctorInfo);
        }
        return result;
    }
}
