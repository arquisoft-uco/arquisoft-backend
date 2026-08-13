package com.arquisoft.fichas.domain.estadoevaluacionficha;

import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.time.Instant;
import java.util.UUID;

public final class EstadoEvaluacionFichaDomain {

    private UUID id;
    private UUID evaluacionFichaPerfilId;
    private EstadoEvaluacion estadoEvaluacion;
    private Instant fechaActualizacion;

    private EstadoEvaluacionFichaDomain() {}

    private EstadoEvaluacionFichaDomain(
            UUID id,
            UUID evaluacionFichaPerfilId,
            EstadoEvaluacion estadoEvaluacion,
            Instant fechaActualizacion) {
        this.id = id;
        this.evaluacionFichaPerfilId = evaluacionFichaPerfilId;
        this.estadoEvaluacion = estadoEvaluacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public static EstadoEvaluacionFichaDomain crear(UUID evaluacionFichaPerfilId) {
        var aggregate = new EstadoEvaluacionFichaDomain();
        var result = new ValidationResult();

        aggregate.setId();
        aggregate.setEvaluacionFichaPerfilId(evaluacionFichaPerfilId, result);
        aggregate.setEstadoEvaluacionInicial();
        aggregate.setFechaActualizacion();

        result.lanzarSiTieneErrores();
        return aggregate;
    }

    public static EstadoEvaluacionFichaDomain crearConEstado(
            UUID evaluacionFichaPerfilId,
            EstadoEvaluacion estadoEvaluacion,
            EstadoEvaluacion ultimoEstado) {
        var aggregate = new EstadoEvaluacionFichaDomain();
        var result = new ValidationResult();

        aggregate.setId();
        aggregate.setEvaluacionFichaPerfilId(evaluacionFichaPerfilId, result);
        aggregate.setEstadoEvaluacion(estadoEvaluacion, ultimoEstado, result);
        aggregate.setFechaActualizacion();

        result.lanzarSiTieneErrores();
        return aggregate;
    }

    public static EstadoEvaluacionFichaDomain reconstruir(
            UUID id,
            UUID evaluacionFichaPerfilId,
            EstadoEvaluacion estadoEvaluacion,
            Instant fechaActualizacion) {
        return new EstadoEvaluacionFichaDomain(
                id,
                evaluacionFichaPerfilId,
                estadoEvaluacion,
                fechaActualizacion);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setEvaluacionFichaPerfilId(UUID evaluacionFichaPerfilId, ValidationResult result) {
        if (!DomainValidator.noNulo(
                evaluacionFichaPerfilId,
                FichasFields.EstadoEvaluacionFicha.EVALUACION_FICHA_PERFIL,
                FichasCodes.EstadoEvaluacionFicha.EVALUACION_REQUERIDA,
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
                FichasFields.EstadoEvaluacionFicha.ESTADO_EVALUACION,
                FichasCodes.EstadoEvaluacionFicha.ESTADO_REQUERIDO,
                result)) {
            return;
        }

        if (estadoEvaluacion.esEnEvaluacion()) {
            result.agregarError(
                    FichasFields.EstadoEvaluacionFicha.ESTADO_EVALUACION,
                    FichasCodes.EstadoEvaluacionFicha.ESTADO_EN_EVALUACION_NO_MANUAL,
                    Mensajes.obtener(EstadoEvaluacionFichaKey.ERROR_EN_EVALUACION_NO_MANUAL));
            return;
        }

        if (!UtilObjeto.esNulo(ultimoEstado) && ultimoEstado.esTerminal()) {
            result.agregarError(
                    FichasFields.EstadoEvaluacionFicha.ESTADO_EVALUACION,
                    FichasCodes.EstadoEvaluacionFicha.TRANSICION_INVALIDA,
                    Mensajes.obtener(EstadoEvaluacionFichaKey.ERROR_TRANSICION_DESDE_TERMINAL));
        }
        this.estadoEvaluacion = estadoEvaluacion;
    }

    private void setFechaActualizacion() {
        this.fechaActualizacion = UtilFecha.generarInstanteActual();
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
