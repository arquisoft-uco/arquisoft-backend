package com.arquisoft.fichas.domain.revisionitem.rules.impl;

import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemSinObservacionesException;
import com.arquisoft.fichas.domain.revisionitem.model.ObservacionesRevisionItem;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionItemConObservacionesRuleImplTest {

    private final RevisionItemConObservacionesRuleImpl regla = new RevisionItemConObservacionesRuleImpl();

    @Test
    void debePasar_cuandoHayObservaciones() {
        // Arrange
        var observaciones = new ObservacionesRevisionItem(UtilUUID.generarNuevoUUID(), 2, 0);

        // Act & Assert
        assertThatCode(() -> regla.validar(observaciones)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoNoHayObservaciones() {
        // Arrange
        var revisionItem = UtilUUID.generarNuevoUUID();
        var observaciones = new ObservacionesRevisionItem(revisionItem, 0, 0);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(observaciones))
                .isInstanceOfSatisfying(RevisionItemSinObservacionesException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(FichasCodes.RevisionItem.SIN_OBSERVACIONES);
                    assertThat(ex.getError().getMessage()).doesNotContain(revisionItem.toString());
                });
    }
}
