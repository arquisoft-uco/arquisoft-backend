package com.arquisoft.proyectos.application.proyectogrado.command.finder.impl;

import com.arquisoft.proyectos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProyectoGradoBloqueadoPorIdFinderImplTest {

    @Mock
    private ProyectoGradoOutputPort proyectoGradoOutputPort;

    @InjectMocks
    private ProyectoGradoBloqueadoPorIdFinderImpl finder;

    @Test
    void debeReconstruirElProyecto_cuandoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        when(proyectoGradoOutputPort.obtenerBloqueadoPorId(id)).thenReturn(Optional.of(
                new ProyectoGradoEntity(id, fichaPerfil, "Titulo", coordinador)));

        // Act
        var proyecto = finder.obtener(id);

        // Assert
        assertThat(proyecto.getId()).isEqualTo(id);
        assertThat(proyecto.getFichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(proyecto.getTituloProyecto()).isEqualTo("Titulo");
        assertThat(proyecto.getCoordinador()).isEqualTo(coordinador);
    }

    @Test
    void debeDevolverVacio_cuandoElProyectoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(proyectoGradoOutputPort.obtenerBloqueadoPorId(id)).thenReturn(Optional.empty());

        // Act
        var proyecto = finder.obtener(id);

        // Assert
        assertThat(proyecto).isSameAs(ProyectoGradoDomain.VACIO);
    }
}
