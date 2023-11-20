package com.CitasHospital.Repository;

import com.CitasHospital.Domain.Nurses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface NursesRepository extends JpaRepository<Nurses, String> {

}
