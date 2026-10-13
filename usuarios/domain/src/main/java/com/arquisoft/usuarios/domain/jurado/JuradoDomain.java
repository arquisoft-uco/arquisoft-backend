package com.arquisoft.usuarios.domain.jurado;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class JuradoDomain {

    public static final JuradoDomain VACIO =
            JuradoDomain.reconstruir(UtilUUID.obtenerUUIDPorDefecto(), UtilFecha.VACIO);

    private UUID usuario;
    private Instant eliminadoEn;

    private JuradoDomain() {}

    public static JuradoDomain crear(UUID usuario) {
        var jurado = new JuradoDomain();
        var result = new ValidationResult();

        jurado.setUsuario(usuario, result);
        jurado.setEliminadoEnInicial();

        result.lanzarSiTieneErrores();
        return jurado;
    }

    public static JuradoDomain reconstruir(UUID usuario, Instant eliminadoEn) {
        var jurado = new JuradoDomain();
        jurado.usuario = usuario;
        jurado.eliminadoEn = UtilObjeto.aplicarPorDefecto(eliminadoEn, UtilFecha.VACIO);
        return jurado;
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
                UsuariosFields.Jurado.USUARIO,
                UsuariosCodes.Jurado.USUARIO_REQUERIDO, result)) {
            return;
        }
        this.usuario = usuario;
    }

    private void setEliminadoEnInicial() {
        this.eliminadoEn = UtilFecha.VACIO;
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
