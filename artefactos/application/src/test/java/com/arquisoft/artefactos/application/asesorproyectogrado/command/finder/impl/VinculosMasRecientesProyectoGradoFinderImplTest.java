package com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.impl;

import com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VinculosMasRecientesProyectoGradoFinderImplTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-20T10:00:00Z");

    @Mock
    private AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;

    @InjectMocks
    private VinculosMasRecientesProyectoGradoFinderImpl finder;

    @Test
    void debeMapearALosDomains_cuandoElPuertoDevuelveVinculos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var primero = new AsesorProyectoGradoEntity(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), proyecto, OCURRIDO_EN, UtilFecha.VACIO);
        var segundo = new AsesorProyectoGradoEntity(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), proyecto, OCURRIDO_EN, UtilFecha.VACIO);
        when(asesorProyectoGradoOutputPort.obtenerMasRecientesPorProyectoGrado(proyecto))
                .thenReturn(List.of(primero, segundo));

        // Act
        var vinculos = finder.obtener(proyecto);

        // Assert
        assertThat(vinculos).hasSize(2);
        assertThat(vinculos.get(0).getId()).isEqualTo(primero.id());
        assertThat(vinculos.get(1).getAsesor()).isEqualTo(segundo.asesor());
    }

    @Test
    void debeDevolverListaVacia_cuandoElProyectoNoTieneVinculos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        when(asesorProyectoGradoOutputPort.obtenerMasRecientesPorProyectoGrado(proyecto)).thenReturn(List.of());

        // Act
        var vinculos = finder.obtener(proyecto);

        // Assert
        assertThat(vinculos).isEmpty();
    }
}
