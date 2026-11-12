package com.arquisoft.biblioteca.infrastructure.estadodocumentacion.query.secondaryadapter.repository;

import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoDocumentacionMigracionTest {

    private static final String MIGRACION = "db/migration/biblioteca/V20261112164955__crear_estado_documentacion.sql";
    private static final Pattern FILA_SEMBRADA = Pattern.compile("^\\s*\\('([A-Z_]+)',\\s*'([^']*)'", Pattern.MULTILINE);

    @Test
    void debeCoincidirConElEnum_cuandoSeComparanIdsYNombresSembrados() throws IOException {
        // Arrange
        String sql;
        try (var entrada = getClass().getClassLoader().getResourceAsStream(MIGRACION)) {
            assertThat(entrada).isNotNull();
            sql = new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }

        // Act
        var sembrados = FILA_SEMBRADA.matcher(sql).results()
                .map(fila -> fila.group(1) + "=" + fila.group(2))
                .toList();
        var delEnum = Arrays.stream(EstadoDocumentacion.values())
                .filter(estado -> estado != EstadoDocumentacion.VACIO)
                .map(estado -> estado.getId() + "=" + estado.getNombre())
                .toList();

        // Assert
        assertThat(sembrados).containsExactlyInAnyOrderElementsOf(delEnum);
    }
}
