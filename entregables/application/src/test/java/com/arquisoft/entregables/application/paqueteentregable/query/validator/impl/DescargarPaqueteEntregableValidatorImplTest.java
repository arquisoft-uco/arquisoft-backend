package com.arquisoft.entregables.application.paqueteentregable.query.validator.impl;

import com.arquisoft.entregables.application.paqueteentregable.query.criteria.DescargaPaqueteEntregableCriteria;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.exception.EntregableProyectoGradoNoEncontradoException;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.EstadoEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.exception.EstadoEntregableProyectoGradoNoEncontradoException;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;
import com.arquisoft.entregables.domain.paqueteentregable.exception.ActorSinAccesoPaqueteEntregableException;
import com.arquisoft.entregables.domain.paqueteentregable.exception.PaqueteEntregableCanceladoException;
import com.arquisoft.entregables.domain.paqueteentregable.exception.PaqueteEntregableEnPreparacionException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DescargarPaqueteEntregableValidatorImplTest {

    private final DescargarPaqueteEntregableValidatorImpl validator = new DescargarPaqueteEntregableValidatorImpl();

    private static PaqueteEntregableDomain paquete() {
        return PaqueteEntregableDomain.crear(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "bucket",
                "paquetes/x.zip", "p.zip", 10L, Instant.now());
    }

    private static EstadoEntregableProyectoGradoDomain estado(EstadoEntregable estado) {
        return EstadoEntregableProyectoGradoDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                estado, Instant.now());
    }

    private static EntregableProyectoGradoDomain entregable(UUID asesor) {
        return EntregableProyectoGradoDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 2,
                asesor);
    }

    private static DescargaPaqueteEntregableCriteria criteria(UUID actor, boolean comoAsesor, boolean comoEstudiante) {
        return new DescargaPaqueteEntregableCriteria(UtilUUID.generarNuevoUUID(), actor, comoAsesor, comoEstudiante);
    }

    @Test
    void debeLanzarNoEncontrado_cuandoEntregableInexistente() {
        // Arrange
        var criteria = criteria(UtilUUID.generarNuevoUUID(), true, false);
        var inexistente = EntregableProyectoGradoDomain.VACIO;
        var paquete = paquete();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, inexistente, estado(EstadoEntregable.DISPONIBLE_EVALUACION), false, paquete))
                .isInstanceOf(EntregableProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarSinAcceso_cuandoActorAjeno() {
        // Arrange
        var criteria = criteria(UtilUUID.generarNuevoUUID(), true, true);
        var otroAsesor = entregable(UtilUUID.generarNuevoUUID());
        var paquete = paquete();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, otroAsesor, estado(EstadoEntregable.DISPONIBLE_EVALUACION), false, paquete))
                .isInstanceOf(ActorSinAccesoPaqueteEntregableException.class);
    }

    @Test
    void debeLanzarEnPreparacion_cuandoSinPaquete() {
        // Arrange
        var actor = UtilUUID.generarNuevoUUID();
        var criteria = criteria(actor, true, false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, entregable(actor), estado(EstadoEntregable.DISPONIBLE_EVALUACION), false,
                PaqueteEntregableDomain.VACIO))
                .isInstanceOf(PaqueteEntregableEnPreparacionException.class);
    }

    @Test
    void debePriorizarExistenciaSobreAcceso() {
        // Arrange
        var criteria = criteria(UtilUUID.generarNuevoUUID(), false, false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                criteria, EntregableProyectoGradoDomain.VACIO, estado(EstadoEntregable.DISPONIBLE_EVALUACION), false, PaqueteEntregableDomain.VACIO))
                .isInstanceOf(EntregableProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debePriorizarAccesoSobrePreparacion() {
        // Arrange
        var criteria = criteria(UtilUUID.generarNuevoUUID(), true, true);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                criteria, entregable(UtilUUID.generarNuevoUUID()), estado(EstadoEntregable.DISPONIBLE_EVALUACION), false,
                PaqueteEntregableDomain.VACIO))
                .isInstanceOf(ActorSinAccesoPaqueteEntregableException.class);
    }

    @Test
    void debePasar_cuandoTodoEsValido() {
        // Arrange
        var actor = UtilUUID.generarNuevoUUID();
        var criteria = criteria(actor, false, true);
        var paquete = paquete();

        // Act & Assert
        assertThatCode(() -> validator.validar(criteria,
                entregable(UtilUUID.generarNuevoUUID()), estado(EstadoEntregable.DISPONIBLE_EVALUACION), true, paquete))
                .doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoElEntregableEstaRechazado() {
        // Arrange
        var actor = UtilUUID.generarNuevoUUID();
        var criteria = criteria(actor, true, false);
        var rechazado = entregable(actor);
        var paquete = paquete();

        // Act & Assert
        assertThatCode(() -> validator.validar(criteria, rechazado, estado(EstadoEntregable.RECHAZADO), false, paquete)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoNoHayEstadoActual() {
        // Arrange
        var actor = UtilUUID.generarNuevoUUID();
        var criteria = criteria(actor, true, false);
        var entregable = entregable(actor);
        var paquete = paquete();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, entregable,
                EstadoEntregableProyectoGradoDomain.VACIO, false, paquete))
                .isInstanceOf(EstadoEntregableProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarCancelado_cuandoElEntregableEstaCanceladoAntesDeDisponible() {
        // Arrange
        var actor = UtilUUID.generarNuevoUUID();
        var criteria = criteria(actor, true, false);
        var cancelado = entregable(actor);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, cancelado, estado(EstadoEntregable.CANCELADO), false,
                PaqueteEntregableDomain.VACIO))
                .isInstanceOf(PaqueteEntregableCanceladoException.class);
    }
}
