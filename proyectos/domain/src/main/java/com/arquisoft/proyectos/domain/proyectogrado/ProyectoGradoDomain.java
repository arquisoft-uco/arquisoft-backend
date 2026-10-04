package com.arquisoft.proyectos.domain.proyectogrado;

import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGrado;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.message.constant.ProyectosLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ProyectoGradoDomain {

    public static final ProyectoGradoDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO,
            UtilUUID.obtenerUUIDPorDefecto(), EstadoProyectoGrado.VACIO);

    private UUID id;
    private UUID fichaPerfil;
    private String tituloProyecto;
    private UUID coordinador;
    private EstadoProyectoGrado estadoProyectoGrado;

    private ProyectoGradoDomain() {}

    public static ProyectoGradoDomain crear(UUID fichaPerfil, String tituloProyecto, UUID coordinador) {
        var proyecto = new ProyectoGradoDomain();
        var result = new ValidationResult();

        proyecto.setId();
        proyecto.setFichaPerfil(fichaPerfil, result);
        proyecto.setTituloProyecto(tituloProyecto, result);
        proyecto.setCoordinador(coordinador, result);
        proyecto.setEstadoInicial();

        result.lanzarSiTieneErrores();
        return proyecto;
    }

    public static ProyectoGradoDomain reconstruir(UUID id, UUID fichaPerfil, String tituloProyecto,
                                                   UUID coordinador, EstadoProyectoGrado estadoProyectoGrado) {
        var proyecto = new ProyectoGradoDomain();
        proyecto.id = id;
        proyecto.fichaPerfil = fichaPerfil;
        proyecto.tituloProyecto = tituloProyecto;
        proyecto.coordinador = coordinador;
        proyecto.estadoProyectoGrado = estadoProyectoGrado;
        return proyecto;
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setFichaPerfil(UUID fichaPerfil, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(fichaPerfil,
                ProyectosFields.ProyectoGrado.FICHA_PERFIL,
                ProyectosCodes.ProyectoGrado.FICHA_PERFIL_ID_REQUERIDO, result)) {
            return;
        }
        this.fichaPerfil = fichaPerfil;
    }

    private void setTituloProyecto(String tituloProyecto, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(tituloProyecto);
        if (!ValidatorTexto.noEnBlanco(recortado,
                ProyectosFields.ProyectoGrado.TITULO_PROYECTO,
                ProyectosCodes.ProyectoGrado.TITULO_REQUERIDO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado, ProyectosLimits.ProyectoGrado.TITULO_MAX,
                ProyectosFields.ProyectoGrado.TITULO_PROYECTO,
                ProyectosCodes.ProyectoGrado.TITULO_LONGITUD_MAXIMA, result)) {
            return;
        }
        this.tituloProyecto = recortado;
    }

    private void setCoordinador(UUID coordinador, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(coordinador,
                ProyectosFields.ProyectoGrado.COORDINADOR,
                ProyectosCodes.ProyectoGrado.COORDINADOR_ID_REQUERIDO, result)) {
            return;
        }
        this.coordinador = coordinador;
    }

    private void setEstadoInicial() {
        this.estadoProyectoGrado = EstadoProyectoGrado.EN_PROCESO;
    }

    public UUID getId() {
        return id;
    }

    public UUID getFichaPerfil() {
        return fichaPerfil;
    }

    public String getTituloProyecto() {
        return tituloProyecto;
    }

    public UUID getCoordinador() {
        return coordinador;
    }

    public EstadoProyectoGrado getEstadoProyectoGrado() {
        return estadoProyectoGrado;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
