package com.CitasHospital.Domain;

import com.CitasHospital.Controller.Inputs.NursesAppointmentsInput;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
@Entity
@Data
@NoArgsConstructor
@ToString

public class NursesAppointments {
    @Id
    @NotNull(message = "Id cannot be null")
    @NotEmpty(message = "Id cannot be empty")
    private String id;
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

    public NursesAppointments(String dniPatients, String dniNurses, LocalDate days, LocalTime hours) {
        this.id = UUID.randomUUID().toString();
        this.dniPatients = dniPatients;
        this.dniNurses = dniNurses;
        this.days = days;
        this.hours = hours;
    }

    public static NursesAppointments getAppointmentsNurses(NursesAppointmentsInput nursesAppointmentsInput){
        return new NursesAppointments(nursesAppointmentsInput.getDniPatients(),nursesAppointmentsInput.getDniNurses(),nursesAppointmentsInput.getDays(),nursesAppointmentsInput.getHours());
    }
}
