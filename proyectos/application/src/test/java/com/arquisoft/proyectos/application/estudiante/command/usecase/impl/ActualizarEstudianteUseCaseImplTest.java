package com.arquisoft.proyectos.application.estudiante.command.usecase.impl;

import com.arquisoft.proyectos.application.estudiante.command.finder.EstudiantePorIdFinder;
import com.arquisoft.proyectos.application.estudiante.command.result.ActualizacionEstudianteResult;
import com.arquisoft.proyectos.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarEstudianteUseCaseImplTest {

    @Mock
    private EstudianteOutputPort estudianteOutputPort;
    @Mock
    private EstudiantePorIdFinder estudiantePorIdFinder;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private ActualizarEstudianteUseCaseImpl useCase;

    @Test
    void debeActualizarYRetornarActualizada_cuandoElEventoEsMasNuevo() {
        // Arrange
        var id = UUID.randomUUID();
        var vigente = EstudianteDomain.crear(id, "20161020123", "Ana Perez",
                "ana@uco.edu.co", Instant.parse("2026-09-01T10:00:00Z"));
        var entrada = EstudianteDomain.crear(id, "20161020999", "Ana Actualizada",
                "actualizada@uco.edu.co", Instant.parse("2026-09-16T10:00:00Z"));
        when(estudiantePorIdFinder.obtener(id)).thenReturn(vigente);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOf(ActualizacionEstudianteResult.Actualizada.class);
        verify(estudianteOutputPort, times(1)).actualizar(any());
    }

    @Test
    void debeDescartar_cuandoElEventoEsIgualOMasViejoQueElVigente() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.parse("2026-09-16T10:00:00Z");
        var vigente = EstudianteDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);
        var entrada = EstudianteDomain.crear(id, "20161020999", "Ana Actualizada",
                "actualizada@uco.edu.co", ocurridoEn);
        when(estudiantePorIdFinder.obtener(id)).thenReturn(vigente);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOf(ActualizacionEstudianteResult.Descartada.class);
        verify(estudianteOutputPort, never()).actualizar(any());
    }

    @Test
    void debeReportarNoReplicado_cuandoElEstudianteNoExisteEnLaReplica() {
        // Arrange
        var id = UUID.randomUUID();
        var entrada = EstudianteDomain.crear(id, "20161020999", "Ana Actualizada",
                "actualizada@uco.edu.co", Instant.now());
        when(estudiantePorIdFinder.obtener(id)).thenReturn(EstudianteDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOf(ActualizacionEstudianteResult.NoReplicado.class);
        verify(estudianteOutputPort, never()).actualizar(any());
    }
}
