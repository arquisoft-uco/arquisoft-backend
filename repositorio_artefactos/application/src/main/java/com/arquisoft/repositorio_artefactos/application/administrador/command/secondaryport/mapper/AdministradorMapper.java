package com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport.mapper;

import com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport.entity.AdministradorEntity;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;

public final class AdministradorMapper {

    private AdministradorMapper() {}

    public static AdministradorDomain toDomain(AdministradorEntity entity) {
        return AdministradorDomain.reconstruir(
                entity.id(),
                entity.identificador(),
                entity.nombre(),
                entity.email(),
                entity.ocurridoEn(),
                entity.eliminadoEn());
    }

    public static AdministradorEntity toEntity(AdministradorDomain administrador) {
        return new AdministradorEntity(
                administrador.getId(),
                administrador.getIdentificador(),
                administrador.getNombre(),
                administrador.getEmail(),
                administrador.getOcurridoEn(),
                administrador.getEliminadoEn());
    }
}
