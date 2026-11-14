package com.arquisoft.biblioteca.infrastructure.autordocumentacion.query.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarAutoresDocumentacionRequestMapperTest {

    @Test
    void debeConstruirElQueryConLaDocumentacion_cuandoElIdEsValido() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarAutoresDocumentacionRequestMapper.toQuery(documentacion);

        // Assert
        assertThat(query.documentacion()).isEqualTo(documentacion);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoElIdEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarAutoresDocumentacionRequestMapper.toQuery(null))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
