package com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository.mapper;

import com.arquisoft.usuarios.application.usuario.query.readmodel.UsuarioReadModel;
import com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository.UsuarioJpaQueryEntity;

public final class UsuarioQueryMapper {

    private UsuarioQueryMapper() {}

    public static UsuarioReadModel toReadModel(UsuarioJpaQueryEntity entity) {
        return new UsuarioReadModel(
                entity.getId(),
                entity.getIdentificador(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getContacto(),
                entity.getEstado(),
                entity.isVigente(),
                entity.isEsEstudiante(),
                entity.isEsAsesor(),
                entity.isEsAsesorFicha(),
                entity.isEsCoordinador(),
                entity.isEsRepresentanteComite());
        // TODO HU233: pasar entity.isEsAdministrador()
        // TODO HU242: pasar entity.isEsBibliotecario()
        // TODO HU252: pasar entity.isEsJurado()
    }
}
