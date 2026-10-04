package com.arquisoft.usuarios.infrastructure.administrador.command.secondaryadapter.mapper;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.usuarios.application.administrador.command.secondaryport.entity.AdministradorEntity;
import com.arquisoft.usuarios.infrastructure.administrador.command.secondaryadapter.entity.AdministradorJpaEntity;

import java.time.Instant;

public final class AdministradorJpaMapper {

    private AdministradorJpaMapper() {}

    public static AdministradorEntity toEntity(AdministradorJpaEntity jpaEntity) {
        return new AdministradorEntity(
                jpaEntity.getUsuarioId(),
                UtilObjeto.aplicarPorDefecto(jpaEntity.getEliminadoEn(), UtilFecha.VACIO));
    }

    public static AdministradorJpaEntity toJpaEntity(AdministradorEntity entity) {
        return AdministradorJpaEntity.builder()
                .usuarioId(entity.usuario())
                .eliminadoEn(aColumna(entity.eliminadoEn()))
                .build();
    }

    public static Instant aColumna(Instant eliminadoEn) {
        return UtilFecha.VACIO.equals(eliminadoEn) ? null : eliminadoEn;
    }
}
