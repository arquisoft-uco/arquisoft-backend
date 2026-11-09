package com.arquisoft.entregables.application.entregableproyectogrado.command.validator.impl;

import com.arquisoft.entregables.domain.entregableproyectogrado.CambioEstadoEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.exception.CancelacionEntregableNoPermitidaException;
import com.arquisoft.entregables.domain.entregableproyectogrado.exception.CierreEntregableNoPermitidoException;
import com.arquisoft.entregables.domain.entregableproyectogrado.exception.EntregableProyectoGradoNoEncontradoException;
import com.arquisoft.entregables.domain.entregableproyectogrado.exception.EstadoDestinoEntregableNoPermitidoException;
import com.arquisoft.entregables.domain.entregableproyectogrado.exception.EstadoEntregableTerminalException;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.entregables.domain.proyectogrado.exception.ProyectoGradoFinalizadoException;
import com.arquisoft.entregables.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarEstadoEntregableProyectoGradoValidatorImplTest {

    private final CambiarEstadoEntregableProyectoGradoValidatorImpl validator =
            new CambiarEstadoEntregableProyectoGradoValidatorImpl();

    private static final ProyectoGradoDomain PROYECTO =
            ProyectoGradoDomain.crear(UtilUUID.generarNuevoUUID(), "Sistema de gestion");

    private static EntregableProyectoGradoDomain entregable(EstadoEntregable estado) {
        return EntregableProyectoGradoDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                1, UtilUUID.generarNuevoUUID(), estado);
    }

    private static CambioEstadoEntregableProyectoGradoDomain cambio(String estado) {
        return CambioEstadoEntregableProyectoGradoDomain.crear(UtilUUID.generarNuevoUUID(), estado);
    }

    @ParameterizedTest
    @EnumSource(value = EstadoEntregable.class, mode = EnumSource.Mode.INCLUDE, names = {"DISPONIBLE_EVALUACION"})
    void debePasar_cuandoCancelaDesdeDisponibleEvaluacion(EstadoEntregable origen) {
        // Arrange
        var cambio = cambio("CANCELADO");
        var entregable = entregable(origen);

        // Act & Assert
        assertThatCode(() -> validator.validar(cambio, entregable, PROYECTO)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @CsvSource({
            "CERRADO,CANCELADO", "CERRADO,CERRADO", "CERRADO,RECHAZADO", "RECHAZADO,CANCELADO", "RECHAZADO,CERRADO",
            "CANCELADO,CANCELADO", "CANCELADO,CERRADO", "CANCELADO,RECHAZADO"})
    void debeLanzarTerminal_cuandoElEntregableEsTerminal(EstadoEntregable origen, String destino) {
        // Arrange
        var cambio = cambio(destino);
        var entregable = entregable(origen);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, entregable, PROYECTO))
                .isInstanceOf(EstadoEntregableTerminalException.class);
    }

    @Test
    void debePriorizarTerminal_cuandoElDestinoTambienEsInvalido() {
        // Arrange
        var cambio = cambio("EN_EVALUACION");
        var entregable = entregable(EstadoEntregable.CERRADO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, entregable, PROYECTO))
                .isInstanceOf(EstadoEntregableTerminalException.class);
    }

    @Test
    void debePasar_cuandoCierraDesdeAprobado() {
        // Arrange
        var cambio = cambio("CERRADO");
        var entregable = entregable(EstadoEntregable.APROBADO);

        // Act & Assert
        assertThatCode(() -> validator.validar(cambio, entregable, PROYECTO)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoElEntregableEsVacio() {
        // Arrange
        var cambio = cambio("CANCELADO");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, EntregableProyectoGradoDomain.VACIO, PROYECTO))
                .isInstanceOf(EntregableProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarDestinoNoPermitido_cuandoElDestinoEsOtro() {
        // Arrange
        var cambio = cambio("RECHAZADO");
        var entregable = entregable(EstadoEntregable.APROBADO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, entregable, PROYECTO))
                .isInstanceOf(EstadoDestinoEntregableNoPermitidoException.class);
    }

    @Test
    void debeLanzarCierreNoPermitido_cuandoCierraDesdeEnEvaluacion() {
        // Arrange
        var cambio = cambio("CERRADO");
        var entregable = entregable(EstadoEntregable.EN_EVALUACION);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, entregable, PROYECTO))
                .isInstanceOf(CierreEntregableNoPermitidoException.class);
    }

    @Test
    void debeLanzarProyectoNoEncontrado_cuandoElProyectoDelEntregableNoExiste() {
        // Arrange
        var cambio = cambio("CANCELADO");
        var entregable = entregable(EstadoEntregable.APROBADO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, entregable, ProyectoGradoDomain.VACIO))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debePriorizarProyectoFinalizado_cuandoElEntregableTambienEsTerminal() {
        // Arrange
        var cambio = cambio("CANCELADO");
        var entregable = entregable(EstadoEntregable.CERRADO);
        var finalizado = ProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "Sistema de gestion", EstadoProyecto.FINALIZADO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, entregable, finalizado))
                .isInstanceOf(ProyectoGradoFinalizadoException.class);
    }

    @Test
    void debePriorizarExistencia_cuandoFallanTodasLasReglas() {
        // Arrange
        var cambio = cambio("RECHAZADO");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, EntregableProyectoGradoDomain.VACIO, PROYECTO))
                .isInstanceOf(EntregableProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debePriorizarTerminal_cuandoElEntregableEstaRechazadoYElDestinoEsInvalido() {
        // Arrange
        var cambio = cambio("EN_EVALUACION");
        var entregable = entregable(EstadoEntregable.RECHAZADO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, entregable, PROYECTO))
                .isInstanceOf(EstadoEntregableTerminalException.class);
    }

    @ParameterizedTest
    @EnumSource(value = EstadoEntregable.class, mode = EnumSource.Mode.INCLUDE,
            names = {"EN_EVALUACION", "APROBADO", "APROBADO_CON_OBSERVACIONES"})
    void debeLanzarCancelacionNoPermitida_cuandoCancelaDesdeUnEstadoDistintoDeDisponible(EstadoEntregable origen) {
        // Arrange
        var cambio = cambio("CANCELADO");
        var entregable = entregable(origen);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, entregable, PROYECTO))
                .isInstanceOf(CancelacionEntregableNoPermitidaException.class);
    }
}
