package com.healthcare.app.controller;

import com.healthcare.app.model.Patient;
import com.healthcare.app.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients") // Base path for patient endpoints
public class PatientController {

    private static final Logger log = LoggerFactory.getLogger(PatientController.class);

    @Autowired
    private PatientService patientService;

    @Operation(summary = "Get all patients", description = "Retrieve a list of all patients.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Patient.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Patient>> getAllPatients() {
        log.info("Fetching all patients...");
        List<Patient> patients = patientService.getAllPatients();
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @Operation(summary = "Get a patient by ID", description = "Retrieve a patient by their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Patient.class))),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Patient> getPatientById(@Parameter(description = "ID of patient to be retrieved", required = true) @PathVariable Long id) {
        log.info("Fetching patient with id: {}", id);
        Patient patient = patientService.getPatientById(id);
        if (patient != null) {
            return new ResponseEntity<>(patient, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Create a new patient", description = "Add a new patient to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Patient.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid input"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Patient> createPatient(@Parameter(description = "Patient object to be created", required = true, schema = @Schema(implementation = Patient.class)) @RequestBody Patient patient) {
        log.info("Creating a new patient: {}", patient);
        Patient createdPatient = patientService.createPatient(patient);
        return new ResponseEntity<>(createdPatient, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing patient", description = "Update an existing patient's information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Patient.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid input"),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Patient> updatePatient(@Parameter(description = "ID of patient to be updated", required = true) @PathVariable Long id,
                                                 @Parameter(description = "Updated patient object", required = true, schema = @Schema(implementation = Patient.class)) @RequestBody Patient patient) {
        log.info("Updating patient with id: {}, data: {}", id, patient);
        Patient updatedPatient = patientService.updatePatient(id, patient);
        if (updatedPatient != null) {
            return new ResponseEntity<>(updatedPatient, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Delete a patient", description = "Delete a patient from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Patient deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePatient(@Parameter(description = "ID of patient to be deleted", required = true) @PathVariable Long id) {
        log.info("Deleting patient with id: {}", id);
        patientService.deletePatient(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
