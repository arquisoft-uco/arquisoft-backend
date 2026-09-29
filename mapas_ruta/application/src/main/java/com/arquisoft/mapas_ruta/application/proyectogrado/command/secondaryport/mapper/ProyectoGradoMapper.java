package com.arquisoft.mapas_ruta.application.proyectogrado.command.secondaryport.mapper;

import com.arquisoft.mapas_ruta.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.EstadoProyectoGrado;

public final class ProyectoGradoMapper {

    private ProyectoGradoMapper() {}

    public static ProyectoGradoDomain toDomain(ProyectoGradoEntity entity) {
        return ProyectoGradoDomain.reconstruir(
                entity.id(),
                EstadoProyectoGrado.desde(entity.estadoProyectoGrado()),
                entity.coordinador(),
                entity.fichaPerfil(),
                entity.tituloProyecto());
    }
}
