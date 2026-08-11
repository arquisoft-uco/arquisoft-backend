package com.arquisoft.seguridad.domain.auth.secondaryport;

import java.util.List;

public interface UsuarioActualOutputPort {

    String obtenerIdUsuario();

    String obtenerCorreo();

    String obtenerNombreUsuario();

    boolean tieneRol(String rol);

    List<String> obtenerRoles();
}
