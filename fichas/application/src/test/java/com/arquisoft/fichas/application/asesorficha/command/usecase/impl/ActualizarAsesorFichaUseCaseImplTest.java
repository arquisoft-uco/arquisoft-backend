package com.arquisoft.fichas.application.asesorficha.command.usecase.impl;

import com.arquisoft.fichas.application.asesorficha.command.finder.AsesorFichaPorIdFinder;
import com.arquisoft.fichas.application.asesorficha.command.result.ActualizacionAsesorFichaResult;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class ActualizarAsesorFichaUseCaseImplTest {

    @Mock
    private AsesorFichaOutputPort asesorFichaOutputPort;
    @Mock
    private AsesorFichaPorIdFinder asesorFichaPorIdFinder;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private ActualizarAsesorFichaUseCaseImpl useCase;

    @Test
    void debeActualizarYRetornarActualizada_cuandoElEventoEsMasNuevo() {
        // Arrange
        var id = UUID.randomUUID();
        var vigente = AsesorFichaDomain.crear(id, "20161020123", "Juan Pérez",
                "juan@example.com", Instant.parse("2026-09-01T10:00:00Z"));
        var entrada = AsesorFichaDomain.crear(id, "20161020999", "Juan Actualizado",
                "actualizado@example.com", Instant.parse("2026-09-16T10:00:00Z"));
        when(asesorFichaPorIdFinder.obtener(id)).thenReturn(vigente);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOf(ActualizacionAsesorFichaResult.Actualizada.class);
        verify(asesorFichaOutputPort, times(1)).actualizar(any());
    }

    @Test
    void debeDescartar_cuandoElEventoEsIgualOMasViejoQueElVigente() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.parse("2026-09-16T10:00:00Z");
        var vigente = AsesorFichaDomain.crear(id, "20161020123", "Juan Pérez", "juan@example.com", ocurridoEn);
        var entrada = AsesorFichaDomain.crear(id, "20161020999", "Juan Actualizado",
                "actualizado@example.com", ocurridoEn);
        when(asesorFichaPorIdFinder.obtener(id)).thenReturn(vigente);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOf(ActualizacionAsesorFichaResult.Descartada.class);
        verify(asesorFichaOutputPort, never()).actualizar(any());
    }

    @Test
    void debeReportarNoReplicado_cuandoElAsesorFichaNoExisteEnLaReplica() {
        // Arrange
        var id = UUID.randomUUID();
        var entrada = AsesorFichaDomain.crear(id, "20161020999", "Juan Actualizado",
                "actualizado@example.com", Instant.now());
        when(asesorFichaPorIdFinder.obtener(id)).thenReturn(AsesorFichaDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOf(ActualizacionAsesorFichaResult.NoReplicado.class);
        verify(asesorFichaOutputPort, never()).actualizar(any());
    }

    @Test
    void debeActualizarConservandoEliminadoEn_cuandoElAsesorFichaEstaDadoDeBaja() {
        // Arrange
        var id = UUID.randomUUID();
        var eliminadoEn = Instant.parse("2026-09-01T10:00:00Z");
        var eliminado = AsesorFichaDomain.reconstruir(id, "20161020123", "Juan Pérez", "juan@example.com",
                eliminadoEn, eliminadoEn);
        var entrada = AsesorFichaDomain.crear(id, "20161020999", "Juan Actualizado",
                "actualizado@example.com", Instant.parse("2026-09-16T10:00:00Z"));
        when(asesorFichaPorIdFinder.obtener(id)).thenReturn(eliminado);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOf(ActualizacionAsesorFichaResult.Actualizada.class);
        var captor = ArgumentCaptor.forClass(AsesorFichaEntity.class);
        verify(asesorFichaOutputPort, times(1)).actualizar(captor.capture());
        assertThat(captor.getValue().nombre()).isEqualTo("Juan Actualizado");
        assertThat(captor.getValue().eliminadoEn()).isEqualTo(eliminadoEn);
    }
}
