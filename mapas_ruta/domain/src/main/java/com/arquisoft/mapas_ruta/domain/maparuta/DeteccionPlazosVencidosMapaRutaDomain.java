package com.arquisoft.mapas_ruta.domain.maparuta;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.LocalDate;

public final class DeteccionPlazosVencidosMapaRutaDomain {

    private LocalDate hoy;

    private DeteccionPlazosVencidosMapaRutaDomain() {}

    public static DeteccionPlazosVencidosMapaRutaDomain crear(LocalDate hoy) {
        var deteccion = new DeteccionPlazosVencidosMapaRutaDomain();
        var result = new ValidationResult();

        deteccion.setHoy(hoy, result);

        result.lanzarSiTieneErrores();
        return deteccion;
    }

    private void setHoy(LocalDate hoy, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(hoy,
                MapasRutaFields.MapaRuta.HOY,
                MapasRutaCodes.MapaRuta.HOY_REQUERIDA, result)) {
            return;
        }
        this.hoy = hoy;
    }

    public LocalDate getHoy() {
        return hoy;
    }
}
