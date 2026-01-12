package com.demo.seguridad.puerto.entrada.usuario;

import com.demo.seguridad.modelo.Organizacion;
import com.demo.seguridad.modelo.Usuario;

import java.util.List;

public interface ListarUsuarios {
    List<Usuario> listarUsuariosPorOrganizacionYNombre(Usuario usuario);
}
