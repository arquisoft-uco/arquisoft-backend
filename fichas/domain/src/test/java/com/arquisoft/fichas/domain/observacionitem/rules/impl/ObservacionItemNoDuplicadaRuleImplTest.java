package com.arquisoft.fichas.domain.observacionitem.rules.impl;

import com.arquisoft.fichas.domain.observacionitem.exception.ObservacionItemDuplicadaException;
import com.arquisoft.fichas.domain.observacionitem.model.DisponibilidadObservacionItem;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObservacionItemNoDuplicadaRuleImplTest {

    private final ObservacionItemNoDuplicadaRuleImpl regla = new ObservacionItemNoDuplicadaRuleImpl();

    @Test
    void debePasar_cuandoNoHayCoincidencias() {
        // Arrange
        var disponibilidad = new DisponibilidadObservacionItem(UUID.randomUUID(), "Observación", 0L);

        // Act & Assert
        assertThatCode(() -> regla.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoYaExisteElMismoTexto() {
        // Arrange
        var disponibilidad = new DisponibilidadObservacionItem(UUID.randomUUID(), "Observación", 1L);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(disponibilidad))
                .isInstanceOf(ObservacionItemDuplicadaException.class);
    }
}
