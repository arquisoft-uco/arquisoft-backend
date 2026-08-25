package com.arquisoft.seguridad.application.auth.command.primaryport.mapper;

import com.arquisoft.seguridad.application.auth.command.primaryport.model.TokenSesionCommand;
import com.arquisoft.shared.exception.DomainException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CerrarSesionMapperTest {

    @Test
    void debeConstruirLaSesion_cuandoElComandoEsValido() {
        // Arrange
        TokenSesionCommand command = new TokenSesionCommand("jti-123", 120L);

        // Act
        var sesion = CerrarSesionMapper.toDomain(command);

        // Assert
        assertThat(sesion.getIdentificadorToken()).isEqualTo("jti-123");
        assertThat(sesion.getTiempoVidaRestante()).isEqualTo(120L);
    }

    @Test
    void debeLanzarExcepcion_cuandoIdentificadorEsVacio() {
        // Arrange
        TokenSesionCommand command = new TokenSesionCommand("   ", 120L);

        // Act / Assert
        assertThatThrownBy(() -> CerrarSesionMapper.toDomain(command))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void debeConstruirLaSesionSinInvalidacion_cuandoElTokenYaExpiro() {
        // Arrange — TTL 0 es el valor que produce CerrarSesionRequestMapper para un token vencido
        TokenSesionCommand command = new TokenSesionCommand("jti-123", 0L);

        // Act
        var sesion = CerrarSesionMapper.toDomain(command);

        // Assert
        assertThat(sesion.requiereInvalidacion()).isFalse();
    }

    @Test
    void debeLanzarExcepcion_cuandoTiempoDeVidaEsNegativo() {
        // Arrange
        TokenSesionCommand command = new TokenSesionCommand("jti-123", -1L);

        // Act / Assert
        assertThatThrownBy(() -> CerrarSesionMapper.toDomain(command))
                .isInstanceOf(DomainException.class);
    }
}

