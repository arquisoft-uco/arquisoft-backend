package com.arquisoft.entregables.application.paqueteentregable.query.validator.impl;

import com.arquisoft.entregables.application.paqueteentregable.query.criteria.DescargaPaqueteEntregableCriteria;
import com.arquisoft.entregables.domain.entregableproyectogrado.exception.EntregableProyectoGradoNoEncontradoException;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;
import com.arquisoft.entregables.domain.paqueteentregable.exception.ActorSinAccesoPaqueteEntregableException;
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

    private static DescargaPaqueteEntregableCriteria criteria(UUID actor, boolean comoAsesor, boolean comoEstudiante) {
        return new DescargaPaqueteEntregableCriteria(UtilUUID.generarNuevoUUID(), actor, comoAsesor, comoEstudiante);
    }

    @Test
    void debeLanzarNoEncontrado_cuandoEntregableInexistente() {
        // Arrange
        var criteria = criteria(UtilUUID.generarNuevoUUID(), true, false);
        var inexistente = UtilUUID.obtenerUUIDPorDefecto();
        var paquete = paquete();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, inexistente, false, paquete))
                .isInstanceOf(EntregableProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarSinAcceso_cuandoActorAjeno() {
        // Arrange
        var criteria = criteria(UtilUUID.generarNuevoUUID(), true, true);
        var otroAsesor = UtilUUID.generarNuevoUUID();
        var paquete = paquete();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, otroAsesor, false, paquete))
                .isInstanceOf(ActorSinAccesoPaqueteEntregableException.class);
    }

    @Test
    void debeLanzarEnPreparacion_cuandoSinPaquete() {
        // Arrange
        var actor = UtilUUID.generarNuevoUUID();
        var criteria = criteria(actor, true, false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, actor, false, PaqueteEntregableDomain.VACIO))
                .isInstanceOf(PaqueteEntregableEnPreparacionException.class);
    }

    @Test
    void debePriorizarExistenciaSobreAcceso() {
        // Arrange
        var criteria = criteria(UtilUUID.generarNuevoUUID(), false, false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                criteria, UtilUUID.obtenerUUIDPorDefecto(), false, PaqueteEntregableDomain.VACIO))
                .isInstanceOf(EntregableProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debePriorizarAccesoSobrePreparacion() {
        // Arrange
        var criteria = criteria(UtilUUID.generarNuevoUUID(), true, true);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                criteria, UtilUUID.generarNuevoUUID(), false, PaqueteEntregableDomain.VACIO))
                .isInstanceOf(ActorSinAccesoPaqueteEntregableException.class);
    }

    @Test
    void debePasar_cuandoTodoEsValido() {
        // Arrange
        var actor = UtilUUID.generarNuevoUUID();
        var criteria = criteria(actor, false, true);
        var paquete = paquete();

        // Act & Assert
        assertThatCode(() -> validator.validar(criteria, UtilUUID.generarNuevoUUID(), true, paquete))
                .doesNotThrowAnyException();
    }
}
