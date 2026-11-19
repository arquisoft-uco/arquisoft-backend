package com.arquisoft.mapas_ruta.domain.proyectogrado;

import com.arquisoft.mapas_ruta.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
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
            UtilUUID.obtenerUUIDPorDefecto(),
            EstadoProyectoGrado.VACIO,
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilTexto.VACIO,
            UtilFecha.VACIO);

    private UUID id;
    private EstadoProyectoGrado estadoProyectoGrado;
    private UUID coordinador;
    private UUID fichaPerfil;
    private String tituloProyecto;
    private Instant ocurridoEn;

    private ProyectoGradoDomain() {}

    public static ProyectoGradoDomain crear(
            UUID id, UUID coordinador, UUID fichaPerfil, String tituloProyecto, Instant ocurridoEn) {
        var proyecto = new ProyectoGradoDomain();
        var result = new ValidationResult();

        proyecto.setId(id, result);
        proyecto.setCoordinador(coordinador, result);
        proyecto.setFichaPerfil(fichaPerfil, result);
        proyecto.setTituloProyecto(tituloProyecto, result);
        proyecto.setEstadoProyectoGradoInicial();
        proyecto.setOcurridoEn(ocurridoEn, result);

        result.lanzarSiTieneErrores();
        return proyecto;
    }

    public static ProyectoGradoDomain reconstruir(UUID id, EstadoProyectoGrado estadoProyectoGrado, UUID coordinador,
                                                  UUID fichaPerfil, String tituloProyecto, Instant ocurridoEn) {
        var proyecto = new ProyectoGradoDomain();
        proyecto.id = id;
        proyecto.estadoProyectoGrado = estadoProyectoGrado;
        proyecto.coordinador = coordinador;
        proyecto.fichaPerfil = fichaPerfil;
        proyecto.tituloProyecto = tituloProyecto;
        proyecto.ocurridoEn = UtilObjeto.aplicarPorDefecto(ocurridoEn, UtilFecha.VACIO);
        return proyecto;
    }

    private void setId(UUID id, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(id,
                MapasRutaFields.ProyectoGrado.ID,
                MapasRutaCodes.ProyectoGrado.ID_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setCoordinador(UUID coordinador, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(coordinador,
                MapasRutaFields.ProyectoGrado.COORDINADOR,
                MapasRutaCodes.ProyectoGrado.COORDINADOR_REQUERIDO, result)) {
            return;
        }
        this.coordinador = coordinador;
    }

    private void setFichaPerfil(UUID fichaPerfil, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(fichaPerfil,
                MapasRutaFields.ProyectoGrado.FICHA_PERFIL,
                MapasRutaCodes.ProyectoGrado.FICHA_PERFIL_REQUERIDA, result)) {
            return;
        }
        this.fichaPerfil = fichaPerfil;
    }

    private void setTituloProyecto(String tituloProyecto, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(tituloProyecto,
                MapasRutaFields.ProyectoGrado.TITULO_PROYECTO,
                MapasRutaCodes.ProyectoGrado.TITULO_PROYECTO_REQUERIDO, result)) {
            return;
        }
        this.tituloProyecto = tituloProyecto;
    }

    private void setEstadoProyectoGradoInicial() {
        this.estadoProyectoGrado = EstadoProyectoGrado.EN_PROCESO;
    }

    private void setOcurridoEn(Instant ocurridoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(ocurridoEn,
                MapasRutaFields.ProyectoGrado.OCURRIDO_EN,
                MapasRutaCodes.ProyectoGrado.OCURRIDO_EN_REQUERIDO, result)) {
            return;
        }
        this.ocurridoEn = ocurridoEn;
    }

    public UUID getId() {
        return id;
    }

    public EstadoProyectoGrado getEstadoProyectoGrado() {
        return estadoProyectoGrado;
    }

    public UUID getCoordinador() {
        return coordinador;
    }

    public UUID getFichaPerfil() {
        return fichaPerfil;
    }

    public String getTituloProyecto() {
        return tituloProyecto;
    }

    public Instant getOcurridoEn() {
        return ocurridoEn;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
