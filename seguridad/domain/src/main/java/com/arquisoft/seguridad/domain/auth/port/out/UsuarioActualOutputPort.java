package com.arquisoft.seguridad.domain.auth.port.out;

import java.util.List;

public interface UsuarioActualOutputPort {

    String obtenerIdUsuario();

    String obtenerCorreo();

    String obtenerNombreUsuario();

    boolean tieneRol(String rol);

    List<String> obtenerRoles();
}
