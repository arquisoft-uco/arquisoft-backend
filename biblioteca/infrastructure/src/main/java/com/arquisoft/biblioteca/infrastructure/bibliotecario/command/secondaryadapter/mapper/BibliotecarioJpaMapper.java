package com.arquisoft.biblioteca.infrastructure.bibliotecario.command.secondaryadapter.mapper;

import com.arquisoft.biblioteca.application.bibliotecario.command.secondaryport.entity.BibliotecarioEntity;
import com.arquisoft.biblioteca.infrastructure.bibliotecario.command.secondaryadapter.entity.BibliotecarioJpaEntity;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;

import java.time.Instant;

public final class BibliotecarioJpaMapper {

    private BibliotecarioJpaMapper() {}

    public static BibliotecarioEntity toEntity(BibliotecarioJpaEntity jpaEntity) {
        return new BibliotecarioEntity(
                jpaEntity.getId(),
                jpaEntity.getIdentificador(),
                jpaEntity.getNombre(),
                jpaEntity.getEmail(),
                jpaEntity.getOcurridoEn(),
                UtilObjeto.aplicarPorDefecto(jpaEntity.getEliminadoEn(), UtilFecha.VACIO));
    }

    public static BibliotecarioJpaEntity toJpaEntity(BibliotecarioEntity entity) {
        return BibliotecarioJpaEntity.builder()
                .id(entity.id())
                .identificador(entity.identificador())
                .nombre(entity.nombre())
                .email(entity.email())
                .ocurridoEn(entity.ocurridoEn())
                .eliminadoEn(aColumna(entity.eliminadoEn()))
                .build();
    }

    public static Instant aColumna(Instant eliminadoEn) {
        return UtilFecha.VACIO.equals(eliminadoEn) ? null : eliminadoEn;
    }
}
