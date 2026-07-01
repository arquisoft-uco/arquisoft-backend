package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.persistence;

import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilAggregate;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteFichaPerfilMapperTest {

    private final EstudianteFichaPerfilMapper mapper = new EstudianteFichaPerfilMapper();

    @Test
    void debeMaperarADominio_cuandoEntityEsValida() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        EstudianteFichaPerfilJpaEntity entity = EstudianteFichaPerfilJpaEntity.builder()
                .id(id)
                .fichaPerfilId(fichaPerfilId)
                .estudianteId(estudianteId)
                .build();

        // Act
        EstudianteFichaPerfilAggregate aggregate = mapper.toDomain(entity);

        // Assert
        assertThat(aggregate.getId()).isEqualTo(id);
        assertThat(aggregate.getFichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(aggregate.getEstudianteId()).isEqualTo(estudianteId);
    }

    @Test
    void debeMaperarAJpaEntity_cuandoAggregateEsValido() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        EstudianteFichaPerfilAggregate aggregate = EstudianteFichaPerfilAggregate.reconstruir(
                id,
                fichaPerfilId,
                estudianteId
        );

        // Act
        EstudianteFichaPerfilJpaEntity entity = mapper.toJpaEntity(aggregate);

        // Assert
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getFichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(entity.getEstudianteId()).isEqualTo(estudianteId);
    }

    @Test
    void debePreservarIds_cuandoMapeaIdaYVuelta() {
        // Arrange
        UUID idOriginal = UUID.randomUUID();
        UUID fichaPerfilIdOriginal = UUID.randomUUID();
        UUID estudianteIdOriginal = UUID.randomUUID();

        EstudianteFichaPerfilJpaEntity entityOriginal = EstudianteFichaPerfilJpaEntity.builder()
                .id(idOriginal)
                .fichaPerfilId(fichaPerfilIdOriginal)
                .estudianteId(estudianteIdOriginal)
                .build();

        // Act
        EstudianteFichaPerfilAggregate aggregate = mapper.toDomain(entityOriginal);
        EstudianteFichaPerfilJpaEntity entityMapeada = mapper.toJpaEntity(aggregate);

        // Assert
        assertThat(entityMapeada.getId()).isEqualTo(idOriginal);
        assertThat(entityMapeada.getFichaPerfilId()).isEqualTo(fichaPerfilIdOriginal);
        assertThat(entityMapeada.getEstudianteId()).isEqualTo(estudianteIdOriginal);
    }
}
