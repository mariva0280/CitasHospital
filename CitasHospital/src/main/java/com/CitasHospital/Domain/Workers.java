package com.CitasHospital.Domain;

import com.CitasHospital.Exception.InvalidTimeException;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.*;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@MappedSuperclass
public class Workers {
    @Id
    @NotNull(message = "Dni cannot be null")
    @NotEmpty(message = "Dni cannot be empty")
    @Pattern(regexp = "^[0-9]{8}[a-zA-Z]$", message = "Invalid DNI format")
    private String dni;
    @NotNull(message = "Name cannot be null")
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @Min(value = 1 , message = ("The membership number must be greater than or equal to 1"))
    @Max(value = 999999, message =("The membership number must be less thar or equal to 999999"))
    private int numColeg;

    private LocalTime startTime;

    private LocalTime endTime;


    public Workers(String name, String dni, int numColeg) {
        this.name = name;
        this.dni = dni;
        this.numColeg = numColeg;
    }


    public void addSchedule(LocalTime startTimeSche, LocalTime endTimeSche)throws InvalidTimeException {
        if(startTime.isAfter(endTime)) throw new InvalidTimeException("The start time must be before the end time");
        this.startTime = startTimeSche;
        this.endTime = endTimeSche;
    }
}
