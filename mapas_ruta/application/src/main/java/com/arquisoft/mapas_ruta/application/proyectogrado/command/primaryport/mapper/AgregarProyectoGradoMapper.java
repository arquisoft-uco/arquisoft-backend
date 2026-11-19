package com.arquisoft.mapas_ruta.application.proyectogrado.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.proyectogrado.command.primaryport.model.AgregarProyectoGradoCommand;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ProyectoGradoDomain;

public final class AgregarProyectoGradoMapper {

    private AgregarProyectoGradoMapper() {}

    public static ProyectoGradoDomain toDomain(AgregarProyectoGradoCommand command) {
        return ProyectoGradoDomain.crear(
                command.proyectoGrado(), command.coordinador(), command.fichaPerfil(),
                command.tituloProyecto(), command.ocurridoEn());
    }
}
