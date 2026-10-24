package com.arquisoft.artefactos.application.asesorproyectogrado.command.usecase.impl;

import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.AsesorProyectoGradoPorVinculoFinder;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.model.VinculoAsesorProyectoGrado;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.result.FinalizacionAsesorProyectoGradoResult;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinalizarAsesorProyectoGradoUseCaseImplTest {

    private static final Instant ANTES = Instant.parse("2026-10-01T10:00:00Z");
    private static final Instant DESPUES = Instant.parse("2026-10-02T10:00:00Z");

    @Mock
    private AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;
    @Mock
    private AsesorProyectoGradoPorVinculoFinder asesorProyectoGradoPorVinculoFinder;
    @Mock
    private AppLogger logger;

    private FinalizarAsesorProyectoGradoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new FinalizarAsesorProyectoGradoUseCaseImpl(
                asesorProyectoGradoOutputPort, asesorProyectoGradoPorVinculoFinder, logger);
    }

    @Test
    void debeFinalizarYGuardar_cuandoElVinculoVigenteRecibeUnEventoMasNuevo() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var entrada = AsesorProyectoGradoDomain.crear(asesor, proyecto, DESPUES);
        var vigente = AsesorProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), asesor, proyecto, ANTES, null);
        when(asesorProyectoGradoPorVinculoFinder.obtener(new VinculoAsesorProyectoGrado(asesor, proyecto)))
                .thenReturn(vigente);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isEqualTo(new FinalizacionAsesorProyectoGradoResult.Finalizada(asesor, proyecto));
        var captor = ArgumentCaptor.forClass(AsesorProyectoGradoEntity.class);
        verify(asesorProyectoGradoPorVinculoFinder, times(1)).obtener(any());
        verify(asesorProyectoGradoOutputPort, times(1)).guardar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(vigente.getId());
        assertThat(captor.getValue().finalizadoEn()).isEqualTo(DESPUES);
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(DESPUES);
    }

    @Test
    void debeGuardarLapidaFinalizada_cuandoNoHabiaVinculo() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var entrada = AsesorProyectoGradoDomain.crear(asesor, proyecto, DESPUES);
        when(asesorProyectoGradoPorVinculoFinder.obtener(any())).thenReturn(AsesorProyectoGradoDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isEqualTo(new FinalizacionAsesorProyectoGradoResult.Lapida(asesor, proyecto));
        var captor = ArgumentCaptor.forClass(AsesorProyectoGradoEntity.class);
        verify(asesorProyectoGradoOutputPort).guardar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(entrada.getId());
        assertThat(captor.getValue().finalizadoEn()).isEqualTo(DESPUES);
    }

    @Test
    void debeRetornarDescartadaSinGuardar_cuandoElEventoNoEsMasNuevoQueElGuardado() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var entrada = AsesorProyectoGradoDomain.crear(asesor, proyecto, ANTES);
        when(asesorProyectoGradoPorVinculoFinder.obtener(any())).thenReturn(AsesorProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), asesor, proyecto, DESPUES, null));

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isEqualTo(
                new FinalizacionAsesorProyectoGradoResult.Descartada(asesor, proyecto, DESPUES));
        verify(asesorProyectoGradoOutputPort, never()).guardar(any());
    }
}
