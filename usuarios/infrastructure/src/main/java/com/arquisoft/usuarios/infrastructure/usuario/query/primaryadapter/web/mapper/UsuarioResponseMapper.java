package com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.usuario.query.readmodel.UsuarioReadModel;
import com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web.dto.UsuarioResponseDTO;

public final class UsuarioResponseMapper {

    private UsuarioResponseMapper() {}

    public static UsuarioResponseDTO toResponse(UsuarioReadModel readModel) {
        return new UsuarioResponseDTO(
                readModel.id(),
                readModel.identificador(),
                readModel.nombre(),
                readModel.email(),
                readModel.contacto(),
                readModel.estado(),
                readModel.vigente(),
                readModel.esEstudiante(),
                readModel.esAsesor(),
                readModel.esAsesorFicha(),
                readModel.esCoordinador(),
                readModel.esRepresentanteComite(),
                readModel.esAdministrador());
        // TODO HU242: pasar readModel.esBibliotecario()
        // TODO HU252: pasar readModel.esJurado()
    }
}
