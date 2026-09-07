package com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.primaryport.model.SincronizarEntregableProyectoAccesoCommand;
import com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.usecase.SincronizarEntregableProyectoAccesoUseCase;
import com.arquisoft.evaluaciones.domain.entregableproyectoacceso.EntregableProyectoAccesoDomain;
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
class SincronizarEntregableProyectoAccesoInteractorImplTest {

    @Mock
    private SincronizarEntregableProyectoAccesoUseCase useCase;

    @InjectMocks
    private SincronizarEntregableProyectoAccesoInteractorImpl interactor;

    @Test
    void debeMapearElCommandADominioYDelegarEnElUseCase() {
        // Arrange
        UUID entregable = UUID.randomUUID();
        UUID proyecto = UUID.randomUUID();
        Instant ocurridoEn = Instant.now();
        var command = SincronizarEntregableProyectoAccesoCommand.crear(
                entregable.toString(), proyecto.toString(), 3, ocurridoEn);

        // Act
        interactor.ejecutar(command);

        // Assert
        ArgumentCaptor<EntregableProyectoAccesoDomain> captor =
                ArgumentCaptor.forClass(EntregableProyectoAccesoDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getEntregable()).isEqualTo(entregable);
        assertThat(captor.getValue().getProyecto()).isEqualTo(proyecto);
        assertThat(captor.getValue().getVersionEntregable()).isEqualTo(3);
        assertThat(captor.getValue().getOcurridoEn()).isEqualTo(ocurridoEn);
    }
}
