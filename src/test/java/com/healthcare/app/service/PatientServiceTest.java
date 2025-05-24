package com.healthcare.app.service;

import com.healthcare.app.model.Patient;
import com.healthcare.app.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient testPatient;

    @BeforeEach
    void setUp() {
        testPatient = new Patient();
        testPatient.setId(1L);
        testPatient.setFirstName("John");
        testPatient.setLastName("Doe");
        testPatient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testPatient.setContactNumber("123-456-7890");
        testPatient.setMedicalHistory("No significant medical history");
    }

    @Test
    @DisplayName("Should create a new patient successfully")
    void createPatient() {
        // Arrange
        when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);

        // Act
        Patient createdPatient = patientService.createPatient(testPatient);

        // Assert
        assertNotNull(createdPatient);
        assertEquals(testPatient.getId(), createdPatient.getId());
        assertEquals(testPatient.getFirstName(), createdPatient.getFirstName());
        assertEquals(testPatient.getLastName(), createdPatient.getLastName());
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    @DisplayName("Should retrieve a patient by ID successfully")
    void getPatientById() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));

        // Act
        Patient foundPatient = patientService.getPatientById(1L);

        // Assert
        assertNotNull(foundPatient);
        assertEquals(testPatient.getId(), foundPatient.getId());
        assertEquals(testPatient.getFirstName(), foundPatient.getFirstName());
        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when patient not found")
    void getPatientByIdNotFound() {
        // Arrange
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> patientService.getPatientById(99L));
        verify(patientRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Should retrieve all patients successfully")
    void getAllPatients() {
        // Arrange
        Patient patient2 = new Patient();
        patient2.setId(2L);
        patient2.setFirstName("Jane");
        patient2.setLastName("Smith");
        patient2.setDateOfBirth(LocalDate.of(1985, 5, 15));

        List<Patient> patientList = Arrays.asList(testPatient, patient2);
        when(patientRepository.findAll()).thenReturn(patientList);

        // Act
        List<Patient> foundPatients = patientService.getAllPatients();

        // Assert
        assertNotNull(foundPatients);
        assertEquals(2, foundPatients.size());
        assertEquals(testPatient.getFirstName(), foundPatients.get(0).getFirstName());
        assertEquals(patient2.getFirstName(), foundPatients.get(1).getFirstName());
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update a patient successfully")
    void updatePatient() {
        // Arrange
        Patient updatedPatient = new Patient();
        updatedPatient.setFirstName("John Updated");
        updatedPatient.setLastName("Doe Updated");
        updatedPatient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        updatedPatient.setContactNumber("987-654-3210");
        updatedPatient.setMedicalHistory("Updated medical history");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Patient result = patientService.updatePatient(1L, updatedPatient);

        // Assert
        assertNotNull(result);
        assertEquals(updatedPatient.getFirstName(), result.getFirstName());
        assertEquals(updatedPatient.getLastName(), result.getLastName());
        assertEquals(updatedPatient.getContactNumber(), result.getContactNumber());
        assertEquals(updatedPatient.getMedicalHistory(), result.getMedicalHistory());
        verify(patientRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating non-existent patient")
    void updatePatientNotFound() {
        // Arrange
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> patientService.updatePatient(99L, testPatient));
        verify(patientRepository, times(1)).findById(99L);
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    @DisplayName("Should delete a patient successfully")
    void deletePatient() {
        // Arrange
        when(patientRepository.existsById(1L)).thenReturn(true);
        doNothing().when(patientRepository).deleteById(1L);

        // Act
        patientService.deletePatient(1L);

        // Assert
        verify(patientRepository, times(1)).existsById(1L);
        verify(patientRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should not throw exception when deleting non-existent patient")
    void deletePatientNotFound() {
        // Arrange
        when(patientRepository.existsById(99L)).thenReturn(false);

        // Act
        patientService.deletePatient(99L);

        // Assert
        verify(patientRepository, times(1)).existsById(99L);
        verify(patientRepository, never()).deleteById(99L);
    }
}
