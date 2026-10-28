package com.arquisoft.entregables.domain.entregableproyectogrado;

import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.message.constant.EntregablesLimits;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorNumero;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class EntregableProyectoGradoDomain {

    private UUID id;
    private UUID proyectoGrado;
    private int version;
    private UUID asesor;
    private EstadoEntregable estadoEntregable;

    private EntregableProyectoGradoDomain() {}

    public static EntregableProyectoGradoDomain crear(UUID proyectoGrado, int ultimaVersion, UUID asesor) {
        var entregable = new EntregableProyectoGradoDomain();
        var result = new ValidationResult();

        entregable.setId();
        entregable.setProyectoGrado(proyectoGrado, result);
        entregable.setVersion(ultimaVersion, result);
        entregable.setAsesor(asesor, result);
        entregable.setEstadoEntregableInicial();

        result.lanzarSiTieneErrores();
        return entregable;
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                EntregablesFields.EntregableProyectoGrado.PROYECTO_GRADO,
                EntregablesCodes.EntregableProyectoGrado.PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setVersion(int ultimaVersion, ValidationResult result) {
        if (!ValidatorNumero.valorMinimo(ultimaVersion,
                EntregablesLimits.EntregableProyectoGrado.ULTIMA_VERSION_MIN,
                EntregablesFields.EntregableProyectoGrado.ULTIMA_VERSION,
                EntregablesCodes.EntregableProyectoGrado.ULTIMA_VERSION_INVALIDA, result)) {
            return;
        }
        this.version = ultimaVersion + 1;
    }

    private void setAsesor(UUID asesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesor,
                EntregablesFields.EntregableProyectoGrado.ASESOR,
                EntregablesCodes.EntregableProyectoGrado.ASESOR_REQUERIDO, result)) {
            return;
        }
        this.asesor = asesor;
    }

    private void setEstadoEntregableInicial() {
        this.estadoEntregable = EstadoEntregable.DISPONIBLE_EVALUACION;
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

    public UUID getAsesor() {
        return asesor;
    }

    public EstadoEntregable getEstadoEntregable() {
        return estadoEntregable;
    }
}
