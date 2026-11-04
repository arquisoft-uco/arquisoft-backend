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

public final class CambioEstadoEntregableProyectoGradoDomain {

    private UUID entregableProyectoGrado;
    private EstadoEntregable estadoEntregable;

    private CambioEstadoEntregableProyectoGradoDomain() {}

    public static CambioEstadoEntregableProyectoGradoDomain crear(UUID entregableProyectoGrado, String estadoEntregable) {
        var cambio = new CambioEstadoEntregableProyectoGradoDomain();
        var result = new ValidationResult();

        cambio.setEntregableProyectoGrado(entregableProyectoGrado, result);
        cambio.setEstadoEntregable(estadoEntregable, result);

        result.lanzarSiTieneErrores();
        return cambio;
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
        if (!EstadoEntregable.esValido(recortado)) {
            result.agregarError(
                    EntregablesFields.EntregableProyectoGrado.ESTADO_ENTREGABLE,
                    EntregablesCodes.EntregableProyectoGrado.ESTADO_ENTREGABLE_INVALIDO,
                    Mensajes.formatear(EntregableProyectoGradoKey.ERROR_ESTADO_INVALIDO));
            return;
        }
        this.estadoEntregable = EstadoEntregable.desde(recortado);
    }

    public UUID getEntregableProyectoGrado() {
        return entregableProyectoGrado;
    }

    public EstadoEntregable getEstadoEntregable() {
        return estadoEntregable;
    }
}
