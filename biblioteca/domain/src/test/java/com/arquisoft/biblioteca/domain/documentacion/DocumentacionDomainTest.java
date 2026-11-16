package com.arquisoft.biblioteca.domain.documentacion;

import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaLimits;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentacionDomainTest {

    @Test
    void debeReconstruirConLosSeisAtributos_cuandoSeIndicanTodos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var bibliotecario = UtilUUID.generarNuevoUUID();
        var fecha = Instant.parse("2026-11-15T10:00:00Z");

        // Act
        var documentacion = DocumentacionDomain.reconstruir(
                id, "https://minio.test/a.zip", fecha, entregable, bibliotecario, EstadoDocumentacion.PUBLICADO);

        // Assert
        assertThat(documentacion.esVacio()).isFalse();
        assertThat(documentacion.getId()).isEqualTo(id);
        assertThat(documentacion.getUrl()).isEqualTo("https://minio.test/a.zip");
        assertThat(documentacion.getFechaPublicacion()).isEqualTo(fecha);
        assertThat(documentacion.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(documentacion.getBibliotecario()).isEqualTo(bibliotecario);
        assertThat(documentacion.getEstadoDocumentacion()).isEqualTo(EstadoDocumentacion.PUBLICADO);
    }

    @Test
    void debeCrearPendienteConUrlRecortadaYFechaDelServidor_cuandoLosDatosSonValidos() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var bibliotecario = UtilUUID.generarNuevoUUID();
        var antes = Instant.now();

        // Act
        var documentacion = DocumentacionDomain.crear(entregable, bibliotecario, " entregables ", " proyectos/a.zip ");

        // Assert
        assertThat(documentacion.getId()).isNotNull();
        assertThat(documentacion.getUrl()).isEqualTo("entregables/proyectos/a.zip");
        assertThat(documentacion.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(documentacion.getBibliotecario()).isEqualTo(bibliotecario);
        assertThat(documentacion.getEstadoDocumentacion()).isEqualTo(EstadoDocumentacion.PENDIENTE);
        assertThat(documentacion.getFechaPublicacion()).isAfterOrEqualTo(antes);
    }

    @Test
    void debeAcumularErrores_cuandoFaltanIdsYUbicacion() {
        // Act & Assert
        assertThatThrownBy(() -> DocumentacionDomain.crear(null, null, " ", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                BibliotecaCodes.Documentacion.URL_REQUERIDA,
                                BibliotecaCodes.Documentacion.ENTREGABLE_REQUERIDO,
                                BibliotecaCodes.Documentacion.BIBLIOTECARIO_REQUERIDO));
    }

    @Test
    void debeRechazar_cuandoLaUrlCompuestaSuperaElLimite() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var bibliotecario = UtilUUID.generarNuevoUUID();
        var clave = "k".repeat(BibliotecaLimits.Documentacion.URL_MAX);

        // Act & Assert
        assertThatThrownBy(() -> DocumentacionDomain.crear(entregable, bibliotecario, "bucket", clave))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(BibliotecaCodes.Documentacion.URL_LONGITUD_EXCEDIDA)));
    }

    @Test
    void debeSerVacio_cuandoEsElCentinela() {
        // Act & Assert
        assertThat(DocumentacionDomain.VACIO.esVacio()).isTrue();
        assertThat(DocumentacionDomain.VACIO.getEstadoDocumentacion()).isEqualTo(EstadoDocumentacion.VACIO);
        assertThat(UtilUUID.esPorDefecto(DocumentacionDomain.VACIO.getId())).isTrue();
    }
}
