package com.arquisoft.proyectos.domain.asesorproyectogrado;

import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.message.key.proyectos.EstadoAsesorKey;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class CambioEstadoAsesorProyectoGradoDomain {

    private UUID proyectoGrado;
    private UUID asesor;
    private UUID coordinador;
    private EstadoAsesor estadoNuevo;

    private CambioEstadoAsesorProyectoGradoDomain() {}

    public static CambioEstadoAsesorProyectoGradoDomain crear(UUID proyectoGrado, UUID asesor,
                                                              UUID coordinador, String estadoAsesor) {
        var cambio = new CambioEstadoAsesorProyectoGradoDomain();
        var result = new ValidationResult();

        cambio.setProyectoGrado(proyectoGrado, result);
        cambio.setAsesor(asesor, result);
        cambio.setCoordinador(coordinador, result);
        cambio.setEstadoNuevo(estadoAsesor, result);

        result.lanzarSiTieneErrores();
        return cambio;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                ProyectosFields.AsesorProyectoGrado.PROYECTO_GRADO,
                ProyectosCodes.AsesorProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setAsesor(UUID asesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesor,
                ProyectosFields.AsesorProyectoGrado.ASESOR,
                ProyectosCodes.AsesorProyectoGrado.ASESOR_ID_REQUERIDO, result)) {
            return;
        }
        this.asesor = asesor;
    }

    private void setCoordinador(UUID coordinador, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(coordinador,
                ProyectosFields.AsesorProyectoGrado.COORDINADOR,
                ProyectosCodes.AsesorProyectoGrado.COORDINADOR_ID_REQUERIDO, result)) {
            return;
        }
        this.coordinador = coordinador;
    }

    private void setEstadoNuevo(String estadoAsesor, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(estadoAsesor,
                ProyectosFields.AsesorProyectoGrado.ESTADO_ASESOR,
                ProyectosCodes.AsesorProyectoGrado.ESTADO_ASESOR_REQUERIDO, result)) {
            return;
        }
        if (!EstadoAsesor.esValido(estadoAsesor)) {
            result.agregarError(
                    ProyectosFields.AsesorProyectoGrado.ESTADO_ASESOR,
                    ProyectosCodes.AsesorProyectoGrado.ESTADO_ASESOR_INVALIDO,
                    Mensajes.formatear(EstadoAsesorKey.ERROR_NO_ENCONTRADO, estadoAsesor));
            return;
        }
        this.estadoNuevo = EstadoAsesor.desde(estadoAsesor);
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public UUID getAsesor() {
        return asesor;
    }

    public UUID getCoordinador() {
        return coordinador;
    }

    public EstadoAsesor getEstadoNuevo() {
        return estadoNuevo;
    }
}
