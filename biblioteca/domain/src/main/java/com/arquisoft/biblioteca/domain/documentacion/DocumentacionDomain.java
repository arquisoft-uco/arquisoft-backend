package com.arquisoft.biblioteca.domain.documentacion;

import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.message.constant.BibliotecaLimits;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.time.Instant;
import java.util.UUID;

public final class DocumentacionDomain {

    public static final DocumentacionDomain VACIO = DocumentacionDomain.reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, UtilFecha.VACIO,
            UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(), EstadoDocumentacion.VACIO);

    private static final String SEPARADOR_RUTA = "/";

    private UUID id;
    private String url;
    private Instant fechaPublicacion;
    private UUID entregableProyectoGrado;
    private UUID bibliotecario;
    private EstadoDocumentacion estadoDocumentacion;

    private DocumentacionDomain() {}

    public static DocumentacionDomain crear(UUID entregableProyectoGrado, UUID bibliotecario, String bucket,
                                            String claveObjeto) {
        var documentacion = new DocumentacionDomain();
        var result = new ValidationResult();

        documentacion.setId();
        documentacion.setUrl(bucket, claveObjeto, result);
        documentacion.setFechaPublicacion();
        documentacion.setEntregableProyectoGrado(entregableProyectoGrado, result);
        documentacion.setBibliotecario(bibliotecario, result);
        documentacion.setEstadoInicial();

        result.lanzarSiTieneErrores();
        return documentacion;
    }

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

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setUrl(String bucket, String claveObjeto, ValidationResult result) {
        var bucketRecortado = UtilTexto.aplicarTrim(bucket);
        var claveRecortada = UtilTexto.aplicarTrim(claveObjeto);
        if (!ValidatorTexto.noEnBlanco(bucketRecortado,
                BibliotecaFields.Documentacion.URL,
                BibliotecaCodes.Documentacion.URL_REQUERIDA, result)) {
            return;
        }
        if (!ValidatorTexto.noEnBlanco(claveRecortada,
                BibliotecaFields.Documentacion.URL,
                BibliotecaCodes.Documentacion.URL_REQUERIDA, result)) {
            return;
        }
        var compuesta = bucketRecortado + SEPARADOR_RUTA + claveRecortada;
        if (!ValidatorLongitud.longitudMaxima(compuesta, BibliotecaLimits.Documentacion.URL_MAX,
                BibliotecaFields.Documentacion.URL,
                BibliotecaCodes.Documentacion.URL_LONGITUD_EXCEDIDA, result)) {
            return;
        }
        this.url = compuesta;
    }

    private void setFechaPublicacion() {
        this.fechaPublicacion = UtilFecha.generarInstanteActual();
    }

    private void setEntregableProyectoGrado(UUID entregableProyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(entregableProyectoGrado,
                BibliotecaFields.Documentacion.ENTREGABLE_PROYECTO_GRADO,
                BibliotecaCodes.Documentacion.ENTREGABLE_REQUERIDO, result)) {
            return;
        }
        this.entregableProyectoGrado = entregableProyectoGrado;
    }

    private void setBibliotecario(UUID bibliotecario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(bibliotecario,
                BibliotecaFields.Documentacion.BIBLIOTECARIO,
                BibliotecaCodes.Documentacion.BIBLIOTECARIO_REQUERIDO, result)) {
            return;
        }
        this.bibliotecario = bibliotecario;
    }

    private void setEstadoInicial() {
        this.estadoDocumentacion = EstadoDocumentacion.PENDIENTE;
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
