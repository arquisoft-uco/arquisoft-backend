package com.arquisoft.artefactos.application.proyectogrado.command.finder.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindersProyectoGradoPorArtefactoTest {

    @Mock
    private ProyectoGradoOutputPort outputPort;

    @Test
    void debeRetornarProyecto_cuandoElArtefactoLoTiene() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerPorArtefacto(artefacto))
                .thenReturn(Optional.of(new ProyectoGradoEntity(proyecto, "Proyecto", null, null, null)));

        // Act
        var resultado = new ProyectoGradoPorArtefactoFinderImpl(outputPort).obtener(artefacto);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getId()).isEqualTo(proyecto);
        assertThat(resultado.getNombre()).isEqualTo("Proyecto");
    }

    @Test
    void debeRetornarVacio_cuandoElArtefactoNoTieneProyecto() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerPorArtefacto(artefacto)).thenReturn(Optional.empty());

        // Act
        var resultado = new ProyectoGradoPorArtefactoFinderImpl(outputPort).obtener(artefacto);

        // Assert
        assertThat(resultado.esVacio()).isTrue();
    }

    @Test
    void debeRetornarProyectoBloqueado_cuandoSeBuscaPorIdYExiste() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerBloqueadoPorId(proyecto))
                .thenReturn(Optional.of(new ProyectoGradoEntity(proyecto, "Proyecto", null, null, null)));

        // Act
        var resultado = new ProyectoGradoBloqueadoPorIdFinderImpl(outputPort).obtener(proyecto);

        // Assert
        assertThat(resultado.getId()).isEqualTo(proyecto);
        assertThat(resultado.esVacio()).isFalse();
    }

    @Test
    void debeRetornarVacio_cuandoNoHayProyectoParaBloquearPorId() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerBloqueadoPorId(proyecto)).thenReturn(Optional.empty());

        // Act
        var resultado = new ProyectoGradoBloqueadoPorIdFinderImpl(outputPort).obtener(proyecto);

        // Assert
        assertThat(resultado.esVacio()).isTrue();
    }

    @Test
    void debeRetornarProyectoBloqueado_cuandoElArtefactoLoTiene() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerBloqueadoPorArtefacto(artefacto))
                .thenReturn(Optional.of(new ProyectoGradoEntity(proyecto, "Proyecto", null, null, null)));

        // Act
        var resultado = new ProyectoGradoBloqueadoPorArtefactoFinderImpl(outputPort).obtener(artefacto);

        // Assert
        assertThat(resultado.getId()).isEqualTo(proyecto);
        assertThat(resultado.esVacio()).isFalse();
    }

    @Test
    void debeRetornarVacio_cuandoNoHayProyectoParaBloquear() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerBloqueadoPorArtefacto(artefacto)).thenReturn(Optional.empty());

        // Act
        var resultado = new ProyectoGradoBloqueadoPorArtefactoFinderImpl(outputPort).obtener(artefacto);

        // Assert
        assertThat(resultado.esVacio()).isTrue();
    }
}
