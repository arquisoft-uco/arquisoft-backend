package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.proyectos.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstudianteProyectoGradoAccesoQueryOutputAdapterTest {

    @Autowired
    private EstudianteProyectoGradoQueryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private EstudianteProyectoGradoAccesoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstudianteProyectoGradoAccesoQueryOutputAdapter(repository);
    }

    private UUID vincular(UUID proyectoGrado, Instant eliminadoEn) {
        var estudiante = UtilUUID.generarNuevoUUID();
        entityManager.persist(new EstudianteJpaEntity(estudiante, "2020", "Ana", "ana@soyuco.edu.co",
                Instant.parse("2026-09-01T10:00:00Z"), eliminadoEn));
        entityManager.persist(new EstudianteProyectoGradoJpaEntity(UtilUUID.generarNuevoUUID(), estudiante,
                proyectoGrado));
        entityManager.flush();
        entityManager.clear();
        return estudiante;
    }

    @Test
    void debeRetornarTrue_cuandoEstudianteVinculadoYVigente() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = vincular(proyectoGrado, null);

        // Act
        var resultado = adapter.existeVinculoVigente(proyectoGrado, estudiante);

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoEstudianteDadoDeBaja() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = vincular(proyectoGrado, Instant.parse("2026-09-10T10:00:00Z"));

        // Act
        var resultado = adapter.existeVinculoVigente(proyectoGrado, estudiante);

        // Assert
        assertThat(resultado).isFalse();
    }

    @Test
    void debeRetornarFalse_cuandoEstudianteVinculadoAOtroProyecto() {
        // Arrange
        var estudiante = vincular(UtilUUID.generarNuevoUUID(), null);

        // Act
        var resultado = adapter.existeVinculoVigente(UtilUUID.generarNuevoUUID(), estudiante);

        // Assert
        assertThat(resultado).isFalse();
    }
}
