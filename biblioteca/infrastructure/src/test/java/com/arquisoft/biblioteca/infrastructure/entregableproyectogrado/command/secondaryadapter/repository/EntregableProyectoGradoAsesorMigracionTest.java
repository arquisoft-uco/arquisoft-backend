package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.command.secondaryadapter.repository;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class EntregableProyectoGradoAsesorMigracionTest {

    private static final String MIGRACION =
            "db/migration/biblioteca/V20261119223828__eliminar_asesor_id_entregable_proyecto_grado.sql";

    @Test
    void debeEliminarSoloAsesorId_cuandoSeLeeLaMigracion() throws IOException {
        // Arrange
        String sql;
        try (var entrada = getClass().getClassLoader().getResourceAsStream(MIGRACION)) {
            assertThat(entrada).isNotNull();
            sql = new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }

        // Act
        var sentencias = sql.lines().filter(linea -> !linea.startsWith("--")).collect(Collectors.joining());

        // Assert
        assertThat(sentencias.strip()).isEqualTo("ALTER TABLE entregable_proyecto_grado DROP COLUMN asesor_id;");
    }
}
