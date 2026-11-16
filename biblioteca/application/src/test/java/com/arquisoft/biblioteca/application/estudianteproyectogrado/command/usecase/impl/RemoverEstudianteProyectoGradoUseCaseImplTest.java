package com.arquisoft.biblioteca.application.estudianteproyectogrado.command.usecase.impl;

import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.finder.VinculoEstudianteProyectoGradoFinder;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.result.RemocionEstudianteProyectoGradoResult;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverEstudianteProyectoGradoUseCaseImplTest {

    private static final Instant T1 = Instant.parse("2026-11-15T10:00:00Z");
    private static final Instant T2 = Instant.parse("2026-11-16T10:00:00Z");

    @Mock
    private EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;
    @Mock
    private VinculoEstudianteProyectoGradoFinder vinculoFinder;
    @Mock
    private AppLogger logger;

    private RemoverEstudianteProyectoGradoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RemoverEstudianteProyectoGradoUseCaseImpl(
                estudianteProyectoGradoOutputPort, vinculoFinder, logger);
    }

    @Test
    void debeInsertarLapida_cuandoElVinculoNoExiste() {
        // Arrange
        var entrada = EstudianteProyectoGradoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), T1);
        when(vinculoFinder.obtener(entrada)).thenReturn(EstudianteProyectoGradoDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOf(RemocionEstudianteProyectoGradoResult.Lapida.class);
        var captor = ArgumentCaptor.forClass(EstudianteProyectoGradoEntity.class);
        verify(estudianteProyectoGradoOutputPort).guardar(captor.capture());
        assertThat(captor.getValue().autor()).isEqualTo(entrada.getAutor());
        assertThat(captor.getValue().eliminadoEn()).isEqualTo(T1);
    }

    @Test
    void noDebeGuardar_cuandoElEventoEsMasViejoQueElVinculo() {
        // Arrange
        var entrada = EstudianteProyectoGradoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), T1);
        var existente = EstudianteProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), entrada.getAutor(), entrada.getProyectoGrado(), T2, UtilFecha.VACIO);
        when(vinculoFinder.obtener(entrada)).thenReturn(existente);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(RemocionEstudianteProyectoGradoResult.Descartada.class,
                descartada -> assertThat(descartada.ocurridoEnVigente()).isEqualTo(T2));
        verify(estudianteProyectoGradoOutputPort, never()).guardar(any());
    }

    @Test
    void debeRemoverElVinculoExistente_cuandoElEventoEsPosterior() {
        // Arrange
        var entrada = EstudianteProyectoGradoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), T2);
        var existente = EstudianteProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), entrada.getAutor(), entrada.getProyectoGrado(), T1, UtilFecha.VACIO);
        when(vinculoFinder.obtener(entrada)).thenReturn(existente);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOf(RemocionEstudianteProyectoGradoResult.Removida.class);
        var captor = ArgumentCaptor.forClass(EstudianteProyectoGradoEntity.class);
        verify(estudianteProyectoGradoOutputPort).guardar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(existente.getId());
        assertThat(captor.getValue().eliminadoEn()).isEqualTo(T2);
    }
}
