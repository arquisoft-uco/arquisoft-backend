package com.arquisoft.artefactos.domain.revisionasesor;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EliminacionRevisionAsesorDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var eliminacion = EliminacionRevisionAsesorDomain.crear(revision, asesor);

        // Assert
        assertThat(eliminacion.getRevisionAsesor()).isEqualTo(revision);
        assertThat(eliminacion.getAsesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularFieldErrors_cuandoFaltanRevisionYAsesor() {
        // Act & Assert
        assertThatThrownBy(() -> EliminacionRevisionAsesorDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(2);
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.RevisionAsesor.ID);
                        assertThat(error.codigoError()).isEqualTo(ArtefactosCodes.RevisionAsesor.ID_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.RevisionAsesor.ASESOR);
                        assertThat(error.codigoError()).isEqualTo(ArtefactosCodes.RevisionAsesor.ASESOR_REQUERIDO);
                    });
                });
    }
}
