package com.arquisoft.seguridad.application.autenticacion.command.secondaryport;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioPorCorreoOutputPort {

    Optional<UUID> obtenerIdPorCorreo(String correo);
}
