package com.arquisoft.biblioteca.application.proyectogrado.command.secondaryport.mapper;

import com.arquisoft.biblioteca.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.biblioteca.domain.proyectogrado.ProyectoGradoDomain;

public final class ProyectoGradoMapper {

    private ProyectoGradoMapper() {}

    public static ProyectoGradoDomain toDomain(ProyectoGradoEntity entity) {
        return ProyectoGradoDomain.reconstruir(entity.id(), entity.titulo(), entity.ocurridoEn());
    }

    public static ProyectoGradoEntity toEntity(ProyectoGradoDomain proyecto) {
        return new ProyectoGradoEntity(proyecto.getId(), proyecto.getTitulo(), proyecto.getOcurridoEn());
    }
}
