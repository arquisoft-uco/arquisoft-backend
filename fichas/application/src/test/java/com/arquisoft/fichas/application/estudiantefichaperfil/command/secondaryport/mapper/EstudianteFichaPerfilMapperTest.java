package com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.mapper;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.entity.EstudianteFichaPerfilEntity;
import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteFichaPerfilMapperTest {

    @Test
    void debeMaperarADominio_cuandoEntityEsValida() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        EstudianteFichaPerfilEntity entity = EstudianteFichaPerfilEntity.builder()
                .id(id)
                .fichaPerfilId(fichaPerfilId)
                .estudianteId(estudianteId)
                .build();

        // Act
        EstudianteFichaPerfilDomain aggregate = EstudianteFichaPerfilMapper.toDomain(entity);

        // Assert
        assertThat(aggregate.getId()).isEqualTo(id);
        assertThat(aggregate.getFichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(aggregate.getEstudianteId()).isEqualTo(estudianteId);
    }

    @Test
    void debeMaperarAEntity_cuandoAggregateEsValido() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        EstudianteFichaPerfilDomain aggregate = EstudianteFichaPerfilDomain.reconstruir(
                id,
                fichaPerfilId,
                estudianteId
        );

        // Act
        EstudianteFichaPerfilEntity entity = EstudianteFichaPerfilMapper.toEntity(aggregate);

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

        EstudianteFichaPerfilEntity entityOriginal = EstudianteFichaPerfilEntity.builder()
                .id(idOriginal)
                .fichaPerfilId(fichaPerfilIdOriginal)
                .estudianteId(estudianteIdOriginal)
                .build();

        // Act
        EstudianteFichaPerfilDomain aggregate = EstudianteFichaPerfilMapper.toDomain(entityOriginal);
        EstudianteFichaPerfilEntity entityMapeada = EstudianteFichaPerfilMapper.toEntity(aggregate);

        // Assert
        assertThat(entityMapeada.getId()).isEqualTo(idOriginal);
        assertThat(entityMapeada.getFichaPerfilId()).isEqualTo(fichaPerfilIdOriginal);
        assertThat(entityMapeada.getEstudianteId()).isEqualTo(estudianteIdOriginal);
    }
}
