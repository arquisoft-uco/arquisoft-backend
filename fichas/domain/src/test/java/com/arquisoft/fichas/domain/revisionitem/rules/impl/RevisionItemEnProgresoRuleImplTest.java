package com.arquisoft.fichas.domain.revisionitem.rules.impl;

import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemNoEnProgresoException;
import com.arquisoft.fichas.domain.revisionitem.model.EstadoRevisionItem;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionItemEnProgresoRuleImplTest {

    private final RevisionItemEnProgresoRuleImpl regla = new RevisionItemEnProgresoRuleImpl();

    @Test
    void debePasar_cuandoElEstadoEsEnProgreso() {
        // Arrange
        var estado = new EstadoRevisionItem(UtilUUID.generarNuevoUUID(), EstadoRevision.EN_PROGRESO.getId());

        // Act & Assert
        assertThatCode(() -> regla.validar(estado)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoElEstadoEsNuevaVisualizadaOCorreccionDisponible() {
        // Arrange
        var revisionItem = UtilUUID.generarNuevoUUID();
        var estados = new EstadoRevision[] {
            EstadoRevision.NUEVA, EstadoRevision.VISUALIZADA, EstadoRevision.CORRECCION_DISPONIBLE
        };

        // Act & Assert
        for (var estado : estados) {
            assertThatThrownBy(() -> regla.validar(new EstadoRevisionItem(revisionItem, estado.getId())))
                    .isInstanceOfSatisfying(RevisionItemNoEnProgresoException.class, ex -> {
                        assertThat(ex.getCodigoError()).isEqualTo(FichasCodes.RevisionItem.NO_EN_PROGRESO);
                        assertThat(ex.getError().getMessage()).doesNotContain(revisionItem.toString());
                    });
        }
    }
}
