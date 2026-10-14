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

class AsignacionJuradosEvaluacionDomainTest {

    @Test
    void debeCrearAsignacion_cuandoDatosValidos() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var juradoUno = UtilUUID.generarNuevoUUID();
        var juradoDos = UtilUUID.generarNuevoUUID();
        var jurados = List.of(
                EvaluacionJuradoDomain.crear(evaluacion, juradoUno),
                EvaluacionJuradoDomain.crear(evaluacion, juradoDos));

        // Act
        var asignacion = AsignacionJuradosEvaluacionDomain.crear(evaluacion, jurados);

        // Assert
        assertThat(asignacion.getEvaluacion()).isEqualTo(evaluacion);
        assertThat(asignacion.getJuradosEvaluacion()).hasSize(2);
        assertThat(asignacion.getJurados()).containsExactlyInAnyOrder(juradoUno, juradoDos);
    }

    @Test
    void debeRechazarEvaluacion_cuandoEsNula() {
        // Arrange
        var jurados = List.of(EvaluacionJuradoDomain.crear(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID()));

        // Act & Assert
        assertThatThrownBy(() -> AsignacionJuradosEvaluacionDomain.crear(null, jurados))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(
                                        tuple(EvaluacionesFields.EvaluacionJurado.EVALUACION,
                                                EvaluacionesCodes.EvaluacionJurado.EVALUACION_REQUERIDO)));
    }

    @Test
    void debeRechazarLoteVacio_cuandoListaEsNulaOVacia() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> AsignacionJuradosEvaluacionDomain.crear(evaluacion, null))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(
                                        tuple(EvaluacionesFields.AsignacionJuradosEvaluacion.JURADOS,
                                                EvaluacionesCodes.AsignacionJuradosEvaluacion.LOTE_VACIO)));
        assertThatThrownBy(() -> AsignacionJuradosEvaluacionDomain.crear(evaluacion, List.of()))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.AsignacionJuradosEvaluacion.LOTE_VACIO));
    }

    @Test
    void debeRechazarJuradosRepetidos_cuandoDosEntradasComparten() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var repetido = UtilUUID.generarNuevoUUID();
        var jurados = List.of(
                EvaluacionJuradoDomain.crear(evaluacion, repetido),
                EvaluacionJuradoDomain.crear(evaluacion, repetido));

        // Act & Assert
        assertThatThrownBy(() -> AsignacionJuradosEvaluacionDomain.crear(evaluacion, jurados))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.AsignacionJuradosEvaluacion.JURADOS_REPETIDOS));
    }
}
