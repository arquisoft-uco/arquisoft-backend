package com.demo.seguridad.puerto.entrada.acceso;

import com.demo.seguridad.modelo.Usuario;

public interface CambiarClave {
    void cambiarClave(String clave, Usuario usuario);
}
