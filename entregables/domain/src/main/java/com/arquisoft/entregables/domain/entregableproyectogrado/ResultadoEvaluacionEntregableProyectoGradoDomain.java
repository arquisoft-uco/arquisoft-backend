package com.arquisoft.entregables.domain.entregableproyectogrado;

import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ResultadoEvaluacionEntregableProyectoGradoDomain {

    private UUID entregableProyectoGrado;
    private EstadoEntregable estadoEntregable;

    private ResultadoEvaluacionEntregableProyectoGradoDomain() {}

    public static ResultadoEvaluacionEntregableProyectoGradoDomain crear(
            UUID entregableProyectoGrado, String estadoEntregable) {
        var resultado = new ResultadoEvaluacionEntregableProyectoGradoDomain();
        var result = new ValidationResult();

        resultado.setEntregableProyectoGrado(entregableProyectoGrado, result);
        resultado.setEstadoEntregable(estadoEntregable, result);

        result.lanzarSiTieneErrores();
        return resultado;
    }

    public boolean esAprobado() {
        return estadoEntregable == EstadoEntregable.APROBADO
                || estadoEntregable == EstadoEntregable.APROBADO_CON_OBSERVACIONES;
    }

    public boolean esRechazado() {
        return estadoEntregable == EstadoEntregable.RECHAZADO;
    }

    private void setEntregableProyectoGrado(UUID entregableProyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(entregableProyectoGrado,
                EntregablesFields.EntregableProyectoGrado.ID,
                EntregablesCodes.EntregableProyectoGrado.ID_REQUERIDO, result)) {
            return;
        }
        this.entregableProyectoGrado = entregableProyectoGrado;
    }

    private void setEstadoEntregable(String estadoEntregable, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(estadoEntregable);
        if (!ValidatorTexto.noEnBlanco(recortado,
                EntregablesFields.EntregableProyectoGrado.ESTADO_ENTREGABLE,
                EntregablesCodes.EntregableProyectoGrado.ESTADO_ENTREGABLE_REQUERIDO, result)) {
            return;
        }
        if (!EstadoEntregable.esValido(recortado) || !esResultadoDeEvaluacion(EstadoEntregable.desde(recortado))) {
            result.agregarError(
                    EntregablesFields.EntregableProyectoGrado.ESTADO_ENTREGABLE,
                    EntregablesCodes.EntregableProyectoGrado.RESULTADO_EVALUACION_INVALIDO,
                    Mensajes.formatear(EntregableProyectoGradoKey.ERROR_RESULTADO_EVALUACION_INVALIDO));
            return;
        }
        this.estadoEntregable = EstadoEntregable.desde(recortado);
    }

    private static boolean esResultadoDeEvaluacion(EstadoEntregable estado) {
        return estado == EstadoEntregable.APROBADO
                || estado == EstadoEntregable.APROBADO_CON_OBSERVACIONES
                || estado == EstadoEntregable.RECHAZADO;
    }

    public UUID getEntregableProyectoGrado() {
        return entregableProyectoGrado;
    }

    public EstadoEntregable getEstadoEntregable() {
        return estadoEntregable;
    }
}
