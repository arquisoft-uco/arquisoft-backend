package com.arquisoft.artefactos.application.asesorproyectogrado.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.usecase.TraspasarArtefactosUseCase;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.AsesorProyectoGradoPorVinculoFinder;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.UltimoOcurridoEnProyectoGradoFinder;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.model.VinculoAsesorProyectoGrado;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.primaryport.mapper.AgregarAsesorProyectoGradoMapper;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.primaryport.model.AgregarAsesorProyectoGradoCommand;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.result.AgregacionAsesorProyectoGradoResult;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AgregacionAsesorProyectoGradoDomain;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarAsesorProyectoGradoUseCaseImplTest {

    private static final Instant ANTES = Instant.parse("2026-10-01T10:00:00Z");
    private static final Instant DESPUES = Instant.parse("2026-10-02T10:00:00Z");

    @Mock
    private AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;
    @Mock
    private AsesorProyectoGradoPorVinculoFinder asesorProyectoGradoPorVinculoFinder;
    @Mock
    private TraspasarArtefactosUseCase traspasarArtefactosUseCase;
    @Mock
    private UltimoOcurridoEnProyectoGradoFinder ultimoOcurridoEnProyectoGradoFinder;
    @Mock
    private AppLogger logger;

    private AgregarAsesorProyectoGradoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarAsesorProyectoGradoUseCaseImpl(
                asesorProyectoGradoOutputPort, asesorProyectoGradoPorVinculoFinder,
                ultimoOcurridoEnProyectoGradoFinder, traspasarArtefactosUseCase, logger);
    }

    private static AsesorProyectoGradoDomain existente(UUID asesor, UUID proyecto, Instant ocurridoEn,
                                                       Instant finalizadoEn) {
        return AsesorProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), asesor, proyecto, ocurridoEn, finalizadoEn);
    }

    private static AgregacionAsesorProyectoGradoDomain agregacion(UUID asesor, UUID proyecto, Instant ocurridoEn) {
        return AgregarAsesorProyectoGradoMapper.toDomain(
                new AgregarAsesorProyectoGradoCommand(asesor, proyecto, ocurridoEn));
    }

    @Test
    void debeGuardarYTraspasar_cuandoNoHayVinculo() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var entrada = agregacion(asesor, proyecto, DESPUES);
        when(asesorProyectoGradoPorVinculoFinder.obtener(new VinculoAsesorProyectoGrado(asesor, proyecto)))
                .thenReturn(AsesorProyectoGradoDomain.VACIO);
        when(ultimoOcurridoEnProyectoGradoFinder.obtener(any())).thenReturn(UtilFecha.VACIO);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isEqualTo(new AgregacionAsesorProyectoGradoResult.Agregada(asesor, proyecto));
        var captor = ArgumentCaptor.forClass(AsesorProyectoGradoEntity.class);
        verify(asesorProyectoGradoPorVinculoFinder, times(1)).obtener(any());
        verify(asesorProyectoGradoOutputPort, times(1)).guardar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(entrada.getVinculo().getId());
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(DESPUES);
        assertThat(captor.getValue().finalizadoEn()).isEqualTo(UtilFecha.VACIO);
        var orden = inOrder(asesorProyectoGradoOutputPort, traspasarArtefactosUseCase);
        orden.verify(asesorProyectoGradoOutputPort).guardar(any());
        orden.verify(traspasarArtefactosUseCase).ejecutar(entrada.getTraspaso());
    }

    @Test
    void debeGuardarSinTraspasar_cuandoEsAtrasadoRespectoAOtroAsesorDelProyecto() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var entrada = agregacion(asesor, proyecto, ANTES);
        when(asesorProyectoGradoPorVinculoFinder.obtener(any())).thenReturn(AsesorProyectoGradoDomain.VACIO);
        when(ultimoOcurridoEnProyectoGradoFinder.obtener(proyecto)).thenReturn(DESPUES);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isEqualTo(new AgregacionAsesorProyectoGradoResult.Agregada(asesor, proyecto));
        verify(ultimoOcurridoEnProyectoGradoFinder, times(1)).obtener(proyecto);
        verify(asesorProyectoGradoOutputPort, times(1)).guardar(any());
        verify(traspasarArtefactosUseCase, never()).ejecutar(any());
    }

    @Test
    void debeReactivarSinTraspasar_cuandoOtroAsesorDelProyectoTieneUnEventoMasNuevo() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var entrada = agregacion(asesor, proyecto, DESPUES);
        var finalizado = existente(asesor, proyecto, ANTES, ANTES);
        var masNuevo = Instant.parse("2026-10-03T10:00:00Z");
        when(asesorProyectoGradoPorVinculoFinder.obtener(any())).thenReturn(finalizado);
        when(ultimoOcurridoEnProyectoGradoFinder.obtener(proyecto)).thenReturn(masNuevo);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isEqualTo(new AgregacionAsesorProyectoGradoResult.Reactivada(asesor, proyecto));
        verify(asesorProyectoGradoOutputPort, times(1)).guardar(any());
        verify(traspasarArtefactosUseCase, never()).ejecutar(any());
    }

    @Test
    void debeNoTraspasar_cuandoEsDescartada() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var entrada = agregacion(asesor, proyecto, ANTES);
        when(asesorProyectoGradoPorVinculoFinder.obtener(any()))
                .thenReturn(existente(asesor, proyecto, DESPUES, null));

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isEqualTo(new AgregacionAsesorProyectoGradoResult.Descartada(asesor, proyecto, DESPUES));
        verify(asesorProyectoGradoOutputPort, never()).guardar(any());
        verify(traspasarArtefactosUseCase, never()).ejecutar(any());
    }

    @Test
    void debeAvanzarYTraspasar_cuandoEsDuplicadaYEsElVinculoMasReciente() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var entrada = agregacion(asesor, proyecto, DESPUES);
        var vigente = existente(asesor, proyecto, ANTES, null);
        when(asesorProyectoGradoPorVinculoFinder.obtener(any())).thenReturn(vigente);
        when(ultimoOcurridoEnProyectoGradoFinder.obtener(any())).thenReturn(UtilFecha.VACIO);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isEqualTo(new AgregacionAsesorProyectoGradoResult.Duplicada(asesor, proyecto));
        var captor = ArgumentCaptor.forClass(AsesorProyectoGradoEntity.class);
        verify(asesorProyectoGradoOutputPort, times(1)).guardar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(vigente.getId());
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(DESPUES);
        assertThat(captor.getValue().finalizadoEn()).isEqualTo(UtilFecha.VACIO);
        var orden = inOrder(asesorProyectoGradoOutputPort, traspasarArtefactosUseCase);
        orden.verify(asesorProyectoGradoOutputPort).guardar(any());
        orden.verify(traspasarArtefactosUseCase).ejecutar(entrada.getTraspaso());
    }

    @Test
    void debeAvanzarSinTraspasar_cuandoEsDuplicadaYOtroAsesorDelProyectoTieneUnEventoMasNuevo() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var entrada = agregacion(asesor, proyecto, DESPUES);
        var vigente = existente(asesor, proyecto, ANTES, null);
        var masNuevo = Instant.parse("2026-10-03T10:00:00Z");
        when(asesorProyectoGradoPorVinculoFinder.obtener(any())).thenReturn(vigente);
        when(ultimoOcurridoEnProyectoGradoFinder.obtener(proyecto)).thenReturn(masNuevo);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isEqualTo(new AgregacionAsesorProyectoGradoResult.Duplicada(asesor, proyecto));
        var captor = ArgumentCaptor.forClass(AsesorProyectoGradoEntity.class);
        verify(asesorProyectoGradoOutputPort, times(1)).guardar(captor.capture());
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(DESPUES);
        verify(traspasarArtefactosUseCase, never()).ejecutar(any());
    }

    @Test
    void debeReactivarYTraspasar_cuandoElVinculoFinalizadoRecibeUnEventoMasNuevo() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var entrada = agregacion(asesor, proyecto, DESPUES);
        var finalizado = existente(asesor, proyecto, ANTES, ANTES);
        when(asesorProyectoGradoPorVinculoFinder.obtener(any())).thenReturn(finalizado);
        when(ultimoOcurridoEnProyectoGradoFinder.obtener(any())).thenReturn(UtilFecha.VACIO);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isEqualTo(new AgregacionAsesorProyectoGradoResult.Reactivada(asesor, proyecto));
        var captor = ArgumentCaptor.forClass(AsesorProyectoGradoEntity.class);
        verify(asesorProyectoGradoOutputPort).guardar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(finalizado.getId());
        assertThat(captor.getValue().finalizadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(DESPUES);
        var orden = inOrder(asesorProyectoGradoOutputPort, traspasarArtefactosUseCase);
        orden.verify(asesorProyectoGradoOutputPort).guardar(any());
        orden.verify(traspasarArtefactosUseCase).ejecutar(entrada.getTraspaso());
    }
}
