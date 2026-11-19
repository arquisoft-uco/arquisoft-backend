package com.arquisoft.evaluaciones.domain.evaluacionasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionAsesorDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var evaluacionAsesor = EvaluacionAsesorDomain.crear(evaluacion, asesor);

        // Assert
        assertThat(evaluacionAsesor.getId()).isNotNull();
        assertThat(evaluacionAsesor.getEvaluacion()).isEqualTo(evaluacion);
        assertThat(evaluacionAsesor.getAsesor()).isEqualTo(asesor);
        assertThat(evaluacionAsesor.esVacio()).isFalse();
    }

    @Test
    void debeAcumularErrores_cuandoEvaluacionYAsesorNulos() {
        // Act & Assert
        assertThatThrownBy(() -> EvaluacionAsesorDomain.crear(null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores())
                                .extracting("codigoError")
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.EvaluacionAsesor.EVALUACION_REQUERIDO,
                                        EvaluacionesCodes.EvaluacionAsesor.ASESOR_REQUERIDO));
    }

    @Test
    void debeReconstruirSinValidar_yResponderTieneAsesor() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var evaluacionAsesor = EvaluacionAsesorDomain.reconstruir(id, UtilUUID.generarNuevoUUID(), asesor);

        // Assert
        assertThat(evaluacionAsesor.getId()).isEqualTo(id);
        assertThat(evaluacionAsesor.tieneAsesor(asesor)).isTrue();
        assertThat(evaluacionAsesor.tieneAsesor(UtilUUID.generarNuevoUUID())).isFalse();
    }

    @Test
    void debeSerVacio_cuandoEsElCentinela() {
        // Act & Assert
        assertThat(EvaluacionAsesorDomain.VACIO.esVacio()).isTrue();
        assertThat(UtilUUID.esPorDefecto(EvaluacionAsesorDomain.VACIO.getAsesor())).isTrue();
    }
}
