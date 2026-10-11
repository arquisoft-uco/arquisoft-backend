package com.arquisoft.usuarios.application.administrador.command.secondaryport.mapper;

import com.arquisoft.usuarios.application.administrador.command.secondaryport.entity.AdministradorEntity;
import com.arquisoft.usuarios.domain.administrador.AdministradorDomain;

public final class AdministradorMapper {

    private AdministradorMapper() {}

    public static AdministradorDomain toDomain(AdministradorEntity entity) {
        return AdministradorDomain.reconstruir(entity.usuario(), entity.eliminadoEn());
    }

    public static AdministradorEntity toEntity(AdministradorDomain administrador) {
        return new AdministradorEntity(administrador.getUsuario(), administrador.getEliminadoEn());
    }
}
