package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.exception.EstudianteNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.artefacto.model.PropiedadArtefacto;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudiantePropietarioArtefactoRuleImplTest {

    private final EstudiantePropietarioArtefactoRuleImpl regla = new EstudiantePropietarioArtefactoRuleImpl();

    @Test
    void debeLanzar_cuandoNoVinculado() {
        // Arrange
        var propiedad = new PropiedadArtefacto(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(propiedad))
                .isInstanceOf(EstudianteNoPropietarioArtefactoException.class)
                .extracting("codigoError")
                .isEqualTo(ArtefactosCodes.Artefacto.ESTUDIANTE_NO_PROPIETARIO);
    }

    @Test
    void debePasar_cuandoVinculado() {
        // Arrange
        var propiedad = new PropiedadArtefacto(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(propiedad)).doesNotThrowAnyException();
    }
}
