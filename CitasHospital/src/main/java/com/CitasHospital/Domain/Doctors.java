package com.CitasHospital.Domain;

import com.CitasHospital.Controller.Inputs.DoctorsInput;
import lombok.Data;


import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.validation.constraints.*;
import java.time.LocalTime;


@Entity
@Data
@NoArgsConstructor
public class Doctors extends Workers{


    @Min(value = 0 , message = ("The experience cannot be less than zero"))
    @Max(value = 100 , message = ("The experience cannot be more than 100"))
    private int experience;
    private LocalTime startTime;
    private LocalTime endTime;

    public Doctors(String name, String dni, int numColeg,int experience) {
        super(name, dni, numColeg);
        this.experience = experience;
    }

    public static Doctors getDoctors(DoctorsInput doctorsInput){
        return new Doctors(doctorsInput.getName(),doctorsInput.getDni(),doctorsInput.getNumColeg(),doctorsInput.getExperience());
    }
    @Override
    public LocalTime getStartTime(){
        return startTime;
    }
    @Override
    public LocalTime getEndTime(){
        return endTime;
    }
    @Override
    public void addSchedule(LocalTime startTime, LocalTime endTime)  {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
