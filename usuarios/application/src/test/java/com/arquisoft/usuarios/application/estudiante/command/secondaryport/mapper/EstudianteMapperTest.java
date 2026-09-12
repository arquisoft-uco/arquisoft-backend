package com.arquisoft.usuarios.application.estudiante.command.secondaryport.mapper;

import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteMapperTest {

    @Test
    void debeMapearDomainAEntity_enEstudianteMapper() {
        // Arrange
        var usuario = UUID.randomUUID();
        var estudiante = EstudianteDomain.crear(usuario);

        // Act
        var entity = EstudianteMapper.toEntity(estudiante);

        // Assert
        assertThat(entity.usuario()).isEqualTo(usuario);
    }

    @Test
    void debeMapearEntityADomain_cuandoSeLee() {
        // Arrange
        var usuario = UUID.randomUUID();
        var entity = new com.arquisoft.usuarios.application.estudiante.command.secondaryport.entity
                .EstudianteEntity(usuario);

        // Act
        var domain = EstudianteMapper.toDomain(entity);

        // Assert
        assertThat(domain.getUsuario()).isEqualTo(usuario);
    }
}
