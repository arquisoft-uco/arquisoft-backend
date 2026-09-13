package com.arquisoft.usuarios.application.usuario.command.secondaryport;

import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.RegistroIdentidadEntity;

import java.util.UUID;

public interface ProveedorIdentidadOutputPort {

    boolean existeEmail(String email);

    UUID registrar(RegistroIdentidadEntity registro);

    void eliminar(UUID usuarioId);
}
