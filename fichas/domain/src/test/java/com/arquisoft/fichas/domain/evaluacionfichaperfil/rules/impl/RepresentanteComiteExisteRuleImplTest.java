package com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.evaluacionfichaperfil.exception.RepresentanteComiteNoEncontradoException;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ExistenciaRepresentanteComite;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RepresentanteComiteExisteRuleImplTest {

    private final RepresentanteComiteExisteRuleImpl regla = new RepresentanteComiteExisteRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoElRepresentanteNoExiste() {
        // Arrange
        var existencia = new ExistenciaRepresentanteComite(UUID.randomUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOf(RepresentanteComiteNoEncontradoException.class);
    }

    @Test
    void debePasar_cuandoElRepresentanteExiste() {
        // Arrange
        var existencia = new ExistenciaRepresentanteComite(UUID.randomUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }
}
