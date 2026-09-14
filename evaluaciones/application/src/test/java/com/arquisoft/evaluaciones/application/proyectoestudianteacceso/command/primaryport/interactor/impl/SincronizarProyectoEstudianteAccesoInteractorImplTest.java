package com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.primaryport.model.SincronizarProyectoEstudianteAccesoCommand;
import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.usecase.SincronizarProyectoEstudianteAccesoUseCase;
import com.arquisoft.evaluaciones.domain.proyectoestudianteacceso.ProyectoEstudianteAccesoDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SincronizarProyectoEstudianteAccesoInteractorImplTest {

    @Mock
    private SincronizarProyectoEstudianteAccesoUseCase useCase;

    @InjectMocks
    private SincronizarProyectoEstudianteAccesoInteractorImpl interactor;

    @Test
    void debeMapearElCommandAlDomain_yDelegarEnElUseCase() {
        // Arrange
        SincronizarProyectoEstudianteAccesoCommand command = new SincronizarProyectoEstudianteAccesoCommand(
                UUID.randomUUID(), UUID.randomUUID(), true, Instant.now());

        // Act
        interactor.ejecutar(command);

        // Assert
        ArgumentCaptor<ProyectoEstudianteAccesoDomain> captor =
                ArgumentCaptor.forClass(ProyectoEstudianteAccesoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getProyecto()).isEqualTo(command.proyecto());
        assertThat(captor.getValue().getEstudiante()).isEqualTo(command.estudiante());
        assertThat(captor.getValue().isActivo()).isTrue();
    }
}
