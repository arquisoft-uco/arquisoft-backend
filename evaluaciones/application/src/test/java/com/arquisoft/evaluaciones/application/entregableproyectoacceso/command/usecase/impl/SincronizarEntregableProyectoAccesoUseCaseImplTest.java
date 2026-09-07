package com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.usecase.impl;

import com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.secondaryport.EntregableProyectoAccesoOutputPort;
import com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.secondaryport.entity.EntregableProyectoAccesoEntity;
import com.arquisoft.evaluaciones.domain.entregableproyectoacceso.EntregableProyectoAccesoDomain;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SincronizarEntregableProyectoAccesoUseCaseImplTest {

    @Mock
    private EntregableProyectoAccesoOutputPort outputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private SincronizarEntregableProyectoAccesoUseCaseImpl useCase;

    @Test
    void debeGuardar_cuandoNoExisteProyeccionPrevia() {
        // Arrange
        UUID entregable = UUID.randomUUID();
        EntregableProyectoAccesoDomain entrada =
                EntregableProyectoAccesoDomain.crear(entregable, UUID.randomUUID(), 1, Instant.now());
        when(outputPort.buscarPorEntregable(entregable)).thenReturn(Optional.empty());

        // Act
        useCase.ejecutar(entrada);

        // Assert
        ArgumentCaptor<EntregableProyectoAccesoEntity> captor =
                ArgumentCaptor.forClass(EntregableProyectoAccesoEntity.class);
        verify(outputPort).guardar(captor.capture());
        assertThat(captor.getValue().entregable()).isEqualTo(entregable);
    }

    @Test
    void debeActualizar_cuandoElEventoEsMasRecienteQueElExistente() {
        // Arrange
        UUID entregable = UUID.randomUUID();
        UUID proyecto = UUID.randomUUID();
        Instant ahora = Instant.now();
        EntregableProyectoAccesoDomain entrada =
                EntregableProyectoAccesoDomain.crear(entregable, proyecto, 2, ahora);
        EntregableProyectoAccesoEntity existente =
                new EntregableProyectoAccesoEntity(entregable, proyecto, 1, true, ahora.minusSeconds(60));
        when(outputPort.buscarPorEntregable(entregable)).thenReturn(Optional.of(existente));

        // Act
        useCase.ejecutar(entrada);

        // Assert
        ArgumentCaptor<EntregableProyectoAccesoEntity> captor =
                ArgumentCaptor.forClass(EntregableProyectoAccesoEntity.class);
        verify(outputPort).guardar(captor.capture());
        assertThat(captor.getValue().versionEntregable()).isEqualTo(2);
    }

    @Test
    void noDebeGuardar_cuandoElEventoNoEsMasRecienteQueElExistente() {
        // Arrange
        UUID entregable = UUID.randomUUID();
        UUID proyecto = UUID.randomUUID();
        Instant ahora = Instant.now();

        EntregableProyectoAccesoDomain eventoAnterior =
                EntregableProyectoAccesoDomain.crear(entregable, proyecto, 1, ahora.minusSeconds(60));
        EntregableProyectoAccesoEntity existenteMasNuevo =
                new EntregableProyectoAccesoEntity(entregable, proyecto, 2, true, ahora);
        when(outputPort.buscarPorEntregable(entregable)).thenReturn(Optional.of(existenteMasNuevo));

        EntregableProyectoAccesoDomain eventoDuplicado =
                EntregableProyectoAccesoDomain.crear(entregable, proyecto, 2, ahora);

        // Act
        useCase.ejecutar(eventoAnterior);
        useCase.ejecutar(eventoDuplicado);

        // Assert
        verify(outputPort, never()).guardar(any());
    }
}
