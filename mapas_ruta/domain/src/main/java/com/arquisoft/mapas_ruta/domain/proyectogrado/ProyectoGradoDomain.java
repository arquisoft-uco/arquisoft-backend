package com.arquisoft.mapas_ruta.domain.proyectogrado;

import com.arquisoft.mapas_ruta.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public final class ProyectoGradoDomain {

    public static final ProyectoGradoDomain VACIO = new ProyectoGradoDomain(
            UtilUUID.obtenerUUIDPorDefecto(),
            EstadoProyectoGrado.VACIO,
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilUUID.obtenerUUIDPorDefecto(),
            UtilTexto.VACIO);

    private UUID id;
    private EstadoProyectoGrado estadoProyectoGrado;
    private UUID coordinador;
    private UUID fichaPerfil;
    private String tituloProyecto;

    private ProyectoGradoDomain(UUID id, EstadoProyectoGrado estadoProyectoGrado, UUID coordinador,
                                UUID fichaPerfil, String tituloProyecto) {
        this.id = id;
        this.estadoProyectoGrado = estadoProyectoGrado;
        this.coordinador = coordinador;
        this.fichaPerfil = fichaPerfil;
        this.tituloProyecto = tituloProyecto;
    }

    public static ProyectoGradoDomain reconstruir(UUID id, EstadoProyectoGrado estadoProyectoGrado, UUID coordinador,
                                                  UUID fichaPerfil, String tituloProyecto) {
        return new ProyectoGradoDomain(id, estadoProyectoGrado, coordinador, fichaPerfil, tituloProyecto);
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

    public boolean esVacio() {
        return this == VACIO;
    }
}
