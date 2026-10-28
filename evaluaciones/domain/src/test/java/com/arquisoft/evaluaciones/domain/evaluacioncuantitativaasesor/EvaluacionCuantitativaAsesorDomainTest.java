package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.constant.EvaluacionesLimits;
import com.arquisoft.shared.util.UtilNumero;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class EvaluacionCuantitativaAsesorDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();

        // Act
        var evaluacion = EvaluacionCuantitativaAsesorDomain.crear(
                evaluacionAsesor, item, EvaluacionesLimits.EvaluacionCuantitativaAsesor.PUNTAJE_MAX);

        // Assert
        assertThat(evaluacion.getId()).isNotNull();
        assertThat(evaluacion.getEvaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(evaluacion.getItem()).isEqualTo(item);
        assertThat(evaluacion.getPuntaje()).isEqualTo(EvaluacionesLimits.EvaluacionCuantitativaAsesor.PUNTAJE_MAX);
        assertThat(evaluacion.esVacio()).isFalse();
    }

    @Test
    void debeAcumularErrores_cuandoEvaluacionItemYPuntajeSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> EvaluacionCuantitativaAsesorDomain.crear(null, null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.EvaluacionCuantitativaAsesor.EVALUACION_ASESOR,
                                                EvaluacionesCodes.EvaluacionCuantitativaAsesor.EVALUACION_ASESOR_REQUERIDO),
                                        tuple(EvaluacionesFields.EvaluacionCuantitativaAsesor.ITEM,
                                                EvaluacionesCodes.EvaluacionCuantitativaAsesor.ITEM_REQUERIDO),
                                        tuple(EvaluacionesFields.EvaluacionCuantitativaAsesor.PUNTAJE,
                                                EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJE_REQUERIDO)));
    }

    @Test
    void debeRechazar_cuandoPuntajeFueraDeRango() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        var porEncima = EvaluacionesLimits.EvaluacionCuantitativaAsesor.PUNTAJE_MAX + 1;

        // Act & Assert
        assertThatThrownBy(() -> EvaluacionCuantitativaAsesorDomain.crear(evaluacionAsesor, item, porEncima))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(
                                        tuple(EvaluacionesFields.EvaluacionCuantitativaAsesor.PUNTAJE,
                                                EvaluacionesCodes.EvaluacionCuantitativaAsesor.PUNTAJE_FUERA_DE_RANGO)));
        assertThatThrownBy(() -> EvaluacionCuantitativaAsesorDomain.crear(evaluacionAsesor, item, -1))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void debeReconstruir_conTodosLosCamposSinValidar() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();

        // Act
        var evaluacion = EvaluacionCuantitativaAsesorDomain.reconstruir(id, evaluacionAsesor, item, 1000);

        // Assert
        assertThat(evaluacion.getId()).isEqualTo(id);
        assertThat(evaluacion.getEvaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(evaluacion.getItem()).isEqualTo(item);
        assertThat(evaluacion.getPuntaje()).isEqualTo(1000);
    }

    @Test
    void debeSerVacio_soloElCentinela() {
        // Arrange
        var reconstruida = EvaluacionCuantitativaAsesorDomain.reconstruir(
                UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(),
                UtilUUID.obtenerUUIDPorDefecto(), UtilNumero.CERO);

        // Act & Assert
        assertThat(EvaluacionCuantitativaAsesorDomain.VACIO.esVacio()).isTrue();
        assertThat(reconstruida.esVacio()).isFalse();
    }
}
