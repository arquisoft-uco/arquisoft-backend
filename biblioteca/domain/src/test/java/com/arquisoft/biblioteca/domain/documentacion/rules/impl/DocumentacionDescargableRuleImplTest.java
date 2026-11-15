package com.arquisoft.biblioteca.domain.documentacion.rules.impl;

import com.arquisoft.biblioteca.domain.documentacion.exception.DocumentacionNoDescargableException;
import com.arquisoft.biblioteca.domain.documentacion.model.AccesoDescargaDocumentacion;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentacionDescargableRuleImplTest {

    private final DocumentacionDescargableRuleImpl rule = new DocumentacionDescargableRuleImpl();

    @ParameterizedTest
    @CsvSource({
            "PUBLICADO, false",
            "PUBLICADO, true",
            "PENDIENTE, true",
            "NO_PUBLICADO, true"
    })
    void debeNoLanzar_cuandoEstaPublicadaOElLlamanteEsBibliotecario(EstadoDocumentacion estado,
                                                                    boolean comoBibliotecario) {
        // Arrange
        var acceso = new AccesoDescargaDocumentacion(UtilUUID.generarNuevoUUID(), estado, comoBibliotecario);

        // Act & Assert
        assertThatCode(() -> rule.validar(acceso)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @CsvSource({
            "PENDIENTE, false",
            "NO_PUBLICADO, false",
            "VACIO, true",
            "VACIO, false"
    })
    void debeLanzarNoDescargable_cuandoNoExisteONoEstaPermitida(EstadoDocumentacion estado,
                                                                boolean comoBibliotecario) {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var acceso = new AccesoDescargaDocumentacion(documentacion, estado, comoBibliotecario);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(acceso))
                .isInstanceOfSatisfying(DocumentacionNoDescargableException.class, ex -> {
                    assertThat(ex.getCodigoError())
                            .isEqualTo(BibliotecaCodes.Documentacion.NO_DESCARGABLE);
                    assertThat(ex.getError().getMessage())
                            .doesNotContain(documentacion.toString());
                    assertThat(ex.getMessage())
                            .contains(documentacion.toString());
                });
    }
}
