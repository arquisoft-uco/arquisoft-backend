package com.arquisoft.artefactos.infrastructure.estudiante.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstudianteCommandOutputAdapterTest {

    @Autowired
    private EstudianteCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private EstudianteCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstudianteCommandOutputAdapter(repository);
    }

    @Test
    void debeObtenerPorId_cuandoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(EstudianteJpaEntity.builder().id(id).nombre("Luis").email("luis@uco.edu.co").build());
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerPorId(id);

        // Assert
        assertThat(resultado).hasValueSatisfying(entity -> {
            assertThat(entity.id()).isEqualTo(id);
            assertThat(entity.nombre()).isEqualTo("Luis");
            assertThat(entity.email()).isEqualTo("luis@uco.edu.co");
        });
    }

    @Test
    void debeRetornarVacio_cuandoNoExiste() {
        // Act
        var resultado = adapter.obtenerPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }
}
