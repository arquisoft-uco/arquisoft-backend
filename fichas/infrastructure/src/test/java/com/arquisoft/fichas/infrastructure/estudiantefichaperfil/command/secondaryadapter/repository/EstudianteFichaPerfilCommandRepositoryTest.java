package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.entity.EstudianteFichaPerfilJpaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstudianteFichaPerfilCommandRepositoryTest {

    @Autowired
    private EstudianteFichaPerfilCommandRepository repository;

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

    @Test
    void debeEliminar_cuandoRelacionExisteEnBD() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        var entity = EstudianteFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaId)
                .estudianteId(estudianteId)
                .build();
        repository.saveAndFlush(entity);

        // Act
        repository.deleteByFichaPerfilIdAndEstudianteId(fichaId, estudianteId);
        repository.flush();

        // Assert
        boolean existe = repository.existsByFichaPerfilIdAndEstudianteId(fichaId, estudianteId);
        assertThat(existe).isFalse();
    }

    @Test
    void debeRetornarLosEstudiantesDeLaFicha_cuandoExistenVarios() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID otraFicha = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        UUID estudiante2 = UUID.randomUUID();
        repository.saveAndFlush(EstudianteFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID()).fichaPerfilId(fichaId).estudianteId(estudiante1).build());
        repository.saveAndFlush(EstudianteFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID()).fichaPerfilId(fichaId).estudianteId(estudiante2).build());
        repository.saveAndFlush(EstudianteFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID()).fichaPerfilId(otraFicha).estudianteId(UUID.randomUUID()).build());

        // Act
        var resultado = repository.findEstudianteIdByFichaPerfilId(fichaId);

        // Assert
        assertThat(resultado).containsExactlyInAnyOrder(estudiante1, estudiante2);
    }
}
