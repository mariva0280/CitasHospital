package com.CitasHospital.Controller;

import com.CitasHospital.Controller.Inputs.DoctorsAppointmentsInput;
import com.CitasHospital.Controller.Inputs.DoctorsInput;
import com.CitasHospital.Domain.Doctors;
import com.CitasHospital.Domain.DoctorsAppointments;
import com.CitasHospital.Exception.*;
import com.CitasHospital.Repository.DoctorsRepository;
import com.CitasHospital.Service.DoctorsService;
import com.CitasHospital.Service.SchedulesService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

@RestController

public class DoctorsController {
    @Autowired
    private DoctorsRepository doctorsRepository;
    @Autowired
    private DoctorsService doctorsService;
    @Autowired
    private SchedulesService schedulesService;


    @ApiOperation(value = "Adding new doctor")
    @ApiResponses(value = {
            @ApiResponse(code = 201,message = "Doctor added successfully."),
            @ApiResponse(code = 226, message = "Doctor already exists in data base")
    })
    @PostMapping("/doctors")

    public ResponseEntity<String>addDoctors(
            @ApiParam(value = "Information about add doctor", required = true)
            @Valid @RequestBody DoctorsInput doctorsInput) {
        try{
            doctorsService.addDoctors(doctorsInput);
            return ResponseEntity.status(HttpStatus.CREATED).body("Doctor added successfully.");
        }catch (DoctorsExistException e){
            return ResponseEntity.status(HttpStatus.IM_USED).body("Doctor already exists in data base.");
        }
    }

    @ApiOperation(value = "Adding new schedule")
    @ApiResponses(value = {
            @ApiResponse(code = 201,message = "Hour added successfully"),
            @ApiResponse(code = 400, message = "Doctor doesn't exist in data base"),
            @ApiResponse(code= 408, message = "Invalid time"),
            @ApiResponse(code = 409, message = "Doctor already has a schedule assigned")
    })

