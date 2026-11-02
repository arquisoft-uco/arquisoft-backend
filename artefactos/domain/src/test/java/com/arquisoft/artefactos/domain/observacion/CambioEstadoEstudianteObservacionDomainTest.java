package com.arquisoft.artefactos.domain.observacion;

import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambioEstadoEstudianteObservacionDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var cambio = CambioEstadoEstudianteObservacionDomain.crear(observacion, "  SOLUCIONADA ", estudiante);

        // Assert
        assertThat(cambio.getObservacion()).isEqualTo(observacion);
        assertThat(cambio.getEstado()).isEqualTo(EstadoObservacion.SOLUCIONADA);
        assertThat(cambio.getEstudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeAcumularErrores_cuandoNingunDatoLlega() {
        // Act & Assert
        assertThatThrownBy(() -> CambioEstadoEstudianteObservacionDomain.crear(null, "  ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(3);
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.Observacion.ID);
                        assertThat(error.codigoError()).isEqualTo(ArtefactosCodes.Observacion.ID_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.Observacion.ESTADO);
                        assertThat(error.codigoError()).isEqualTo(ArtefactosCodes.Observacion.ESTADO_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.Observacion.ESTUDIANTE);
                        assertThat(error.codigoError()).isEqualTo(ArtefactosCodes.Observacion.ESTUDIANTE_REQUERIDO);
                    });
                });
    }

    @Test
    void debeRechazar_cuandoEstadoFueraDelCatalogo() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> CambioEstadoEstudianteObservacionDomain.crear(observacion, "VACIO", estudiante))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).singleElement().satisfies(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.Observacion.ESTADO);
                        assertThat(error.codigoError()).isEqualTo(ArtefactosCodes.Observacion.ESTADO_INVALIDO);
                    });
                });
    }
}
