package com.CitasHospital.Domain;

import com.CitasHospital.Controller.Inputs.NursesInput;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.time.LocalTime;


@Entity
@Data
@NoArgsConstructor
public class Nurses extends Workers{

    private LocalTime startTime;
    private LocalTime endTime;

    public Nurses(String name, String dni, int numColeg) {
        super(name, dni, numColeg);

    }

    public static Nurses getNurses(NursesInput nursesInput){
        return new Nurses(nursesInput.getName(),nursesInput.getDni(),nursesInput.getNumColeg());
    }
    @Override
    public LocalTime getStartTime() {
        return startTime;
    }
    @Override
    public LocalTime getEndTime() {
        return endTime;
    }
    @Override
    public void addSchedule(LocalTime startTime, LocalTime endTime){
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
