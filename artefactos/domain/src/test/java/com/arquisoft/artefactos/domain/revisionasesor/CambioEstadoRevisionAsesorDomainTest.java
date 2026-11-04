package com.arquisoft.artefactos.domain.revisionasesor;

import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambioEstadoRevisionAsesorDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var cambio = CambioEstadoRevisionAsesorDomain.crear(revision, "  RESUELTA ", estudiante);

        // Assert
        assertThat(cambio.getRevisionAsesor()).isEqualTo(revision);
        assertThat(cambio.getEstado()).isEqualTo(EstadoRevisionAsesor.RESUELTA);
        assertThat(cambio.getEstudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeAcumularFieldErrors_cuandoTodoInvalido() {
        // Act & Assert
        assertThatThrownBy(() -> CambioEstadoRevisionAsesorDomain.crear(null, "  ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(3);
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.RevisionAsesor.ID);
                        assertThat(error.codigoError()).isEqualTo(ArtefactosCodes.RevisionAsesor.ID_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.RevisionAsesor.ESTADO);
                        assertThat(error.codigoError()).isEqualTo(ArtefactosCodes.RevisionAsesor.ESTADO_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.RevisionAsesor.ESTUDIANTE);
                        assertThat(error.codigoError()).isEqualTo(ArtefactosCodes.RevisionAsesor.ESTUDIANTE_REQUERIDO);
                    });
                });
    }

    @Test
    void debeRechazar_cuandoEstadoFueraDelCatalogo() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> CambioEstadoRevisionAsesorDomain.crear(revision, "VACIO", estudiante))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).singleElement().satisfies(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.RevisionAsesor.ESTADO);
                        assertThat(error.codigoError()).isEqualTo(ArtefactosCodes.RevisionAsesor.ESTADO_INVALIDO);
                    });
                });
    }
}
