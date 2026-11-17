package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambioPuntajeEvaluacionCuantitativaAsesorDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var evaluacionCuantitativaAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var cambio = CambioPuntajeEvaluacionCuantitativaAsesorDomain.crear(
                evaluacionCuantitativaAsesor, asesor, 450);

        // Assert
        assertThat(cambio.getEvaluacionCuantitativaAsesor()).isEqualTo(evaluacionCuantitativaAsesor);
        assertThat(cambio.getAsesor()).isEqualTo(asesor);
        assertThat(cambio.getNuevoPuntaje()).isEqualTo(450);
    }

    @Test
    void debeAcumularFieldErrors_cuandoTodoEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> CambioPuntajeEvaluacionCuantitativaAsesorDomain.crear(null, null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.EvaluacionCuantitativaAsesor.ID_REQUERIDO,
                                        EvaluacionesCodes.EvaluacionCuantitativaAsesor.ASESOR_REQUERIDO,
                                        EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJE_REQUERIDO));
    }

    @Test
    void debeFallar_cuandoPuntajeFueraDeRango() {
        // Arrange
        var evaluacionCuantitativaAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> CambioPuntajeEvaluacionCuantitativaAsesorDomain.crear(
                evaluacionCuantitativaAsesor, asesor, -1))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJE_FUERA_DE_RANGO));
        assertThatThrownBy(() -> CambioPuntajeEvaluacionCuantitativaAsesorDomain.crear(
                evaluacionCuantitativaAsesor, asesor, 501))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJE_FUERA_DE_RANGO));
    }
}
