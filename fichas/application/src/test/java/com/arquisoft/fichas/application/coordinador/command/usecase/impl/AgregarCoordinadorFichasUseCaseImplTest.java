package com.arquisoft.fichas.application.coordinador.command.usecase.impl;

import com.arquisoft.fichas.application.coordinador.command.finder.CoordinadorPorIdFinder;
import com.arquisoft.fichas.application.coordinador.command.result.AgregacionCoordinadorResult;
import com.arquisoft.fichas.application.coordinador.command.secondaryport.CoordinadorOutputPort;
import com.arquisoft.fichas.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import com.arquisoft.fichas.domain.coordinador.CoordinadorDomain;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarCoordinadorFichasUseCaseImplTest {

    @Mock
    private CoordinadorOutputPort coordinadorOutputPort;
    @Mock
    private CoordinadorPorIdFinder coordinadorPorIdFinder;
    @Mock
    private AppLogger logger;

    private AgregarCoordinadorFichasUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarCoordinadorFichasUseCaseImpl(coordinadorOutputPort, coordinadorPorIdFinder, logger);
    }

    private CoordinadorDomain coordinador(UUID id, Instant ocurridoEn) {
        return CoordinadorDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);
    }

    @Test
    void debeAgregarCoordinador_cuandoNoExisteLaFila() {
        // Arrange
        var id = UUID.randomUUID();
        var coordinador = coordinador(id, Instant.now());
        when(coordinadorPorIdFinder.obtener(id)).thenReturn(Optional.empty());

        // Act
        var resultado = useCase.ejecutar(coordinador);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionCoordinadorResult.Agregada.class,
                agregada -> assertThat(agregada.coordinador()).isEqualTo(id));
        verify(coordinadorOutputPort, times(1)).guardar(any());
        verify(coordinadorPorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeRetornarDuplicada_cuandoElEventoEsMasNuevoPeroLaFilaExiste() {
        // Arrange
        var id = UUID.randomUUID();
        var vigente = Instant.now().minus(1, ChronoUnit.HOURS);
        var coordinador = coordinador(id, Instant.now());
        when(coordinadorPorIdFinder.obtener(id)).thenReturn(
                Optional.of(new CoordinadorEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", vigente)));

        // Act
        var resultado = useCase.ejecutar(coordinador);

        // Assert
        assertThat(resultado).asInstanceOf(type(AgregacionCoordinadorResult.Duplicada.class))
                .extracting(AgregacionCoordinadorResult.Duplicada::coordinador)
                .isEqualTo(id);
        verify(coordinadorOutputPort, never()).guardar(any());
    }

    @Test
    void debeRetornarDescartada_cuandoElEventoEsMasViejoQueLaFila() {
        // Arrange
        var id = UUID.randomUUID();
        var vigente = Instant.now();
        var coordinador = coordinador(id, vigente.minus(1, ChronoUnit.HOURS));
        when(coordinadorPorIdFinder.obtener(id)).thenReturn(
                Optional.of(new CoordinadorEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", vigente)));

        // Act
        var resultado = useCase.ejecutar(coordinador);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionCoordinadorResult.Descartada.class,
                descartada -> {
                    assertThat(descartada.coordinador()).isEqualTo(id);
                    assertThat(descartada.ocurridoEnVigente()).isEqualTo(vigente);
                });
        verify(coordinadorOutputPort, never()).guardar(any());
    }

    @Test
    void debeRetornarDescartada_cuandoOcurridoEnEsIgual() {
        // Arrange
        var id = UUID.randomUUID();
        var vigente = Instant.now();
        var coordinador = coordinador(id, vigente);
        when(coordinadorPorIdFinder.obtener(id)).thenReturn(
                Optional.of(new CoordinadorEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", vigente)));

        // Act
        var resultado = useCase.ejecutar(coordinador);

        // Assert
        assertThat(resultado).isInstanceOf(AgregacionCoordinadorResult.Descartada.class);
        verify(coordinadorOutputPort, never()).guardar(any());
    }
}
