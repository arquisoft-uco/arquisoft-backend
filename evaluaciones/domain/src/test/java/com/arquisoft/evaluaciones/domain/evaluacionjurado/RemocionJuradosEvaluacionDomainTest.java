package com.arquisoft.evaluaciones.domain.evaluacionjurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class RemocionJuradosEvaluacionDomainTest {

    @Test
    void debeCrearRemocion_cuandoDatosValidos() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var juradoUno = UtilUUID.generarNuevoUUID();
        var juradoDos = UtilUUID.generarNuevoUUID();

        // Act
        var remocion = RemocionJuradosEvaluacionDomain.crear(evaluacion, List.of(juradoUno, juradoDos));

        // Assert
        assertThat(remocion.getEvaluacion()).isEqualTo(evaluacion);
        assertThat(remocion.getJurados()).containsExactlyInAnyOrder(juradoUno, juradoDos);
    }

    @Test
    void debeAcumularErrores_cuandoEvaluacionNulaYLoteVacio() {
        // Act & Assert
        assertThatThrownBy(() -> RemocionJuradosEvaluacionDomain.crear(null, List.of()))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.EvaluacionJurado.EVALUACION,
                                                EvaluacionesCodes.EvaluacionJurado.EVALUACION_REQUERIDO),
                                        tuple(EvaluacionesFields.AsignacionJuradosEvaluacion.JURADOS,
                                                EvaluacionesCodes.AsignacionJuradosEvaluacion.LOTE_VACIO)));
    }

    @Test
    void debeRechazarJuradosRepetidos_cuandoDosEntradasComparten() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var repetido = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> RemocionJuradosEvaluacionDomain.crear(evaluacion, List.of(repetido, repetido)))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.AsignacionJuradosEvaluacion.JURADOS_REPETIDOS));
    }
}
