package com.arquisoft.seguridad.application.auth.command.secondaryport;

import java.util.List;

public interface UsuarioActualOutputPort {

    String obtenerIdUsuario();

    String obtenerCorreo();

    String obtenerNombreUsuario();

    boolean tieneRol(String rol);

    List<String> obtenerRoles();
}
