package com.arquisoft.usuarios.infrastructure.bibliotecario.query.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.bibliotecario.query.readmodel.BibliotecarioReadModel;
import com.arquisoft.usuarios.infrastructure.bibliotecario.query.primaryadapter.web.dto.BibliotecarioResponseDTO;

public final class BibliotecarioResponseMapper {

    private BibliotecarioResponseMapper() {}

    public static BibliotecarioResponseDTO toResponse(BibliotecarioReadModel readModel) {
        return new BibliotecarioResponseDTO(
                readModel.id(),
                readModel.identificador(),
                readModel.nombre(),
                readModel.email(),
                readModel.contacto(),
                readModel.estado(),
                readModel.vigente());
    }
}
