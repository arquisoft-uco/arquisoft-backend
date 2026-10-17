package com.arquisoft.mapas_ruta.domain.maparuta;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorFecha;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.LocalDate;
import java.util.UUID;

public final class MapaRutaDomain {

    public static final MapaRutaDomain VACIO = new MapaRutaDomain(
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            LocalDate.MIN,
            LocalDate.MIN);

    private UUID id;
    private UUID proyectoGrado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private MapaRutaDomain() {}

    private MapaRutaDomain(UUID id, UUID proyectoGrado, LocalDate fechaInicio, LocalDate fechaFin) {
        this.id = id;
        this.proyectoGrado = proyectoGrado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public static MapaRutaDomain crear(UUID proyectoGrado, LocalDate fechaInicio, LocalDate fechaFin) {
        var mapaRuta = new MapaRutaDomain();
        var result = new ValidationResult();

        mapaRuta.setId();
        mapaRuta.setProyectoGrado(proyectoGrado, result);
        mapaRuta.setFechaInicio(fechaInicio, result);
        mapaRuta.setFechaFin(fechaFin, result);
        mapaRuta.validarRangoFechas(result);

        result.lanzarSiTieneErrores();
        return mapaRuta;
    }

    public static MapaRutaDomain reconstruir(UUID id, UUID proyectoGrado, LocalDate fechaInicio, LocalDate fechaFin) {
        return new MapaRutaDomain(id, proyectoGrado, fechaInicio, fechaFin);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                MapasRutaFields.MapaRuta.PROYECTO_GRADO,
                MapasRutaCodes.MapaRuta.PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    private void setFechaInicio(LocalDate fechaInicio, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(fechaInicio,
                MapasRutaFields.MapaRuta.FECHA_INICIO,
                MapasRutaCodes.MapaRuta.FECHA_INICIO_REQUERIDA, result)) {
            return;
        }
        this.fechaInicio = fechaInicio;
    }

    private void setFechaFin(LocalDate fechaFin, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(fechaFin,
                MapasRutaFields.MapaRuta.FECHA_FIN,
                MapasRutaCodes.MapaRuta.FECHA_FIN_REQUERIDA, result)) {
            return;
        }
        this.fechaFin = fechaFin;
    }

    private void validarRangoFechas(ValidationResult result) {
        if (UtilObjeto.esNulo(fechaInicio) || UtilObjeto.esNulo(fechaFin)) {
            return;
        }
        ValidatorFecha.posterior(fechaFin, fechaInicio,
                MapasRutaFields.MapaRuta.FECHA_FIN,
                MapasRutaCodes.MapaRuta.FECHA_FIN_NO_POSTERIOR, result);
    }

    public UUID getId() {
        return id;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
