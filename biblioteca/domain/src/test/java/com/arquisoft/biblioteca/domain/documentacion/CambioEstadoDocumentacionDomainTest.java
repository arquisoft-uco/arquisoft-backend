package com.arquisoft.biblioteca.domain.documentacion;

import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambioEstadoDocumentacionDomainTest {

    @Test
    void debeCrearConEstadoRecortado_cuandoLosDatosSonValidos() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();

        // Act
        var cambio = CambioEstadoDocumentacionDomain.crear(documentacion, " NO_PUBLICADO ");

        // Assert
        assertThat(cambio.getDocumentacion()).isEqualTo(documentacion);
        assertThat(cambio.getEstado()).isEqualTo(EstadoDocumentacion.NO_PUBLICADO);
    }

    @Test
    void debeAcumularErrores_cuandoDocumentacionNulaYEstadoFueraDelCatalogo() {
        // Act & Assert
        assertThatThrownBy(() -> CambioEstadoDocumentacionDomain.crear(null, "FOO"))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(2);
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(BibliotecaFields.Documentacion.DOCUMENTACION);
                        assertThat(error.codigoError())
                                .isEqualTo(BibliotecaCodes.Documentacion.DOCUMENTACION_ID_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(BibliotecaFields.Documentacion.ESTADO);
                        assertThat(error.codigoError()).isEqualTo(BibliotecaCodes.Documentacion.ESTADO_INVALIDO);
                    });
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"FOO", "VACIO", "publicado"})
    void debeRechazarComoInvalido_cuandoElEstadoNoEstaEnElCatalogo(String estado) {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> CambioEstadoDocumentacionDomain.crear(documentacion, estado))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(BibliotecaCodes.Documentacion.ESTADO_INVALIDO)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void debeRechazarComoRequerido_cuandoElEstadoEstaEnBlanco(String estado) {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> CambioEstadoDocumentacionDomain.crear(documentacion, estado))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(BibliotecaCodes.Documentacion.ESTADO_REQUERIDO)));
    }
}
