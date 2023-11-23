package com.CitasHospital.Service;


import com.CitasHospital.Exception.InvalidTimeException;
import org.springframework.stereotype.Service;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
@Service

public class SchedulesService {

    public  List<LocalDate> getTimeWindow(LocalDate day) {
        List<LocalDate> nextWeek = new ArrayList<>();
        LocalDate day1 = day.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        for (int i = 0; i <= 4; i++) {
            day = day1.plusDays(i);
            nextWeek.add(day);
        }
        return nextWeek;
    }

    public List<LocalTime> getHoursList(LocalTime startTime, LocalTime endTime) throws InvalidTimeException{
        List<LocalTime>hoursList = new ArrayList<>();

        if(startTime.isAfter(endTime)){
            throw new InvalidTimeException("The start time must be before the end time");
        }

        //añade cada hora dentro del rango a hoursList
        while(!startTime.isAfter(endTime)){
            hoursList.add(startTime);
            startTime=startTime.plusMinutes(10);
        }

        return hoursList;
    }

}
