package com.arquisoft.fichas.application.usuario.command.port.in;

import java.util.UUID;

public interface RegistrarUsuarioInputPort {

    void registrar(UUID usuarioId, String email, String rol);
}
