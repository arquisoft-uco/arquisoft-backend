package com.arquisoft.mapas_ruta.infrastructure.planreal.command.secondaryadapter;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class PlanRealMigracionTest {

    private static final String MIGRACION = "/db/migration/mapas_ruta/V20261103203057__crear_plan_real.sql";

    @Test
    void debeDeclararOnDeleteCascade_enFkPrTarea() throws IOException {
        // Arrange
        try (var flujo = getClass().getResourceAsStream(MIGRACION)) {
            assertThat(flujo).isNotNull();
            var sql = new String(flujo.readAllBytes(), StandardCharsets.UTF_8);

            // Act
            var restriccion = sql.lines()
                    .filter(linea -> linea.contains("fk_pr_tarea"))
                    .findFirst()
                    .orElse("");

            // Assert
            assertThat(restriccion).containsIgnoringCase("ON DELETE CASCADE");
        }
    }
}
