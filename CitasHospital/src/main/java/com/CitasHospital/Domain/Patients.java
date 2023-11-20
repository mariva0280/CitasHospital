package com.CitasHospital.Domain;

import com.CitasHospital.Controller.Inputs.PatientsInput;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
@Data
@NoArgsConstructor
public class Patients {
    @Id
    @NotNull(message = "Dni cannot be null")
    @NotEmpty(message = "Dni cannot be empty")
    @Pattern(regexp = "^[0-9]{8}[a-zA-Z]$", message = "Invalid DNI format")
    private String dni;

    @NotNull(message = "Name cannot be null")
    @NotEmpty(message = "Name cannot be empty")
    private String name;
    @NotNull(message = "Direction cannot be null")
    @NotEmpty(message = "Direction cannot be empty")
    private String direction;

    public Patients(String dni, String name, String direction) {
        this.dni = dni;
        this.name = name;
        this.direction = direction;
    }
    public static Patients getPatients(PatientsInput patientsInput){
        return new Patients(patientsInput.getDni(), patientsInput.getName(), patientsInput.getDirection());
    }
}
