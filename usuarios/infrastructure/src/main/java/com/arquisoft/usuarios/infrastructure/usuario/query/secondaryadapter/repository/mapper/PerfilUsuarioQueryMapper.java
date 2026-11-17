package com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository.mapper;

import com.arquisoft.shared.message.constant.UsuariosRoles;
import com.arquisoft.usuarios.application.usuario.query.readmodel.EstadoPerfilReadModel;
import com.arquisoft.usuarios.application.usuario.query.readmodel.PerfilUsuarioReadModel;
import com.arquisoft.usuarios.application.usuario.query.readmodel.RolPerfilReadModel;
import com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository.PerfilUsuarioJpaQueryEntity;

import java.util.ArrayList;
import java.util.List;

public final class PerfilUsuarioQueryMapper {

    private PerfilUsuarioQueryMapper() {}

    public static PerfilUsuarioReadModel toReadModel(PerfilUsuarioJpaQueryEntity entity) {
        return new PerfilUsuarioReadModel(
                entity.getId(),
                entity.getIdentificador(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getContacto(),
                new EstadoPerfilReadModel(entity.getEstado(), entity.getEstadoNombre()),
                toRoles(entity));
    }

    private static List<RolPerfilReadModel> toRoles(PerfilUsuarioJpaQueryEntity entity) {
        var roles = new ArrayList<RolPerfilReadModel>();
        agregarSi(entity.isEsEstudiante(), UsuariosRoles.ESTUDIANTE, roles);
        agregarSi(entity.isEsAsesor(), UsuariosRoles.ASESOR, roles);
        agregarSi(entity.isEsAsesorFicha(), UsuariosRoles.ASESOR_FICHA, roles);
        agregarSi(entity.isEsCoordinador(), UsuariosRoles.COORDINADOR, roles);
        agregarSi(entity.isEsRepresentanteComite(), UsuariosRoles.REPRESENTANTE_COMITE, roles);
        agregarSi(entity.isEsAdministrador(), UsuariosRoles.ADMINISTRADOR, roles);
        agregarSi(entity.isEsBibliotecario(), UsuariosRoles.BIBLIOTECARIO, roles);
        agregarSi(entity.isEsJurado(), UsuariosRoles.JURADO, roles);
        return List.copyOf(roles);
    }

    private static void agregarSi(boolean vigente, String rol, ArrayList<RolPerfilReadModel> roles) {
        if (vigente) {
            roles.add(new RolPerfilReadModel(rol, UsuariosRoles.etiqueta(rol)));
        }
    }
}
