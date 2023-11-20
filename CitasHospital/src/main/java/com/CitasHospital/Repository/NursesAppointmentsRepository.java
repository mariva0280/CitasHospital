package com.CitasHospital.Repository;

import com.CitasHospital.Domain.NursesAppointments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository

public interface NursesAppointmentsRepository extends JpaRepository <NursesAppointments,String> {
    List<NursesAppointments> findByDniPatientsAndDaysOrderByHours(String dniPatients, LocalDate days);
    boolean existsByDaysAndHoursBetween(LocalDate day, LocalTime hours, LocalTime plusMinutes);
    boolean existsByDniNursesAndDaysAndHours(String dniNurses, LocalDate days, LocalTime hours);
    List<NursesAppointments> findByDniNursesAndDaysOrderByHours(String dniNurses, LocalDate startDate);
}
