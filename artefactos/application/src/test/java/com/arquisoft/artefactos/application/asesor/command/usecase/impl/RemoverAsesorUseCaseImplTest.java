package com.arquisoft.artefactos.application.asesor.command.usecase.impl;

import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.asesor.command.result.RemocionAsesorResult;
import com.arquisoft.artefactos.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.artefactos.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.shared.logger.AppLogger;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverAsesorUseCaseImplTest {

    private static final Instant VIGENTE_EN = Instant.parse("2026-09-24T10:00:00Z");

    @Mock
    private AsesorOutputPort asesorOutputPort;
    @Mock
    private AsesorPorIdFinder asesorPorIdFinder;
    @Mock
    private AppLogger logger;

    private RemoverAsesorUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RemoverAsesorUseCaseImpl(asesorOutputPort, asesorPorIdFinder, logger);
    }

    private AsesorDomain entrada(UUID id, Instant ocurridoEn) {
        return AsesorDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);
    }

    private AsesorDomain vigente(UUID id) {
        return AsesorDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co", VIGENTE_EN, null);
    }

    @Test
    void debeEliminarLogicamenteYRetornarRemovida_cuandoElEventoEsMasNuevo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = VIGENTE_EN.plusSeconds(3600);
        when(asesorPorIdFinder.obtener(id)).thenReturn(vigente(id));

        // Act
        var resultado = useCase.ejecutar(entrada(id, ocurridoEn));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionAsesorResult.Removida.class,
                removida -> assertThat(removida.asesor()).isEqualTo(id));
        verify(asesorPorIdFinder, times(1)).obtener(id);
        verify(asesorOutputPort, times(1)).eliminarLogica(id, ocurridoEn);
        verify(asesorOutputPort, never()).guardar(any());
    }

    @Test
    void debeDescartarSinEscribir_cuandoElEventoNoEsMasNuevoQueElVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(asesorPorIdFinder.obtener(id)).thenReturn(vigente(id));

        // Act
        var resultado = useCase.ejecutar(entrada(id, VIGENTE_EN));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionAsesorResult.Descartada.class, descartada -> {
            assertThat(descartada.asesor()).isEqualTo(id);
            assertThat(descartada.ocurridoEnVigente()).isEqualTo(VIGENTE_EN);
        });
        verify(asesorOutputPort, never()).eliminarLogica(any(), any());
        verify(asesorOutputPort, never()).guardar(any());
    }

    @Test
    void debeInsertarLapidaYRetornarLapida_cuandoElAsesorNuncaSeReplico() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = VIGENTE_EN;
        when(asesorPorIdFinder.obtener(id)).thenReturn(AsesorDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(entrada(id, ocurridoEn));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionAsesorResult.Lapida.class,
                lapida -> assertThat(lapida.asesor()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(AsesorEntity.class);
        verify(asesorOutputPort, times(1)).guardar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(id);
        assertThat(captor.getValue().eliminadoEn()).isEqualTo(ocurridoEn);
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(ocurridoEn);
        verify(asesorOutputPort, never()).eliminarLogica(any(), any());
    }
}
