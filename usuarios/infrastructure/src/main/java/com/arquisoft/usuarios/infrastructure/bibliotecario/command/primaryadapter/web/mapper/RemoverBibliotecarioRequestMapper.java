package com.arquisoft.usuarios.infrastructure.bibliotecario.command.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.bibliotecario.command.primaryport.model.RemoverBibliotecarioCommand;

import java.util.UUID;

public final class RemoverBibliotecarioRequestMapper {

    private RemoverBibliotecarioRequestMapper() {}

    public static RemoverBibliotecarioCommand toCommand(UUID usuarioId) {
        return RemoverBibliotecarioCommand.crear(usuarioId);
    }
}
