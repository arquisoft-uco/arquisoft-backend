package com.arquisoft.fichas.domain.estadofichaperfil;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.time.Instant;
import java.util.UUID;

public final class EstadoFichaPerfilDomain {

    public static final EstadoFichaPerfilDomain VACIO = new EstadoFichaPerfilDomain(
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            EstadoFicha.VACIO,
            UtilFecha.VACIO);

    private UUID id;
    private UUID fichaPerfil;
    private EstadoFicha estadoFicha;
    private Instant fechaActualizacion;

    private EstadoFichaPerfilDomain() {}

    private EstadoFichaPerfilDomain(UUID id, UUID fichaPerfil, EstadoFicha estadoFicha, Instant fechaActualizacion) {
        this.id = id;
        this.fichaPerfil = fichaPerfil;
        this.estadoFicha = estadoFicha;
        this.fechaActualizacion = fechaActualizacion;
    }

    public static EstadoFichaPerfilDomain crear(UUID fichaPerfilId) {
        var aggregate = new EstadoFichaPerfilDomain();
        var result = new ValidationResult();

        aggregate.setId();
        aggregate.setFichaPerfil(fichaPerfilId, result);
        aggregate.setEstadoFichaInicial();
        aggregate.setFechaActualizacion();

        result.lanzarSiTieneErrores();
        return aggregate;
    }

    public static EstadoFichaPerfilDomain reconstruir(UUID id, UUID fichaPerfilId,
                                                         EstadoFicha estadoFicha,
                                                         Instant fechaActualizacion) {
        return new EstadoFichaPerfilDomain(id, fichaPerfilId, estadoFicha, fechaActualizacion);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setFichaPerfil(UUID fichaPerfil, ValidationResult result) {
        if (!DomainValidator.noNulo(fichaPerfil,
                FichasFields.EstadoFichaPerfil.FICHA_PERFIL,
                FichasCodes.EstadoFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result)) {
            return;
        }
        this.fichaPerfil = fichaPerfil;
    }

    private void setEstadoFichaInicial() {
        this.estadoFicha = EstadoFicha.EN_CONSTRUCCION;
    }

    private void setFechaActualizacion() {
        this.fechaActualizacion = UtilFecha.generarInstanteActual();
    }

    public UUID getId() {
        return id;
    }

    public UUID getFichaPerfil() {
        return fichaPerfil;
    }

    public EstadoFicha getEstadoFicha() {
        return estadoFicha;
    }

    public Instant getFechaActualizacion() {
        return fechaActualizacion;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
