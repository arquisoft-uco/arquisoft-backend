package com.arquisoft.artefactos.domain.observacion.rules;

import com.arquisoft.artefactos.domain.observacion.exception.ObservacionDuplicadaException;
import com.arquisoft.artefactos.domain.observacion.model.UnicidadObservacionRevision;
import com.arquisoft.artefactos.domain.observacion.rules.impl.ObservacionDescripcionUnicaRuleImpl;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObservacionDescripcionUnicaRuleImplTest {

    @Test
    void debeLanzar_cuandoLaDescripcionYaExiste() {
        // Arrange
        var regla = new ObservacionDescripcionUnicaRuleImpl();
        var revision = UtilUUID.generarNuevoUUID();
        var entrada = new UnicidadObservacionRevision(revision, true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(ObservacionDuplicadaException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.Observacion.DUPLICADA);
                    assertThat(ex.getError().getMessage()).isNotBlank().doesNotContain(revision.toString(), "??");
                    assertThat(ex.getMessage()).contains(revision.toString());
                });
    }

    @Test
    void noDebeLanzar_cuandoLaDescripcionNoExiste() {
        // Arrange
        var regla = new ObservacionDescripcionUnicaRuleImpl();
        var entrada = new UnicidadObservacionRevision(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }
}
