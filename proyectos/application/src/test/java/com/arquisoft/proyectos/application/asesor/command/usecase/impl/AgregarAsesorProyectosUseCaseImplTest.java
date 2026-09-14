package com.arquisoft.proyectos.application.asesor.command.usecase.impl;

import com.arquisoft.proyectos.application.asesor.command.finder.AsesorProyectosPorIdFinder;
import com.arquisoft.proyectos.application.asesor.command.result.AgregacionAsesorResult;
import com.arquisoft.proyectos.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.proyectos.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.proyectos.domain.asesor.AsesorDomain;
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
class AgregarAsesorProyectosUseCaseImplTest {

    @Mock
    private AsesorOutputPort asesorOutputPort;
    @Mock
    private AsesorProyectosPorIdFinder asesorProyectosPorIdFinder;
    @Mock
    private AppLogger logger;

    private AgregarAsesorProyectosUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarAsesorProyectosUseCaseImpl(asesorOutputPort, asesorProyectosPorIdFinder, logger);
    }

    private AsesorDomain asesor(UUID id, Instant ocurridoEn) {
        return AsesorDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);
    }

    @Test
    void debeAgregarAsesor_cuandoNoExisteLaFila() {
        // Arrange
        var id = UUID.randomUUID();
        var asesor = asesor(id, Instant.now());
        when(asesorProyectosPorIdFinder.obtener(id)).thenReturn(Optional.empty());

        // Act
        var resultado = useCase.ejecutar(asesor);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionAsesorResult.Agregada.class,
                agregada -> assertThat(agregada.asesor()).isEqualTo(id));
        verify(asesorOutputPort, times(1)).guardar(any());
        verify(asesorProyectosPorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeRetornarDuplicada_cuandoElEventoEsMasNuevoPeroLaFilaExiste() {
        // Arrange
        var id = UUID.randomUUID();
        var vigente = Instant.now().minus(1, ChronoUnit.HOURS);
        var asesor = asesor(id, Instant.now());
        when(asesorProyectosPorIdFinder.obtener(id)).thenReturn(
                Optional.of(new AsesorEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", vigente)));

        // Act
        var resultado = useCase.ejecutar(asesor);

        // Assert
        assertThat(resultado).asInstanceOf(type(AgregacionAsesorResult.Duplicada.class))
                .extracting(AgregacionAsesorResult.Duplicada::asesor)
                .isEqualTo(id);
        verify(asesorOutputPort, never()).guardar(any());
    }

    @Test
    void debeRetornarDescartada_cuandoElEventoEsMasViejoQueLaFila() {
        // Arrange
        var id = UUID.randomUUID();
        var vigente = Instant.now();
        var asesor = asesor(id, vigente.minus(1, ChronoUnit.HOURS));
        when(asesorProyectosPorIdFinder.obtener(id)).thenReturn(
                Optional.of(new AsesorEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", vigente)));

        // Act
        var resultado = useCase.ejecutar(asesor);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionAsesorResult.Descartada.class,
                descartada -> {
                    assertThat(descartada.asesor()).isEqualTo(id);
                    assertThat(descartada.ocurridoEnVigente()).isEqualTo(vigente);
                });
        verify(asesorOutputPort, never()).guardar(any());
    }

    @Test
    void debeRetornarDescartada_cuandoOcurridoEnEsIgual() {
        // Arrange
        var id = UUID.randomUUID();
        var vigente = Instant.now();
        var asesor = asesor(id, vigente);
        when(asesorProyectosPorIdFinder.obtener(id)).thenReturn(
                Optional.of(new AsesorEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", vigente)));

        // Act
        var resultado = useCase.ejecutar(asesor);

        // Assert
        assertThat(resultado).isInstanceOf(AgregacionAsesorResult.Descartada.class);
        verify(asesorOutputPort, never()).guardar(any());
    }
}
