package com.arquisoft.usuarios.domain.administrador;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class AdministradorDomain {

    public static final AdministradorDomain VACIO =
            AdministradorDomain.reconstruir(UtilUUID.obtenerUUIDPorDefecto(), UtilFecha.VACIO);

    private UUID usuario;
    private Instant eliminadoEn;

    private AdministradorDomain() {}

    public static AdministradorDomain crear(UUID usuario) {
        var administrador = new AdministradorDomain();
        var result = new ValidationResult();

        administrador.setUsuario(usuario, result);
        administrador.eliminadoEn = UtilFecha.VACIO;

        result.lanzarSiTieneErrores();
        return administrador;
    }

    public static AdministradorDomain reconstruir(UUID usuario, Instant eliminadoEn) {
        var administrador = new AdministradorDomain();
        administrador.usuario = usuario;
        administrador.eliminadoEn = UtilObjeto.aplicarPorDefecto(eliminadoEn, UtilFecha.VACIO);
        return administrador;
    }

    public void reactivar() {
        this.eliminadoEn = UtilFecha.VACIO;
    }

    public boolean estaEliminado() {
        return !UtilFecha.VACIO.equals(eliminadoEn);
    }

    private void setUsuario(UUID usuario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(usuario,
                UsuariosFields.Administrador.USUARIO,
                UsuariosCodes.Administrador.USUARIO_REQUERIDO, result)) {
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
