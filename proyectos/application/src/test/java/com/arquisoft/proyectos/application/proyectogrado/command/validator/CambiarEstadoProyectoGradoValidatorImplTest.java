package com.arquisoft.proyectos.application.proyectogrado.command.validator;

import com.arquisoft.proyectos.application.proyectogrado.command.validator.impl.CambiarEstadoProyectoGradoValidatorImpl;
import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.AsesorProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoproyectogrado.exception.EstadoProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.proyectogrado.CambioEstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoSinEntregableException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.TransicionEstadoProyectoNoPermitidaException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.UltimoEntregableNoDefinitivoException;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarEstadoProyectoGradoValidatorImplTest {

    private final CambiarEstadoProyectoGradoValidatorImpl validator = new CambiarEstadoProyectoGradoValidatorImpl();

    private final ProyectoGradoDomain proyecto = ProyectoGradoDomain.crear(
            UtilUUID.generarNuevoUUID(), "Sistema de gestión", UtilUUID.generarNuevoUUID());
    private final EstadoProyectoGradoDomain enProceso = EstadoProyectoGradoDomain.crear(proyecto.getId());
    private final EstadoProyectoGradoDomain finalizado = EstadoProyectoGradoDomain.reconstruir(
            UtilUUID.generarNuevoUUID(), proyecto.getId(), EstadoProyecto.FINALIZADO,
            UtilFecha.generarInstanteActual(), null);
    private final EstadoProyectoGradoDomain atrasado = EstadoProyectoGradoDomain.reconstruir(
            UtilUUID.generarNuevoUUID(), proyecto.getId(), EstadoProyecto.ATRASADO,
            UtilFecha.generarInstanteActual(), null);
    private final CambioEstadoProyectoGradoDomain cambio = CambioEstadoProyectoGradoDomain.crear(
            proyecto.getId(), UtilUUID.generarNuevoUUID(), "FINALIZADO");

    private EntregableProyectoGradoDomain entregable(EstadoEntregable estado) {
        return EntregableProyectoGradoDomain.reconstruir(UtilUUID.generarNuevoUUID(), proyecto.getId(), 1, estado,
                Instant.parse("2026-11-01T10:00:00Z"));
    }

    @Test
    void debePasar_cuandoTodasLasReglasSeCumplen() {
        // Act & Assert
        assertThatCode(() -> validator.validar(cambio, proyecto, enProceso, true, entregable(EstadoEntregable.CERRADO)))
                .doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoElProyectoEstaAtrasado() {
        // Act & Assert
        assertThatCode(() -> validator.validar(cambio, proyecto, atrasado, true,
                entregable(EstadoEntregable.CERRADO)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarUltimoNoDefinitivo_cuandoElUltimoEstaRechazado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, proyecto, enProceso, true, entregable(EstadoEntregable.RECHAZADO)))
                .isInstanceOf(UltimoEntregableNoDefinitivoException.class);
    }

    @Test
    void debeLanzarUltimoNoDefinitivo_cuandoElUltimoEstaCancelado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, proyecto, enProceso, true, entregable(EstadoEntregable.CANCELADO)))
                .isInstanceOf(UltimoEntregableNoDefinitivoException.class);
    }

    @Test
    void debeLanzarProyectoNoEncontrado_cuandoElProyectoNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, ProyectoGradoDomain.VACIO, enProceso, true,
                entregable(EstadoEntregable.CERRADO)))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarEstadoNoEncontrado_cuandoNoHayEstadoActual() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, proyecto, EstadoProyectoGradoDomain.VACIO, true,
                entregable(EstadoEntregable.CERRADO)))
                .isInstanceOf(EstadoProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarAsesorNoEncontrado_cuandoElAsesorNoEstaAsignado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, proyecto, enProceso, false,
                entregable(EstadoEntregable.CERRADO)))
                .isInstanceOf(AsesorProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarTransicionNoPermitida_cuandoElProyectoYaEstaFinalizado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, proyecto, finalizado, true,
                entregable(EstadoEntregable.CERRADO)))
                .isInstanceOf(TransicionEstadoProyectoNoPermitidaException.class);
    }

    @Test
    void debeLanzarSinEntregable_cuandoNoHayUltimoEntregable() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, proyecto, enProceso, true,
                EntregableProyectoGradoDomain.VACIO))
                .isInstanceOf(ProyectoGradoSinEntregableException.class);
    }

    @Test
    void debeLanzarUltimoNoDefinitivo_cuandoElUltimoEstaDisponibleEvaluacion() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, proyecto, enProceso, true,
                entregable(EstadoEntregable.DISPONIBLE_EVALUACION)))
                .isInstanceOf(UltimoEntregableNoDefinitivoException.class);
    }

    @Test
    void debePriorizarEnOrden_cuandoVariasReglasFallanALaVez() {
        // Arrange
        var noTerminal = entregable(EstadoEntregable.DISPONIBLE_EVALUACION);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, ProyectoGradoDomain.VACIO,
                EstadoProyectoGradoDomain.VACIO, false, EntregableProyectoGradoDomain.VACIO))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
        assertThatThrownBy(() -> validator.validar(cambio, proyecto, EstadoProyectoGradoDomain.VACIO, false,
                noTerminal))
                .isInstanceOf(EstadoProyectoGradoNoEncontradoException.class);
        assertThatThrownBy(() -> validator.validar(cambio, proyecto, finalizado, false, noTerminal))
                .isInstanceOf(AsesorProyectoGradoNoEncontradoException.class);
        assertThatThrownBy(() -> validator.validar(cambio, proyecto, finalizado, true,
                EntregableProyectoGradoDomain.VACIO))
                .isInstanceOf(TransicionEstadoProyectoNoPermitidaException.class);
        assertThatThrownBy(() -> validator.validar(cambio, proyecto, enProceso, true,
                EntregableProyectoGradoDomain.VACIO))
                .isInstanceOf(ProyectoGradoSinEntregableException.class);
    }
}
