package com.arquisoft.fichas.domain.revisionitem.rules.impl;

import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemNoEncontradaException;
import com.arquisoft.fichas.domain.revisionitem.model.ExistenciaRevisionItem;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionItemExisteRuleImplTest {

    private final RevisionItemExisteRuleImpl regla = new RevisionItemExisteRuleImpl();

    @Test
    void debePasar_cuandoLaRevisionExiste() {
        // Arrange
        var existencia = new ExistenciaRevisionItem(UUID.randomUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoLaRevisionNoExiste() {
        // Arrange
        UUID item = UUID.randomUUID();
        var existencia = new ExistenciaRevisionItem(item, false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOf(RevisionItemNoEncontradaException.class)
                .hasMessageContaining(item.toString());
    }
}
