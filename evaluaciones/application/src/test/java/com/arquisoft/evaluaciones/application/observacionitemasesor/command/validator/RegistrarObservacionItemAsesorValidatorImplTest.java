package com.arquisoft.evaluaciones.application.observacionitemasesor.command.validator;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.validator.impl.RegistrarObservacionItemAsesorValidatorImpl;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.ContextoEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.ObservacionItemAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.RegistroObservacionItemAsesorDomain;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarObservacionItemAsesorValidatorImplTest {

    private final RegistrarObservacionItemAsesorValidatorImpl validator =
            new RegistrarObservacionItemAsesorValidatorImpl();

    private RegistroObservacionItemAsesorDomain registro(UUID evaluacion, UUID asesor) {
        return RegistroObservacionItemAsesorDomain.crear(
                ObservacionItemAsesorDomain.crear(evaluacion, "Sustento"), asesor);
    }

    private ContextoEvaluacionCuantitativaAsesorDomain contexto(UUID evaluacion, UUID asesor, EstadoEvaluacion estado) {
        return ContextoEvaluacionCuantitativaAsesorDomain.reconstruir(
                evaluacion, UtilUUID.generarNuevoUUID(), asesor, estado);
    }

    @Test
    void debePasar_cuandoTodoCorrecto() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(
                registro(evaluacion, asesor), contexto(evaluacion, asesor, EstadoEvaluacion.EN_PROGRESO), false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeRechazarEvaluacionInexistente_primero() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var registro = registro(evaluacion, UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(registro, ContextoEvaluacionCuantitativaAsesorDomain.VACIO, true))
                .isInstanceOfSatisfying(DomainException.class, exception -> assertThat(exception.getCodigoError())
                        .isEqualTo(EvaluacionesCodes.ObservacionItemAsesor.EVALUACION_NO_ENCONTRADA));
    }

    @Test
    void debeRechazarAsesorNoAsignado() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var registro = registro(evaluacion, UtilUUID.generarNuevoUUID());
        var contexto = contexto(evaluacion, UtilUUID.generarNuevoUUID(), EstadoEvaluacion.FINALIZADA);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(registro, contexto, true))
                .isInstanceOfSatisfying(DomainException.class, exception -> assertThat(exception.getCodigoError())
                        .isEqualTo(EvaluacionesCodes.EvaluacionCualitativaAsesor.NO_PERTENECE_ASESOR));
    }

    @Test
    void debeRechazarEvaluacionFinalizada() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var registro = registro(evaluacion, asesor);
        var contexto = contexto(evaluacion, asesor, EstadoEvaluacion.FINALIZADA);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(registro, contexto, true))
                .isInstanceOfSatisfying(DomainException.class, exception -> assertThat(exception.getCodigoError())
                        .isEqualTo(EvaluacionesCodes.Evaluacion.ESTADO_FINALIZADA));
    }

    @Test
    void debeRechazarDescripcionDuplicada() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var registro = registro(evaluacion, asesor);
        var contexto = contexto(evaluacion, asesor, EstadoEvaluacion.EN_PROGRESO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(registro, contexto, true))
                .isInstanceOfSatisfying(DomainException.class, exception -> assertThat(exception.getCodigoError())
                        .isEqualTo(EvaluacionesCodes.ObservacionItemAsesor.DESCRIPCION_DUPLICADA));
    }
}
