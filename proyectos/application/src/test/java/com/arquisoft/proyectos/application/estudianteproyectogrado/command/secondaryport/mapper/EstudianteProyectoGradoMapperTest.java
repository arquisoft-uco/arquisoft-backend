package com.arquisoft.proyectos.application.estudianteproyectogrado.command.secondaryport.mapper;

import com.arquisoft.proyectos.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteProyectoGradoMapperTest {

    @Test
    void debeConservarTodosLosCampos_cuandoSeHaceElViajeDominioEntidadDominio() {
        // Arrange
        var vinculo = EstudianteProyectoGradoDomain.crear(UUID.randomUUID(), List.of(UUID.randomUUID())).getFirst();

        // Act
        var entidad = EstudianteProyectoGradoMapper.toEntity(vinculo);
        var reconstruido = EstudianteProyectoGradoMapper.toDomain(entidad);

        // Assert
        assertThat(entidad.id()).isEqualTo(vinculo.getId());
        assertThat(reconstruido.getId()).isEqualTo(vinculo.getId());
        assertThat(reconstruido.getEstudiante()).isEqualTo(vinculo.getEstudiante());
        assertThat(reconstruido.getProyectoGrado()).isEqualTo(vinculo.getProyectoGrado());
    }
}
