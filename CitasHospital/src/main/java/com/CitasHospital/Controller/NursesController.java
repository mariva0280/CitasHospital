package com.CitasHospital.Controller;

import com.CitasHospital.Controller.Inputs.NursesAppointmentsInput;
import com.CitasHospital.Controller.Inputs.NursesInput;
import com.CitasHospital.Controller.Outputs.NursesAppointmentsOutput;
import com.CitasHospital.Domain.Doctors;
import com.CitasHospital.Domain.Nurses;
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

    @PostMapping("/nurses")
    public ResponseEntity<String>addNurses(
            @Valid @RequestBody NursesInput nursesInput){
        try{
            nursesService.addNurses(nursesInput);
            return ResponseEntity.status(HttpStatus.CREATED).body("Nurse added successfully");
        }catch (InvalidDniException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (NursesExistException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
    @ApiOperation(value = "Adding new schedule")
    @ApiResponses(value = {
            @ApiResponse(code = 200,message = "Nurses schedule added successfully")
    })

    @PostMapping("nurses/{dni}/add-schedule/{startTime}/{endTime}")
    public ResponseEntity<Nurses> addScheduleNurses(@Valid @PathVariable String dni, @Valid @PathVariable LocalTime startTime, @Valid @PathVariable LocalTime endTime) throws
            NursesDoesntExistsException, InvalidTimeException,NurseScheduleConflictException {
        try{
            nursesService.addScheduleNurses(dni, startTime,endTime);
            return  ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (NursesDoesntExistsException e){
            throw e;
        }catch (InvalidTimeException e){
            throw e;
        }catch (NurseScheduleConflictException e){
            throw e;
        }
    }
    @ApiOperation(value = "Updating nurse schedule for the next time window")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Nurse schedule updated successfully")
    })
    @PutMapping("nurses/{dni}/update-schedule/{startTime}/{endTime}")
    public ResponseEntity<Doctors> updateDoctorSchedule(
            @Valid @PathVariable String dni,
            @Valid @RequestParam LocalTime startTime,
            @Valid @RequestParam LocalTime endTime) throws NursesDoesntExistsException,InvalidTimeException,NurseScheduleConflictException {
        try {
            nursesService.updateScheduleNurse(dni, startTime, endTime);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (NursesDoesntExistsException e) {
            throw e;
        } catch (InvalidTimeException e) {
            throw e;
        } catch (NurseScheduleConflictException e) {
            throw e;
        }
    }
    @ApiOperation(value = "Adding a new appointment for a nurse")
    @PostMapping("/nurses/{dni}/appointments")
    public ResponseEntity<String> addNursesAppointment(

            @Valid @RequestBody NursesAppointmentsInput appointmentsInput) {
        try {
            nursesService.addNurseAppointment(appointmentsInput);
            return ResponseEntity.status(HttpStatus.CREATED).body("Appointment added successfully");
        }catch (PatientsDoesntExistsException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (NursesDoesntExistsException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (InvalidTimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (NurseScheduleConflictException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (AppointmentExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }catch (InvalidAppointmentIntervalException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }catch (WorkersScheduleNotSetException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
    @ApiOperation(value = "Check available slots for nurses.")
    @GetMapping("nurses/{dni}/available-slots")
    public ResponseEntity<List<Map<String,Object>>> getAvailableSlotsForNurse(
            @PathVariable String dni,
            @RequestParam(name = "startDate") @DateTimeFormat(iso = DATE) LocalDate startDate) throws InvalidTimeException,NursesDoesntExistsException{
        try {
            Nurses nurse = nursesRepository.findById(dni).orElse(null);
            if(nurse == null) {
                throw new NursesDoesntExistsException("Nurse doesn't exist in data base");
            }

            List<Map<String,Object>> availableSlotsPerDayList = nursesService.getAvailableSlotsForNurse(nurse, startDate);
            return ResponseEntity.ok(availableSlotsPerDayList);
        }catch (InvalidTimeException e) {
            throw e;
        }
    }
    @ApiOperation(value = "Consult appointments of patient per day ordered by hours.")
    @GetMapping("nurses/appointments/patients/{dniPatients}/{days}")
    public ResponseEntity<List<NursesAppointmentsOutput>>getAppointmentsForPatientOnDayNurses(
            @ApiParam(value = "Dni of the patient", required = true) @Valid @PathVariable String dniPatients,
            @DateTimeFormat(iso = DATE) @Valid @PathVariable LocalDate days) throws PatientsDoesntExistsException,EmptyListException,InvalidTimeException{
        try{
            List<NursesAppointmentsOutput> appointmentsList = nursesService.getAppointmentsForPatientOnDayNurses(dniPatients, days);
            return ResponseEntity.ok(appointmentsList);
        }catch (PatientsDoesntExistsException e){
            throw e;
        }catch (EmptyListException e) {
            throw e;
        }catch (InvalidTimeException e){
            throw e;
        }
    }
}

