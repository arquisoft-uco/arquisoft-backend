package com.arquisoft.biblioteca.domain.autordocumentacion;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AutorDocumentacionDomainTest {

    @Test
    void debeCrearConIdGenerado_cuandoLosDatosSonValidos() {
        // Arrange
        var autor = UtilUUID.generarNuevoUUID();
        var documentacion = UtilUUID.generarNuevoUUID();

        // Act
        var vinculo = AutorDocumentacionDomain.crear(autor, documentacion);

        // Assert
        assertThat(vinculo.getId()).isNotNull();
        assertThat(vinculo.getAutor()).isEqualTo(autor);
        assertThat(vinculo.getDocumentacion()).isEqualTo(documentacion);
    }

    @Test
    void debeAcumularErrores_cuandoFaltanAutorYDocumentacion() {
        // Act & Assert
        assertThatThrownBy(() -> AutorDocumentacionDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                BibliotecaCodes.AutorDocumentacion.AUTOR_REQUERIDO,
                                BibliotecaCodes.AutorDocumentacion.DOCUMENTACION_ID_REQUERIDO));
    }
}
