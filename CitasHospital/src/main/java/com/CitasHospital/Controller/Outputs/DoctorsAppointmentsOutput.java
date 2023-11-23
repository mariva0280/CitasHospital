package com.CitasHospital.Controller.Outputs;

import com.CitasHospital.Domain.DoctorsAppointments;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
@Data
public class DoctorsAppointmentsOutput {
    @NotNull(message = "Dni patients cannot be null")
    @NotEmpty(message = "Dni patients cannot be empty")
    private String dniPatients;
    @NotNull(message = "Dni doctors cannot be null")
    @NotEmpty(message = "Dni doctors cannot be empty")
    private String dniDoctors;
    @NotNull(message = "Date cannot be null")
    private LocalDate days;
    @NotNull(message = "Time cannot be null")

    private LocalTime hours;

    public DoctorsAppointmentsOutput(String dniPatients, String dniDoctors, LocalDate days, LocalTime hours) {
        this.dniPatients = dniPatients;
        this.dniDoctors = dniDoctors;
        this.days = days;
        this.hours = hours;
    }
    public static DoctorsAppointmentsOutput getAppointmentsDoctors(DoctorsAppointments doctorsAppointments){
        return new DoctorsAppointmentsOutput(doctorsAppointments.getDniPatients(), doctorsAppointments.getDniDoctors(),
                doctorsAppointments.getDays(),doctorsAppointments.getHours());
    }
}
