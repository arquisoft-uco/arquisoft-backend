package com.arquisoft.proyectos.application.proyectogrado.command.finder.impl;

import com.arquisoft.proyectos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProyectoGradoPorIdFinderImplTest {

    @Mock
    private ProyectoGradoOutputPort proyectoGradoOutputPort;

    @InjectMocks
    private ProyectoGradoPorIdFinderImpl finder;

    @Test
    void debeReconstruirElProyecto_cuandoExiste() {
        // Arrange
        var id = UUID.randomUUID();
        var fichaPerfil = UUID.randomUUID();
        var coordinador = UUID.randomUUID();
        when(proyectoGradoOutputPort.obtenerPorId(id)).thenReturn(Optional.of(
                new ProyectoGradoEntity(id, fichaPerfil, "Titulo", coordinador, "LISTO_PARA_REVISION")));

        // Act
        var proyecto = finder.obtener(id);

        // Assert
        assertThat(proyecto.getId()).isEqualTo(id);
        assertThat(proyecto.getFichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(proyecto.getTituloProyecto()).isEqualTo("Titulo");
        assertThat(proyecto.getCoordinador()).isEqualTo(coordinador);
        assertThat(proyecto.getEstadoProyectoGrado()).isEqualTo(EstadoProyectoGrado.LISTO_PARA_REVISION);
    }

    @Test
    void debeDevolverVacio_cuandoElProyectoNoExiste() {
        // Arrange
        var id = UUID.randomUUID();
        when(proyectoGradoOutputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var proyecto = finder.obtener(id);

        // Assert
        assertThat(proyecto).isSameAs(ProyectoGradoDomain.VACIO);
    }
}
