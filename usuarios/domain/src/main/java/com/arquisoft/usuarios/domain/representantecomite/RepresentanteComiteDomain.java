package com.arquisoft.usuarios.domain.representantecomite;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class RepresentanteComiteDomain {

    public static final RepresentanteComiteDomain VACIO =
            RepresentanteComiteDomain.reconstruir(UtilUUID.obtenerUUIDPorDefecto(), UtilFecha.VACIO);

    private UUID usuario;
    private Instant eliminadoEn;

    private RepresentanteComiteDomain() {}

    public static RepresentanteComiteDomain crear(UUID usuario) {
        var representanteComite = new RepresentanteComiteDomain();
        var result = new ValidationResult();

        representanteComite.setUsuario(usuario, result);
        representanteComite.eliminadoEn = UtilFecha.VACIO;

        result.lanzarSiTieneErrores();
        return representanteComite;
    }

    public static RepresentanteComiteDomain reconstruir(UUID usuario, Instant eliminadoEn) {
        var representanteComite = new RepresentanteComiteDomain();
        representanteComite.usuario = usuario;
        representanteComite.eliminadoEn = UtilObjeto.aplicarPorDefecto(eliminadoEn, UtilFecha.VACIO);
        return representanteComite;
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
                UsuariosFields.RepresentanteComite.USUARIO,
                UsuariosCodes.RepresentanteComite.USUARIO_REQUERIDO, result)) {
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
