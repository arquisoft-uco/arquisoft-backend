package com.arquisoft.usuarios.domain.asesorficha;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class AsesorFichaDomain {

    public static final AsesorFichaDomain VACIO =
            AsesorFichaDomain.reconstruir(UtilUUID.obtenerUUIDPorDefecto(), UtilFecha.VACIO);

    private UUID usuario;
    private Instant eliminadoEn;

    private AsesorFichaDomain() {}

    public static AsesorFichaDomain crear(UUID usuario) {
        var asesorFicha = new AsesorFichaDomain();
        var result = new ValidationResult();

        asesorFicha.setUsuario(usuario, result);
        asesorFicha.eliminadoEn = UtilFecha.VACIO;

        result.lanzarSiTieneErrores();
        return asesorFicha;
    }

    public static AsesorFichaDomain reconstruir(UUID usuario, Instant eliminadoEn) {
        var asesorFicha = new AsesorFichaDomain();
        asesorFicha.usuario = usuario;
        asesorFicha.eliminadoEn = UtilObjeto.aplicarPorDefecto(eliminadoEn, UtilFecha.VACIO);
        return asesorFicha;
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
                UsuariosFields.AsesorFicha.USUARIO,
                UsuariosCodes.AsesorFicha.USUARIO_REQUERIDO, result)) {
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
