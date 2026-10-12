package com.arquisoft.fichas.application.estudiante.command.usecase.impl;

import com.arquisoft.fichas.application.estudiante.command.finder.EstudiantePorIdFinder;
import com.arquisoft.fichas.application.estudiante.command.result.RemocionEstudianteResult;
import com.arquisoft.fichas.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.fichas.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.fichas.domain.estudiante.EstudianteDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilFecha;
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
class RemoverEstudianteUseCaseImplTest {

    private static final Instant ANTES = Instant.parse("2026-09-01T10:00:00Z");
    private static final Instant DESPUES = Instant.parse("2026-09-16T10:00:00Z");

    @Mock
    private EstudianteOutputPort estudianteOutputPort;
    @Mock
    private EstudiantePorIdFinder estudiantePorIdFinder;
    @Mock
    private AppLogger logger;

    private RemoverEstudianteUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RemoverEstudianteUseCaseImpl(estudianteOutputPort, estudiantePorIdFinder, logger);
    }

    private EstudianteDomain evento(UUID id, Instant ocurridoEn) {
        return EstudianteDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);
    }

    private EstudianteDomain replicado(UUID id, Instant ocurridoEn) {
        return EstudianteDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn,
                UtilFecha.VACIO);
    }

    @Test
    void debeInsertarLapidaRemovida_cuandoElEstudianteNuncaSeReplico() {
        // Arrange
        var id = UUID.randomUUID();
        when(estudiantePorIdFinder.obtener(id)).thenReturn(EstudianteDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(evento(id, DESPUES));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionEstudianteResult.Lapida.class,
                lapida -> assertThat(lapida.estudiante()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(EstudianteEntity.class);
        verify(estudianteOutputPort, times(1)).guardar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(id);
        assertThat(captor.getValue().eliminadoEn()).isEqualTo(DESPUES);
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(DESPUES);
        verify(estudianteOutputPort, never()).eliminarLogica(any(), any());
        verify(estudiantePorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeEliminarLogicamente_cuandoElEventoEsPosteriorAlReplicado() {
        // Arrange
        var id = UUID.randomUUID();
        when(estudiantePorIdFinder.obtener(id)).thenReturn(replicado(id, ANTES));

        // Act
        var resultado = useCase.ejecutar(evento(id, DESPUES));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionEstudianteResult.Removida.class,
                removida -> assertThat(removida.estudiante()).isEqualTo(id));
        verify(estudianteOutputPort, times(1)).eliminarLogica(id, DESPUES);
        verify(estudianteOutputPort, never()).guardar(any());
        verify(estudiantePorIdFinder, times(1)).obtener(id);
    }

    @Test
    void debeDescartarSinEscribir_cuandoElEventoEsAnteriorAlReplicado() {
        // Arrange
        var id = UUID.randomUUID();
        when(estudiantePorIdFinder.obtener(id)).thenReturn(replicado(id, DESPUES));

        // Act
        var resultado = useCase.ejecutar(evento(id, ANTES));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionEstudianteResult.Descartada.class, descartada -> {
            assertThat(descartada.estudiante()).isEqualTo(id);
            assertThat(descartada.ocurridoEnVigente()).isEqualTo(DESPUES);
        });
        verify(estudianteOutputPort, never()).guardar(any());
        verify(estudianteOutputPort, never()).eliminarLogica(any(), any());
    }

    @Test
    void debeDescartarSinEscribir_cuandoElRemovidoLlegaRepetido() {
        // Arrange
        var id = UUID.randomUUID();
        var yaRemovido = EstudianteDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co",
                DESPUES, DESPUES);
        when(estudiantePorIdFinder.obtener(id)).thenReturn(yaRemovido);

        // Act
        var resultado = useCase.ejecutar(evento(id, DESPUES));

        // Assert
        assertThat(resultado).isInstanceOf(RemocionEstudianteResult.Descartada.class);
        verify(estudianteOutputPort, never()).guardar(any());
        verify(estudianteOutputPort, never()).eliminarLogica(any(), any());
    }
}
