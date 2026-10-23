package com.arquisoft.usuarios.application.usuario.command.secondaryport.entity;

import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;

public record RegistroIdentidadEntity(
        String email,
        String nombres,
        String apellidos,
        List<String> roles) {

    public RegistroIdentidadEntity {
        roles = UtilColeccion.aplicarPorDefecto(roles);
    }
}
