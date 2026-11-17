package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class EntregableProyectoGradoUbicacionMigracionTest {

    private static final String MIGRACION =
            "db/migration/biblioteca/V20261114213055__agregar_ubicacion_archivo_entregable_proyecto_grado.sql";

    @Test
    void debeAgregarTresColumnasNulasConSusAnchos_cuandoSeLeeLaMigracion() throws IOException {
        // Arrange
        String sql;
        try (var entrada = getClass().getClassLoader().getResourceAsStream(MIGRACION)) {
            assertThat(entrada).isNotNull();
            sql = new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }

        // Act & Assert
        assertThat(sql).contains("ALTER TABLE entregable_proyecto_grado");
        assertThat(sql).contains("ADD COLUMN bucket VARCHAR(63) NULL",
                "ADD COLUMN clave_objeto VARCHAR(2000) NULL",
                "ADD COLUMN nombre_archivo VARCHAR(255) NULL");
        assertThat(sql).doesNotContainIgnoringCase("DEFAULT")
                .doesNotContain("INSERT")
                .doesNotContain("UPDATE");
    }
}
