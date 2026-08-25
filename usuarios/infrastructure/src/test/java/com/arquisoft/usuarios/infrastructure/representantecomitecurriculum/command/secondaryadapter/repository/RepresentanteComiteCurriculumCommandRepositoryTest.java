package com.arquisoft.usuarios.infrastructure.representantecomitecurriculum.command.secondaryadapter.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RepresentanteComiteCurriculumCommandRepositoryTest {

    @Autowired
    private RepresentanteComiteCurriculumCommandRepository repository;

    @Test
    void debeRetornarFalse_cuandoNoExistsByUsuario() {
        // Arrange
        UUID usuarioInexistente = UUID.randomUUID();

        // Act
        Boolean existe = repository.existsByUsuario(usuarioInexistente);

        // Assert
        assertThat(existe).isFalse();
    }
}
