package com.arquisoft.mapas_ruta.application.tarea.command.validator;

import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.mapas_ruta.domain.tarea.ModificacionTareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;

public interface ModificarInformacionTareaValidator {

    void validar(ModificacionTareaDomain modificacion, TareaDomain tarea, MapaRutaDomain mapaRuta,
                 ProyectoGradoDomain proyectoGrado,
                 boolean descripcionEnUso, boolean asesorAsignado);
}
