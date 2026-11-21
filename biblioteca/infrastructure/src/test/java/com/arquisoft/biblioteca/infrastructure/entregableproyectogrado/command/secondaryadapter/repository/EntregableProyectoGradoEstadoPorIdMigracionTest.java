package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.command.secondaryadapter.repository;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class EntregableProyectoGradoEstadoPorIdMigracionTest {

    private static final String MIGRACION =
            "db/migration/biblioteca/V20261120190716__refactor_ubicacion_estado_entregable_proyecto_grado.sql";

    @Test
    void debeEjecutarLasCincoSentenciasEnOrden_cuandoSeLeeLaMigracion() throws IOException {
        // Arrange
        String sql;
        try (var entrada = getClass().getClassLoader().getResourceAsStream(MIGRACION)) {
            assertThat(entrada).isNotNull();
            sql = new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }

        // Act
        var sentencias = sql.lines().filter(linea -> !linea.startsWith("--")).toList();

        // Assert
        assertThat(sentencias).containsExactly(
                "ALTER TABLE entregable_proyecto_grado DROP COLUMN bucket;",
                "ALTER TABLE entregable_proyecto_grado ADD COLUMN estado_entregable_id VARCHAR(60);",
                "UPDATE entregable_proyecto_grado SET estado_entregable_id = "
                        + "UPPER(REPLACE(TRIM(estado_entregable_nombre), ' ', '_'));",
                "ALTER TABLE entregable_proyecto_grado ALTER COLUMN estado_entregable_id SET NOT NULL;",
                "ALTER TABLE entregable_proyecto_grado DROP COLUMN estado_entregable_nombre;");
    }
}
