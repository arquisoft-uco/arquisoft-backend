package com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstudianteCommandRepositoryTest {

    @Autowired
    private EstudianteCommandRepository repository;

    @Test
    void debeRetornarTrue_cuandoEstudianteExiste() {
        // Arrange
        EstudianteEntity estudiante = EstudianteEntity.builder()
                .id(UUID.randomUUID())
                .identificador("1234567890")
                .nombre("Estudiante de prueba")
                .email("estudiante@example.com")
                .build();
        repository.save(estudiante);

        // Act
        boolean resultado = repository.existsById(estudiante.getId());

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoEstudianteNoExiste() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        boolean resultado = repository.existsById(id);

        // Assert
        assertThat(resultado).isFalse();
    }
}
