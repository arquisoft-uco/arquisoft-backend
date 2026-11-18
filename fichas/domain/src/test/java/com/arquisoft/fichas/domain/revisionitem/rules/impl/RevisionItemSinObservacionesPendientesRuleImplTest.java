package com.arquisoft.fichas.domain.revisionitem.rules.impl;

import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemConObservacionesPendientesException;
import com.arquisoft.fichas.domain.revisionitem.model.ObservacionesRevisionItem;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionItemSinObservacionesPendientesRuleImplTest {

    private final RevisionItemSinObservacionesPendientesRuleImpl regla =
            new RevisionItemSinObservacionesPendientesRuleImpl();

    @Test
    void debePasar_cuandoNoHayObservacionesPendientes() {
        // Arrange
        var observaciones = new ObservacionesRevisionItem(UtilUUID.generarNuevoUUID(), 3, 0);

        // Act & Assert
        assertThatCode(() -> regla.validar(observaciones)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoHayObservacionesPendientes() {
        // Arrange
        var revisionItem = UtilUUID.generarNuevoUUID();
        var observaciones = new ObservacionesRevisionItem(revisionItem, 3, 2);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(observaciones))
                .isInstanceOfSatisfying(RevisionItemConObservacionesPendientesException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(FichasCodes.RevisionItem.CON_OBSERVACIONES_PENDIENTES);
                    assertThat(ex.getError().getMessage()).doesNotContain(revisionItem.toString());
                });
    }
}
