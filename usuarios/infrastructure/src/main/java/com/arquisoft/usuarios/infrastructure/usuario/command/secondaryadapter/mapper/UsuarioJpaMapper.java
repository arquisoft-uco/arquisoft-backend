package com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.mapper;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.UsuarioEntity;
import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;

import java.time.Instant;

public final class UsuarioJpaMapper {

    private UsuarioJpaMapper() {}

    public static UsuarioEntity toEntity(UsuarioJpaEntity jpaEntity) {
        return new UsuarioEntity(
                jpaEntity.getId(),
                jpaEntity.getIdentificador(),
                jpaEntity.getNombre(),
                jpaEntity.getEmail(),
                jpaEntity.getContacto(),
                jpaEntity.getEstadoId(),
                UtilObjeto.aplicarPorDefecto(jpaEntity.getEliminadoEn(), UtilFecha.VACIO));
    }

    public static UsuarioJpaEntity toJpaEntity(UsuarioEntity entity) {
        return UsuarioJpaEntity.builder()
                .id(entity.id())
                .identificador(entity.identificador())
                .nombre(entity.nombre())
                .email(entity.email())
                .contacto(entity.contacto())
                .estadoId(entity.estado())
                .eliminadoEn(aColumna(entity.eliminadoEn()))
                .build();
    }

    public static Instant aColumna(Instant eliminadoEn) {
        return UtilFecha.VACIO.equals(eliminadoEn) ? null : eliminadoEn;
    }
}
