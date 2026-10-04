package com.arquisoft.usuarios.infrastructure.bibliotecario.command.secondaryadapter.mapper;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.usuarios.application.bibliotecario.command.secondaryport.entity.BibliotecarioEntity;
import com.arquisoft.usuarios.infrastructure.bibliotecario.command.secondaryadapter.entity.BibliotecarioJpaEntity;

import java.time.Instant;

public final class BibliotecarioJpaMapper {

    private BibliotecarioJpaMapper() {}

    public static BibliotecarioEntity toEntity(BibliotecarioJpaEntity jpaEntity) {
        return new BibliotecarioEntity(
                jpaEntity.getUsuarioId(),
                UtilObjeto.aplicarPorDefecto(jpaEntity.getEliminadoEn(), UtilFecha.VACIO));
    }

    public static BibliotecarioJpaEntity toJpaEntity(BibliotecarioEntity entity) {
        return BibliotecarioJpaEntity.builder()
                .usuarioId(entity.usuario())
                .eliminadoEn(aColumna(entity.eliminadoEn()))
                .build();
    }

    public static Instant aColumna(Instant eliminadoEn) {
        return UtilFecha.VACIO.equals(eliminadoEn) ? null : eliminadoEn;
    }
}
