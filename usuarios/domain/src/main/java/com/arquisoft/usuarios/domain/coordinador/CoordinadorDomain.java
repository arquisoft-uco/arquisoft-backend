package com.arquisoft.usuarios.domain.coordinador;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class CoordinadorDomain {

    public static final CoordinadorDomain VACIO =
            CoordinadorDomain.reconstruir(UtilUUID.obtenerUUIDPorDefecto(), UtilFecha.VACIO);

    private UUID usuario;
    private Instant eliminadoEn;

    private CoordinadorDomain() {}

    public static CoordinadorDomain crear(UUID usuario) {
        var coordinador = new CoordinadorDomain();
        var result = new ValidationResult();

        coordinador.setUsuario(usuario, result);
        coordinador.eliminadoEn = UtilFecha.VACIO;

        result.lanzarSiTieneErrores();
        return coordinador;
    }

    public static CoordinadorDomain reconstruir(UUID usuario, Instant eliminadoEn) {
        var coordinador = new CoordinadorDomain();
        coordinador.usuario = usuario;
        coordinador.eliminadoEn = UtilObjeto.aplicarPorDefecto(eliminadoEn, UtilFecha.VACIO);
        return coordinador;
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
                UsuariosFields.Coordinador.USUARIO,
                UsuariosCodes.Coordinador.USUARIO_REQUERIDO, result)) {
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
