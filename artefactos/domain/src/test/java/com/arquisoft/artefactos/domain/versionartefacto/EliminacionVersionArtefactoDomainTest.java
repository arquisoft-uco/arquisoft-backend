package com.arquisoft.artefactos.domain.versionartefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EliminacionVersionArtefactoDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();

        // Act
        var eliminacion = EliminacionVersionArtefactoDomain.crear(version, solicitante);

        // Assert
        assertThat(eliminacion.getVersionArtefacto()).isEqualTo(version);
        assertThat(eliminacion.getSolicitante()).isEqualTo(solicitante);
    }

    @Test
    void debeAcumularErrores_cuandoVersionYSolicitanteSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> EliminacionVersionArtefactoDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("codigoError")
                            .containsExactlyInAnyOrder(ArtefactosCodes.VersionArtefacto.VERSION_ARTEFACTO_REQUERIDA,
                                    ArtefactosCodes.VersionArtefacto.SOLICITANTE_REQUERIDO);
                });
    }
}
