package com.arquisoft.solicitudes.domain.remitente;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RemitenteDomainTest {

    @Test
    void debeCrearElRemitente_cuandoElUsuarioEsValido() {
        // Arrange
        UUID usuario = UUID.randomUUID();

        // Act
        RemitenteDomain remitente = RemitenteDomain.crear(usuario);

        // Assert
        assertThat(remitente.getId()).isNotNull();
        assertThat(remitente.getUsuario()).isEqualTo(usuario);
    }

    @Test
    void debeAcumularError_cuandoElUsuarioEsNulo() {
        // Act
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> RemitenteDomain.crear(null));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Remitente.USUARIO)).isTrue();
    }
}
