package com.arquisoft.fichas.application.estudiante.command.usecase.impl;

import com.arquisoft.fichas.application.estudiante.command.finder.EstudiantePorIdFinder;
import com.arquisoft.fichas.application.estudiante.command.result.AgregacionEstudianteResult;
import com.arquisoft.fichas.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.fichas.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.fichas.domain.estudiante.EstudianteDomain;
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
class AgregarEstudianteFichasUseCaseImplTest {

    @Mock
    private EstudianteOutputPort estudianteOutputPort;
    @Mock
    private EstudiantePorIdFinder estudiantePorIdFinder;
    @Mock
    private AppLogger logger;

    private AgregarEstudianteFichasUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarEstudianteFichasUseCaseImpl(estudianteOutputPort, estudiantePorIdFinder, logger);
    }

    private EstudianteDomain estudiante(UUID id, Instant ocurridoEn) {
        return EstudianteDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);
    }

    @Test
    void debeAgregarEstudiante_cuandoNoExisteLaFila() {
        // Arrange
        var id = UUID.randomUUID();
        var estudiante = estudiante(id, Instant.now());
        when(estudiantePorIdFinder.obtener(id)).thenReturn(Optional.empty());

        // Act
        var resultado = useCase.ejecutar(estudiante);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionEstudianteResult.Agregada.class,
                agregada -> assertThat(agregada.estudiante()).isEqualTo(id));
        verify(estudianteOutputPort, times(1)).guardar(any());
        verify(estudiantePorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeRetornarDuplicada_cuandoElEventoEsMasNuevoPeroLaFilaExiste() {
        // Arrange
        var id = UUID.randomUUID();
        var vigente = Instant.now().minus(1, ChronoUnit.HOURS);
        var estudiante = estudiante(id, Instant.now());
        when(estudiantePorIdFinder.obtener(id)).thenReturn(
                Optional.of(new EstudianteEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", vigente)));

        // Act
        var resultado = useCase.ejecutar(estudiante);

        // Assert
        assertThat(resultado).asInstanceOf(type(AgregacionEstudianteResult.Duplicada.class))
                .extracting(AgregacionEstudianteResult.Duplicada::estudiante)
                .isEqualTo(id);
        verify(estudianteOutputPort, never()).guardar(any());
    }

    @Test
    void debeRetornarDescartada_cuandoElEventoEsMasViejoQueLaFila() {
        // Arrange
        var id = UUID.randomUUID();
        var vigente = Instant.now();
        var estudiante = estudiante(id, vigente.minus(1, ChronoUnit.HOURS));
        when(estudiantePorIdFinder.obtener(id)).thenReturn(
                Optional.of(new EstudianteEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", vigente)));

        // Act
        var resultado = useCase.ejecutar(estudiante);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionEstudianteResult.Descartada.class,
                descartada -> {
                    assertThat(descartada.estudiante()).isEqualTo(id);
                    assertThat(descartada.ocurridoEnVigente()).isEqualTo(vigente);
                });
        verify(estudianteOutputPort, never()).guardar(any());
    }

    @Test
    void debeRetornarDescartada_cuandoOcurridoEnEsIgual() {
        // Arrange
        var id = UUID.randomUUID();
        var vigente = Instant.now();
        var estudiante = estudiante(id, vigente);
        when(estudiantePorIdFinder.obtener(id)).thenReturn(
                Optional.of(new EstudianteEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", vigente)));

        // Act
        var resultado = useCase.ejecutar(estudiante);

        // Assert
        assertThat(resultado).isInstanceOf(AgregacionEstudianteResult.Descartada.class);
        verify(estudianteOutputPort, never()).guardar(any());
    }
}
