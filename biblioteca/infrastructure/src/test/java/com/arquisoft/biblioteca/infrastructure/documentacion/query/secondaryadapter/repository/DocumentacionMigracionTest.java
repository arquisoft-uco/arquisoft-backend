package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentacionMigracionTest {

    private static final String MIGRACION = "db/migration/biblioteca/V20261112184346__crear_documentacion.sql";

    @Test
    void debeCrearLasCuatroTablasConElDdlDelMer_cuandoSeLeeLaMigracion() throws IOException {
        // Arrange
        String sql;
        try (var entrada = getClass().getClassLoader().getResourceAsStream(MIGRACION)) {
            assertThat(entrada).isNotNull();
            sql = new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }

        // Act & Assert
        assertThat(sql).contains("CREATE TABLE entregable_proyecto_grado", "CREATE TABLE autor",
                "CREATE TABLE documentacion", "CREATE TABLE autor_documentacion");
        assertThat(sql).contains("uk_epg_proyecto_version", "uk_doc_entregable", "uk_doc_url",
                "uk_ad_autor_documentacion");
        assertThat(sql).contains("idx_epg_proyecto", "idx_doc_bibliotecario", "idx_doc_estado",
                "idx_ad_documentacion", "idx_ad_autor");
        assertThat(sql).contains("fk_doc_entregable", "fk_doc_bibliotecario", "fk_doc_estado", "fk_ad_autor",
                "fk_ad_documentacion");
        assertThat(sql).doesNotContain("ALTER TABLE");
    }
}