    @PostMapping("doctors/{dni}/add-schedule/{startTime}/{endTime}")
    public ResponseEntity<Doctors> addScheduleDoctors(@Valid @PathVariable String dni,@Valid @RequestParam LocalTime startTime,@Valid @RequestParam LocalTime endTime) {
        try{
            doctorsService.addScheduleDoctors(dni, startTime, endTime);
            return  ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (DoctorsDoesntExistsExcpetion e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        }catch (InvalidTimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }catch (DoctorScheduleConflictException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

    }
    @ApiOperation(value = "Updating doctor schedule for the next time window")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Doctor schedule updated successfully"),
            @ApiResponse(code = 400, message = "Doctor doesn't exist in data base"),
            @ApiResponse(code = 408, message = "Invalid time"),
            @ApiResponse(code = 409, message = "Doctor doesn't have a schedule assigned yet")
    })
    @PutMapping("doctors/{dni}/update-schedule/{startTime}/{endTime}")
    public ResponseEntity<Doctors> updateDoctorSchedule(
            @Valid @PathVariable String dni,
            @Valid @RequestParam LocalTime startTime,
            @Valid @RequestParam LocalTime endTime) {
        try {
            doctorsService.updateScheduleDoctor(dni, startTime, endTime);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (DoctorsDoesntExistsExcpetion e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InvalidTimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (DoctorScheduleConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
   @ApiOperation(value = "Adding a new appointment for a doctor")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Appointment added successfully"),
            @ApiResponse(code = 404, message = "Patient or doctor doesn't exist in the database."),
            @ApiResponse(code = 400, message = "Appointment validation hours and week failed."),
            @ApiResponse(code = 409, message  ="Appointment exists or isn't in the correct interval or the doctor's schedule isn't add")
    })
    @PostMapping("/doctors/{dni}/appointments")
    public ResponseEntity<String> addDoctorAppointment(

            @Valid @RequestBody DoctorsAppointmentsInput doctorsAppointmentsInput) {
        try {
            doctorsService.addDoctorAppointment(doctorsAppointmentsInput);
            return ResponseEntity.status(HttpStatus.CREATED).body("Appointment added successfully");
        }catch (PatientsDoesntExistsException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient doesn't exist in the database");
        }catch (DoctorsDoesntExistsExcpetion e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor doesn't exist in the database");
        }catch (InvalidTimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The appointment must be scheduled for the next week.");
        }catch (DoctorScheduleConflictException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The appointment is outside the doctor's working hours.");
        }catch (AppointmentExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("There is already an appointment scheduled at the requested time.");
        }catch (InvalidAppointmentIntervalException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("There is already an appointment scheduled within 10 minutes of the requested time.");
        }catch (WorkersScheduleNotSetException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Doctor's schedule is not set yet. Please set the doctor's schedule before adding appointments.");
        }

    }
    @ApiOperation(value = "Check available slots for doctors.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "These are the spaces available."),
            @ApiResponse(code = 404, message = "Doctor doesn't exist in the database."),
            @ApiResponse(code = 400, message = "The consult is outside the time window.")
    })
    @GetMapping("doctors/{dni}/available-slots")
    public ResponseEntity<List<Map<String,Object>>> getAvailableSlotsForDoctors(
            @PathVariable String dni,
            @RequestParam(name = "startDate") @DateTimeFormat(iso = DATE) LocalDate startDate
    ) {
        try {
            // Obtener el rango de fechas dentro del cual se buscarán los huecos disponibles
            Doctors doctor = doctorsRepository.findById(dni).orElse(null);
            if(doctor == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            List<Map<String,Object>> availableSlotsPerDayList = doctorsService.getAvailableSlotsForDoctor(doctor,startDate);
            return ResponseEntity.ok(availableSlotsPerDayList);
        } catch (EmptyListException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }catch (InvalidTimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    @ApiOperation(value = "Consult appointments of patient per day ordered by hours.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "These are the appointments for patient."),
            @ApiResponse(code = 404, message = "Patient doesn't exist in the database."),
            @ApiResponse(code = 400, message = "The appointments list for this patient is empty or is outside of time window.")
    })
    @GetMapping("doctors/appointments/patients/{dniPatients}/{days}")
    public ResponseEntity<List<DoctorsAppointments>>getAppointmentsForPatientOnDayDoctors(
            @ApiParam(value = "Dni of the patient", required = true) @Valid @PathVariable String dniPatients,
            @DateTimeFormat(iso = DATE) @Valid @PathVariable LocalDate days){
        try{
            List<DoctorsAppointments> appointmentsList = doctorsService.getAppointmentsForPatientOnDayDoctors(dniPatients, days);
            return ResponseEntity.ok(appointmentsList);
        }catch (PatientsDoesntExistsException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch (EmptyListException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }catch (InvalidTimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    @ApiOperation(value = "List of doctor appointments per week ordered by days and hours.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "These are the appointments for doctor per week."),
            @ApiResponse(code = 404, message = "Doctor doesn't exist in the database."),
            @ApiResponse(code = 400, message = "The appointments list for this doctor is empty.")
    })
    @GetMapping("/doctors/{dniDoctors}/appointments")
    public ResponseEntity<List<DoctorsAppointments>>getAppointmentsForDoctorsByWeek(
            @ApiParam(value = "Dni of the doctor", required = true) @Valid @PathVariable String dniDoctors){
        try{
            List<DoctorsAppointments> appointmentsList = doctorsService.getAppointmentsForDoctorsByWeek(dniDoctors);
            return ResponseEntity.ok(appointmentsList);
        }catch (DoctorsDoesntExistsExcpetion e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch (EmptyListException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        }
    }
    @ApiOperation(value = "List of doctor most occupied in time window.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully retrieved the most occupied doctors."),
            @ApiResponse(code = 400, message = "Cannot retrieve most occupied doctors outside the current time window.")
    })
    @GetMapping("doctors/most-occupied")
    public ResponseEntity<List<Map<String,Object>>>getMostOccupiedDoctorsInTimeWindow(
            @RequestParam(name = "startDate") @DateTimeFormat(iso = DATE) LocalDate startDate){
        try{
            List<Map<String,Object>>mostOccupiedDoctors=doctorsService.getMostOccupiedDoctorsInTimeWindow(startDate);
            return ResponseEntity.ok(mostOccupiedDoctors);
        }catch (InvalidTimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

}
