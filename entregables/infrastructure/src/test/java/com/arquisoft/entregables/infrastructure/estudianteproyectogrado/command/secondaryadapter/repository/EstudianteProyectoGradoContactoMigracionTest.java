package com.arquisoft.entregables.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteProyectoGradoContactoMigracionTest {

    private static final String MIGRACION =
            "db/migration/entregables/V20261119223558__agregar_contactos_estudiante_proyecto_grado.sql";

    @Test
    void debeAgregarNombreYEmailNulablesConSuAncho_cuandoSeLeeLaMigracion() throws IOException {
        // Arrange
        String sql;
        try (var entrada = getClass().getClassLoader().getResourceAsStream(MIGRACION)) {
            assertThat(entrada).isNotNull();
            sql = new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }

        // Act & Assert
        assertThat(sql).contains("ALTER TABLE estudiante_proyecto_grado");
        assertThat(sql).contains("ADD COLUMN nombre VARCHAR(50) NULL", "ADD COLUMN email VARCHAR(50) NULL");
        assertThat(sql).doesNotContainIgnoringCase("DEFAULT")
                .doesNotContain("INSERT")
                .doesNotContain("UPDATE");
    }
}
