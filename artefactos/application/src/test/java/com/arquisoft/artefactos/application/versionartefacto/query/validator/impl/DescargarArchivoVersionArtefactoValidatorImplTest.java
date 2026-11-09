package com.arquisoft.artefactos.application.versionartefacto.query.validator.impl;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.DescargaVersionArtefactoCriteria;
import com.arquisoft.artefactos.domain.versionartefacto.ArchivoVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.exception.ActorSinAccesoVersionArtefactoException;
import com.arquisoft.artefactos.domain.versionartefacto.exception.ArchivoVersionPurgadoException;
import com.arquisoft.artefactos.domain.versionartefacto.exception.VersionArtefactoNoEncontradaException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DescargarArchivoVersionArtefactoValidatorImplTest {

    private final DescargarArchivoVersionArtefactoValidatorImpl validator =
            new DescargarArchivoVersionArtefactoValidatorImpl();
    private final DescargaVersionArtefactoCriteria comoAsesor = new DescargaVersionArtefactoCriteria(
            UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), true, false);
    private final DescargaVersionArtefactoCriteria comoEstudiante = new DescargaVersionArtefactoCriteria(
            UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), false, true);

    private static ArchivoVersionArtefactoDomain archivoDe(UUID asesor) {
        return ArchivoVersionArtefactoDomain.reconstruir(asesor, "versiones/x", 1, "Documento", "Proyecto", false);
    }

    @Test
    void debeLanzarNoEncontrada_cuandoElArchivoEsVacio_conPrioridadSobreElAcceso() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(comoAsesor, ArchivoVersionArtefactoDomain.VACIO, false))
                .isInstanceOf(VersionArtefactoNoEncontradaException.class);
    }

    @Test
    void debeLanzarAccesoDenegado_cuandoElAsesorNoEsElDelArtefacto() {
        // Arrange
        var archivo = archivoDe(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(comoAsesor, archivo, false))
                .isInstanceOf(ActorSinAccesoVersionArtefactoException.class);
    }

    @Test
    void debeLanzarAccesoDenegado_cuandoElEstudianteNoParticipa() {
        // Arrange
        var archivo = archivoDe(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(comoEstudiante, archivo, false))
                .isInstanceOf(ActorSinAccesoVersionArtefactoException.class);
    }

    @Test
    void noDebeLanzar_cuandoEsElAsesorDelArtefacto() {
        // Arrange
        var archivo = archivoDe(comoAsesor.actor());

        // Act & Assert
        assertThatCode(() -> validator.validar(comoAsesor, archivo, false)).doesNotThrowAnyException();
    }

    @Test
    void noDebeLanzar_cuandoElEstudianteParticipa() {
        // Arrange
        var archivo = archivoDe(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatCode(() -> validator.validar(comoEstudiante, archivo, true)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarArchivoPurgado_cuandoElProyectoEstaFinalizado() {
        // Arrange
        var finalizado = ArchivoVersionArtefactoDomain.reconstruir(
                comoAsesor.actor(), "versiones/x", 1, "Documento", "Proyecto", true);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(comoAsesor, finalizado, false))
                .isInstanceOf(ArchivoVersionPurgadoException.class);
    }

    @Test
    void debeValidarAccesoAntesQuePurgado_cuandoElActorNoTieneAccesoYElProyectoEstaFinalizado() {
        // Arrange
        var finalizado = ArchivoVersionArtefactoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "versiones/x", 1, "Documento", "Proyecto", true);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(comoAsesor, finalizado, false))
                .isInstanceOf(ActorSinAccesoVersionArtefactoException.class);
    }
}
