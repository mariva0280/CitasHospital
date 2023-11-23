package com.CitasHospital.Controller.Outputs;

import com.CitasHospital.Domain.NursesAppointments;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
@Data

public class NursesAppointmentsOutput {
    @NotNull(message = "Dni patients cannot be null")
    @NotEmpty(message = "Dni patients cannot be empty")
    private String dniPatients;
    @NotNull(message = "Dni nurse cannot be null")
    @NotEmpty(message = "Dni nurse cannot be empty")
    private String dniNurses;
    @NotNull(message = "Date cannot be null")
    private LocalDate days;
    @NotNull(message = "Time cannot be null")
    private LocalTime hours;

    public NursesAppointmentsOutput( String dniPatients, String dniNurses, LocalDate days, LocalTime hours) {
        this.dniPatients = dniPatients;
        this.dniNurses = dniNurses;
        this.days = days;
        this.hours = hours;
    }

    public static NursesAppointmentsOutput getAppointmentsNurses(NursesAppointments nursesAppointments){
        return new NursesAppointmentsOutput(nursesAppointments.getDniPatients(), nursesAppointments.getDniNurses(),
                nursesAppointments.getDays(),nursesAppointments.getHours());
    }
}
