package com.arquisoft.proyectos.application.asesor.command.usecase.impl;

import com.arquisoft.proyectos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.proyectos.application.asesor.command.result.ActualizacionAsesorResult;
import com.arquisoft.proyectos.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.proyectos.domain.asesor.AsesorDomain;
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
class ActualizarAsesorUseCaseImplTest {

    @Mock
    private AsesorOutputPort asesorOutputPort;
    @Mock
    private AsesorPorIdFinder asesorPorIdFinder;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private ActualizarAsesorUseCaseImpl useCase;

    @Test
    void debeActualizarYRetornarActualizada_cuandoElEventoEsMasNuevo() {
        // Arrange
        var id = UUID.randomUUID();
        var vigente = AsesorDomain.crear(id, "20161020123", "Ana Perez",
                "ana@uco.edu.co", Instant.parse("2026-09-01T10:00:00Z"));
        var entrada = AsesorDomain.crear(id, "20161020999", "Ana Actualizada",
                "actualizada@uco.edu.co", Instant.parse("2026-09-16T10:00:00Z"));
        when(asesorPorIdFinder.obtener(id)).thenReturn(vigente);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOf(ActualizacionAsesorResult.Actualizada.class);
        verify(asesorOutputPort, times(1)).actualizar(any());
    }

    @Test
    void debeDescartar_cuandoElEventoEsIgualOMasViejoQueElVigente() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.parse("2026-09-16T10:00:00Z");
        var vigente = AsesorDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);
        var entrada = AsesorDomain.crear(id, "20161020999", "Ana Actualizada",
                "actualizada@uco.edu.co", ocurridoEn);
        when(asesorPorIdFinder.obtener(id)).thenReturn(vigente);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOf(ActualizacionAsesorResult.Descartada.class);
        verify(asesorOutputPort, never()).actualizar(any());
    }

    @Test
    void debeReportarNoReplicado_cuandoElAsesorNoExisteEnLaReplica() {
        // Arrange
        var id = UUID.randomUUID();
        var entrada = AsesorDomain.crear(id, "20161020999", "Ana Actualizada",
                "actualizada@uco.edu.co", Instant.now());
        when(asesorPorIdFinder.obtener(id)).thenReturn(AsesorDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOf(ActualizacionAsesorResult.NoReplicado.class);
        verify(asesorOutputPort, never()).actualizar(any());
    }
}
