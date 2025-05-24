package com.healthcare.app.service;

import com.healthcare.app.model.Patient;
import com.healthcare.app.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private static final Logger log = LoggerFactory.getLogger(PatientService.class);

    @Autowired
    private PatientRepository patientRepository;

    public Patient createPatient(Patient patient) {
        log.info("Creating a new patient: {}", patient);
        return patientRepository.save(patient);
    }

    public Patient getPatientById(Long id) {
        log.info("Fetching patient with ID: {}", id);
        return patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + id));
    }

    public List<Patient> getAllPatients() {
        log.info("Fetching all patients");
        return patientRepository.findAll();
    }

    public Patient updatePatient(Long id, Patient patientDetails) {
        log.info("Updating patient with ID: {}", id);
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + id));

        // Update fields
        existingPatient.setFirstName(patientDetails.getFirstName());
        existingPatient.setLastName(patientDetails.getLastName());
        existingPatient.setDateOfBirth(patientDetails.getDateOfBirth());
        existingPatient.setContactNumber(patientDetails.getContactNumber());
        existingPatient.setMedicalHistory(patientDetails.getMedicalHistory());

        return patientRepository.save(existingPatient);
    }

    public void deletePatient(Long id) {
        log.info("Deleting patient with ID: {}", id);
        if (patientRepository.existsById(id))
            patientRepository.deleteById(id);
    }
}
