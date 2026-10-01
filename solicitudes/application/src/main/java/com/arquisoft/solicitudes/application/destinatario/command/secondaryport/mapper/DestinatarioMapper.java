package com.arquisoft.solicitudes.application.destinatario.command.secondaryport.mapper;

import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.entity.DestinatarioEntity;
import com.arquisoft.solicitudes.domain.destinatario.DestinatarioDomain;

public final class DestinatarioMapper {

    private DestinatarioMapper() {}

    public static DestinatarioEntity toEntity(DestinatarioDomain domain) {
        return new DestinatarioEntity(domain.getId(), domain.getUsuario());
    }

    public static DestinatarioDomain toDomain(DestinatarioEntity entity) {
        return DestinatarioDomain.reconstruir(entity.id(), entity.usuario());
    }
}
