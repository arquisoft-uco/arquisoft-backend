package com.arquisoft.artefactos.domain.artefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TraspasoArtefactosDomainTest {

    @Test
    void debeCrear_cuandoProyectoYAsesorValidos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var traspaso = TraspasoArtefactosDomain.crear(proyecto, asesor);

        // Assert
        assertThat(traspaso.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(traspaso.getAsesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularErrores_cuandoProyectoYAsesorSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> TraspasoArtefactosDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting(error -> error.campo()).containsExactlyInAnyOrder(
                            ArtefactosFields.Artefacto.PROYECTO_GRADO,
                            ArtefactosFields.Artefacto.ASESOR);
                    assertThat(errores).extracting(error -> error.codigoError()).containsExactlyInAnyOrder(
                            ArtefactosCodes.Artefacto.PROYECTO_GRADO_REQUERIDO,
                            ArtefactosCodes.Artefacto.ASESOR_REQUERIDO);
                });
    }
}
