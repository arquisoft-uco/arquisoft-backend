package com.arquisoft.biblioteca.domain.documentacion;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistroDocumentacionDomainTest {

    @Test
    void debeCrearConLosIds_cuandoSonValidos() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var bibliotecario = UtilUUID.generarNuevoUUID();

        // Act
        var registro = RegistroDocumentacionDomain.crear(entregable, bibliotecario);

        // Assert
        assertThat(registro.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(registro.getBibliotecario()).isEqualTo(bibliotecario);
    }

    @Test
    void debeAcumularErrores_cuandoFaltanLosDosIds() {
        // Act & Assert
        assertThatThrownBy(() -> RegistroDocumentacionDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                BibliotecaCodes.Documentacion.ENTREGABLE_REQUERIDO,
                                BibliotecaCodes.Documentacion.BIBLIOTECARIO_REQUERIDO));
    }
}
