package com.arquisoft.usuarios.application.usuario.command.secondaryport;

import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.ModificacionIdentidadEntity;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.RegistroIdentidadEntity;

import java.util.List;
import java.util.UUID;

public interface ProveedorIdentidadOutputPort {

    boolean existeEmail(String email);

    boolean existeEmailEnOtraIdentidad(String email, UUID usuario);

    void actualizar(ModificacionIdentidadEntity modificacion);

    void asignarRoles(UUID usuario, List<String> roles);

    UUID registrar(RegistroIdentidadEntity registro);

    void eliminar(UUID usuarioId);

    void revocarRol(UUID usuario, String rol);

    void cambiarHabilitacion(UUID usuario, boolean habilitado);
}
