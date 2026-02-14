package com.demo.seguridad.casouso.acceso;

import com.demo.seguridad.modelo.CredencialRegistro;
import com.demo.seguridad.puerto.entrada.acceso.RegistrarAdministrador;
import com.demo.seguridad.puerto.salida.credencial.CredencialRepository;
import com.demo.seguridad.puerto.salida.usuario.UsuarioRepository;

public class RegistrarAdministradorImpl implements RegistrarAdministrador {
    private final UsuarioRepository usuarioRepository;
    private final CredencialRepository credencialRepository;

    public RegistrarAdministradorImpl(CredencialRepository credencialRepository, UsuarioRepository usuarioRepository) {
        this.credencialRepository = credencialRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void registrarAdministrador(CredencialRegistro credencialRegistro) {
        Integer idUsuario = usuarioRepository.crearUsuario(credencialRegistro.getUsuario());
        credencialRegistro.toBuilder().ejecutarValidaciones().construir();
    }
}
