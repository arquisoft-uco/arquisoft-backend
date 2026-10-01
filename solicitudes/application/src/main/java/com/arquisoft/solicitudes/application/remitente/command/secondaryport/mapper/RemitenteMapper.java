package com.arquisoft.solicitudes.application.remitente.command.secondaryport.mapper;

import com.arquisoft.solicitudes.application.remitente.command.secondaryport.entity.RemitenteEntity;
import com.arquisoft.solicitudes.domain.remitente.RemitenteDomain;

public final class RemitenteMapper {

    private RemitenteMapper() {}

    public static RemitenteEntity toEntity(RemitenteDomain domain) {
        return new RemitenteEntity(domain.getId(), domain.getUsuario());
    }

    public static RemitenteDomain toDomain(RemitenteEntity entity) {
        return RemitenteDomain.reconstruir(entity.id(), entity.usuario());
    }
}
