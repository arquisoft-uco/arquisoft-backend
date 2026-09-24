package com.arquisoft.usuarios.application.usuario.command.secondaryport.mapper;

import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.UsuarioEntity;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

public final class UsuarioMapper {

    private UsuarioMapper() {}

    public static UsuarioDomain toDomain(UsuarioEntity entity) {
        return UsuarioDomain.reconstruir(
                entity.id(),
                entity.identificador(),
                entity.nombre(),
                entity.email(),
                entity.contacto(),
                EstadoUsuario.desde(entity.estado()));
    }

    public static UsuarioEntity toEntity(UsuarioDomain domain) {
        return new UsuarioEntity(
                domain.getId(),
                domain.getIdentificador(),
                domain.getNombre(),
                domain.getEmail(),
                domain.getContacto(),
                domain.getEstado().getId());
    }
}
