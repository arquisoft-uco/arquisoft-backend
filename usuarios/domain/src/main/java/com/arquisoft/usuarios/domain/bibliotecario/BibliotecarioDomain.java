package com.arquisoft.usuarios.domain.bibliotecario;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class BibliotecarioDomain {

    public static final BibliotecarioDomain VACIO =
            BibliotecarioDomain.reconstruir(UtilUUID.obtenerUUIDPorDefecto(), UtilFecha.VACIO);

    private UUID usuario;
    private Instant eliminadoEn;

    private BibliotecarioDomain() {}

    public static BibliotecarioDomain crear(UUID usuario) {
        var bibliotecario = new BibliotecarioDomain();
        var result = new ValidationResult();

        bibliotecario.setUsuario(usuario, result);
        bibliotecario.eliminadoEn = UtilFecha.VACIO;

        result.lanzarSiTieneErrores();
        return bibliotecario;
    }

    public static BibliotecarioDomain reconstruir(UUID usuario, Instant eliminadoEn) {
        var bibliotecario = new BibliotecarioDomain();
        bibliotecario.usuario = usuario;
        bibliotecario.eliminadoEn = UtilObjeto.aplicarPorDefecto(eliminadoEn, UtilFecha.VACIO);
        return bibliotecario;
    }

    public void remover(Instant instante) {
        this.eliminadoEn = instante;
    }

    public void reactivar() {
        this.eliminadoEn = UtilFecha.VACIO;
    }

    public boolean estaEliminado() {
        return !UtilFecha.VACIO.equals(eliminadoEn);
    }

    private void setUsuario(UUID usuario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(usuario,
                UsuariosFields.Bibliotecario.USUARIO,
                UsuariosCodes.Bibliotecario.USUARIO_REQUERIDO, result)) {
            return;
        }
        this.usuario = usuario;
    }

    public UUID getUsuario() {
        return usuario;
    }

    public Instant getEliminadoEn() {
        return eliminadoEn;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
