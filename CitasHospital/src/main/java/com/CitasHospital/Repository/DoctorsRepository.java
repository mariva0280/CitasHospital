package com.CitasHospital.Repository;

import com.CitasHospital.Domain.Doctors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface DoctorsRepository extends JpaRepository <Doctors,String> {

}
