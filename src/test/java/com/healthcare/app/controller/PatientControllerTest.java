package com.healthcare.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.app.config.SecurityConfig;
import com.healthcare.app.model.Patient;
import com.healthcare.app.service.PatientService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
@Import(SecurityConfig.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
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
    @DisplayName("GET /api/v1/patients - Should return all patients")
    void getAllPatients() throws Exception {
        // Arrange
        Patient patient2 = new Patient();
        patient2.setId(2L);
        patient2.setFirstName("Jane");
        patient2.setLastName("Smith");
        patient2.setDateOfBirth(LocalDate.of(1985, 5, 15));
        
        List<Patient> patientList = Arrays.asList(testPatient, patient2);
        when(patientService.getAllPatients()).thenReturn(patientList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/patients")
                .with(SecurityMockMvcRequestPostProcessors.user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].firstName", is("Jane")));

        verify(patientService, times(1)).getAllPatients();
    }

    @Test
    @DisplayName("GET /api/v1/patients/{id} - Should return patient by ID")
    void getPatientById() throws Exception {
        // Arrange
        when(patientService.getPatientById(1L)).thenReturn(testPatient);

        // Act & Assert
        mockMvc.perform(get("/api/v1/patients/1")
                .with(SecurityMockMvcRequestPostProcessors.user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")));

        verify(patientService, times(1)).getPatientById(1L);
    }

    @Test
    @DisplayName("GET /api/v1/patients/{id} - Should return 404 when patient not found")
    void getPatientByIdNotFound() throws Exception {
        // Arrange
        when(patientService.getPatientById(99L)).thenThrow(new EntityNotFoundException("Patient not found with id: 99"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/patients/99")
                .with(SecurityMockMvcRequestPostProcessors.user("user").roles("USER")))
                .andExpect(status().isNotFound());

        verify(patientService, times(1)).getPatientById(99L);
    }

    @Test
    @DisplayName("POST /api/v1/patients - Should create a new patient")
    void createPatient() throws Exception {
        // Arrange
        when(patientService.createPatient(any(Patient.class))).thenReturn(testPatient);

        // Act & Assert
        mockMvc.perform(post("/api/v1/patients")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPatient)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")));

        verify(patientService, times(1)).createPatient(any(Patient.class));
    }

    @Test
    @DisplayName("PUT /api/v1/patients/{id} - Should update an existing patient")
    void updatePatient() throws Exception {
        // Arrange
        Patient updatedPatient = new Patient();
        updatedPatient.setId(1L);
        updatedPatient.setFirstName("John Updated");
        updatedPatient.setLastName("Doe Updated");
        updatedPatient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        updatedPatient.setContactNumber("987-654-3210");
        updatedPatient.setMedicalHistory("Updated medical history");

        when(patientService.updatePatient(eq(1L), any(Patient.class))).thenReturn(updatedPatient);

        // Act & Assert
        mockMvc.perform(put("/api/v1/patients/1")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPatient)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("John Updated")))
                .andExpect(jsonPath("$.lastName", is("Doe Updated")));

        verify(patientService, times(1)).updatePatient(eq(1L), any(Patient.class));
    }

    @Test
    @DisplayName("PUT /api/v1/patients/{id} - Should return 404 when updating non-existent patient")
    void updatePatientNotFound() throws Exception {
        // Arrange
        when(patientService.updatePatient(eq(99L), any(Patient.class)))
                .thenThrow(new EntityNotFoundException("Patient not found with id: 99"));

        // Act & Assert
        mockMvc.perform(put("/api/v1/patients/99")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPatient)))
                .andExpect(status().isNotFound());

        verify(patientService, times(1)).updatePatient(eq(99L), any(Patient.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/patients/{id} - Should delete a patient")
    void deletePatient() throws Exception {
        // Arrange
        doNothing().when(patientService).deletePatient(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/patients/1")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());

        verify(patientService, times(1)).deletePatient(1L);
    }

    @Test
    @DisplayName("Should return 403 when user with insufficient privileges tries to update patient")
    void updatePatientWithInsufficientPrivileges() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/v1/patients/1")
                .with(SecurityMockMvcRequestPostProcessors.user("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPatient)))
                .andExpect(status().isForbidden());

        verify(patientService, never()).updatePatient(anyLong(), any(Patient.class));
    }

    @Test
    @DisplayName("Should return 403 when user with insufficient privileges tries to delete patient")
    void deletePatientWithInsufficientPrivileges() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/patients/1")
                .with(SecurityMockMvcRequestPostProcessors.user("user").roles("USER")))
                .andExpect(status().isForbidden());

        verify(patientService, never()).deletePatient(anyLong());
    }
}
