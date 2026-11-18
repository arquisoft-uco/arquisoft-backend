package com.arquisoft.biblioteca.application.autordocumentacion.query.primaryport.model;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarAutoresDocumentacionQueryTest {

    @Test
    void debeCrear_cuandoLaDocumentacionEsValida() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarAutoresDocumentacionQuery.crear(documentacion);

        // Assert
        assertThat(query.documentacion()).isEqualTo(documentacion);
    }

    @Test
    void debeLanzar400ConFieldError_cuandoLaDocumentacionEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarAutoresDocumentacionQuery.crear(null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> {
                            assertThat(error.campo()).isEqualTo(BibliotecaFields.AutorDocumentacion.DOCUMENTACION);
                            assertThat(error.codigoError())
                                    .isEqualTo(BibliotecaCodes.AutorDocumentacion.DOCUMENTACION_ID_REQUERIDO);
                        }));
    }
}
