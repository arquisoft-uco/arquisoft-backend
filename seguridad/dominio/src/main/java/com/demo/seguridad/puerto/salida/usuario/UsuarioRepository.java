package com.demo.seguridad.puerto.salida.usuario;

import com.demo.seguridad.modelo.Usuario;

import java.util.List;

public interface UsuarioRepository {
    Integer crearUsuario(Usuario usuario);
    List<Usuario> consultarUsuariosPorOrganizacionYNombre(Usuario usuario);
    void editarUsuario(Usuario usuario);
    Usuario consultarUsuarioPorId(Integer id);
}
