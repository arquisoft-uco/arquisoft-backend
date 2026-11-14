package com.arquisoft.entregables.domain.proyectogrado;

import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.time.Instant;
import java.util.UUID;

public final class ProyectoGradoDomain {

    public static final ProyectoGradoDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, EstadoProyecto.VACIO, UtilFecha.VACIO);

    private UUID id;
    private String nombre;
    private EstadoProyecto estadoProyecto;
    private Instant ocurridoEn;

    private ProyectoGradoDomain() {}

    public static ProyectoGradoDomain crear(UUID id, String nombre) {
        var proyecto = new ProyectoGradoDomain();
        var result = new ValidationResult();
        proyecto.setId(id, result);
        proyecto.setNombre(nombre, result);
        proyecto.setEstadoProyectoInicial();
        proyecto.setOcurridoEnInicial();
        result.lanzarSiTieneErrores();
        return proyecto;
    }

    public static ProyectoGradoDomain reconstruir(
            UUID id, String nombre, EstadoProyecto estadoProyecto, Instant ocurridoEn) {
        var proyecto = new ProyectoGradoDomain();
        proyecto.id = id;
        proyecto.nombre = nombre;
        proyecto.estadoProyecto = estadoProyecto;
        proyecto.ocurridoEn = UtilObjeto.aplicarPorDefecto(ocurridoEn, UtilFecha.VACIO);
        return proyecto;
    }

    private void setId(UUID id, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(id,
                EntregablesFields.ProyectoGrado.ID,
                EntregablesCodes.ProyectoGrado.ID_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setNombre(String nombre, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(nombre,
                EntregablesFields.ProyectoGrado.NOMBRE,
                EntregablesCodes.ProyectoGrado.NOMBRE_REQUERIDO, result)) {
            return;
        }
        this.nombre = nombre;
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

    public EstadoProyecto getEstadoProyecto() {
        return estadoProyecto;
    }

    public Instant getOcurridoEn() {
        return ocurridoEn;
    }

    public boolean estaCerrado() {
        return estadoProyecto == EstadoProyecto.FINALIZADO || estadoProyecto == EstadoProyecto.CANCELADO;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
