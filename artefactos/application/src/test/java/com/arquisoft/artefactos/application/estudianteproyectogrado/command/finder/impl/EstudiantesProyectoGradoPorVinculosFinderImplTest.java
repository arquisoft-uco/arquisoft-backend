package com.arquisoft.artefactos.application.estudianteproyectogrado.command.finder.impl;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.artefactos.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.artefactos.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudiantesProyectoGradoPorVinculosFinderImplTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-20T10:00:00Z");

    @Mock
    private EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;

    @InjectMocks
    private EstudiantesProyectoGradoPorVinculosFinderImpl finder;

    @Test
    void debeConsultarUnaVezConTodosLosEstudiantesYMapearAlDominio() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var estudianteA = UtilUUID.generarNuevoUUID();
        var estudianteB = UtilUUID.generarNuevoUUID();
        var estudianteC = UtilUUID.generarNuevoUUID();
        var agregacion = AgregacionEstudiantesProyectoGradoDomain.crear(List.of(
                EstudianteProyectoGradoDomain.crear(estudianteA, proyecto, OCURRIDO_EN),
                EstudianteProyectoGradoDomain.crear(estudianteB, proyecto, OCURRIDO_EN),
                EstudianteProyectoGradoDomain.crear(estudianteC, proyecto, OCURRIDO_EN)));
        var entity = new EstudianteProyectoGradoEntity(
                UtilUUID.generarNuevoUUID(), estudianteA, proyecto, OCURRIDO_EN, UtilFecha.VACIO);
        when(estudianteProyectoGradoOutputPort.obtenerPorProyectoGradoYEstudiantes(
                proyecto, List.of(estudianteA, estudianteB, estudianteC))).thenReturn(List.of(entity));

        // Act
        var existentes = finder.obtener(agregacion);

        // Assert
        assertThat(existentes).singleElement().satisfies(vinculo -> {
            assertThat(vinculo.getId()).isEqualTo(entity.id());
            assertThat(vinculo.getEstudiante()).isEqualTo(estudianteA);
        });
        verify(estudianteProyectoGradoOutputPort, times(1)).obtenerPorProyectoGradoYEstudiantes(
                proyecto, List.of(estudianteA, estudianteB, estudianteC));
    }
}
