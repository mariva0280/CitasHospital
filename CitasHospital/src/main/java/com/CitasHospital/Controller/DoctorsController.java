package com.CitasHospital.Controller;

import com.CitasHospital.Controller.Inputs.DoctorsAppointmentsInput;
import com.CitasHospital.Controller.Inputs.DoctorsInput;
import com.CitasHospital.Controller.Outputs.DoctorsAppointmentsOutput;
import com.CitasHospital.Domain.Doctors;
import com.CitasHospital.Exception.*;
import com.CitasHospital.Repository.DoctorsRepository;
import com.CitasHospital.Service.DoctorsService;
import com.CitasHospital.Service.SchedulesService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;
import static org.springframework.format.annotation.DateTimeFormat.ISO.TIME;

@RestController

public class DoctorsController {
    @Autowired
    private DoctorsRepository doctorsRepository;
    @Autowired
    private DoctorsService doctorsService;
    @Autowired
    private SchedulesService schedulesService;


    @ApiOperation(value = "Adding new doctor")
    @PostMapping("/doctors")

    public ResponseEntity<String>addDoctors(
            @Valid @RequestBody DoctorsInput doctorsInput){
        try{
            doctorsService.addDoctors(doctorsInput);
            return ResponseEntity.status(HttpStatus.CREATED).body("Doctor added successfully.");
        }catch (InvalidDniException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (DoctorsExistException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
    @ApiOperation(value = "Adding new schedule")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Doctor schedule added successfully")
    })
    @PostMapping("doctors/{dni}/add-schedule/{startTime}/{endTime}")
    public ResponseEntity<Doctors> addScheduleDoctors(@Valid @PathVariable String dni,@Valid @PathVariable LocalTime startTime, @Valid @PathVariable LocalTime endTime) throws
            DoctorsDoesntExistsExcpetion,DoctorScheduleConflictException {
        try{
            doctorsService.addScheduleDoctors(dni, startTime, endTime);
            return  ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (DoctorsDoesntExistsExcpetion e){
            throw e;
        }catch (DoctorScheduleConflictException e){
            throw e;
        }
    }
    @ApiOperation(value = "Updating doctor schedule for the next time window")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Doctor schedule updated successfully")
    })
    @PutMapping("doctors/{dni}/update-schedule/{startTime}/{endTime}")
    public ResponseEntity<Doctors> updateDoctorSchedule(
            @Valid @PathVariable String dni,
            @Valid @RequestParam LocalTime startTime,
            @Valid @RequestParam LocalTime endTime) throws DoctorsDoesntExistsExcpetion,DoctorScheduleConflictException {
        try {
            doctorsService.updateScheduleDoctor(dni, startTime, endTime);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (DoctorsDoesntExistsExcpetion e) {
            throw e;
        }catch (DoctorScheduleConflictException e) {
            throw e;
        }
    }
   @ApiOperation(value = "Adding a new appointment for a doctor")
    @PostMapping("/doctors/{dni}/appointments")
    public ResponseEntity<String> addDoctorAppointment(

            @Valid @RequestBody DoctorsAppointmentsInput doctorsAppointmentsInput) {
        try {
            doctorsService.addDoctorAppointment(doctorsAppointmentsInput);
            return ResponseEntity.status(HttpStatus.CREATED).body("Appointment added successfully");
        }catch (PatientsDoesntExistsException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (DoctorsDoesntExistsExcpetion e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (InvalidTimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (DoctorScheduleConflictException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }catch (AppointmentExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }catch (InvalidAppointmentIntervalException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (WorkersScheduleNotSetException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }
    @ApiOperation(value = "Check available slots for doctors.")
    @GetMapping("doctors/{dni}/available-slots")
    public ResponseEntity<List<Map<String,Object>>> getAvailableSlotsForDoctors(
            @PathVariable String dni,
            @RequestParam(name = "startDate") @DateTimeFormat(iso = DATE) LocalDate startDate
    ) throws InvalidTimeException,DoctorsDoesntExistsExcpetion{
        try {
            Doctors doctor = doctorsRepository.findById(dni).orElse(null);
            if(doctor == null){
                throw new DoctorsDoesntExistsExcpetion("Doctor doesn't exist in data base");
            }
            List<Map<String,Object>> availableSlotsPerDayList = doctorsService.getAvailableSlotsForDoctor(doctor,startDate);
            return ResponseEntity.ok(availableSlotsPerDayList);
        }catch (InvalidTimeException e) {
            throw e;
        }
    }
    @ApiOperation(value = "Consult appointments of patient per day ordered by hours.")
    @GetMapping("doctors/appointments/patients/{dniPatients}/{days}")
    public ResponseEntity<List<DoctorsAppointmentsOutput>>getAppointmentsForPatientOnDayDoctors(
            @Valid @PathVariable String dniPatients,
            @DateTimeFormat(iso = DATE) @Valid @PathVariable LocalDate days) throws PatientsDoesntExistsException,EmptyListException,InvalidTimeException{
        try{
            List<DoctorsAppointmentsOutput> appointmentsList = doctorsService.getAppointmentsForPatientOnDayDoctors(dniPatients, days);
            return ResponseEntity.ok(appointmentsList);
        }catch (PatientsDoesntExistsException e){
            throw e;
        }catch (EmptyListException e) {
            throw e;
        }catch (InvalidTimeException e){
            throw e;
        }
    }
    @ApiOperation(value = "List of doctor appointments per week ordered by days and hours.")
    @GetMapping("/doctors/{dniDoctors}/appointments")
    public ResponseEntity<List<DoctorsAppointmentsOutput>>getAppointmentsForDoctorsByWeek(
            @Valid @PathVariable String dniDoctors) throws DoctorsDoesntExistsExcpetion,EmptyListException{
        try{
            List<DoctorsAppointmentsOutput> appointmentsList = doctorsService.getAppointmentsForDoctorsByWeek(dniDoctors);
            return ResponseEntity.ok(appointmentsList);
        }catch (DoctorsDoesntExistsExcpetion e){
            throw e;
        }catch (EmptyListException e) {
            throw e;
        }
    }
    @ApiOperation(value = "List of doctor most occupied in time window.")
    @GetMapping("doctors/most-occupied")
    public ResponseEntity<List<Map<String,Object>>>getMostOccupiedDoctorsInTimeWindow(
            @RequestParam(name = "startDate") @DateTimeFormat(iso = DATE) LocalDate startDate) throws InvalidTimeException{
        try{
            List<Map<String,Object>>mostOccupiedDoctors=doctorsService.getMostOccupiedDoctorsInTimeWindow(startDate);
            return ResponseEntity.ok(mostOccupiedDoctors);
        }catch (InvalidTimeException e){
            throw e;
        }
    }
}
