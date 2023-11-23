package com.CitasHospital.Controller.Inputs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor

public class DoctorsAppointmentsInput {
    @NotNull(message = "Id cannot be null")
    @NotEmpty(message = "Id cannot be empty")
    private String id;
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

    public DoctorsAppointmentsInput(String dniPatients, String dniDoctors, LocalDate days, LocalTime hours) {
        this.id = null;
        this.dniPatients = dniPatients;
        this.dniDoctors = dniDoctors;
        this.days = days;
        this.hours = hours;
    }
}
