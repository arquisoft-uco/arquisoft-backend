package com.arquisoft.proyectos.application.proyectogrado.command.secondaryport.mapper;

import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectoGradoMapperTest {

    @Test
    void debeConservarTodosLosCampos_cuandoSeHaceElViajeDominioEntidadDominio() {
        // Arrange
        var proyecto = ProyectoGradoDomain.crear(UUID.randomUUID(), "Sistema de gestión", UUID.randomUUID());

        // Act
        var entidad = ProyectoGradoMapper.toEntity(proyecto);
        var reconstruido = ProyectoGradoMapper.toDomain(entidad);

        // Assert
        assertThat(entidad.estadoProyectoGrado()).isEqualTo(EstadoProyectoGrado.EN_PROCESO.getId());
        assertThat(reconstruido.getId()).isEqualTo(proyecto.getId());
        assertThat(reconstruido.getFichaPerfil()).isEqualTo(proyecto.getFichaPerfil());
        assertThat(reconstruido.getTituloProyecto()).isEqualTo(proyecto.getTituloProyecto());
        assertThat(reconstruido.getCoordinador()).isEqualTo(proyecto.getCoordinador());
        assertThat(reconstruido.getEstadoProyectoGrado()).isEqualTo(EstadoProyectoGrado.EN_PROCESO);
    }
}
