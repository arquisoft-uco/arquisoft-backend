package com.arquisoft.entregables.application.entregableproyectogrado.command.validator.impl;

import com.arquisoft.entregables.domain.entregableproyectogrado.CambioEstadoEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.exception.CancelacionEntregableNoPermitidaException;
import com.arquisoft.entregables.domain.entregableproyectogrado.exception.CierreEntregableNoPermitidoException;
import com.arquisoft.entregables.domain.asesorproyectogrado.exception.AsesorNoAsignadoProyectoGradoException;
import com.arquisoft.entregables.domain.entregableproyectogrado.exception.DisponibilidadEvaluacionEntregableNoPermitidaException;
import com.arquisoft.entregables.domain.entregableproyectogrado.exception.EntregableProyectoGradoNoEncontradoException;
import com.arquisoft.entregables.domain.entregableproyectogrado.exception.EstadoDestinoEntregableNoPermitidoException;
import com.arquisoft.entregables.domain.entregableproyectogrado.exception.EstadoEntregableTerminalException;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.EstadoEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.exception.EstadoEntregableProyectoGradoNoEncontradoException;
import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.entregables.domain.proyectogrado.exception.ProyectoGradoCanceladoException;
import com.arquisoft.entregables.domain.proyectogrado.exception.ProyectoGradoFinalizadoException;
import com.arquisoft.entregables.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarEstadoEntregableProyectoGradoValidatorImplTest {

    private static final UUID ASESOR = UtilUUID.generarNuevoUUID();

    private static final ProyectoGradoDomain PROYECTO =
            ProyectoGradoDomain.crear(UtilUUID.generarNuevoUUID(), "Sistema de gestion");

    private final CambiarEstadoEntregableProyectoGradoValidatorImpl validator =
            new CambiarEstadoEntregableProyectoGradoValidatorImpl();

    private static EstadoEntregableProyectoGradoDomain estado(EstadoEntregable estado) {
        return EstadoEntregableProyectoGradoDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                estado, Instant.now());
    }

    private static EntregableProyectoGradoDomain entregable() {
        return EntregableProyectoGradoDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                1, ASESOR);
    }

    private static CambioEstadoEntregableProyectoGradoDomain cambio(String estado) {
        return CambioEstadoEntregableProyectoGradoDomain.crear(UtilUUID.generarNuevoUUID(), estado, ASESOR);
    }

    @ParameterizedTest
    @EnumSource(value = EstadoEntregable.class, mode = EnumSource.Mode.INCLUDE, names = {"NUEVO", "RECHAZADO"})
    void debePasar_cuandoCancelaDesdeNuevoORechazado(EstadoEntregable origen) {
        // Arrange
        var cambio = cambio("CANCELADO");
        var entregable = entregable();
        var estado = estado(origen);

        // Act & Assert
        assertThatCode(() -> validator.validar(cambio, entregable, estado, PROYECTO, ASESOR)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @CsvSource({
            "CERRADO,CANCELADO", "CERRADO,CERRADO", "CERRADO,RECHAZADO",
            "CANCELADO,CANCELADO", "CANCELADO,CERRADO", "CANCELADO,RECHAZADO"})
    void debeLanzarTerminal_cuandoElEntregableEsTerminal(EstadoEntregable origen, String destino) {
        // Arrange
        var cambio = cambio(destino);
        var entregable = entregable();
        var estado = estado(origen);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, entregable, estado, PROYECTO, ASESOR))
                .isInstanceOf(EstadoEntregableTerminalException.class);
    }

    @Test
    void debePriorizarTerminal_cuandoElDestinoTambienEsInvalido() {
        // Arrange
        var cambio = cambio("EN_EVALUACION");
        var entregable = entregable();
        var estado = estado(EstadoEntregable.CERRADO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, entregable, estado, PROYECTO, ASESOR))
                .isInstanceOf(EstadoEntregableTerminalException.class);
    }

    @ParameterizedTest
    @EnumSource(value = EstadoEntregable.class, names = {"APROBADO", "RECHAZADO"})
    void debePasar_cuandoCierraDesdeAprobadoORechazado(EstadoEntregable origen) {
        // Arrange
        var cambio = cambio("CERRADO");
        var entregable = entregable();
        var estado = estado(origen);

        // Act & Assert
        assertThatCode(() -> validator.validar(cambio, entregable, estado, PROYECTO, ASESOR)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoElEntregableEsVacio() {
        // Arrange
        var cambio = cambio("CANCELADO");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, EntregableProyectoGradoDomain.VACIO,
                estado(EstadoEntregable.APROBADO), PROYECTO, ASESOR))
                .isInstanceOf(EntregableProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarEstadoNoEncontrado_cuandoElHistorialEstaVacio() {
        // Arrange
        var cambio = cambio("CANCELADO");
        var entregable = entregable();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, entregable,
                EstadoEntregableProyectoGradoDomain.VACIO, PROYECTO, ASESOR))
                .isInstanceOf(EstadoEntregableProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debePriorizarEstadoNoEncontrado_cuandoElProyectoTambienEsVacio() {
        // Arrange
        var cambio = cambio("CANCELADO");
        var entregable = entregable();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, entregable,
                EstadoEntregableProyectoGradoDomain.VACIO, ProyectoGradoDomain.VACIO, ASESOR))
                .isInstanceOf(EstadoEntregableProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarDestinoNoPermitido_cuandoElDestinoEsOtro() {
        // Arrange
        var cambio = cambio("RECHAZADO");
        var entregable = entregable();
        var estado = estado(EstadoEntregable.APROBADO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, entregable, estado, PROYECTO, ASESOR))
                .isInstanceOf(EstadoDestinoEntregableNoPermitidoException.class);
    }

    @Test
    void debeLanzarCierreNoPermitido_cuandoCierraDesdeEnEvaluacion() {
        // Arrange
        var cambio = cambio("CERRADO");
        var entregable = entregable();
        var estado = estado(EstadoEntregable.EN_EVALUACION);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, entregable, estado, PROYECTO, ASESOR))
                .isInstanceOf(CierreEntregableNoPermitidoException.class);
    }

    @Test
    void debeLanzarProyectoNoEncontrado_cuandoElProyectoDelEntregableNoExiste() {
        // Arrange
        var cambio = cambio("CANCELADO");
        var entregable = entregable();
        var estado = estado(EstadoEntregable.APROBADO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, entregable, estado, ProyectoGradoDomain.VACIO, ASESOR))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debePriorizarProyectoFinalizado_cuandoElEntregableTambienEsTerminal() {
        // Arrange
        var cambio = cambio("CANCELADO");
        var entregable = entregable();
        var estado = estado(EstadoEntregable.CERRADO);
        var finalizado = ProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "Sistema de gestion", EstadoProyecto.FINALIZADO, null);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, entregable, estado, finalizado, ASESOR))
                .isInstanceOf(ProyectoGradoFinalizadoException.class);
    }

    @Test
    void debeLanzar_cuandoProyectoCancelado() {
        // Arrange
        var cambio = cambio("CANCELADO");
        var entregable = entregable();
        var estado = estado(EstadoEntregable.APROBADO);
        var cancelado = ProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "Sistema de gestion", EstadoProyecto.CANCELADO, null);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, entregable, estado, cancelado, ASESOR))
                .isInstanceOf(ProyectoGradoCanceladoException.class);
    }

    @Test
    void debePriorizarExistencia_cuandoFallanTodasLasReglas() {
        // Arrange
        var cambio = cambio("RECHAZADO");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, EntregableProyectoGradoDomain.VACIO,
                estado(EstadoEntregable.APROBADO), PROYECTO, ASESOR))
                .isInstanceOf(EntregableProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarDestinoNoPermitido_cuandoElEntregableEstaRechazadoYElDestinoEsOtro() {
        // Arrange
        var cambio = cambio("EN_EVALUACION");
        var entregable = entregable();
        var estado = estado(EstadoEntregable.RECHAZADO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, entregable, estado, PROYECTO, ASESOR))
                .isInstanceOf(EstadoDestinoEntregableNoPermitidoException.class);
    }

    @ParameterizedTest
    @EnumSource(value = EstadoEntregable.class, mode = EnumSource.Mode.INCLUDE,
            names = {"DISPONIBLE_EVALUACION", "EN_EVALUACION", "APROBADO", "APROBADO_CON_OBSERVACIONES"})
    void debeLanzarCancelacionNoPermitida_cuandoCancelaDesdeUnEstadoDistintoDeNuevoYRechazado(EstadoEntregable origen) {
        // Arrange
        var cambio = cambio("CANCELADO");
        var entregable = entregable();
        var estado = estado(origen);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, entregable, estado, PROYECTO, ASESOR))
                .isInstanceOf(CancelacionEntregableNoPermitidaException.class);
    }

    @Test
    void debePasar_cuandoPasaDeNuevoADisponibleEvaluacion() {
        // Arrange
        var cambio = cambio("DISPONIBLE_EVALUACION");
        var entregable = entregable();
        var estado = estado(EstadoEntregable.NUEVO);

        // Act & Assert
        assertThatCode(() -> validator.validar(cambio, entregable, estado, PROYECTO, ASESOR)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoEntregable.class, names = {"DISPONIBLE_EVALUACION", "EN_EVALUACION", "RECHAZADO",
            "APROBADO", "APROBADO_CON_OBSERVACIONES"})
    void debeLanzarDisponibilidadNoPermitida_cuandoElOrigenNoEsNuevo(EstadoEntregable origen) {
        // Arrange
        var cambio = cambio("DISPONIBLE_EVALUACION");
        var entregable = entregable();
        var estado = estado(origen);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, entregable, estado, PROYECTO, ASESOR))
                .isInstanceOf(DisponibilidadEvaluacionEntregableNoPermitidaException.class);
    }

    @Test
    void debeLanzarAsesorNoAsignado_cuandoElAsesorNoEsElVigenteDelProyecto() {
        // Arrange
        var cambio = CambioEstadoEntregableProyectoGradoDomain.crear(
                UtilUUID.generarNuevoUUID(), "DISPONIBLE_EVALUACION", UtilUUID.generarNuevoUUID());
        var entregable = entregable();
        var estado = estado(EstadoEntregable.NUEVO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, entregable, estado, PROYECTO, ASESOR))
                .isInstanceOf(AsesorNoAsignadoProyectoGradoException.class);
    }

    @Test
    void debeLanzarEstadoNoEncontrado_cuandoElHistorialEstaVacioAunqueElAsesorNoSeaElVigente() {
        // Arrange
        var cambio = CambioEstadoEntregableProyectoGradoDomain.crear(
                UtilUUID.generarNuevoUUID(), "CANCELADO", UtilUUID.generarNuevoUUID());
        var entregable = entregable();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, entregable,
                EstadoEntregableProyectoGradoDomain.VACIO, PROYECTO, ASESOR))
                .isInstanceOf(EstadoEntregableProyectoGradoNoEncontradoException.class);
    }
}
