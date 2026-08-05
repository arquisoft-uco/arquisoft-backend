package com.arquisoft.fichas.infrastructure.estudiante.persistence;

import com.arquisoft.fichas.domain.estudiante.aggregate.EstudianteDomain;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteMapperTest {

    private final EstudianteMapper mapper = new EstudianteMapper();

    @Test
    void debeMaperarADominio_cuandoEntityEsValida() {
        // Arrange
        UUID id = UUID.randomUUID();
        EstudianteEntity entity = EstudianteEntity.builder()
                .id(id)
                .identificador("20161020123")
                .nombre("Juan Pérez")
                .email("juan.perez@example.com")
                .build();

        // Act
        EstudianteDomain aggregate = mapper.toDomain(entity);

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
        EstudianteEntity entity = mapper.toEntity(aggregate);

        // Assert
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getIdentificador()).isEqualTo("20161020123");
        assertThat(entity.getNombre()).isEqualTo("Juan Pérez");
        assertThat(entity.getEmail()).isEqualTo("juan.perez@example.com");
    }

    @Test
    void debePreservarId_cuandoMapeaIdaYVuelta() {
        // Arrange
        UUID idOriginal = UUID.randomUUID();
        EstudianteEntity entityOriginal = EstudianteEntity.builder()
                .id(idOriginal)
                .identificador("20161020123")
                .nombre("Juan Pérez")
                .email("juan.perez@example.com")
                .build();

        // Act
        EstudianteDomain aggregate = mapper.toDomain(entityOriginal);
        EstudianteEntity entityMapeada = mapper.toEntity(aggregate);

        // Assert
        assertThat(entityMapeada.getId()).isEqualTo(idOriginal);
        assertThat(entityMapeada.getIdentificador()).isEqualTo(entityOriginal.getIdentificador());
        assertThat(entityMapeada.getNombre()).isEqualTo(entityOriginal.getNombre());
        assertThat(entityMapeada.getEmail()).isEqualTo(entityOriginal.getEmail());
    }
}
