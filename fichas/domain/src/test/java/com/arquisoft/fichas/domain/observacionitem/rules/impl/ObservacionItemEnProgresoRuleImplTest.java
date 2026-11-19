package com.arquisoft.fichas.domain.observacionitem.rules.impl;

import com.arquisoft.fichas.domain.estadoobservacionrevision.EstadoObservacionRevision;
import com.arquisoft.fichas.domain.observacionitem.exception.ObservacionItemNoEnProgresoException;
import com.arquisoft.fichas.domain.observacionitem.model.EstadoObservacionItem;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObservacionItemEnProgresoRuleImplTest {

    private final ObservacionItemEnProgresoRuleImpl regla = new ObservacionItemEnProgresoRuleImpl();

    @Test
    void debePasar_cuandoLaObservacionEstaEnProgreso() {
        // Arrange
        var estado = new EstadoObservacionItem(UtilUUID.generarNuevoUUID(), EstadoObservacionRevision.EN_PROGRESO.getId());

        // Act & Assert
        assertThatCode(() -> regla.validar(estado)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEnProgreso_cuandoLaObservacionEstaPendiente() {
        // Arrange
        var estado = new EstadoObservacionItem(UtilUUID.generarNuevoUUID(), EstadoObservacionRevision.PENDIENTE.getId());

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(estado))
                .isInstanceOf(ObservacionItemNoEnProgresoException.class)
                .hasFieldOrPropertyWithValue("codigoError", FichasCodes.ObservacionItem.NO_EN_PROGRESO);
    }

    @Test
    void debeLanzarNoEnProgreso_cuandoLaObservacionYaEstaCerrada() {
        // Arrange
        var estado = new EstadoObservacionItem(UtilUUID.generarNuevoUUID(), EstadoObservacionRevision.CERRADO.getId());

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(estado))
                .isInstanceOf(ObservacionItemNoEnProgresoException.class)
                .hasFieldOrPropertyWithValue("codigoError", FichasCodes.ObservacionItem.NO_EN_PROGRESO);
    }
}
