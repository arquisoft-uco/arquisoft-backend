package com.arquisoft.biblioteca.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class ReplicaProyectoGradoMigracionTest {

    private static String leer(String ruta) throws IOException {
        try (var entrada = ReplicaProyectoGradoMigracionTest.class.getClassLoader().getResourceAsStream(ruta)) {
            assertThat(entrada).isNotNull();
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    void debeCrearLaTablaDeProyectoSinForaneas_cuandoSeLeeLaMigracion() throws IOException {
        // Arrange
        var sql = leer("db/migration/biblioteca/V20261115041457__crear_proyecto_grado.sql");

        // Act & Assert
        assertThat(sql).contains("CREATE TABLE proyecto_grado", "titulo VARCHAR(100) NOT NULL",
                "ocurrido_en TIMESTAMPTZ NOT NULL");
        assertThat(sql).doesNotContain("REFERENCES");
    }

    @Test
    void debeCrearLaTablaDeVinculosConUnicoEIndicesPropios_cuandoSeLeeLaMigracion() throws IOException {
        // Arrange
        var sql = leer("db/migration/biblioteca/V20261115041458__crear_estudiante_proyecto_grado.sql");

        // Act & Assert
        assertThat(sql).contains("CREATE TABLE estudiante_proyecto_grado",
                "eliminado_en TIMESTAMPTZ NULL",
                "UNIQUE (proyecto_grado_id, autor_id)",
                "CREATE INDEX idx_espg_autor",
                "WHERE eliminado_en IS NULL");
        assertThat(sql).doesNotContain("REFERENCES").doesNotContain("CREATE INDEX idx_epg_proyecto");
    }
}
