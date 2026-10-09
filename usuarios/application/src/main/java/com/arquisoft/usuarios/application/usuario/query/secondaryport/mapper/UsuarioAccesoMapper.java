package com.arquisoft.usuarios.application.usuario.query.secondaryport.mapper;

import com.arquisoft.usuarios.application.usuario.query.secondaryport.entity.UsuarioAccesoEntity;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

public final class UsuarioAccesoMapper {

    private UsuarioAccesoMapper() {}

    public static UsuarioDomain toDomain(UsuarioAccesoEntity entity) {
        return UsuarioDomain.reconstruir(
                entity.id(),
                entity.identificador(),
                entity.nombre(),
                entity.email(),
                entity.contacto(),
                EstadoUsuario.desde(entity.estado()),
                entity.eliminadoEn());
    }
}
