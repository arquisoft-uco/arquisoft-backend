package com.arquisoft.usuarios.domain.representantecomitecurriculum.rules.impl;

import com.arquisoft.usuarios.domain.representantecomitecurriculum.exception.UsuarioYaEsRepresentanteException;
import com.arquisoft.usuarios.domain.representantecomitecurriculum.model.DisponibilidadRepresentante;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RepresentanteComiteUnicoRuleImplTest {

    private final RepresentanteComiteUnicoRuleImpl rule = new RepresentanteComiteUnicoRuleImpl();

    @Test
    void debePasar_cuandoUsuarioNoEsRepresentante() {
        // Arrange
        UUID usuario = UUID.randomUUID();
        var entrada = new DisponibilidadRepresentante(usuario, false);

        // Act / Assert
        assertThatCode(() -> rule.validar(entrada))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarUsuarioYaEsRepresentante_cuandoYaEsRepresentante() {
        // Arrange
        UUID usuario = UUID.randomUUID();
        var entrada = new DisponibilidadRepresentante(usuario, true);

        // Act / Assert
        assertThatThrownBy(() -> rule.validar(entrada))
                .isInstanceOf(UsuarioYaEsRepresentanteException.class);
    }
}
