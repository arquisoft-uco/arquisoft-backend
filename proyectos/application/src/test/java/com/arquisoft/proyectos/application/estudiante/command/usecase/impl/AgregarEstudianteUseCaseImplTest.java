package com.arquisoft.proyectos.application.estudiante.command.usecase.impl;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.proyectos.application.estudiante.command.finder.EstudiantePorIdFinder;
import com.arquisoft.proyectos.application.estudiante.command.result.AgregacionEstudianteResult;
import com.arquisoft.proyectos.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.proyectos.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarEstudianteUseCaseImplTest {

    @Mock
    private EstudianteOutputPort estudianteOutputPort;
    @Mock
    private EstudiantePorIdFinder estudiantePorIdFinder;
    @Mock
    private AppLogger logger;

    private AgregarEstudianteUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarEstudianteUseCaseImpl(estudianteOutputPort, estudiantePorIdFinder, logger);
    }

    private EstudianteDomain estudiante(UUID id, Instant ocurridoEn) {
        return EstudianteDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);
    }

    @Test
    void debeAgregarEstudiante_cuandoNoExisteLaFila() {
        // Arrange
        var id = UUID.randomUUID();
        var estudiante = estudiante(id, Instant.now());
        when(estudiantePorIdFinder.obtener(id)).thenReturn(EstudianteDomain.VACIO);

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
        when(estudiantePorIdFinder.obtener(id))
                .thenReturn(EstudianteDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co", vigente, UtilFecha.VACIO));

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
        when(estudiantePorIdFinder.obtener(id))
                .thenReturn(EstudianteDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co", vigente, UtilFecha.VACIO));

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
        when(estudiantePorIdFinder.obtener(id))
                .thenReturn(EstudianteDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co", vigente, UtilFecha.VACIO));

        // Act
        var resultado = useCase.ejecutar(estudiante);

        // Assert
        assertThat(resultado).isInstanceOf(AgregacionEstudianteResult.Descartada.class);
        verify(estudianteOutputPort, never()).guardar(any());
    }

    @Test
    void debeReactivarSinGuardar_cuandoElEventoEsPosteriorALaBaja() {
        // Arrange
        var id = UUID.randomUUID();
        var baja = Instant.now().minus(1, ChronoUnit.HOURS);
        var ocurridoEn = Instant.now();
        when(estudiantePorIdFinder.obtener(id)).thenReturn(
                EstudianteDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co", baja, baja));
        var estudiante = EstudianteDomain.crear(id, "20161020999", "Ana Gomez", "ana.gomez@uco.edu.co", ocurridoEn);

        // Act
        var resultado = useCase.ejecutar(estudiante);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(AgregacionEstudianteResult.Reactivada.class,
                reactivada -> assertThat(reactivada.estudiante()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(EstudianteEntity.class);
        verify(estudianteOutputPort, times(1)).reactivar(captor.capture());
        assertThat(captor.getValue().identificador()).isEqualTo("20161020999");
        assertThat(captor.getValue().nombre()).isEqualTo("Ana Gomez");
        assertThat(captor.getValue().email()).isEqualTo("ana.gomez@uco.edu.co");
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(ocurridoEn);
        assertThat(captor.getValue().eliminadoEn()).isEqualTo(UtilFecha.VACIO);
        verify(estudianteOutputPort, never()).guardar(any());
    }

    @Test
    void debeDescartarSinReactivar_cuandoElAgregadoEsAnteriorALaLapida() {
        // Arrange
        var id = UUID.randomUUID();
        var baja = Instant.now();
        when(estudiantePorIdFinder.obtener(id)).thenReturn(
                EstudianteDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co", baja, baja));

        // Act
        var resultado = useCase.ejecutar(estudiante(id, baja.minus(1, ChronoUnit.HOURS)));

        // Assert
        assertThat(resultado).isInstanceOf(AgregacionEstudianteResult.Descartada.class);
        verify(estudianteOutputPort, never()).reactivar(any());
        verify(estudianteOutputPort, never()).guardar(any());
    }
}
