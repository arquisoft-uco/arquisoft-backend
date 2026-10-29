package com.arquisoft.artefactos.application.estudianteproyectogrado.command.usecase.impl;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.finder.EstudiantesProyectoGradoPorVinculosFinder;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.result.AgregacionEstudiantesProyectoGradoResult;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.artefactos.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.artefactos.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarEstudiantesProyectoGradoUseCaseImplTest {

    private static final Instant T1 = Instant.parse("2026-10-01T10:00:00Z");
    private static final Instant T2 = Instant.parse("2026-10-02T10:00:00Z");
    private static final Instant T3 = Instant.parse("2026-10-03T10:00:00Z");

    @Mock
    private EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;
    @Mock
    private EstudiantesProyectoGradoPorVinculosFinder estudiantesProyectoGradoPorVinculosFinder;
    @Mock
    private AppLogger logger;

    private AgregarEstudiantesProyectoGradoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AgregarEstudiantesProyectoGradoUseCaseImpl(
                estudianteProyectoGradoOutputPort, estudiantesProyectoGradoPorVinculosFinder, logger);
    }

    @Test
    void debeClasificarCadaEstudianteYGuardarAusentesDuplicadosMasNuevosYReactivados_cuandoHayExistentes() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var ausente = UtilUUID.generarNuevoUUID();
        var descartado = UtilUUID.generarNuevoUUID();
        var duplicado = UtilUUID.generarNuevoUUID();
        var igual = UtilUUID.generarNuevoUUID();
        var reactivado = UtilUUID.generarNuevoUUID();
        var entrada = AgregacionEstudiantesProyectoGradoDomain.crear(List.of(
                EstudianteProyectoGradoDomain.crear(ausente, proyecto, T2),
                EstudianteProyectoGradoDomain.crear(descartado, proyecto, T2),
                EstudianteProyectoGradoDomain.crear(duplicado, proyecto, T2),
                EstudianteProyectoGradoDomain.crear(igual, proyecto, T2),
                EstudianteProyectoGradoDomain.crear(reactivado, proyecto, T2)));
        var vinculoReactivado = EstudianteProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), reactivado, proyecto, T1, T1);
        when(estudiantesProyectoGradoPorVinculosFinder.obtener(entrada)).thenReturn(List.of(
                EstudianteProyectoGradoDomain.reconstruir(
                        UtilUUID.generarNuevoUUID(), descartado, proyecto, T3, UtilFecha.VACIO),
                EstudianteProyectoGradoDomain.reconstruir(
                        UtilUUID.generarNuevoUUID(), duplicado, proyecto, T1, UtilFecha.VACIO),
                EstudianteProyectoGradoDomain.reconstruir(
                        UtilUUID.generarNuevoUUID(), igual, proyecto, T2, UtilFecha.VACIO),
                vinculoReactivado));

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isEqualTo(new AgregacionEstudiantesProyectoGradoResult(proyecto, 1, 1, 1, 2));
        var captor = ArgumentCaptor.<List<EstudianteProyectoGradoEntity>>captor();
        verify(estudianteProyectoGradoOutputPort, times(1)).guardarTodos(captor.capture());
        assertThat(captor.getValue()).extracting(EstudianteProyectoGradoEntity::estudiante)
                .containsExactlyInAnyOrder(ausente, duplicado, reactivado);
        assertThat(captor.getValue()).filteredOn(e -> e.estudiante().equals(duplicado)).singleElement()
                .satisfies(e -> {
                    assertThat(e.destituidoEn()).isEqualTo(UtilFecha.VACIO);
                    assertThat(e.ocurridoEn()).isEqualTo(T2);
                });
        assertThat(captor.getValue()).filteredOn(e -> e.estudiante().equals(reactivado)).singleElement()
                .satisfies(e -> {
                    assertThat(e.id()).isEqualTo(vinculoReactivado.getId());
                    assertThat(e.destituidoEn()).isEqualTo(UtilFecha.VACIO);
                    assertThat(e.ocurridoEn()).isEqualTo(T2);
                });
    }

    @Test
    void debeConsultarUnaVezYGuardarUnaVez_cuandoLlegaMasDeUnEstudiante() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var entrada = AgregacionEstudiantesProyectoGradoDomain.crear(List.of(
                EstudianteProyectoGradoDomain.crear(UtilUUID.generarNuevoUUID(), proyecto, T2),
                EstudianteProyectoGradoDomain.crear(UtilUUID.generarNuevoUUID(), proyecto, T2),
                EstudianteProyectoGradoDomain.crear(UtilUUID.generarNuevoUUID(), proyecto, T2)));
        when(estudiantesProyectoGradoPorVinculosFinder.obtener(any())).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isEqualTo(new AgregacionEstudiantesProyectoGradoResult(proyecto, 3, 0, 0, 0));
        verify(estudiantesProyectoGradoPorVinculosFinder, times(1)).obtener(any());
        var captor = ArgumentCaptor.<List<EstudianteProyectoGradoEntity>>captor();
        verify(estudianteProyectoGradoOutputPort, times(1)).guardarTodos(captor.capture());
        assertThat(captor.getValue()).hasSize(3);
    }
}
