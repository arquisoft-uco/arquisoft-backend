package com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEnlaceDocumentacionRequestMapperTest {

    @Test
    void debeConstruirQuery_cuandoRecibeElIdDeLaDocumentacion() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEnlaceDocumentacionRequestMapper.toQuery(documentacion);

        // Assert
        assertThat(query.documentacion()).isEqualTo(documentacion);
    }
}
