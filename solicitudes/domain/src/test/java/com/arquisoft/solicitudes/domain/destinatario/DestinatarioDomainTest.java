package com.arquisoft.solicitudes.domain.destinatario;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DestinatarioDomainTest {

    @Test
    void debeCrearElDestinatario_cuandoElUsuarioEsValido() {
        // Arrange
        UUID usuario = UUID.randomUUID();

        // Act
        DestinatarioDomain destinatario = DestinatarioDomain.crear(usuario);

        // Assert
        assertThat(destinatario.getId()).isNotNull();
        assertThat(destinatario.getUsuario()).isEqualTo(usuario);
    }

    @Test
    void debeAcumularError_cuandoElUsuarioEsNulo() {
        // Act
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> DestinatarioDomain.crear(null));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Destinatario.USUARIO)).isTrue();
    }
}
