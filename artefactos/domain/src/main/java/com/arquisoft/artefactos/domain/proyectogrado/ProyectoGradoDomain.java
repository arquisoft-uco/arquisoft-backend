package com.arquisoft.artefactos.domain.proyectogrado;

import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyecto;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.message.constant.ArtefactosLimits;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.time.Instant;
import java.util.UUID;

public final class ProyectoGradoDomain {

    public static final ProyectoGradoDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, UtilFecha.VACIO, EstadoProyecto.VACIO,
            UtilFecha.VACIO);

    private UUID id;
    private String nombre;
    private Instant entregadoEn;
    private EstadoProyecto estadoProyecto;
    private Instant ocurridoEn;

    private ProyectoGradoDomain() {}

    public static ProyectoGradoDomain crear(UUID id, String nombre) {
        var proyecto = new ProyectoGradoDomain();
        var result = new ValidationResult();
        proyecto.setId(id, result);
        proyecto.setNombre(nombre, result);
        proyecto.setEntregadoEn();
        proyecto.setEstadoProyectoInicial();
        proyecto.setOcurridoEnInicial();
        result.lanzarSiTieneErrores();
        return proyecto;
    }

    public static ProyectoGradoDomain reconstruir(UUID id, String nombre, Instant entregadoEn,
                                                  EstadoProyecto estadoProyecto, Instant ocurridoEn) {
        var proyecto = new ProyectoGradoDomain();
        proyecto.id = id;
        proyecto.nombre = nombre;
        proyecto.entregadoEn = UtilObjeto.aplicarPorDefecto(entregadoEn, UtilFecha.VACIO);
        proyecto.estadoProyecto = estadoProyecto;
        proyecto.ocurridoEn = UtilObjeto.aplicarPorDefecto(ocurridoEn, UtilFecha.VACIO);
        return proyecto;
    }

    private void setId(UUID id, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(id,
                ArtefactosFields.ProyectoGrado.ID,
                ArtefactosCodes.ProyectoGrado.ID_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setNombre(String nombre, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(nombre);
        if (!ValidatorTexto.noEnBlanco(recortado,
                ArtefactosFields.ProyectoGrado.NOMBRE,
                ArtefactosCodes.ProyectoGrado.NOMBRE_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado,
                ArtefactosLimits.ProyectoGrado.NOMBRE_MAX,
                ArtefactosFields.ProyectoGrado.NOMBRE,
                ArtefactosCodes.ProyectoGrado.NOMBRE_DEMASIADO_LARGO, result)) {
            return;
        }
        this.nombre = recortado;
    }

    private void setEntregadoEn() {
        this.entregadoEn = UtilFecha.VACIO;
    }

    private void setEstadoProyectoInicial() {
        this.estadoProyecto = EstadoProyecto.EN_PROCESO;
    }

    private void setOcurridoEnInicial() {
        this.ocurridoEn = UtilFecha.VACIO;
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Instant getEntregadoEn() {
        return entregadoEn;
    }

    public EstadoProyecto getEstadoProyecto() {
        return estadoProyecto;
    }

    public Instant getOcurridoEn() {
        return ocurridoEn;
    }

    public boolean estaEntregado() {
        return !UtilFecha.VACIO.equals(entregadoEn);
    }

    public boolean estaFinalizado() {
        return estadoProyecto == EstadoProyecto.FINALIZADO;
    }

    public boolean estaCancelado() {
        return estadoProyecto == EstadoProyecto.CANCELADO;
    }

    public boolean estaConcluido() {
        return estaFinalizado() || estaCancelado();
    }

    public boolean estaCongelado() {
        return estaEntregado() || estaConcluido();
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
