package com.arquisoft.fichas.application.asesorficha.command.usecase.impl;

import com.arquisoft.fichas.application.asesorficha.command.finder.AsesorFichaPorIdFinder;
import com.arquisoft.fichas.application.asesorficha.command.result.AgregacionAsesorFichaResult;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
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
class AgregarAsesorFichaFichasUseCaseImplTest {

    @Mock
    private AsesorFichaOutputPort asesorFichaOutputPort;
    @Mock
    private AsesorFichaPorIdFinder asesorFichaPorIdFinder;
    @Mock
    private AppLogger logger;

    private AgregarAsesorFichaFichasUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarAsesorFichaFichasUseCaseImpl(asesorFichaOutputPort, asesorFichaPorIdFinder, logger);
    }

    private AsesorFichaDomain asesorFicha(UUID id, Instant ocurridoEn) {
        return AsesorFichaDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);
    }

    @Test
    void debeAgregarAsesorFicha_cuandoNoExisteLaFila() {
        // Arrange
        var id = UUID.randomUUID();
        var asesorFicha = asesorFicha(id, Instant.now());
        when(asesorFichaPorIdFinder.obtener(id)).thenReturn(Optional.empty());

        // Act
        var resultado = useCase.ejecutar(asesorFicha);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionAsesorFichaResult.Agregada.class,
                agregada -> assertThat(agregada.asesorFicha()).isEqualTo(id));
        verify(asesorFichaOutputPort, times(1)).guardar(any());
        verify(asesorFichaPorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeRetornarDuplicada_cuandoElEventoEsMasNuevoPeroLaFilaExiste() {
        // Arrange
        var id = UUID.randomUUID();
        var vigente = Instant.now().minus(1, ChronoUnit.HOURS);
        var asesorFicha = asesorFicha(id, Instant.now());
        when(asesorFichaPorIdFinder.obtener(id)).thenReturn(
                Optional.of(new AsesorFichaEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", vigente)));

        // Act
        var resultado = useCase.ejecutar(asesorFicha);

        // Assert
        assertThat(resultado).asInstanceOf(type(AgregacionAsesorFichaResult.Duplicada.class))
                .extracting(AgregacionAsesorFichaResult.Duplicada::asesorFicha)
                .isEqualTo(id);
        verify(asesorFichaOutputPort, never()).guardar(any());
    }

    @Test
    void debeRetornarDescartada_cuandoElEventoEsMasViejoQueLaFila() {
        // Arrange
        var id = UUID.randomUUID();
        var vigente = Instant.now();
        var asesorFicha = asesorFicha(id, vigente.minus(1, ChronoUnit.HOURS));
        when(asesorFichaPorIdFinder.obtener(id)).thenReturn(
                Optional.of(new AsesorFichaEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", vigente)));

        // Act
        var resultado = useCase.ejecutar(asesorFicha);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionAsesorFichaResult.Descartada.class,
                descartada -> {
                    assertThat(descartada.asesorFicha()).isEqualTo(id);
                    assertThat(descartada.ocurridoEnVigente()).isEqualTo(vigente);
                });
        verify(asesorFichaOutputPort, never()).guardar(any());
    }

    @Test
    void debeRetornarDescartada_cuandoOcurridoEnEsIgual() {
        // Arrange
        var id = UUID.randomUUID();
        var vigente = Instant.now();
        var asesorFicha = asesorFicha(id, vigente);
        when(asesorFichaPorIdFinder.obtener(id)).thenReturn(
                Optional.of(new AsesorFichaEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", vigente)));

        // Act
        var resultado = useCase.ejecutar(asesorFicha);

        // Assert
        assertThat(resultado).isInstanceOf(AgregacionAsesorFichaResult.Descartada.class);
        verify(asesorFichaOutputPort, never()).guardar(any());
    }
}
