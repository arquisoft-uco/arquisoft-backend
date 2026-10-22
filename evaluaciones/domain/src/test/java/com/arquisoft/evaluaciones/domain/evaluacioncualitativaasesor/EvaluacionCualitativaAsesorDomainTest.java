package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class EvaluacionCualitativaAsesorDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();

        // Act
        var evaluacion = EvaluacionCualitativaAsesorDomain.crear(evaluacionAsesor, item, criterio);

        // Assert
        assertThat(evaluacion.getId()).isNotNull();
        assertThat(evaluacion.getEvaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(evaluacion.getItem()).isEqualTo(item);
        assertThat(evaluacion.getCriterio()).isEqualTo(criterio);
        assertThat(evaluacion.esVacio()).isFalse();
    }

    @Test
    void debeAcumularErrores_cuandoEvaluacionItemYCriterioSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> EvaluacionCualitativaAsesorDomain.crear(null, null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.EvaluacionCualitativaAsesor.EVALUACION_ASESOR,
                                                EvaluacionesCodes.EvaluacionCualitativaAsesor.EVALUACION_ASESOR_REQUERIDO),
                                        tuple(EvaluacionesFields.EvaluacionCualitativaAsesor.ITEM,
                                                EvaluacionesCodes.EvaluacionCualitativaAsesor.ITEM_REQUERIDO),
                                        tuple(EvaluacionesFields.EvaluacionCualitativaAsesor.CRITERIO,
                                                EvaluacionesCodes.EvaluacionCualitativaAsesor.CRITERIO_REQUERIDO)));
    }

    @Test
    void debeReconstruir_conTodosLosCamposSinValidar() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();

        // Act
        var evaluacion = EvaluacionCualitativaAsesorDomain.reconstruir(id, evaluacionAsesor, item, criterio);

        // Assert
        assertThat(evaluacion.getId()).isEqualTo(id);
        assertThat(evaluacion.getEvaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(evaluacion.getItem()).isEqualTo(item);
        assertThat(evaluacion.getCriterio()).isEqualTo(criterio);
    }

    @Test
    void debeSerVacio_soloElCentinela() {
        // Arrange
        var reconstruida = EvaluacionCualitativaAsesorDomain.reconstruir(
                UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(),
                UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto());

        // Act & Assert
        assertThat(EvaluacionCualitativaAsesorDomain.VACIO.esVacio()).isTrue();
        assertThat(reconstruida.esVacio()).isFalse();
    }
}
