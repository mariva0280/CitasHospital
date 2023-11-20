package com.CitasHospital.Controller.Inputs;

import lombok.Data;

import javax.persistence.Id;
import javax.validation.constraints.*;

@Data
public class NursesInput {
    @NotNull(message = "Name cannot be null")
    @NotEmpty(message = "Name cannot be empty")
    private String name;
    @Id
    @NotNull(message = "Dni cannot be null")
    @NotEmpty(message = "Dni cannot be empty")
    @Pattern(regexp = "^[0-9]{8}[a-zA-Z]$", message = "Invalid DNI format")
    private String dni;
    @Min(value = 1 , message = ("The membership number must be greater than or equal to 1"))
    @Max(value = 999999, message =("The membership number must be less thar or equal to 999999"))
    private int numColeg;


    public NursesInput(String name, String dni, int numColeg) {
        this.name = name;
        this.dni = dni;
        this.numColeg = numColeg;

    }
}
