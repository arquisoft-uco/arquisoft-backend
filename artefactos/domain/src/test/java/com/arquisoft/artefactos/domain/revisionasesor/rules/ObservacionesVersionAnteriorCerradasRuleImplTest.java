package com.arquisoft.artefactos.domain.revisionasesor.rules;

import com.arquisoft.artefactos.domain.revisionasesor.exception.ObservacionesVersionAnteriorSinCerrarException;
import com.arquisoft.artefactos.domain.revisionasesor.model.ObservacionesVersionAnterior;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.ObservacionesVersionAnteriorCerradasRuleImpl;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObservacionesVersionAnteriorCerradasRuleImplTest {

    @Test
    void debePasar_cuandoNoHayObservacionesSinCerrar() {
        // Arrange
        var regla = new ObservacionesVersionAnteriorCerradasRuleImpl();
        var entrada = new ObservacionesVersionAnterior(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSinCerrar_cuandoExistenObservacionesSinCerrar() {
        // Arrange
        var regla = new ObservacionesVersionAnteriorCerradasRuleImpl();
        var revision = UtilUUID.generarNuevoUUID();
        var entrada = new ObservacionesVersionAnterior(revision, true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(ObservacionesVersionAnteriorSinCerrarException.class, ex -> {
                    assertThat(ex.getCodigoError())
                            .isEqualTo(ArtefactosCodes.RevisionAsesor.OBSERVACIONES_VERSION_ANTERIOR_SIN_CERRAR);
                    assertThat(ex.getError().getMessage()).isNotBlank().doesNotContain(revision.toString(), "??");
                    assertThat(ex.getMessage()).contains(revision.toString());
                });
    }
}
