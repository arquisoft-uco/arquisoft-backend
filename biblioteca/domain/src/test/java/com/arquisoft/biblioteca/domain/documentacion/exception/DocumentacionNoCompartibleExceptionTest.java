package com.arquisoft.biblioteca.domain.documentacion.exception;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentacionNoCompartibleExceptionTest {

    @Test
    void debeUsarCodigoNoCompartibleYNoExponerUuidEnElMensaje_cuandoSeCrea() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();

        // Act
        var excepcion = new DocumentacionNoCompartibleException(documentacion);

        // Assert
        assertThat(excepcion.getCodigoError()).isEqualTo(BibliotecaCodes.Documentacion.NO_COMPARTIBLE);
        assertThat(excepcion.getError().getMessage()).doesNotContain(documentacion.toString());
        assertThat(excepcion.getMessage()).contains(documentacion.toString());
    }
}
