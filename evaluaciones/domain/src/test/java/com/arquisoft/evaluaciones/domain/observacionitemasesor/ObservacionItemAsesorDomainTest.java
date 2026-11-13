package com.arquisoft.evaluaciones.domain.observacionitemasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ObservacionItemAsesorDomainTest {

    @Test
    void debeCrearObservacion_cuandoDatosValidos() {
        // Arrange
        var evaluacionCuantitativaAsesor = UtilUUID.generarNuevoUUID();

        // Act
        var observacion = ObservacionItemAsesorDomain.crear(evaluacionCuantitativaAsesor, "   Buen sustento   ");

        // Assert
        assertThat(observacion.getId()).isNotNull();
        assertThat(observacion.getEvaluacionCuantitativaAsesor()).isEqualTo(evaluacionCuantitativaAsesor);
        assertThat(observacion.getDescripcion()).isEqualTo("Buen sustento");
        assertThat(observacion.esVacio()).isFalse();
    }

    @Test
    void debeAcumularErrores_cuandoEvaluacionNulaYDescripcionEnBlanco() {
        // Act & Assert
        assertThatThrownBy(() -> ObservacionItemAsesorDomain.crear(null, "   "))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.ObservacionItemAsesor.EVALUACION_CUANTITATIVA_ASESOR,
                                                EvaluacionesCodes.ObservacionItemAsesor
                                                        .EVALUACION_CUANTITATIVA_ASESOR_REQUERIDA),
                                        tuple(EvaluacionesFields.ObservacionItemAsesor.DESCRIPCION,
                                                EvaluacionesCodes.ObservacionItemAsesor.DESCRIPCION_REQUERIDA)));
    }

    @Test
    void debeFallar_cuandoDescripcionExcede500() {
        // Arrange
        var descripcionLarga = "a".repeat(501);
        var evaluacionCuantitativaAsesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> ObservacionItemAsesorDomain.crear(evaluacionCuantitativaAsesor, descripcionLarga))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.ObservacionItemAsesor.DESCRIPCION_DEMASIADO_LARGA));
    }

    @Test
    void debeReconstruirSinValidar_yDistinguirElCentinela() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var observacion = ObservacionItemAsesorDomain.reconstruir(id, null, null);

        // Assert
        assertThat(observacion.getId()).isEqualTo(id);
        assertThat(observacion.getEvaluacionCuantitativaAsesor()).isNull();
        assertThat(observacion.getDescripcion()).isNull();
        assertThat(observacion.esVacio()).isFalse();
        assertThat(ObservacionItemAsesorDomain.VACIO.esVacio()).isTrue();
        assertThat(ObservacionItemAsesorDomain.VACIO.getId()).isEqualTo(UtilUUID.obtenerUUIDPorDefecto());
        assertThat(ObservacionItemAsesorDomain.VACIO.getDescripcion()).isEqualTo(UtilTexto.VACIO);
    }
}
