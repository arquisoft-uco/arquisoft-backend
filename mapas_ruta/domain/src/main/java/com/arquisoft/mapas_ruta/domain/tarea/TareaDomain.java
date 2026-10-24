package com.arquisoft.mapas_ruta.domain.tarea;

import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.message.constant.MapasRutaLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class TareaDomain {

    public static final TareaDomain VACIO = new TareaDomain(
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilTexto.VACIO,
            UtilUUID.obtenerUUIDPorDefecto(),
            EstadoItem.VACIO);

    private UUID id;
    private UUID mapaRuta;
    private String descripcion;
    private UUID asesor;
    private EstadoItem estadoItem;

    private TareaDomain() {}

    private TareaDomain(UUID id, UUID mapaRuta, String descripcion, UUID asesor, EstadoItem estadoItem) {
        this.id = id;
        this.mapaRuta = mapaRuta;
        this.descripcion = descripcion;
        this.asesor = asesor;
        this.estadoItem = estadoItem;
    }

    public static TareaDomain crear(UUID mapaRuta, String descripcion, UUID asesor) {
        var tarea = new TareaDomain();
        var result = new ValidationResult();

        tarea.setId();
        tarea.setMapaRuta(mapaRuta, result);
        tarea.setDescripcion(descripcion, result);
        tarea.setAsesor(asesor, result);
        tarea.setEstadoItemInicial();

        result.lanzarSiTieneErrores();
        return tarea;
    }

    public static TareaDomain reconstruir(UUID id, UUID mapaRuta, String descripcion, UUID asesor,
                                          EstadoItem estadoItem) {
        return new TareaDomain(id, mapaRuta, descripcion, asesor, estadoItem);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setMapaRuta(UUID mapaRuta, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(mapaRuta,
                MapasRutaFields.Tarea.MAPA_RUTA,
                MapasRutaCodes.Tarea.MAPA_RUTA_REQUERIDO, result)) {
            return;
        }
        this.mapaRuta = mapaRuta;
    }

    private void setDescripcion(String descripcion, ValidationResult result) {
        var recortada = UtilTexto.aplicarTrim(descripcion);
        if (!ValidatorTexto.noEnBlanco(recortada,
                MapasRutaFields.Tarea.DESCRIPCION,
                MapasRutaCodes.Tarea.DESCRIPCION_REQUERIDA, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortada, MapasRutaLimits.Tarea.DESCRIPCION_MAX,
                MapasRutaFields.Tarea.DESCRIPCION,
                MapasRutaCodes.Tarea.DESCRIPCION_DEMASIADO_LARGA, result)) {
            return;
        }
        this.descripcion = recortada;
    }

    private void setAsesor(UUID asesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesor,
                MapasRutaFields.Tarea.ASESOR,
                MapasRutaCodes.Tarea.ASESOR_REQUERIDO, result)) {
            return;
        }
        this.asesor = asesor;
    }

    private void setEstadoItemInicial() {
        this.estadoItem = EstadoItem.NUEVA;
    }

    public UUID getId() {
        return id;
    }

    public UUID getMapaRuta() {
        return mapaRuta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public UUID getAsesor() {
        return asesor;
    }

    public EstadoItem getEstadoItem() {
        return estadoItem;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
