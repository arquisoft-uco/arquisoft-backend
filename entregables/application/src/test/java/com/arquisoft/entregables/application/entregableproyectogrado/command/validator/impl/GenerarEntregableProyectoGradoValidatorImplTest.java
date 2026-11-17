package com.arquisoft.entregables.application.entregableproyectogrado.command.validator.impl;

import com.arquisoft.entregables.domain.artefacto.ArtefactoDomain;
import com.arquisoft.entregables.domain.artefacto.exception.ProyectoSinArtefactosParaPaqueteException;
import com.arquisoft.entregables.domain.asesor.exception.AsesorNoVigenteException;
import com.arquisoft.entregables.domain.entregableproyectogrado.GeneracionEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.exception.EntregableVigenteYaExisteException;
import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.entregables.domain.proyectogrado.exception.ProyectoGradoCanceladoException;
import com.arquisoft.entregables.domain.proyectogrado.exception.ProyectoGradoFinalizadoException;
import com.arquisoft.entregables.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GenerarEntregableProyectoGradoValidatorImplTest {

    private final GenerarEntregableProyectoGradoValidatorImpl validator =
            new GenerarEntregableProyectoGradoValidatorImpl();

    private static ProyectoGradoDomain proyecto(EstadoProyecto estado) {
        return ProyectoGradoDomain.reconstruir(UtilUUID.generarNuevoUUID(), "Sistema de gestion", estado, null);
    }

    private static List<ArtefactoDomain> artefactos() {
        return List.of(ArtefactoDomain.crear(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Documento",
                new ArtefactoDomain.VersionCerrada(UtilUUID.generarNuevoUUID(), 1, Instant.now()),
                new ArtefactoDomain.DatosRepositorio(
                        UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Repositorio")));
    }

    private static GeneracionEntregableProyectoGradoDomain entrada() {
        return GeneracionEntregableProyectoGradoDomain.crear(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
    }

    @Test
    void debePasar_cuandoTodasLasReglasSeCumplen() {
        // Arrange
        var entrada = entrada();
        var proyecto = proyecto(EstadoProyecto.EN_PROCESO);

        // Act & Assert
        assertThatCode(() -> validator.validar(entrada, proyecto, true, entrada.getAsesor(), false, artefactos())).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarProyectoNoEncontrado_cuandoElProyectoNoExiste() {
        // Arrange
        var entrada = entrada();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, ProyectoGradoDomain.VACIO, true, entrada.getAsesor(), false, artefactos()))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarAsesorNoVigente_cuandoElAsesorNoEstaVigente() {
        // Arrange
        var entrada = entrada();
        var proyecto = proyecto(EstadoProyecto.EN_PROCESO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, proyecto, false, entrada.getAsesor(), false, artefactos()))
                .isInstanceOf(AsesorNoVigenteException.class);
    }

    @Test
    void debeLanzarFinalizado_cuandoElProyectoEstaFinalizado() {
        // Arrange
        var entrada = entrada();
        var proyecto = proyecto(EstadoProyecto.FINALIZADO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, proyecto, true, entrada.getAsesor(), false, artefactos()))
                .isInstanceOf(ProyectoGradoFinalizadoException.class);
    }

    @Test
    void debeLanzar_cuandoProyectoCancelado() {
        // Arrange
        var entrada = entrada();
        var proyecto = proyecto(EstadoProyecto.CANCELADO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, proyecto, true, entrada.getAsesor(), false, artefactos()))
                .isInstanceOf(ProyectoGradoCanceladoException.class);
    }

    @Test
    void debeLanzarVigenteYaExiste_cuandoHayUnEntregableNoCancelado() {
        // Arrange
        var entrada = entrada();
        var proyecto = proyecto(EstadoProyecto.EN_PROCESO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, proyecto, true, entrada.getAsesor(), true, artefactos()))
                .isInstanceOf(EntregableVigenteYaExisteException.class);
    }

    @Test
    void debePriorizarElProyecto_cuandoTodasLasReglasFallan() {
        // Arrange
        var entrada = entrada();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, ProyectoGradoDomain.VACIO, false, entrada.getAsesor(), true, artefactos()))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarSinArtefactos_cuandoElProyectoNoTieneArtefactosVigentes() {
        // Arrange
        var entrada = entrada();
        var proyecto = proyecto(EstadoProyecto.EN_PROCESO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada, proyecto, true, entrada.getAsesor(), false, List.of()))
                .isInstanceOf(ProyectoSinArtefactosParaPaqueteException.class);
    }
}
