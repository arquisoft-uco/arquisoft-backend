package com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.mapper;

import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignarEstadoInicialFichaPerfilMapperTest {

    @Test
    void debeMapearADomain_cuandoDatosValidos() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();

        // Act
        EstadoFichaPerfilDomain estadoInicial = AsignarEstadoInicialFichaPerfilMapper.toDomain(fichaPerfil);

        // Assert
        assertThat(estadoInicial.getId()).isNotNull();
        assertThat(estadoInicial.getFichaPerfil()).isEqualTo(fichaPerfil);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoFichaPerfilEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> AsignarEstadoInicialFichaPerfilMapper.toDomain(null))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.EstadoFichaPerfil.FICHA_PERFIL);
    }
}
