package com.arquisoft.artefactos.infrastructure.estudiante.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

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
    void debeDevolverVigentesYDadosDeBajaSoloDeLosIdsPedidos_cuandoHayOtrosEstudiantes() {
        // Arrange
        var vigente = UtilUUID.generarNuevoUUID();
        var eliminado = UtilUUID.generarNuevoUUID();
        var ajeno = UtilUUID.generarNuevoUUID();
        var baja = Instant.parse("2026-01-01T00:00:00Z");
        sembrar(vigente, "Laura", null);
        sembrar(eliminado, "Pedro", baja);
        sembrar(ajeno, "Ajeno", null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerPorIds(List.of(vigente, eliminado));

        // Assert
        assertThat(resultado).hasSize(2);
        var deLaura = resultado.stream().filter(e -> e.id().equals(vigente)).findFirst().orElseThrow();
        var dePedro = resultado.stream().filter(e -> e.id().equals(eliminado)).findFirst().orElseThrow();
        assertThat(deLaura.nombre()).isEqualTo("Laura");
        assertThat(deLaura.email()).isEqualTo("Laura@uco.edu.co");
        assertThat(deLaura.eliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(dePedro.eliminadoEn()).isEqualTo(baja);
    }

    private void sembrar(UUID id, String nombre, Instant eliminadoEn) {
        entityManager.persist(EstudianteJpaEntity.builder()
                .id(id)
                .identificador("ID-" + nombre)
                .nombre(nombre)
                .email(nombre + "@uco.edu.co")
                .eliminadoEn(eliminadoEn)
                .build());
    }
}
