package com.arquisoft.artefactos.application.estudiante.command.usecase.impl;

import com.arquisoft.artefactos.application.estudiante.command.finder.EstudiantePorIdFinder;
import com.arquisoft.artefactos.application.estudiante.command.result.RemocionEstudianteResult;
import com.arquisoft.artefactos.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.artefactos.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
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
class RemoverEstudianteUseCaseImplTest {

    private static final Instant VIGENTE_EN = Instant.parse("2026-09-24T10:00:00Z");

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

    private EstudianteDomain entrada(UUID id, Instant ocurridoEn) {
        return EstudianteDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);
    }

    private EstudianteDomain vigente(UUID id) {
        return EstudianteDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co", VIGENTE_EN, null);
    }

    @Test
    void debeEliminarLogicamenteYRetornarRemovida_cuandoElEventoEsMasNuevo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = VIGENTE_EN.plusSeconds(3600);
        when(estudiantePorIdFinder.obtener(id)).thenReturn(vigente(id));

        // Act
        var resultado = useCase.ejecutar(entrada(id, ocurridoEn));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionEstudianteResult.Removida.class,
                removida -> assertThat(removida.estudiante()).isEqualTo(id));
        verify(estudiantePorIdFinder, times(1)).obtener(id);
        verify(estudianteOutputPort, times(1)).eliminarLogica(id, ocurridoEn);
        verify(estudianteOutputPort, never()).guardar(any());
    }

    @Test
    void debeDescartarSinEscribir_cuandoElEventoNoEsMasNuevoQueElVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(estudiantePorIdFinder.obtener(id)).thenReturn(vigente(id));

        // Act
        var resultado = useCase.ejecutar(entrada(id, VIGENTE_EN));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionEstudianteResult.Descartada.class, descartada -> {
            assertThat(descartada.estudiante()).isEqualTo(id);
            assertThat(descartada.ocurridoEnVigente()).isEqualTo(VIGENTE_EN);
        });
        verify(estudianteOutputPort, never()).eliminarLogica(any(), any());
        verify(estudianteOutputPort, never()).guardar(any());
    }

    @Test
    void debeInsertarLapidaYRetornarLapida_cuandoElEstudianteNuncaSeReplico() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = VIGENTE_EN;
        when(estudiantePorIdFinder.obtener(id)).thenReturn(EstudianteDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(entrada(id, ocurridoEn));

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionEstudianteResult.Lapida.class,
                lapida -> assertThat(lapida.estudiante()).isEqualTo(id));
        var captor = ArgumentCaptor.forClass(EstudianteEntity.class);
        verify(estudianteOutputPort, times(1)).guardar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(id);
        assertThat(captor.getValue().eliminadoEn()).isEqualTo(ocurridoEn);
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(ocurridoEn);
        verify(estudianteOutputPort, never()).eliminarLogica(any(), any());
    }
}
