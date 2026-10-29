package com.arquisoft.artefactos.domain.revisionasesor;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistroRevisionAsesorDomainTest {

    @Test
    void debeCrear_cuandoRevisionYAsesorPresentes() {
        // Arrange
        var revision = RevisionAsesorDomain.crear(UtilUUID.generarNuevoUUID());
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var registro = RegistroRevisionAsesorDomain.crear(revision, asesor);

        // Assert
        assertThat(registro.getRevision()).isSameAs(revision);
        assertThat(registro.getAsesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularErrores_cuandoRevisionYAsesorNulos() {
        // Act & Assert
        assertThatThrownBy(() -> RegistroRevisionAsesorDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("codigoError")
                            .contains(ArtefactosCodes.RevisionAsesor.ID_REQUERIDO,
                                    ArtefactosCodes.RevisionAsesor.ASESOR_REQUERIDO);
                });
    }
}
