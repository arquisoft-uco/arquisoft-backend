package com.arquisoft.biblioteca.domain.documentacion;

import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;

import java.time.Instant;
import java.util.UUID;

public final class DocumentacionDomain {

    public static final DocumentacionDomain VACIO = DocumentacionDomain.reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, UtilFecha.VACIO,
            UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(), EstadoDocumentacion.VACIO);

    private UUID id;
    private String url;
    private Instant fechaPublicacion;
    private UUID entregableProyectoGrado;
    private UUID bibliotecario;
    private EstadoDocumentacion estadoDocumentacion;

    private DocumentacionDomain() {}

    public static DocumentacionDomain reconstruir(UUID id, String url, Instant fechaPublicacion,
                                                  UUID entregableProyectoGrado, UUID bibliotecario,
                                                  EstadoDocumentacion estadoDocumentacion) {
        var documentacion = new DocumentacionDomain();
        documentacion.id = id;
        documentacion.url = url;
        documentacion.fechaPublicacion = fechaPublicacion;
        documentacion.entregableProyectoGrado = entregableProyectoGrado;
        documentacion.bibliotecario = bibliotecario;
        documentacion.estadoDocumentacion = estadoDocumentacion;
        return documentacion;
    }

    public UUID getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public Instant getFechaPublicacion() {
        return fechaPublicacion;
    }

    public UUID getEntregableProyectoGrado() {
        return entregableProyectoGrado;
    }

    public UUID getBibliotecario() {
        return bibliotecario;
    }

    public EstadoDocumentacion getEstadoDocumentacion() {
        return estadoDocumentacion;
    }

    public boolean esVacio() {
        return this == VACIO;
    }
}
