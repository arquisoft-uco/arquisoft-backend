package com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.finder.VinculoEstudianteProyectoGradoFinder;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.result.RemocionEstudianteProyectoGradoResult;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.evaluaciones.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverEstudianteProyectoGradoUseCaseImplTest {

    private static final Instant GUARDADO_EN = Instant.parse("2026-10-20T10:00:00Z");
    private static final Instant NUEVO = GUARDADO_EN.plusSeconds(3600);

    @Mock
    private EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;
    @Mock
    private VinculoEstudianteProyectoGradoFinder vinculoEstudianteProyectoGradoFinder;
    @Mock
    private AppLogger logger;

    private RemoverEstudianteProyectoGradoUseCaseImpl useCase;

    private final UUID estudiante = UtilUUID.generarNuevoUUID();
    private final UUID proyectoGrado = UtilUUID.generarNuevoUUID();

    @BeforeEach
    void setUp() {
        useCase = new RemoverEstudianteProyectoGradoUseCaseImpl(
                estudianteProyectoGradoOutputPort, vinculoEstudianteProyectoGradoFinder, logger);
    }

    private EstudianteProyectoGradoDomain entrada(Instant ocurridoEn) {
        return EstudianteProyectoGradoDomain.crear(estudiante, proyectoGrado, ocurridoEn);
    }

    private EstudianteProyectoGradoDomain guardado(Instant eliminadoEn) {
        return EstudianteProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), estudiante, proyectoGrado, GUARDADO_EN, eliminadoEn);
    }

    private EstudianteProyectoGradoEntity guardadoCapturado() {
        var captor = ArgumentCaptor.forClass(EstudianteProyectoGradoEntity.class);
        verify(estudianteProyectoGradoOutputPort).guardar(captor.capture());
        return captor.getValue();
    }

    @Test
    void debeRemoverYRetornarRemovida_cuandoElVinculoExisteYElEventoEsMasNuevo() {
        // Arrange
        var entrada = entrada(NUEVO);
        var existente = guardado(UtilFecha.VACIO);
        when(vinculoEstudianteProyectoGradoFinder.obtener(entrada)).thenReturn(existente);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isEqualTo(new RemocionEstudianteProyectoGradoResult.Removida(proyectoGrado, estudiante));
        var guardado = guardadoCapturado();
        assertThat(guardado.id()).isEqualTo(existente.getId());
        assertThat(guardado.eliminadoEn()).isEqualTo(NUEVO);
        assertThat(guardado.ocurridoEn()).isEqualTo(NUEVO);
    }

    @Test
    void debeInsertarLapida_cuandoElVinculoNuncaLlego() {
        // Arrange
        var entrada = entrada(NUEVO);
        when(vinculoEstudianteProyectoGradoFinder.obtener(entrada)).thenReturn(EstudianteProyectoGradoDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isEqualTo(new RemocionEstudianteProyectoGradoResult.Lapida(proyectoGrado, estudiante));
        var guardado = guardadoCapturado();
        assertThat(guardado.id()).isEqualTo(entrada.getId());
        assertThat(guardado.eliminadoEn()).isEqualTo(NUEVO);
    }

    @Test
    void debeDescartar_cuandoElEventoNoEsMasNuevo() {
        // Arrange
        var entrada = entrada(GUARDADO_EN);
        when(vinculoEstudianteProyectoGradoFinder.obtener(entrada)).thenReturn(guardado(UtilFecha.VACIO));

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isEqualTo(
                new RemocionEstudianteProyectoGradoResult.Descartada(proyectoGrado, estudiante, GUARDADO_EN));
        verify(estudianteProyectoGradoOutputPort, never()).guardar(any());
    }

    @Test
    void debeDescartar_cuandoElEventoEsMasViejoQueElVinculoYaDadoDeBaja() {
        // Arrange
        var entrada = entrada(GUARDADO_EN.minusSeconds(3600));
        when(vinculoEstudianteProyectoGradoFinder.obtener(entrada)).thenReturn(guardado(GUARDADO_EN));

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOf(RemocionEstudianteProyectoGradoResult.Descartada.class);
        verify(estudianteProyectoGradoOutputPort, never()).guardar(any());
    }

    @Test
    void debeRemoverDeNuevo_cuandoElEventoEsMasNuevoSobreUnVinculoYaDadoDeBaja() {
        // Arrange
        var entrada = entrada(NUEVO);
        when(vinculoEstudianteProyectoGradoFinder.obtener(entrada)).thenReturn(guardado(GUARDADO_EN));

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isInstanceOf(RemocionEstudianteProyectoGradoResult.Removida.class);
        assertThat(guardadoCapturado().eliminadoEn()).isEqualTo(NUEVO);
    }
}
