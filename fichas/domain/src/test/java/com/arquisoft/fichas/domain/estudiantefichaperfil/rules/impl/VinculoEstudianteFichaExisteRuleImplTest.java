package com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.EstudianteFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ExistenciaVinculoEstudianteFicha;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VinculoEstudianteFichaExisteRuleImplTest {

    private final VinculoEstudianteFichaExisteRuleImpl regla = new VinculoEstudianteFichaExisteRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoElVinculoNoExiste() {
        // Arrange
        var existencia = new ExistenciaVinculoEstudianteFicha(UUID.randomUUID(), UUID.randomUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOf(EstudianteFichaPerfilNoEncontradoException.class);
    }

    @Test
    void debePasar_cuandoElVinculoExiste() {
        // Arrange
        var existencia = new ExistenciaVinculoEstudianteFicha(UUID.randomUUID(), UUID.randomUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }
}
