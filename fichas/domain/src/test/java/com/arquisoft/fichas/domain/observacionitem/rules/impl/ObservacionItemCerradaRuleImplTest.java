package com.arquisoft.fichas.domain.observacionitem.rules.impl;

import com.arquisoft.fichas.domain.estadoobservacionrevision.EstadoObservacionRevision;
import com.arquisoft.fichas.domain.observacionitem.exception.ObservacionItemNoCerradaException;
import com.arquisoft.fichas.domain.observacionitem.model.EstadoObservacionItem;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObservacionItemCerradaRuleImplTest {

    private final ObservacionItemCerradaRuleImpl regla = new ObservacionItemCerradaRuleImpl();

    @Test
    void debePasar_cuandoLaObservacionEstaCerrada() {
        // Arrange
        var estado = new EstadoObservacionItem(UtilUUID.generarNuevoUUID(), EstadoObservacionRevision.CERRADO.getId());

        // Act & Assert
        assertThatCode(() -> regla.validar(estado)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoCerrada_cuandoLaObservacionEstaPendiente() {
        // Arrange
        var observacionItem = UtilUUID.generarNuevoUUID();
        var estado = new EstadoObservacionItem(observacionItem, EstadoObservacionRevision.PENDIENTE.getId());

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(estado))
                .isInstanceOf(ObservacionItemNoCerradaException.class)
                .hasFieldOrPropertyWithValue("codigoError", FichasCodes.ObservacionItem.NO_CERRADA)
                .satisfies(ex -> assertThat(ex.getMessage())
                        .contains(observacionItem.toString(), EstadoObservacionRevision.PENDIENTE.getId()));
    }

    @Test
    void debeLanzarNoCerrada_cuandoLaObservacionEstaEnProgreso() {
        // Arrange
        var estado = new EstadoObservacionItem(UtilUUID.generarNuevoUUID(), EstadoObservacionRevision.EN_PROGRESO.getId());

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(estado))
                .isInstanceOf(ObservacionItemNoCerradaException.class)
                .hasFieldOrPropertyWithValue("codigoError", FichasCodes.ObservacionItem.NO_CERRADA);
    }
}
