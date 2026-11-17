package com.arquisoft.biblioteca.domain.entregableproyectogrado;

import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public final class EntregableProyectoGradoDomain {

    public static final EntregableProyectoGradoDomain VACIO = EntregableProyectoGradoDomain.reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, UtilTexto.VACIO);

    private UUID id;
    private UUID proyectoGrado;
    private String bucket;
    private String claveObjeto;

    private EntregableProyectoGradoDomain() {}

    public static EntregableProyectoGradoDomain reconstruir(UUID id, UUID proyectoGrado, String bucket,
                                                            String claveObjeto) {
        var entregable = new EntregableProyectoGradoDomain();
        entregable.id = id;
        entregable.proyectoGrado = proyectoGrado;
        entregable.bucket = bucket;
        entregable.claveObjeto = claveObjeto;
        return entregable;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public String getBucket() {
        return bucket;
    }

    public String getClaveObjeto() {
        return claveObjeto;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
