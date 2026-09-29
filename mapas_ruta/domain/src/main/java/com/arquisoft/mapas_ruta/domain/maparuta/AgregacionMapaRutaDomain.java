package com.arquisoft.mapas_ruta.domain.maparuta;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class AgregacionMapaRutaDomain {

    private MapaRutaDomain mapaRuta;
    private UUID coordinador;

    private AgregacionMapaRutaDomain() {}

    public static AgregacionMapaRutaDomain crear(MapaRutaDomain mapaRuta, UUID coordinador) {
        var agregacion = new AgregacionMapaRutaDomain();
        var result = new ValidationResult();

        agregacion.setMapaRuta(mapaRuta, result);
        agregacion.setCoordinador(coordinador, result);

        result.lanzarSiTieneErrores();
        return agregacion;
    }

    private void setMapaRuta(MapaRutaDomain mapaRuta, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(mapaRuta,
                MapasRutaFields.MapaRuta.MAPA_RUTA,
                MapasRutaCodes.MapaRuta.MAPA_RUTA_REQUERIDO, result)) {
            return;
        }
        this.mapaRuta = mapaRuta;
    }

    private void setCoordinador(UUID coordinador, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(coordinador,
                MapasRutaFields.MapaRuta.COORDINADOR,
                MapasRutaCodes.MapaRuta.COORDINADOR_REQUERIDO, result)) {
            return;
        }
        this.coordinador = coordinador;
    }

    public MapaRutaDomain getMapaRuta() {
        return mapaRuta;
    }

    public UUID getCoordinador() {
        return coordinador;
    }
}
