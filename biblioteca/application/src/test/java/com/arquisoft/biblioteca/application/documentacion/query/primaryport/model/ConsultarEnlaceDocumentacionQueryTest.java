package com.arquisoft.biblioteca.application.documentacion.query.primaryport.model;

import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEnlaceDocumentacionQueryTest {

    @Test
    void debeCrear_cuandoDocumentacionEsValida() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEnlaceDocumentacionQuery.crear(documentacion);

        // Assert
        assertThat(query.documentacion()).isEqualTo(documentacion);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoDocumentacionEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarEnlaceDocumentacionQuery.crear(null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .contains(BibliotecaCodes.Documentacion.DOCUMENTACION_ID_REQUERIDO));
    }
}
