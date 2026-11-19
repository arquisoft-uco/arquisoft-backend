package com.arquisoft.fichas.domain.revisionitem.rules.impl;

import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemNoCorreccionDisponibleException;
import com.arquisoft.fichas.domain.revisionitem.model.EstadoRevisionItem;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionItemCorreccionDisponibleRuleImplTest {

    private final RevisionItemCorreccionDisponibleRuleImpl regla = new RevisionItemCorreccionDisponibleRuleImpl();

    @Test
    void debePasar_cuandoLaRevisionTieneCorreccionDisponible() {
        // Arrange
        var estado = new EstadoRevisionItem(UtilUUID.generarNuevoUUID(), EstadoRevision.CORRECCION_DISPONIBLE.getId());

        // Act & Assert
        assertThatCode(() -> regla.validar(estado)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoCorreccionDisponible_cuandoLaRevisionEstaEnProgreso() {
        // Arrange
        var estado = new EstadoRevisionItem(UtilUUID.generarNuevoUUID(), EstadoRevision.EN_PROGRESO.getId());

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(estado))
                .isInstanceOf(RevisionItemNoCorreccionDisponibleException.class)
                .hasFieldOrPropertyWithValue("codigoError", FichasCodes.RevisionItem.NO_CORRECCION_DISPONIBLE);
    }

    @Test
    void debeLanzarNoCorreccionDisponible_cuandoLaRevisionEstaNueva() {
        // Arrange
        var estado = new EstadoRevisionItem(UtilUUID.generarNuevoUUID(), EstadoRevision.NUEVA.getId());

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(estado))
                .isInstanceOf(RevisionItemNoCorreccionDisponibleException.class)
                .hasFieldOrPropertyWithValue("codigoError", FichasCodes.RevisionItem.NO_CORRECCION_DISPONIBLE);
    }
}
