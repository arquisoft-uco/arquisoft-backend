package com.arquisoft.mapas_ruta.domain.tarea;

import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.message.key.mapas_ruta.TareaKey;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class CambioEstadoItemTareaDomain {

    private UUID tarea;
    private EstadoItem estadoItem;

    private CambioEstadoItemTareaDomain() {}

    public static CambioEstadoItemTareaDomain crear(UUID tarea, String estadoItem) {
        var cambio = new CambioEstadoItemTareaDomain();
        var result = new ValidationResult();

        cambio.setTarea(tarea, result);
        cambio.setEstadoItem(estadoItem, result);

        result.lanzarSiTieneErrores();
        return cambio;
    }

    private void setTarea(UUID tarea, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(tarea,
                MapasRutaFields.Tarea.TAREA,
                MapasRutaCodes.Tarea.TAREA_REQUERIDA, result)) {
            return;
        }
        this.tarea = tarea;
    }

    private void setEstadoItem(String estadoItem, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(estadoItem);
        if (!ValidatorTexto.noEnBlanco(recortado,
                MapasRutaFields.Tarea.ESTADO_ITEM,
                MapasRutaCodes.Tarea.ESTADO_ITEM_REQUERIDO, result)) {
            return;
        }
        if (!EstadoItem.esValido(recortado)) {
            result.agregarError(
                    MapasRutaFields.Tarea.ESTADO_ITEM,
                    MapasRutaCodes.Tarea.ESTADO_ITEM_INVALIDO,
                    Mensajes.formatear(TareaKey.ERROR_ESTADO_ITEM_NO_ENCONTRADO, recortado));
            return;
        }
        this.estadoItem = EstadoItem.desde(recortado);
    }

    public UUID getTarea() {
        return tarea;
    }

    public EstadoItem getEstadoItem() {
        return estadoItem;
    }
}
