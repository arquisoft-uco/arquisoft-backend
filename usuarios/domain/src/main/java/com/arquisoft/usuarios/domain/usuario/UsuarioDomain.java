package com.arquisoft.usuarios.domain.usuario;

import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;

import java.util.UUID;

public final class UsuarioDomain {

    private UUID id;
    private String identificador;
    private String nombre;
    private String email;
    private String contacto;
    private EstadoUsuario estado;

    private UsuarioDomain() {}

    public static UsuarioDomain crear(UUID id, RegistroUsuarioDomain registro) {
        var usuario = new UsuarioDomain();

        usuario.setId(id);
        usuario.setIdentificador(registro.getIdentificador());
        usuario.setNombre(registro.getNombre());
        usuario.setEmail(registro.getEmail());
        usuario.setContacto(registro.getContacto());
        usuario.setEstadoUsuarioActivo();

        return usuario;
    }

    public static UsuarioDomain reconstruir(UUID id, String identificador, String nombre, String email,
                                            String contacto, EstadoUsuario estado) {
        var usuario = new UsuarioDomain();

        usuario.setId(id);
        usuario.setIdentificador(identificador);
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setContacto(contacto);
        usuario.setEstadoUsuario(estado);

        return usuario;
    }

    private void setId(UUID id) {
        this.id = id;
    }

    private void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    private void setNombre(String nombre) {
        this.nombre = nombre;
    }

    private void setEmail(String email) {
        this.email = email;
    }

    private void setContacto(String contacto) {
        this.contacto = contacto;
    }

    private void setEstadoUsuarioActivo() {
        this.estado = EstadoUsuario.ACTIVO;
    }

    private void setEstadoUsuario(EstadoUsuario estado) {
        this.estado = estado;
    }

    public UUID getId() {
        return id;
    }

    public String getIdentificador() {
        return identificador;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getContacto() {
        return contacto;
    }

    public EstadoUsuario getEstado() {
        return estado;
    }
}
