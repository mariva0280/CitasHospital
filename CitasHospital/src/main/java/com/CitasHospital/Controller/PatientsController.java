package com.CitasHospital.Controller;

import com.CitasHospital.Controller.Inputs.PatientsInput;
import com.CitasHospital.Exception.PatientsExistException;
import com.CitasHospital.Service.PatientsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController

public class PatientsController {
    @Autowired
    private PatientsService patientsService;
    @ApiOperation(value = "Adding new patient")
    @ApiResponses(value = {
            @ApiResponse(code = 201,message = "Patient added successfully."),
            @ApiResponse(code = 226, message = "Patient already exists in data base.")
    })

    @PostMapping("/patients")
    public ResponseEntity<String>addPatients(
            @ApiParam(value = "Information about patient", required = true)
            @Valid @RequestBody PatientsInput patientsInput){
        try{
            patientsService.addPatients(patientsInput);
            return ResponseEntity.status(HttpStatus.CREATED).body("Patient added successfully");
        }catch (PatientsExistException e){
            return ResponseEntity.status(HttpStatus.IM_USED).body("Patient already exists in data base.");
        }
    }
}
