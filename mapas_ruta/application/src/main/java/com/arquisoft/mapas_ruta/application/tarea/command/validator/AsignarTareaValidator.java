package com.arquisoft.mapas_ruta.application.tarea.command.validator;

import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;

public interface AsignarTareaValidator {

    void validar(TareaDomain tarea, MapaRutaDomain mapaRuta, ProyectoGradoDomain proyectoGrado,
                 boolean tareaExiste, boolean asesorAsignado);
}
