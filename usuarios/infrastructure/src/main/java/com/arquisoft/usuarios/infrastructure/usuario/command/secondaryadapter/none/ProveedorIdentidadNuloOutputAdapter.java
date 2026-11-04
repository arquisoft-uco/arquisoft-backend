package com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.none;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.usuarios.ProveedorIdentidadKey;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.ModificacionIdentidadEntity;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.RegistroIdentidadEntity;
import com.arquisoft.usuarios.infrastructure.usuario.exception.ProveedorIdentidadUsuarioNoDisponibleException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "security.identity-provisioning.provider", havingValue = "none")
public class ProveedorIdentidadNuloOutputAdapter implements ProveedorIdentidadOutputPort {

    @Override
    public boolean existeEmail(String email) {
        throw noDisponible();
    }

    @Override
    public boolean existeEmailEnOtraIdentidad(String email, UUID usuario) {
        throw noDisponible();
    }

    @Override
    public void actualizar(ModificacionIdentidadEntity modificacion) {
        throw noDisponible();
    }

    @Override
    public void asignarRoles(UUID usuario, List<String> roles) {
        throw noDisponible();
    }

    @Override
    public UUID registrar(RegistroIdentidadEntity registro) {
        throw noDisponible();
    }

    @Override
    public void eliminar(UUID usuarioId) {
        throw noDisponible();
    }

    @Override
    public void revocarRol(UUID usuario, String rol) {
        throw noDisponible();
    }

    @Override
    public void cambiarHabilitacion(UUID usuario, boolean habilitado) {
        throw noDisponible();
    }

    private static ProveedorIdentidadUsuarioNoDisponibleException noDisponible() {
        return new ProveedorIdentidadUsuarioNoDisponibleException(
                Mensajes.obtener(ProveedorIdentidadKey.ERROR_NO_DISPONIBLE));
    }
}
