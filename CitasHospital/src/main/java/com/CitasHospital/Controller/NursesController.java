package com.CitasHospital.Controller;

import com.CitasHospital.Controller.Inputs.NursesAppointmentsInput;
import com.CitasHospital.Controller.Inputs.NursesInput;
import com.CitasHospital.Domain.Doctors;
import com.CitasHospital.Domain.Nurses;
import com.CitasHospital.Domain.NursesAppointments;
import com.CitasHospital.Exception.*;
import com.CitasHospital.Repository.NursesRepository;
import com.CitasHospital.Service.NursesService;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;


@RestController

public class NursesController {
    @Autowired
    private NursesRepository nursesRepository;
    @Autowired
    private NursesService nursesService;
    @Autowired
    private SchedulesService schedulesService;

    @ApiOperation(value = "Adding new nurse")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Nurse added successfully"),
            @ApiResponse(code = 226, message = "Nurse already exists in data base.")
    })

    @PostMapping("/nurses")
    public ResponseEntity<String>addNurses(
            @ApiParam(value = "Information about add nurse", required = true)
            @Valid @RequestBody NursesInput nursesInput){
        try{
            nursesService.addNurses(nursesInput);
            return ResponseEntity.status(HttpStatus.CREATED).body("Nurse added successfully");
        }catch (NursesExistException e) {
            return ResponseEntity.status(HttpStatus.IM_USED).body("Nurse already exists in data base.");
        }

    }
    @ApiOperation(value = "Adding new schedule")
    @ApiResponses(value = {
            @ApiResponse(code = 201,message = "Hour added successfully"),
            @ApiResponse(code = 400, message = "Nurse doesn't exist in data base"),
            @ApiResponse(code= 408, message = "Invalid time"),
            @ApiResponse(code = 409, message = "Nurse already has a schedule assigned")
    })

    @PostMapping("nurses/{dni}/add-schedule/{startTime}/{endTime}")
    public ResponseEntity<Nurses> addScheduleNurses(@Valid @PathVariable String dni, @Valid @RequestParam LocalTime startTime, @Valid @RequestParam LocalTime endTime){
        try{
            nursesService.addScheduleNurses(dni, startTime,endTime);
            return  ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (NursesDoesntExistsException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch (InvalidTimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }catch (NurseScheduleConflictException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

    }
    @ApiOperation(value = "Updating nurse schedule for the next time window")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Nurse schedule updated successfully"),
            @ApiResponse(code = 400, message = "Nurse doesn't exist in data base"),
            @ApiResponse(code = 408, message = "Invalid time"),
            @ApiResponse(code = 409, message = "Nurse doesn't have a schedule assigned yet")
    })
    @PutMapping("nurses/{dni}/update-schedule/{startTime}/{endTime}")
    public ResponseEntity<Doctors> updateDoctorSchedule(
            @Valid @PathVariable String dni,
            @Valid @RequestParam LocalTime startTime,
            @Valid @RequestParam LocalTime endTime) {
        try {
            nursesService.updateScheduleNurse(dni, startTime, endTime);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (NursesDoesntExistsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InvalidTimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (NurseScheduleConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    @ApiOperation(value = "Adding a new appointment for a nurse")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Appointment added successfully."),
            @ApiResponse(code = 404, message = "Patient or nurse doesn't exist in the database."),
            @ApiResponse(code = 400, message = "Appointment validation hours and week failed."),
            @ApiResponse(code = 409, message  ="Appointment exists or isn't in the correct interval or the nurse's schedule isn't add")
    })
    @PostMapping("/nurses/{dni}/appointments")
    public ResponseEntity<String> addNursesAppointment(

            @Valid @RequestBody NursesAppointmentsInput appointmentsInput) {
        try {
            nursesService.addNurseAppointment(appointmentsInput);
            return ResponseEntity.status(HttpStatus.CREATED).body("Appointment added successfully");
        }catch (PatientsDoesntExistsException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient doesn't exist in the database");
        }catch (NursesDoesntExistsException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nurse doesn't exist in the database");
        }catch (InvalidTimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The appointment must be scheduled for the next week.");
        }catch (NurseScheduleConflictException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The appointment is outside the nurse's working hours.");
        }catch (AppointmentExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("There is already an appointment scheduled at the requested time.");
        }catch (InvalidAppointmentIntervalException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("There is already an appointment scheduled within 10 minutes of the requested time.");
        }catch (WorkersScheduleNotSetException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Nurse's schedule is not set yet. Please set the nurse's schedule before adding appointments.");
        }
    }
    @ApiOperation(value = "Check available slots for nurses.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "These are the spaces available."),
            @ApiResponse(code = 404, message = "Nurse doesn't exist in the database."),
            @ApiResponse(code = 400, message = "The consult is outside the time window.")
    })
    @GetMapping("nurses/{dni}/available-slots")
    public ResponseEntity<List<Map<String,Object>>> getAvailableSlotsForNurse(
            @PathVariable String dni,
            @RequestParam(name = "startDate") @DateTimeFormat(iso = DATE) LocalDate startDate){
        try {
            Nurses nurse = nursesRepository.findById(dni).orElse(null);
            if(nurse == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            List<Map<String,Object>> availableSlotsPerDayList = nursesService.getAvailableSlotsForNurse(nurse, startDate);

            return ResponseEntity.ok(availableSlotsPerDayList);
        } catch (EmptyListException e) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
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
    @GetMapping("nurses/appointments/patients/{dniPatients}/{days}")
    public ResponseEntity<List<NursesAppointments>>getAppointmentsForPatientOnDayNurses(
            @ApiParam(value = "Dni of the patient", required = true) @Valid @PathVariable String dniPatients,
            @DateTimeFormat(iso = DATE) @Valid @PathVariable LocalDate days){
        try{
            List<NursesAppointments> appointmentsList = nursesService.getAppointmentsForPatientOnDayNurses(dniPatients, days);
            return ResponseEntity.ok(appointmentsList);
        }catch (PatientsDoesntExistsException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch (EmptyListException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }catch (InvalidTimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}

