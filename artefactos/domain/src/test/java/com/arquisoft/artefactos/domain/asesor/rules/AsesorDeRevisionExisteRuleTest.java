package com.arquisoft.artefactos.domain.asesor.rules;

import com.arquisoft.artefactos.domain.asesor.exception.AsesorDeRevisionNoEncontradoException;
import com.arquisoft.artefactos.domain.asesor.model.ExistenciaAsesorDeRevision;
import com.arquisoft.artefactos.domain.asesor.rules.impl.AsesorDeRevisionExisteRuleImpl;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorDeRevisionExisteRuleTest {

    @Test
    void debeLanzar_cuandoElAsesorNoExiste() {
        // Arrange
        var regla = new AsesorDeRevisionExisteRuleImpl();
        var entrada = new ExistenciaAsesorDeRevision(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(AsesorDeRevisionNoEncontradoException.class,
                        ex -> assertThat(ex.getCodigoError())
                                .isEqualTo(ArtefactosCodes.Asesor.DE_REVISION_NO_ENCONTRADO));
    }

    @Test
    void noDebeLanzar_cuandoElAsesorExiste() {
        // Arrange
        var regla = new AsesorDeRevisionExisteRuleImpl();
        var entrada = new ExistenciaAsesorDeRevision(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }
}
