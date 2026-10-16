package com.arquisoft.mapas_ruta.application.maparuta.command.validator;

import com.arquisoft.mapas_ruta.domain.maparuta.AgregacionMapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ProyectoGradoDomain;

public interface AgregarMapaRutaValidator {

    void validar(AgregacionMapaRutaDomain agregacion, ProyectoGradoDomain proyectoGrado, boolean mapaRutaExiste);
}
