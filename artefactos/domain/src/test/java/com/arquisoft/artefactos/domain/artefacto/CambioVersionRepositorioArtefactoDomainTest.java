package com.arquisoft.artefactos.domain.artefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambioVersionRepositorioArtefactoDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();

        // Act
        var cambio = CambioVersionRepositorioArtefactoDomain.crear(artefacto, asesor, version);

        // Assert
        assertThat(cambio.getArtefacto()).isEqualTo(artefacto);
        assertThat(cambio.getAsesor()).isEqualTo(asesor);
        assertThat(cambio.getVersionRepositorioArtefacto()).isEqualTo(version);
    }

    @Test
    void debeAcumularErrores_cuandoFaltanArtefactoAsesorYVersion() {
        // Act & Assert
        assertThatThrownBy(() -> CambioVersionRepositorioArtefactoDomain.crear(null, null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting(error -> error.campo()).containsExactlyInAnyOrder(
                            ArtefactosFields.Artefacto.ID,
                            ArtefactosFields.Artefacto.ASESOR,
                            ArtefactosFields.Artefacto.VERSION_REPOSITORIO_ARTEFACTO);
                    assertThat(errores).extracting(error -> error.codigoError()).containsExactlyInAnyOrder(
                            ArtefactosCodes.Artefacto.ID_REQUERIDO,
                            ArtefactosCodes.Artefacto.ASESOR_REQUERIDO,
                            ArtefactosCodes.Artefacto.VERSION_REPOSITORIO_ARTEFACTO_REQUERIDA);
                });
    }
}
