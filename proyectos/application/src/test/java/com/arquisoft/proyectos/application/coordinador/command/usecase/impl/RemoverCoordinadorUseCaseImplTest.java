package com.arquisoft.proyectos.application.coordinador.command.usecase.impl;

import com.arquisoft.proyectos.application.coordinador.command.finder.CoordinadorPorIdFinder;
import com.arquisoft.proyectos.application.coordinador.command.result.RemocionCoordinadorResult;
import com.arquisoft.proyectos.application.coordinador.command.secondaryport.CoordinadorOutputPort;
import com.arquisoft.proyectos.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
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
class RemoverCoordinadorUseCaseImplTest {

    private static final Instant VIGENTE_EN = Instant.parse("2026-09-24T10:00:00Z");

    @Mock
    private CoordinadorOutputPort coordinadorOutputPort;
    @Mock
    private CoordinadorPorIdFinder coordinadorPorIdFinder;
    @Mock
    private AppLogger logger;

    private RemoverCoordinadorUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RemoverCoordinadorUseCaseImpl(coordinadorOutputPort, coordinadorPorIdFinder, logger);
    }

    private CoordinadorDomain entrada(UUID id, Instant ocurridoEn) {
        return CoordinadorDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);
    }

    private CoordinadorDomain vigente(UUID id) {
        return CoordinadorDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co", VIGENTE_EN, null);
    }

    @Test
    void debeEliminarLogicamenteYRetornarRemovida_cuandoElEventoEsMasNuevo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = VIGENTE_EN.plusSeconds(3600);
        when(coordinadorPorIdFinder.obtener(id)).thenReturn(vigente(id));

        // Act
        var resultado = useCase.ejecutar(entrada(id, ocurridoEn));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionCoordinadorResult.Removida.class,
                removida -> assertThat(removida.coordinador()).isEqualTo(id));
        verify(coordinadorPorIdFinder, times(1)).obtener(id);
        verify(coordinadorOutputPort, times(1)).eliminarLogica(id, ocurridoEn);
        verify(coordinadorOutputPort, never()).guardar(any());
    }

    @Test
    void debeDescartarSinEscribir_cuandoElEventoNoEsMasNuevoQueElVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(coordinadorPorIdFinder.obtener(id)).thenReturn(vigente(id));

        // Act
        var resultado = useCase.ejecutar(entrada(id, VIGENTE_EN));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionCoordinadorResult.Descartada.class, descartada -> {
            assertThat(descartada.coordinador()).isEqualTo(id);
            assertThat(descartada.ocurridoEnVigente()).isEqualTo(VIGENTE_EN);
        });
        verify(coordinadorOutputPort, never()).eliminarLogica(any(), any());
        verify(coordinadorOutputPort, never()).guardar(any());
    }

    @Test
    void debeInsertarLapidaYRetornarLapida_cuandoElCoordinadorNuncaSeReplico() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = VIGENTE_EN;
        when(coordinadorPorIdFinder.obtener(id)).thenReturn(CoordinadorDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(entrada(id, ocurridoEn));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionCoordinadorResult.Lapida.class,
                lapida -> assertThat(lapida.coordinador()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(CoordinadorEntity.class);
        verify(coordinadorOutputPort, times(1)).guardar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(id);
        assertThat(captor.getValue().eliminadoEn()).isEqualTo(ocurridoEn);
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(ocurridoEn);
        verify(coordinadorOutputPort, never()).eliminarLogica(any(), any());
    }
}
