package com.arquisoft.mapas_ruta.domain.maparuta;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.LocalDate;
import java.util.UUID;

public final class CambioFechaFinMapaRutaDomain {

    private UUID mapaRuta;
    private LocalDate fechaFin;
    private UUID coordinador;

    private CambioFechaFinMapaRutaDomain() {}

    public static CambioFechaFinMapaRutaDomain crear(UUID mapaRuta, LocalDate fechaFin, UUID coordinador) {
        var cambio = new CambioFechaFinMapaRutaDomain();
        var result = new ValidationResult();

        cambio.setMapaRuta(mapaRuta, result);
        cambio.setFechaFin(fechaFin, result);
        cambio.setCoordinador(coordinador, result);

        result.lanzarSiTieneErrores();
        return cambio;
    }

    private void setMapaRuta(UUID mapaRuta, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(mapaRuta,
                MapasRutaFields.MapaRuta.MAPA_RUTA,
                MapasRutaCodes.MapaRuta.MAPA_RUTA_REQUERIDO, result)) {
            return;
        }
        this.mapaRuta = mapaRuta;
    }

    private void setFechaFin(LocalDate fechaFin, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(fechaFin,
                MapasRutaFields.MapaRuta.FECHA_FIN,
                MapasRutaCodes.MapaRuta.FECHA_FIN_REQUERIDA, result)) {
            return;
        }
        this.fechaFin = fechaFin;
    }

    private void setCoordinador(UUID coordinador, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(coordinador,
                MapasRutaFields.MapaRuta.COORDINADOR,
                MapasRutaCodes.MapaRuta.COORDINADOR_REQUERIDO, result)) {
            return;
        }
        this.coordinador = coordinador;
    }

    public UUID getMapaRuta() {
        return mapaRuta;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public UUID getCoordinador() {
        return coordinador;
    }
}
