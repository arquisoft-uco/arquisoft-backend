package com.arquisoft.fichas.domain.revisionitem.rules.impl;

import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemYaExisteException;
import com.arquisoft.fichas.domain.revisionitem.model.DisponibilidadRevisionItem;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionItemNoDuplicadaRuleImplTest {

    private final RevisionItemNoDuplicadaRuleImpl regla = new RevisionItemNoDuplicadaRuleImpl();

    @Test
    void debePasar_cuandoNoExisteUnaRevisionActiva() {
        // Arrange
        var disponibilidad = new DisponibilidadRevisionItem(UUID.randomUUID(), false);

        // Act & Assert
        assertThatCode(() -> regla.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoYaExisteUnaRevisionActiva() {
        // Arrange
        UUID item = UUID.randomUUID();
        var disponibilidad = new DisponibilidadRevisionItem(item, true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(disponibilidad))
                .isInstanceOf(RevisionItemYaExisteException.class)
                .hasMessageContaining(item.toString());
    }
}
