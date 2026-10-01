package com.arquisoft.seguridad.application.auth.command.primaryport.mapper;

import com.arquisoft.seguridad.application.auth.command.primaryport.model.RefrescarTokenCommand;
import com.arquisoft.shared.exception.DomainException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RefrescarTokenMapperTest {

    @Test
    void debeConstruirElToken_cuandoElComandoEsValido() {
        // Arrange
        var command = RefrescarTokenCommand.crear("token-refresco");

        // Act
        var token = RefrescarTokenMapper.toDomain(command);

        // Assert
        assertThat(token.getValor()).isEqualTo("token-refresco");
    }

    @Test
    void debeLanzarExcepcion_cuandoElValorEsVacio() {
        // Arrange — el record se construye sin pasar por crear(), como haria un mapper de web mal escrito
        var command = new RefrescarTokenCommand("   ");

        // Act / Assert
        assertThatThrownBy(() -> RefrescarTokenMapper.toDomain(command))
                .isInstanceOf(DomainException.class);
    }
}
