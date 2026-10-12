package com.arquisoft.fichas.domain.revisionitem.rules.impl;

import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemCerradaException;
import com.arquisoft.fichas.domain.revisionitem.model.EstadoRevisionItem;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionItemNoCerradaRuleImplTest {

    private final RevisionItemNoCerradaRuleImpl regla = new RevisionItemNoCerradaRuleImpl();

    @Test
    void debePasar_cuandoLaRevisionNoEstaCerrada() {
        // Arrange
        var revisionItem = UUID.randomUUID();

        // Act & Assert — cualquier estado distinto de CERRADA deja pasar
        assertThatCode(() -> regla.validar(new EstadoRevisionItem(revisionItem, "NUEVA")))
                .doesNotThrowAnyException();
        assertThatCode(() -> regla.validar(new EstadoRevisionItem(revisionItem, "EN_PROGRESO")))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoLaRevisionEstaCerrada() {
        // Arrange
        var estado = new EstadoRevisionItem(UUID.randomUUID(), "CERRADA");

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(estado))
                .isInstanceOf(RevisionItemCerradaException.class);
    }
}
