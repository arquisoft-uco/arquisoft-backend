package com.arquisoft.proyectos.domain.asesorproyectogrado;

import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class AsesorProyectoGradoDomain {

    public static final AsesorProyectoGradoDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(),
            EstadoAsesor.VACIO, UtilFecha.VACIO);

    private UUID id;
    private UUID asesor;
    private UUID proyectoGrado;
    private EstadoAsesor estadoAsesor;
    private Instant fechaAsignado;

    private AsesorProyectoGradoDomain() {}

    public static AsesorProyectoGradoDomain crear(UUID asesor, UUID proyectoGrado) {
        var vinculo = new AsesorProyectoGradoDomain();
        var result = new ValidationResult();

        vinculo.setId();
        vinculo.setAsesor(asesor, result);
        vinculo.setProyectoGrado(proyectoGrado, result);
        vinculo.setEstadoAsesorInicial();
        vinculo.setFechaAsignado();

        result.lanzarSiTieneErrores();
        return vinculo;
    }

    public static AsesorProyectoGradoDomain reconstruir(UUID id, UUID asesor, UUID proyectoGrado,
                                                        EstadoAsesor estadoAsesor, Instant fechaAsignado) {
        var vinculo = new AsesorProyectoGradoDomain();
        vinculo.id = id;
        vinculo.asesor = asesor;
        vinculo.proyectoGrado = proyectoGrado;
        vinculo.estadoAsesor = estadoAsesor;
        vinculo.fechaAsignado = fechaAsignado;
        return vinculo;
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setAsesor(UUID asesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesor,
                ProyectosFields.AsesorProyectoGrado.ASESOR,
                ProyectosCodes.AsesorProyectoGrado.ASESOR_ID_REQUERIDO, result)) {
            return;
        }
        this.asesor = asesor;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                ProyectosFields.AsesorProyectoGrado.PROYECTO_GRADO,
                ProyectosCodes.AsesorProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setEstadoAsesorInicial() {
        this.estadoAsesor = EstadoAsesor.ASIGNADO;
    }

    private void setFechaAsignado() {
        this.fechaAsignado = UtilFecha.generarInstanteActual();
    }

    public UUID getId() {
        return id;
    }

    public UUID getAsesor() {
        return asesor;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public EstadoAsesor getEstadoAsesor() {
        return estadoAsesor;
    }

    public Instant getFechaAsignado() {
        return fechaAsignado;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
