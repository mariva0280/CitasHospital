package com.CitasHospital.Service;

import com.CitasHospital.Controller.Inputs.PatientsInput;
import com.CitasHospital.Domain.Patients;
import com.CitasHospital.Exception.PatientsExistException;
import com.CitasHospital.Repository.PatientsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class PatientsService {
    @Autowired
    private PatientsRepository patientsRepository;

    public void addPatients(PatientsInput patientsInput) throws PatientsExistException {
        if(patientsRepository.existsById(patientsInput.getDni())) {
            throw new PatientsExistException("Patient already exists.");
        }
        Patients patient = Patients.getPatients(patientsInput);
        patientsRepository.save(patient);

    }

}
