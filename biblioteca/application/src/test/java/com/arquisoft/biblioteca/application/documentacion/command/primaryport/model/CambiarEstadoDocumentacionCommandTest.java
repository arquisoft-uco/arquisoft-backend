package com.arquisoft.biblioteca.application.documentacion.command.primaryport.model;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarEstadoDocumentacionCommandTest {

    @Test
    void debeCrearConEstadoRecortado_cuandoLosDatosSonValidos() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();

        // Act
        var command = CambiarEstadoDocumentacionCommand.crear(documentacion, " PUBLICADO ");

        // Assert
        assertThat(command.documentacion()).isEqualTo(documentacion);
        assertThat(command.estado()).isEqualTo("PUBLICADO");
    }

    @Test
    void debeAcumularErrores_cuandoDocumentacionNulaYEstadoNulo() {
        // Act & Assert
        assertThatThrownBy(() -> CambiarEstadoDocumentacionCommand.crear(null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(2);
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(BibliotecaFields.Documentacion.DOCUMENTACION);
                        assertThat(error.codigoError())
                                .isEqualTo(BibliotecaCodes.Documentacion.DOCUMENTACION_ID_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(BibliotecaFields.Documentacion.ESTADO);
                        assertThat(error.codigoError()).isEqualTo(BibliotecaCodes.Documentacion.ESTADO_REQUERIDO);
                    });
                });
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void debeRechazarComoRequerido_cuandoElEstadoEstaAusenteOEnBlanco(String estado) {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> CambiarEstadoDocumentacionCommand.crear(documentacion, estado))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(BibliotecaCodes.Documentacion.ESTADO_REQUERIDO)));
    }
}
