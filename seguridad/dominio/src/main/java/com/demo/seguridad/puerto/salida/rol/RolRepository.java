package com.demo.seguridad.puerto.salida.rol;

import com.demo.seguridad.modelo.Aplicacion;
import com.demo.seguridad.modelo.Rol;
import com.demo.seguridad.modelo.Usuario;

import java.util.List;

public interface RolRepository {
    Integer crearRol(Rol rol);
    List<Rol> consultarRolesPorAplicacion(Aplicacion aplicacion);
    List<Rol> consultarRolesPorUsuario(Usuario usuario);
    void editarRol(Rol rol);
}
