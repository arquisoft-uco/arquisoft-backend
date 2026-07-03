package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstudianteFichaPerfilJpaRepositoryTest {

    @Autowired
    private EstudianteFichaPerfilJpaRepository repository;

    @Test
    void debeRetornarFalse_cuandoRelacionNoExiste() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        // Act
        boolean resultado = repository.existsByFichaPerfilIdAndEstudianteId(fichaId, estudianteId);

        // Assert
        assertThat(resultado).isFalse();
    }

    @Test
    void debeContarRelacionesDeLaFicha_cuandoExistenVarias() {
        // Arrange — la entidad usa columnas UUID crudas (sin @ManyToOne), no requiere filas padre
        UUID fichaId = UUID.randomUUID();
        UUID otraFicha = UUID.randomUUID();
        repository.saveAndFlush(EstudianteFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID()).fichaPerfilId(fichaId).estudianteId(UUID.randomUUID()).build());
        repository.saveAndFlush(EstudianteFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID()).fichaPerfilId(fichaId).estudianteId(UUID.randomUUID()).build());
        repository.saveAndFlush(EstudianteFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID()).fichaPerfilId(otraFicha).estudianteId(UUID.randomUUID()).build());

        // Act
        long resultado = repository.countByFichaPerfilId(fichaId);

        // Assert
        assertThat(resultado).isEqualTo(2);
    }
}
