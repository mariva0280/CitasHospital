package com.CitasHospital.Service;

import com.CitasHospital.Controller.Inputs.PatientsInput;
import com.CitasHospital.Domain.Patients;
import com.CitasHospital.Exception.DoctorsExistException;
import com.CitasHospital.Exception.InvalidDniException;
import com.CitasHospital.Exception.NursesExistException;
import com.CitasHospital.Exception.PatientsExistException;
import com.CitasHospital.Repository.DoctorsRepository;
import com.CitasHospital.Repository.NursesRepository;
import com.CitasHospital.Repository.PatientsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class PatientsService {
    @Autowired
    private PatientsRepository patientsRepository;
    @Autowired
    private DoctorsRepository doctorsRepository;
    @Autowired
    private NursesRepository nursesRepository;

    public void addPatients(PatientsInput patientsInput) throws PatientsExistException,InvalidDniException {
        if(doctorsRepository.existsById(patientsInput.getDni())) {
            throw new InvalidDniException("DNI is already in use by a doctor.");
        }
        if(nursesRepository.existsById(patientsInput.getDni())) {
            throw new InvalidDniException("DNI is already in use by a nurse.");
        }
        if(patientsRepository.existsById(patientsInput.getDni())) {
            throw new PatientsExistException("Patient already exists.");
        }
        Patients patient = Patients.getPatients(patientsInput);
        patientsRepository.save(patient);

    }

}
