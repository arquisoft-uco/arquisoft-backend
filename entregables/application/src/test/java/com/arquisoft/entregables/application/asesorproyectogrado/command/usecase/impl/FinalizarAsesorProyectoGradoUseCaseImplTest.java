package com.arquisoft.entregables.application.asesorproyectogrado.command.usecase.impl;

import com.arquisoft.entregables.application.asesorproyectogrado.command.finder.AsesorProyectoGradoPorVinculoFinder;
import com.arquisoft.entregables.application.asesorproyectogrado.command.finder.model.VinculoAsesorProyectoGrado;
import com.arquisoft.entregables.application.asesorproyectogrado.command.result.FinalizacionAsesorProyectoGradoResult;
import com.arquisoft.entregables.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.entregables.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;
import com.arquisoft.entregables.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinalizarAsesorProyectoGradoUseCaseImplTest {

    private static final Instant ANTES = Instant.parse("2026-11-01T10:00:00Z");
    private static final Instant DESPUES = Instant.parse("2026-11-02T10:00:00Z");

    @Mock
    private AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;
    @Mock
    private AsesorProyectoGradoPorVinculoFinder asesorProyectoGradoPorVinculoFinder;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private FinalizarAsesorProyectoGradoUseCaseImpl useCase;

    @Test
    void debeFinalizarYGuardar_cuandoElVinculoExisteYElEventoEsMasReciente() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var existente = AsesorProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), asesor, proyecto, ANTES, null);
        when(asesorProyectoGradoPorVinculoFinder.obtener(new VinculoAsesorProyectoGrado(asesor, proyecto)))
                .thenReturn(existente);

        // Act
        var resultado = useCase.ejecutar(AsesorProyectoGradoDomain.crear(asesor, proyecto, DESPUES));

        // Assert
        assertThat(resultado).isEqualTo(new FinalizacionAsesorProyectoGradoResult.Finalizada(asesor, proyecto));
        var captor = ArgumentCaptor.forClass(AsesorProyectoGradoEntity.class);
        verify(asesorProyectoGradoOutputPort).guardar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(existente.getId());
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(DESPUES);
        assertThat(captor.getValue().finalizadoEn()).isEqualTo(DESPUES);
    }

    @Test
    void debeGuardarLapidaFinalizada_cuandoElFinalizadoLlegaAntesQueElAsignado() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var entrada = AsesorProyectoGradoDomain.crear(asesor, proyecto, DESPUES);
        when(asesorProyectoGradoPorVinculoFinder.obtener(new VinculoAsesorProyectoGrado(asesor, proyecto)))
                .thenReturn(AsesorProyectoGradoDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isEqualTo(new FinalizacionAsesorProyectoGradoResult.Lapida(asesor, proyecto));
        var captor = ArgumentCaptor.forClass(AsesorProyectoGradoEntity.class);
        verify(asesorProyectoGradoOutputPort).guardar(captor.capture());
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(DESPUES);
        assertThat(captor.getValue().finalizadoEn()).isEqualTo(DESPUES);
    }

    @Test
    void debeDescartarSinGuardar_cuandoElEventoNoEsMasRecienteQueElGuardado() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var existente = AsesorProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), asesor, proyecto, DESPUES, null);
        when(asesorProyectoGradoPorVinculoFinder.obtener(new VinculoAsesorProyectoGrado(asesor, proyecto)))
                .thenReturn(existente);

        // Act
        var resultado = useCase.ejecutar(AsesorProyectoGradoDomain.crear(asesor, proyecto, ANTES));

        // Assert
        assertThat(resultado)
                .isEqualTo(new FinalizacionAsesorProyectoGradoResult.Descartada(asesor, proyecto, DESPUES));
        verify(asesorProyectoGradoOutputPort, never()).guardar(any());
        assertThat(existente.estaFinalizado()).isFalse();
    }
}
