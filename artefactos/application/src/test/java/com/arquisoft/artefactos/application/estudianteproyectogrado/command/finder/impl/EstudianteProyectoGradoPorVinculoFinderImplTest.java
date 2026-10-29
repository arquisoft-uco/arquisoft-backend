package com.arquisoft.artefactos.application.estudianteproyectogrado.command.finder.impl;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.finder.model.VinculoEstudianteProyectoGrado;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudianteProyectoGradoPorVinculoFinderImplTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-20T10:00:00Z");

    @Mock
    private EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;

    @InjectMocks
    private EstudianteProyectoGradoPorVinculoFinderImpl finder;

    @Test
    void debeDevolverElVinculoMapeado_cuandoExiste() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var entity = new EstudianteProyectoGradoEntity(
                UtilUUID.generarNuevoUUID(), estudiante, proyecto, OCURRIDO_EN, UtilFecha.VACIO);
        when(estudianteProyectoGradoOutputPort.obtenerPorEstudianteYProyectoGrado(estudiante, proyecto))
                .thenReturn(Optional.of(entity));

        // Act
        var vinculo = finder.obtener(new VinculoEstudianteProyectoGrado(estudiante, proyecto));

        // Assert
        assertThat(vinculo.esVacio()).isFalse();
        assertThat(vinculo.getId()).isEqualTo(entity.id());
        assertThat(vinculo.getOcurridoEn()).isEqualTo(OCURRIDO_EN);
    }

    @Test
    void debeDevolverVacio_cuandoNoExiste() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        when(estudianteProyectoGradoOutputPort.obtenerPorEstudianteYProyectoGrado(estudiante, proyecto))
                .thenReturn(Optional.empty());

        // Act
        var vinculo = finder.obtener(new VinculoEstudianteProyectoGrado(estudiante, proyecto));

        // Assert
        assertThat(vinculo.esVacio()).isTrue();
    }
}
