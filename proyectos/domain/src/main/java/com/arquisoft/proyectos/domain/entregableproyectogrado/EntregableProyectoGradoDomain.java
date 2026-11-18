package com.arquisoft.proyectos.domain.entregableproyectogrado;

import com.arquisoft.proyectos.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.message.key.proyectos.EstadoEntregableKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilNumero;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.time.Instant;
import java.util.UUID;

public final class EntregableProyectoGradoDomain {

    public static final EntregableProyectoGradoDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilNumero.CERO,
            EstadoEntregable.VACIO,
            UtilFecha.VACIO);

    private UUID id;
    private UUID proyectoGrado;
    private int version;
    private EstadoEntregable estadoEntregable;
    private Instant ocurridoEn;

    private EntregableProyectoGradoDomain() {}

    public static EntregableProyectoGradoDomain crear(UUID id, UUID proyectoGrado, int version,
                                                      String estadoEntregable, Instant ocurridoEn) {
        var entregable = new EntregableProyectoGradoDomain();
        var result = new ValidationResult();

        entregable.setId(id, result);
        entregable.setProyectoGrado(proyectoGrado, result);
        entregable.setVersion(version, result);
        entregable.setEstadoEntregable(estadoEntregable, result);
        entregable.setOcurridoEn(ocurridoEn, result);

        result.lanzarSiTieneErrores();
        return entregable;
    }

    public static EntregableProyectoGradoDomain reconstruir(UUID id, UUID proyectoGrado, int version,
                                                            EstadoEntregable estadoEntregable, Instant ocurridoEn) {
        var entregable = new EntregableProyectoGradoDomain();
        entregable.id = id;
        entregable.proyectoGrado = proyectoGrado;
        entregable.version = version;
        entregable.estadoEntregable = estadoEntregable;
        entregable.ocurridoEn = ocurridoEn;
        return entregable;
    }

    private void setId(UUID id, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(id,
                ProyectosFields.EntregableProyectoGrado.ID,
                ProyectosCodes.EntregableProyectoGrado.ID_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                ProyectosFields.EntregableProyectoGrado.PROYECTO_GRADO,
                ProyectosCodes.EntregableProyectoGrado.PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setVersion(int version, ValidationResult result) {
        if (!ValidatorNumero.valorMinimo(version, 1,
                ProyectosFields.EntregableProyectoGrado.VERSION,
                ProyectosCodes.EntregableProyectoGrado.VERSION_INVALIDA, result)) {
            return;
        }
        this.version = version;
    }

    private void setEstadoEntregable(String estadoEntregable, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(estadoEntregable,
                ProyectosFields.EntregableProyectoGrado.ESTADO_ENTREGABLE,
                ProyectosCodes.EntregableProyectoGrado.ESTADO_ENTREGABLE_REQUERIDO, result)) {
            return;
        }
        if (!EstadoEntregable.esValido(estadoEntregable)) {
            result.agregarError(
                    ProyectosFields.EntregableProyectoGrado.ESTADO_ENTREGABLE,
                    ProyectosCodes.EntregableProyectoGrado.ESTADO_ENTREGABLE_INVALIDO,
                    Mensajes.formatear(EstadoEntregableKey.ERROR_NO_ENCONTRADO));
            return;
        }
        this.estadoEntregable = EstadoEntregable.desde(estadoEntregable);
    }

    private void setOcurridoEn(Instant ocurridoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(ocurridoEn,
                ProyectosFields.EntregableProyectoGrado.OCURRIDO_EN,
                ProyectosCodes.EntregableProyectoGrado.OCURRIDO_EN_REQUERIDO, result)) {
            return;
        }
        this.ocurridoEn = ocurridoEn;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public int getVersion() {
        return version;
    }

    public EstadoEntregable getEstadoEntregable() {
        return estadoEntregable;
    }

    public Instant getOcurridoEn() {
        return ocurridoEn;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
