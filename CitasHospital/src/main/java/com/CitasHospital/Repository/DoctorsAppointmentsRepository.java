package com.CitasHospital.Repository;

import com.CitasHospital.Domain.DoctorsAppointments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface DoctorsAppointmentsRepository extends JpaRepository<DoctorsAppointments,String> {
    List<DoctorsAppointments> findByDniPatientsAndDaysOrderByHours(String dniPatients, LocalDate days);
    List<DoctorsAppointments> findByDniDoctorsOrderByDaysAscHoursAsc(String dniDoctors);
    boolean existsByDaysAndHoursBetween(LocalDate day, LocalTime hours, LocalTime plusMinutes);
    boolean existsByDniDoctorsAndDaysAndHours(String dniDoctors, LocalDate days, LocalTime hours);
    List<DoctorsAppointments> findByDniDoctorsAndDaysOrderByHours(String dniDoctors, LocalDate startDate);
}

