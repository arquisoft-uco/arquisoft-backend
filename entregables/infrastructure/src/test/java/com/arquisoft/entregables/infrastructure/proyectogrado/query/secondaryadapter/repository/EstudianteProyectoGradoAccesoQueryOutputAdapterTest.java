package com.arquisoft.entregables.infrastructure.proyectogrado.query.secondaryadapter.repository;

import com.arquisoft.entregables.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
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

    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-20T10:00:00Z");

    @Autowired
    private EstudianteProyectoGradoAccesoQueryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private EstudianteProyectoGradoAccesoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstudianteProyectoGradoAccesoQueryOutputAdapter(repository);
    }

    private void sembrar(UUID estudiante, UUID proyectoGrado, Instant eliminadoEn) {
        entityManager.persist(EstudianteProyectoGradoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).estudianteId(estudiante).proyectoGradoId(proyectoGrado)
                .ocurridoEn(OCURRIDO_EN).eliminadoEn(eliminadoEn).build());
        entityManager.flush();
    }

    @Test
    void debeRetornarTrue_cuandoElEstudianteTieneVinculoVigente() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        sembrar(estudiante, proyectoGrado, null);

        // Act
        var pertenece = adapter.perteneceAlProyecto(estudiante, proyectoGrado);

        // Assert
        assertThat(pertenece).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoNoHayVinculoOEsDeOtroProyecto() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        sembrar(estudiante, UtilUUID.generarNuevoUUID(), null);

        // Act
        var pertenece = adapter.perteneceAlProyecto(estudiante, proyectoGrado);
        var perteneceOtro = adapter.perteneceAlProyecto(UtilUUID.generarNuevoUUID(), proyectoGrado);

        // Assert
        assertThat(pertenece).isFalse();
        assertThat(perteneceOtro).isFalse();
    }

    @Test
    void debeRetornarFalse_cuandoElEstudianteFueDestituido() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        sembrar(estudiante, proyectoGrado, Instant.parse("2026-10-21T10:00:00Z"));

        // Act
        var pertenece = adapter.perteneceAlProyecto(estudiante, proyectoGrado);

        // Assert
        assertThat(pertenece).isFalse();
    }
}
