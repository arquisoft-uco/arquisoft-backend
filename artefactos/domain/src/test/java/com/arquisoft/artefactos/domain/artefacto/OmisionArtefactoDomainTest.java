package com.arquisoft.artefactos.domain.artefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OmisionArtefactoDomainTest {

    @Test
    void debeCrear_cuandoArtefactoYAsesorSonValidos() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var omision = OmisionArtefactoDomain.crear(artefacto, asesor);

        // Assert
        assertThat(omision.getArtefacto()).isEqualTo(artefacto);
        assertThat(omision.getAsesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularErrores_cuandoArtefactoYAsesorSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> OmisionArtefactoDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting(error -> error.campo()).containsExactlyInAnyOrder(
                            ArtefactosFields.Artefacto.ID, ArtefactosFields.Artefacto.ASESOR);
                    assertThat(errores).extracting(error -> error.codigoError()).containsExactlyInAnyOrder(
                            ArtefactosCodes.Artefacto.ID_REQUERIDO, ArtefactosCodes.Artefacto.ASESOR_REQUERIDO);
                });
    }
}
