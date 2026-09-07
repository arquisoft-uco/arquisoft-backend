package com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.usecase.impl;

import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.ProyectoEstudianteAccesoOutputPort;
import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.entity.ProyectoEstudianteAccesoEntity;
import com.arquisoft.evaluaciones.domain.proyectoestudianteacceso.ProyectoEstudianteAccesoDomain;
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
class SincronizarProyectoEstudianteAccesoUseCaseImplTest {

    @Mock
    private ProyectoEstudianteAccesoOutputPort outputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private SincronizarProyectoEstudianteAccesoUseCaseImpl useCase;

    @Test
    void debeAsignar_cuandoNoExisteMembresiaPrevia() {
        // Arrange
        UUID proyecto = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        ProyectoEstudianteAccesoDomain entrada =
                ProyectoEstudianteAccesoDomain.crear(proyecto, estudiante, true, Instant.now());
        when(outputPort.buscarPorProyectoYEstudiante(proyecto, estudiante)).thenReturn(Optional.empty());

        // Act
        useCase.ejecutar(entrada);

        // Assert
        ArgumentCaptor<ProyectoEstudianteAccesoEntity> captor =
                ArgumentCaptor.forClass(ProyectoEstudianteAccesoEntity.class);
        verify(outputPort).guardar(captor.capture());
        assertThat(captor.getValue().activo()).isTrue();
    }

    @Test
    void debeCrearLapida_cuandoLlegaDestitucionSinMembresiaPrevia() {
        // Arrange
        UUID proyecto = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        ProyectoEstudianteAccesoDomain entrada =
                ProyectoEstudianteAccesoDomain.crear(proyecto, estudiante, false, Instant.now());
        when(outputPort.buscarPorProyectoYEstudiante(proyecto, estudiante)).thenReturn(Optional.empty());

        // Act
        useCase.ejecutar(entrada);

        // Assert
        ArgumentCaptor<ProyectoEstudianteAccesoEntity> captor =
                ArgumentCaptor.forClass(ProyectoEstudianteAccesoEntity.class);
        verify(outputPort).guardar(captor.capture());
        assertThat(captor.getValue().activo()).isFalse();
    }

    @Test
    void debeActualizar_cuandoElEventoEsMasRecienteQueElExistente() {
        // Arrange
        UUID proyecto = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        Instant ahora = Instant.now();
        ProyectoEstudianteAccesoDomain destitucion =
                ProyectoEstudianteAccesoDomain.crear(proyecto, estudiante, false, ahora);
        ProyectoEstudianteAccesoEntity existente =
                new ProyectoEstudianteAccesoEntity(proyecto, estudiante, true, ahora.minusSeconds(60));
        when(outputPort.buscarPorProyectoYEstudiante(proyecto, estudiante)).thenReturn(Optional.of(existente));

        // Act
        useCase.ejecutar(destitucion);

        // Assert
        ArgumentCaptor<ProyectoEstudianteAccesoEntity> captor =
                ArgumentCaptor.forClass(ProyectoEstudianteAccesoEntity.class);
        verify(outputPort).guardar(captor.capture());
        assertThat(captor.getValue().activo()).isFalse();
    }

    @Test
    void noDebeActualizar_cuandoElEventoNoEsMasRecienteQueElExistente() {
        // Arrange
        UUID proyecto = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        Instant ahora = Instant.now();

        ProyectoEstudianteAccesoDomain eventoAnterior =
                ProyectoEstudianteAccesoDomain.crear(proyecto, estudiante, false, ahora.minusSeconds(60));
        ProyectoEstudianteAccesoEntity existenteMasNuevo =
                new ProyectoEstudianteAccesoEntity(proyecto, estudiante, true, ahora);
        when(outputPort.buscarPorProyectoYEstudiante(proyecto, estudiante)).thenReturn(Optional.of(existenteMasNuevo));

        ProyectoEstudianteAccesoDomain eventoDuplicado =
                ProyectoEstudianteAccesoDomain.crear(proyecto, estudiante, true, ahora);

        // Act
        useCase.ejecutar(eventoAnterior);
        useCase.ejecutar(eventoDuplicado);

        // Assert
        verify(outputPort, never()).guardar(any());
    }
}
