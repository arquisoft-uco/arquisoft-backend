package com.arquisoft.fichas.domain.fichaperfil.rules.impl;

import com.arquisoft.fichas.domain.fichaperfil.exception.MismoAsesorFichaException;
import com.arquisoft.fichas.domain.fichaperfil.model.CambioAsesorFichaCriteria;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorFichaDiferenteRuleImplTest {

    private final AsesorFichaDiferenteRuleImpl regla = new AsesorFichaDiferenteRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoNuevoAsesorEsIgualAlActual() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        var criteria = new CambioAsesorFichaCriteria(asesorId, asesorId);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(criteria))
                .isInstanceOf(MismoAsesorFichaException.class)
                .hasMessageContaining(asesorId.toString());
    }

    @Test
    void debePasar_cuandoNuevoAsesorEsDiferenteAlActual() {
        // Arrange
        var criteria = new CambioAsesorFichaCriteria(UUID.randomUUID(), UUID.randomUUID());

        // Act & Assert
        assertThatCode(() -> regla.validar(criteria)).doesNotThrowAnyException();
    }
}
