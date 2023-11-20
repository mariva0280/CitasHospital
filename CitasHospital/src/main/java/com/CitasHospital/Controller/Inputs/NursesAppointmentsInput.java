package com.CitasHospital.Controller.Inputs;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
@Data
@NoArgsConstructor

public class NursesAppointmentsInput {
    @NotNull(message = "Id cannot be null")
    @NotEmpty(message = "Id cannot be empty")
    private String id;
    @NotNull(message = "Dni patients cannot be null")
    @NotEmpty(message = "Dni patients cannot be empty")
    private String dniPatients;
    @NotNull(message = "Dni nurse cannot be null")
    @NotEmpty(message = "Dni nurse cannot be empty")
    private String dniNurses;
    private LocalDate days;

    private LocalTime hours;

    public NursesAppointmentsInput(String dniPatients, String dniNurses, LocalDate days, LocalTime hours) {
        this.id = null;
        this.dniPatients = dniPatients;
        this.dniNurses = dniNurses;
        this.days = days;
        this.hours = hours;
    }
}
