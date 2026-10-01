package com.arquisoft.usuarios.domain.asesor;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class AsesorDomain {

    public static final AsesorDomain VACIO =
            AsesorDomain.reconstruir(UtilUUID.obtenerUUIDPorDefecto(), UtilFecha.VACIO);

    private UUID usuario;
    private Instant eliminadoEn;

    private AsesorDomain() {}

    public static AsesorDomain crear(UUID usuario) {
        var asesor = new AsesorDomain();
        var result = new ValidationResult();

        asesor.setUsuario(usuario, result);
        asesor.eliminadoEn = UtilFecha.VACIO;

        result.lanzarSiTieneErrores();
        return asesor;
    }

    public static AsesorDomain reconstruir(UUID usuario, Instant eliminadoEn) {
        var asesor = new AsesorDomain();
        asesor.usuario = usuario;
        asesor.eliminadoEn = UtilObjeto.aplicarPorDefecto(eliminadoEn, UtilFecha.VACIO);
        return asesor;
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
                UsuariosFields.Asesor.USUARIO,
                UsuariosCodes.Asesor.USUARIO_REQUERIDO, result)) {
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
