package com.arquisoft.proyectos.application.coordinador.command.usecase.impl;

import com.arquisoft.proyectos.application.coordinador.command.finder.CoordinadorPorIdFinder;
import com.arquisoft.proyectos.application.coordinador.command.result.ActualizacionCoordinadorResult;
import com.arquisoft.proyectos.application.coordinador.command.secondaryport.CoordinadorOutputPort;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
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
class ActualizarCoordinadorUseCaseImplTest {

    @Mock
    private CoordinadorOutputPort coordinadorOutputPort;
    @Mock
    private CoordinadorPorIdFinder coordinadorPorIdFinder;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private ActualizarCoordinadorUseCaseImpl useCase;

    @Test
    void debeActualizarYRetornarActualizada_cuandoElEventoEsMasNuevo() {
        // Arrange
        var id = UUID.randomUUID();
        var vigente = CoordinadorDomain.crear(id, "20161020123", "Ana Perez",
                "ana@uco.edu.co", Instant.parse("2026-09-01T10:00:00Z"));
        var entrada = CoordinadorDomain.crear(id, "20161020999", "Ana Actualizada",
                "actualizada@uco.edu.co", Instant.parse("2026-09-16T10:00:00Z"));
        when(coordinadorPorIdFinder.obtener(id)).thenReturn(vigente);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOf(ActualizacionCoordinadorResult.Actualizada.class);
        verify(coordinadorOutputPort, times(1)).actualizar(any());
    }

    @Test
    void debeDescartar_cuandoElEventoEsIgualOMasViejoQueElVigente() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.parse("2026-09-16T10:00:00Z");
        var vigente = CoordinadorDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);
        var entrada = CoordinadorDomain.crear(id, "20161020999", "Ana Actualizada",
                "actualizada@uco.edu.co", ocurridoEn);
        when(coordinadorPorIdFinder.obtener(id)).thenReturn(vigente);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOf(ActualizacionCoordinadorResult.Descartada.class);
        verify(coordinadorOutputPort, never()).actualizar(any());
    }

    @Test
    void debeReportarNoReplicado_cuandoElCoordinadorNoExisteEnLaReplica() {
        // Arrange
        var id = UUID.randomUUID();
        var entrada = CoordinadorDomain.crear(id, "20161020999", "Ana Actualizada",
                "actualizada@uco.edu.co", Instant.now());
        when(coordinadorPorIdFinder.obtener(id)).thenReturn(CoordinadorDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOf(ActualizacionCoordinadorResult.NoReplicado.class);
        verify(coordinadorOutputPort, never()).actualizar(any());
    }
}
