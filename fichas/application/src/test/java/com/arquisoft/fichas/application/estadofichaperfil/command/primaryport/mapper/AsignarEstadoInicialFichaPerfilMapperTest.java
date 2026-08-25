package com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.mapper;

import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> AsignarEstadoInicialFichaPerfilMapper.toDomain(null));

        assertThat(excepcion.getValidationResult().tieneErroresDeCampo(FichasFields.EstadoFichaPerfil.FICHA_PERFIL)).isTrue();
    }
}
