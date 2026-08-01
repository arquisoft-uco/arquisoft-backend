package com.arquisoft.fichas.domain.estadoevaluacionficha.aggregate;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.message.FichasMessages;
import com.arquisoft.shared.util.UtilDate;
import com.arquisoft.shared.util.UtilObject;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.time.Instant;
import java.util.UUID;

public final class EstadoEvaluacionFichaAggregate {

    private UUID id;
    private UUID evaluacionFichaPerfilId;
    private EstadoEvaluacion estadoEvaluacion;
    private Instant fechaActualizacion;

    private EstadoEvaluacionFichaAggregate() {}

    private EstadoEvaluacionFichaAggregate(
            UUID id,
            UUID evaluacionFichaPerfilId,
            EstadoEvaluacion estadoEvaluacion,
            Instant fechaActualizacion) {
        this.id = id;
        this.evaluacionFichaPerfilId = evaluacionFichaPerfilId;
        this.estadoEvaluacion = estadoEvaluacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public static EstadoEvaluacionFichaAggregate crear(UUID evaluacionFichaPerfilId) {
        var aggregate = new EstadoEvaluacionFichaAggregate();
        var result = new ValidationResult();

        aggregate.setId();
        aggregate.setEvaluacionFichaPerfilId(evaluacionFichaPerfilId, result);
        aggregate.setEstadoEvaluacionInicial();
        aggregate.setFechaActualizacion();

        result.lanzarSiTieneErrores();
        return aggregate;
    }

    public static EstadoEvaluacionFichaAggregate crearConEstado(
            UUID evaluacionFichaPerfilId,
            EstadoEvaluacion estadoEvaluacion,
            EstadoEvaluacion ultimoEstado) {
        var aggregate = new EstadoEvaluacionFichaAggregate();
        var result = new ValidationResult();

        aggregate.setId();
        aggregate.setEvaluacionFichaPerfilId(evaluacionFichaPerfilId, result);
        aggregate.setEstadoEvaluacion(estadoEvaluacion, ultimoEstado, result);
        aggregate.setFechaActualizacion();

        result.lanzarSiTieneErrores();
        return aggregate;
    }

    public static EstadoEvaluacionFichaAggregate reconstruir(
            UUID id,
            UUID evaluacionFichaPerfilId,
            EstadoEvaluacion estadoEvaluacion,
            Instant fechaActualizacion) {
        return new EstadoEvaluacionFichaAggregate(
                id,
                evaluacionFichaPerfilId,
                estadoEvaluacion,
                fechaActualizacion);
    }

    private void setId() {
        this.id = UtilUUID.generateNewUUID();
    }

    private void setEvaluacionFichaPerfilId(UUID evaluacionFichaPerfilId, ValidationResult result) {
        if (!DomainValidator.noNulo(
                evaluacionFichaPerfilId,
                FichasMessages.EstadoEvaluacionFicha.CAMPO_EVALUACION_FICHA_PERFIL,
                FichasMessages.EstadoEvaluacionFicha.EVALUACION_REQUERIDA,
                result)) {
            return;
        }
        this.evaluacionFichaPerfilId = evaluacionFichaPerfilId;
    }

    private void setEstadoEvaluacionInicial() {
        this.estadoEvaluacion = EstadoEvaluacion.EN_EVALUACION;
    }

    private void setEstadoEvaluacion(EstadoEvaluacion estadoEvaluacion, EstadoEvaluacion ultimoEstado, ValidationResult result) {
        if (!DomainValidator.noNulo(
                estadoEvaluacion,
                FichasMessages.EstadoEvaluacionFicha.CAMPO_ESTADO_EVALUACION,
                FichasMessages.EstadoEvaluacionFicha.ESTADO_REQUERIDO,
                result)) {
            return;
        }

        if (estadoEvaluacion.esEnEvaluacion()) {
            result.agregarError(
                    FichasMessages.EstadoEvaluacionFicha.CAMPO_ESTADO_EVALUACION,
                    FichasMessages.EstadoEvaluacionFicha.ESTADO_EN_EVALUACION_NO_MANUAL,
                    FichasMessages.EstadoEvaluacionFicha.ESTADO_EN_EVALUACION_NO_MANUAL_MSG);
            return;
        }

        if (!UtilObject.isNull(ultimoEstado) && ultimoEstado.esTerminal()) {
            result.agregarError(
                    FichasMessages.EstadoEvaluacionFicha.CAMPO_ESTADO_EVALUACION,
                    FichasMessages.EstadoEvaluacionFicha.TRANSICION_INVALIDA,
                    FichasMessages.EstadoEvaluacionFicha.TRANSICION_DESDE_TERMINAL_SIMPLE_MSG);
        }
        this.estadoEvaluacion = estadoEvaluacion;
    }

    private void setFechaActualizacion() {
        this.fechaActualizacion = UtilDate.generateNewInstantNow();
    }

    public UUID getId() {
        return id;
    }

    public UUID getEvaluacionFichaPerfilId() {
        return evaluacionFichaPerfilId;
    }

    public EstadoEvaluacion getEstadoEvaluacion() {
        return estadoEvaluacion;
    }

    public Instant getFechaActualizacion() {
        return fechaActualizacion;
    }
}
