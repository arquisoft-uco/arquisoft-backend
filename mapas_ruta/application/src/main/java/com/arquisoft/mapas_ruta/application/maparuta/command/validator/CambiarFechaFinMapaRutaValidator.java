package com.arquisoft.mapas_ruta.application.maparuta.command.validator;

import com.arquisoft.mapas_ruta.domain.maparuta.CambioFechaFinMapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ProyectoGradoDomain;

public interface CambiarFechaFinMapaRutaValidator {

    void validar(CambioFechaFinMapaRutaDomain cambio, MapaRutaDomain mapaRuta, ProyectoGradoDomain proyectoGrado);
}
