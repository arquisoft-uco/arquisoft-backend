package com.arquisoft.biblioteca.application.documentacion.query.primaryport.mapper;

import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.ConsultarEnlaceDocumentacionQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEnlaceDocumentacionMapperTest {

    @Test
    void debeMapearDocumentacionAlCriteria_cuandoHayQuery() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var query = ConsultarEnlaceDocumentacionQuery.crear(documentacion);

        // Act
        var criteria = ConsultarEnlaceDocumentacionMapper.toCriteria(query);

        // Assert
        assertThat(criteria.documentacion()).isEqualTo(documentacion);
    }
}
