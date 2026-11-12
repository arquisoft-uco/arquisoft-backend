package com.arquisoft.entregables.application.artefacto.command.validator.impl;

import com.arquisoft.entregables.domain.artefacto.ArtefactoDomain;
import com.arquisoft.entregables.domain.artefacto.RegistroVersionCerradaArtefactoDomain;
import com.arquisoft.entregables.domain.artefacto.exception.ArchivoOrigenArtefactoNoDisponibleException;
import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.entregables.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarVersionCerradaArtefactoValidatorImplTest {

    private final RegistrarVersionCerradaArtefactoValidatorImpl validator =
            new RegistrarVersionCerradaArtefactoValidatorImpl();

    private static RegistroVersionCerradaArtefactoDomain registro() {
        var artefacto = ArtefactoDomain.crear(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Documento",
                new ArtefactoDomain.VersionCerrada(UtilUUID.generarNuevoUUID(), 1, Instant.now()),
                new ArtefactoDomain.DatosRepositorio(
                        UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Repositorio"));
        return RegistroVersionCerradaArtefactoDomain.crear(artefacto, "pendientes/origen.pdf");
    }

    private static ProyectoGradoDomain proyecto() {
        return ProyectoGradoDomain.reconstruir(UtilUUID.generarNuevoUUID(), "Proyecto", EstadoProyecto.EN_PROCESO);
    }

    @Test
    void debePasar_cuandoElProyectoExisteYElArchivoOrigenTambien() {
        // Arrange
        var registro = registro();
        var proyecto = proyecto();

        // Act & Assert
        assertThatCode(() -> validator.validar(registro, proyecto, "etag")).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarArchivoOrigenNoDisponible_cuandoNoHayEtag() {
        // Arrange
        var registro = registro();
        var proyecto = proyecto();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(registro, proyecto, ""))
                .isInstanceOf(ArchivoOrigenArtefactoNoDisponibleException.class);
    }

    @Test
    void debeLanzarProyectoNoEncontradoPrimero_cuandoFaltanElProyectoYElArchivo() {
        // Arrange
        var registro = registro();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(registro, ProyectoGradoDomain.VACIO, ""))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }
}
