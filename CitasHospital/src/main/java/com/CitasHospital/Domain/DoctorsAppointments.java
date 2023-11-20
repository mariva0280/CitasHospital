package com.CitasHospital.Domain;

import com.CitasHospital.Controller.Inputs.DoctorsAppointmentsInput;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Data
@ToString
@NoArgsConstructor
public class DoctorsAppointments {
    @Id
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

    public DoctorsAppointments(String dniPatients, String dniDoctors, LocalDate days, LocalTime hours) {
        this.id = UUID.randomUUID().toString();
        this.dniPatients = dniPatients;
        this.dniDoctors = dniDoctors;
        this.days = days;
        this.hours = hours;
    }

    public static DoctorsAppointments getAppointmentsDoctors(DoctorsAppointmentsInput doctorsAppointmentsInput){
        return new DoctorsAppointments(doctorsAppointmentsInput.getDniPatients(), doctorsAppointmentsInput.getDniDoctors(),doctorsAppointmentsInput.getDays(),doctorsAppointmentsInput.getHours());
    }
}
