package com.CitasHospital.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NursesDoesntExistsException.class)
    public ResponseEntity<String> handleNursesDoesntExistsException(NursesDoesntExistsException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(DoctorsDoesntExistsExcpetion.class)
    public ResponseEntity<String> handleDoctorsDoesntExistsException(DoctorsDoesntExistsExcpetion ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(PatientsDoesntExistsException.class)
    public ResponseEntity<String>handlePatientsDoesntExistsException(PatientsDoesntExistsException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(NursesExistException.class)
    public ResponseEntity<String> handleNursesExistException(NursesExistException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
    @ExceptionHandler(DoctorsExistException.class)
    public ResponseEntity<String>handleDoctorsExistException(DoctorsExistException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
    @ExceptionHandler(PatientsExistException.class)
    public ResponseEntity<String>handelPatientsExistException(PatientsExistException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
    @ExceptionHandler(NurseScheduleConflictException.class)
    public ResponseEntity<String>handleNurseScheduleConflictException(NurseScheduleConflictException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
    @ExceptionHandler(DoctorScheduleConflictException.class)
    public ResponseEntity<String>handleDoctorScheduleConflictException(DoctorScheduleConflictException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidTimeException.class)
    public ResponseEntity<String> handleInvalidTimeException(InvalidTimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

   @ExceptionHandler(EmptyListException.class)
   public ResponseEntity<String>handleEmptyListException(EmptyListException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
   }

    @ExceptionHandler(InvalidAppointmentIntervalException.class)
    public ResponseEntity<String>handleInvalidAppointmentIntervalException(InvalidAppointmentIntervalException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @ExceptionHandler(AppointmentExistsException.class)
    public ResponseEntity<String>handleAppointmentValidationException(AppointmentExistsException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
    @ExceptionHandler(WorkersScheduleNotSetException.class)
    public ResponseEntity<String>handleWorkersScheduleNotSetException(WorkersScheduleNotSetException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(InvalidDniException.class)
    public ResponseEntity<String>handleInvalidFieldException(InvalidDniException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

}
