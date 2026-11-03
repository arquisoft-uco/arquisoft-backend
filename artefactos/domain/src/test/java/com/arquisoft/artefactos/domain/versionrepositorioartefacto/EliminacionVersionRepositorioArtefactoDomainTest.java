package com.arquisoft.artefactos.domain.versionrepositorioartefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EliminacionVersionRepositorioArtefactoDomainTest {

    @Test
    void debeCrear_cuandoVersionValida() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();

        // Act
        var eliminacion = EliminacionVersionRepositorioArtefactoDomain.crear(version);

        // Assert
        assertThat(eliminacion.getVersion()).isEqualTo(version);
    }

    @Test
    void debeAcumularError_cuandoVersionEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> EliminacionVersionRepositorioArtefactoDomain.crear(null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting(error -> error.campo())
                            .containsExactly(ArtefactosFields.VersionRepositorioArtefacto.ID);
                    assertThat(errores).extracting(error -> error.codigoError())
                            .containsExactly(ArtefactosCodes.VersionRepositorioArtefacto.ID_REQUERIDO);
                });
    }
}
