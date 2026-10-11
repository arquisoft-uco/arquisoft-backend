package com.arquisoft.fichas.domain.estadofichaperfil;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ResumenEvaluacionesFicha;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

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

    public static EstadoFichaPerfilDomain crearPorDecision(UUID fichaPerfil, boolean acepta,
                                                           ResumenEvaluacionesFicha resumen) {
        var aggregate = new EstadoFichaPerfilDomain();

        aggregate.setId();
        aggregate.setFichaPerfil(fichaPerfil);
        aggregate.setEstadoPorDecision(acepta, resumen);
        aggregate.setFechaActualizacion();

        return aggregate;
    }

    public static EstadoFichaPerfilDomain crearPorAsesor(UUID fichaPerfil, String estadoFicha) {
        var aggregate = new EstadoFichaPerfilDomain();
        var result = new ValidationResult();

        aggregate.setId();
        aggregate.setFichaPerfil(fichaPerfil, result);
        aggregate.setEstadoFichaAsignadoPorAsesor(estadoFicha, result);
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
        if (!ValidatorObjeto.noNulo(fichaPerfil,
                FichasFields.EstadoFichaPerfil.FICHA_PERFIL,
                FichasCodes.EstadoFichaPerfil.FICHA_PERFIL_ID_REQUERIDO, result)) {
            return;
        }
        this.fichaPerfil = fichaPerfil;
    }

    private void setFichaPerfil(UUID fichaPerfil) {
        this.fichaPerfil = fichaPerfil;
    }

    private void setEstadoFichaInicial() {
        this.estadoFicha = EstadoFicha.EN_CONSTRUCCION;
    }

    private void setEstadoFichaAsignadoPorAsesor(String estadoFicha, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(estadoFicha);
        if (!ValidatorTexto.noEnBlanco(recortado,
                FichasFields.EstadoFichaPerfil.ESTADO_FICHA,
                FichasCodes.EstadoFichaPerfil.ESTADO_FICHA_REQUERIDO, result)) {
            return;
        }
        if (!EstadoFicha.esValido(recortado)) {
            result.agregarError(
                    FichasFields.EstadoFichaPerfil.ESTADO_FICHA,
                    FichasCodes.EstadoFichaPerfil.ESTADO_FICHA_INVALIDO,
                    Mensajes.formatear(EstadoFichaPerfilKey.ERROR_ESTADO_FICHA_INVALIDO, recortado));
            return;
        }
        var estado = EstadoFicha.desde(recortado);
        if (!estado.esAsignablePorAsesor()) {
            result.agregarError(
                    FichasFields.EstadoFichaPerfil.ESTADO_FICHA,
                    FichasCodes.EstadoFichaPerfil.ESTADO_NO_ASIGNABLE_POR_ASESOR,
                    Mensajes.formatear(EstadoFichaPerfilKey.ERROR_ESTADO_NO_ASIGNABLE_POR_ASESOR, estado.getNombre()));
            return;
        }
        this.estadoFicha = estado;
    }

    private void setEstadoPorDecision(boolean acepta, ResumenEvaluacionesFicha resumen) {
        if (!acepta) {
            this.estadoFicha = EstadoFicha.NO_APROBADA;
            return;
        }
        this.estadoFicha = resumen.tieneObservacionesVigentes()
                ? EstadoFicha.APROBADA_CON_OBSERVACIONES
                : EstadoFicha.APROBADA;
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
