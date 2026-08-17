package com.arquisoft.fichas.application.estudiante.command.secondaryport.mapper;

import com.arquisoft.fichas.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.fichas.domain.estudiante.EstudianteDomain;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteMapperTest {

    @Test
    void debeMaperarADominio_cuandoEntityEsValida() {
        // Arrange
        UUID id = UUID.randomUUID();
        EstudianteEntity entity = new EstudianteEntity(
                id, "20161020123", "Juan Pérez", "juan.perez@example.com");

        // Act
        EstudianteDomain aggregate = EstudianteMapper.toDomain(entity);

        // Assert
        assertThat(aggregate.getId()).isEqualTo(id);
        assertThat(aggregate.getIdentificador()).isEqualTo("20161020123");
        assertThat(aggregate.getNombre()).isEqualTo("Juan Pérez");
        assertThat(aggregate.getEmail()).isEqualTo("juan.perez@example.com");
    }

    @Test
    void debeMaperarAEntity_cuandoAggregateEsValido() {
        // Arrange
        UUID id = UUID.randomUUID();
        EstudianteDomain aggregate = EstudianteDomain.reconstruir(
                id,
                "20161020123",
                "Juan Pérez",
                "juan.perez@example.com"
        );

        // Act
        EstudianteEntity entity = EstudianteMapper.toEntity(aggregate);

        // Assert
        assertThat(entity.id()).isEqualTo(id);
        assertThat(entity.identificador()).isEqualTo("20161020123");
        assertThat(entity.nombre()).isEqualTo("Juan Pérez");
        assertThat(entity.email()).isEqualTo("juan.perez@example.com");
    }

    @Test
    void debePreservarId_cuandoMapeaIdaYVuelta() {
        // Arrange
        UUID idOriginal = UUID.randomUUID();
        EstudianteEntity entityOriginal = new EstudianteEntity(
                idOriginal, "20161020123", "Juan Pérez", "juan.perez@example.com");

        // Act
        EstudianteDomain aggregate = EstudianteMapper.toDomain(entityOriginal);
        EstudianteEntity entityMapeada = EstudianteMapper.toEntity(aggregate);

        // Assert
        assertThat(entityMapeada.id()).isEqualTo(idOriginal);
        assertThat(entityMapeada.identificador()).isEqualTo(entityOriginal.identificador());
        assertThat(entityMapeada.nombre()).isEqualTo(entityOriginal.nombre());
        assertThat(entityMapeada.email()).isEqualTo(entityOriginal.email());
    }
}
