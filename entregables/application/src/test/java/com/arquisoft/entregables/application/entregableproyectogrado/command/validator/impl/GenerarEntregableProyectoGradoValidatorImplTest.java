package com.arquisoft.entregables.application.entregableproyectogrado.command.validator.impl;

import com.arquisoft.entregables.domain.asesor.exception.AsesorNoVigenteException;
import com.arquisoft.entregables.domain.entregableproyectogrado.GeneracionEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.exception.EntregableVigenteYaExisteException;
import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.entregables.domain.proyectogrado.exception.ProyectoGradoFinalizadoException;
import com.arquisoft.entregables.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GenerarEntregableProyectoGradoValidatorImplTest {

    private final GenerarEntregableProyectoGradoValidatorImpl validator =
            new GenerarEntregableProyectoGradoValidatorImpl();

    private static ProyectoGradoDomain proyecto(EstadoProyecto estado) {
        return ProyectoGradoDomain.reconstruir(UtilUUID.generarNuevoUUID(), "Sistema de gestion", estado);
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
        assertThatCode(() -> validator.validar(entrada, proyecto, true, false)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarProyectoNoEncontrado_cuandoElProyectoNoExiste() {
        // Arrange
        var entrada = entrada();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, ProyectoGradoDomain.VACIO, true, false))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarAsesorNoVigente_cuandoElAsesorNoEstaVigente() {
        // Arrange
        var entrada = entrada();
        var proyecto = proyecto(EstadoProyecto.EN_PROCESO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, proyecto, false, false))
                .isInstanceOf(AsesorNoVigenteException.class);
    }

    @Test
    void debeLanzarFinalizado_cuandoElProyectoEstaFinalizado() {
        // Arrange
        var entrada = entrada();
        var proyecto = proyecto(EstadoProyecto.FINALIZADO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, proyecto, true, false))
                .isInstanceOf(ProyectoGradoFinalizadoException.class);
    }

    @Test
    void debeLanzarVigenteYaExiste_cuandoHayUnEntregableNoCancelado() {
        // Arrange
        var entrada = entrada();
        var proyecto = proyecto(EstadoProyecto.EN_PROCESO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, proyecto, true, true))
                .isInstanceOf(EntregableVigenteYaExisteException.class);
    }

    @Test
    void debePriorizarElProyecto_cuandoTodasLasReglasFallan() {
        // Arrange
        var entrada = entrada();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada, ProyectoGradoDomain.VACIO, false, true))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }
}
