package com.CitasHospital.Repository;

import com.CitasHospital.Domain.Patients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface PatientsRepository extends JpaRepository<Patients,String> {
}
