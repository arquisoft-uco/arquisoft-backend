package com.arquisoft.entregables.application.entregableproyectogrado.command.validator.impl;

import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.ResultadoEvaluacionEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.exception.EntregableProyectoGradoNoEncontradoException;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.EstadoEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.exception.EstadoEntregableProyectoGradoNoEncontradoException;
import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.entregables.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarResultadoEvaluacionEntregableProyectoGradoValidatorImplTest {

    private final RegistrarResultadoEvaluacionEntregableProyectoGradoValidatorImpl validator =
            new RegistrarResultadoEvaluacionEntregableProyectoGradoValidatorImpl();

    private final ResultadoEvaluacionEntregableProyectoGradoDomain resultado =
            ResultadoEvaluacionEntregableProyectoGradoDomain.crear(UtilUUID.generarNuevoUUID(), "APROBADO");
    private final EntregableProyectoGradoDomain entregable = EntregableProyectoGradoDomain.reconstruir(
            resultado.getEntregableProyectoGrado(), UtilUUID.generarNuevoUUID(), 1, UtilUUID.generarNuevoUUID());
    private final EstadoEntregableProyectoGradoDomain estadoActual = EstadoEntregableProyectoGradoDomain.reconstruir(
            UtilUUID.generarNuevoUUID(), resultado.getEntregableProyectoGrado(),
            EstadoEntregable.DISPONIBLE_EVALUACION, Instant.now());
    private final ProyectoGradoDomain proyecto = ProyectoGradoDomain.reconstruir(
            entregable.getProyectoGrado(), "Sistema de riego", EstadoProyecto.EN_PROCESO, null);

    @Test
    void debePasar_cuandoEntregableEstadoYProyectoExisten() {
        // Act & Assert
        assertThatCode(() -> validator.validar(resultado, entregable, estadoActual, proyecto))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarEntregableNoEncontrado_cuandoElEntregableNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                resultado, EntregableProyectoGradoDomain.VACIO, estadoActual, proyecto))
                .isInstanceOf(EntregableProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarEstadoNoEncontrado_cuandoElEntregableNoTieneEstado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                resultado, entregable, EstadoEntregableProyectoGradoDomain.VACIO, proyecto))
                .isInstanceOf(EstadoEntregableProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarProyectoNoEncontrado_cuandoElProyectoNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(resultado, entregable, estadoActual, ProyectoGradoDomain.VACIO))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeGanarLaReglaDelEntregable_cuandoFaltanEntregableEstadoYProyecto() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(resultado, EntregableProyectoGradoDomain.VACIO,
                EstadoEntregableProyectoGradoDomain.VACIO, ProyectoGradoDomain.VACIO))
                .isInstanceOf(EntregableProyectoGradoNoEncontradoException.class);
    }
}
