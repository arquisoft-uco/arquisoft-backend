package com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.secondaryport.entity.CoordinadorProyectoGradoEntity;
import com.arquisoft.evaluaciones.domain.coordinadorproyectogrado.CoordinadorProyectoGradoDomain;
import com.arquisoft.evaluaciones.domain.coordinadorproyectogrado.model.ContactoCoordinador;

public final class CoordinadorProyectoGradoMapper {

    private CoordinadorProyectoGradoMapper() {}

    public static CoordinadorProyectoGradoEntity toEntity(CoordinadorProyectoGradoDomain domain) {
        return new CoordinadorProyectoGradoEntity(
                domain.getProyectoGrado(), domain.getCoordinador(), domain.getNombre(), domain.getEmail(),
                domain.getOcurridoEn());
    }

    public static ContactoCoordinador toContacto(CoordinadorProyectoGradoEntity entity) {
        return new ContactoCoordinador(entity.coordinador(), entity.nombre(), entity.email());
    }
}
