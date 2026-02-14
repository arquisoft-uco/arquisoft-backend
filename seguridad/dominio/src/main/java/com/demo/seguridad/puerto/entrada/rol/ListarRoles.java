package com.demo.seguridad.puerto.entrada.rol;

import com.demo.seguridad.modelo.Aplicacion;
import com.demo.seguridad.modelo.Rol;
import com.demo.seguridad.modelo.Usuario;

import java.util.List;

public interface ListarRoles {
    List<Rol> listarRolesPorAplicacion(Aplicacion aplicacion);
    List<Rol> listarRolesPorUsuario(Usuario usuario);
}
