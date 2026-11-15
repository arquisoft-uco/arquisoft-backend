package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.validator.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoPerteneceAsesorException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.CambioPuntajeEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.ContextoEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception.CambioPuntajeEvaluacionAsesorFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception.EvaluacionCuantitativaAsesorInexistenteException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception.PuntajeEvaluacionCuantitativaAsesorExcedeValorItemException;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.ItemCuantitativoAsesorDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarPuntajeEvaluacionCuantitativaAsesorValidatorImplTest {

    private final CambiarPuntajeEvaluacionCuantitativaAsesorValidatorImpl validator =
            new CambiarPuntajeEvaluacionCuantitativaAsesorValidatorImpl();

    private final UUID evaluacionCuantitativaAsesor = UtilUUID.generarNuevoUUID();
    private final UUID evaluacionAsesor = UtilUUID.generarNuevoUUID();
    private final UUID asesor = UtilUUID.generarNuevoUUID();

    @Test
    void debePasar_cuandoTodoEsValido() {
        // Arrange
        var cambio = cambio(asesor, 300);

        // Act & Assert
        assertThatCode(() -> validator.validar(cambio, contexto(asesor, EstadoEvaluacion.EN_PROGRESO), item(300)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarInexistente_cuandoContextoVacio() {
        // Arrange
        var cambio = cambio(asesor, 300);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, ContextoEvaluacionCuantitativaAsesorDomain.VACIO, ItemCuantitativoAsesorDomain.VACIO))
                .isInstanceOf(EvaluacionCuantitativaAsesorInexistenteException.class);
    }

    @Test
    void debeLanzarPropiedad_cuandoAsesorNoEsElDueno() {
        // Arrange
        var cambio = cambio(UtilUUID.generarNuevoUUID(), 300);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, contexto(asesor, EstadoEvaluacion.EN_PROGRESO), item(300)))
                .isInstanceOf(EvaluacionAsesorNoPerteneceAsesorException.class);
    }

    @Test
    void debeLanzarFinalizada_cuandoEvaluacionFinalizada() {
        // Arrange
        var cambio = cambio(asesor, 300);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, contexto(asesor, EstadoEvaluacion.FINALIZADA), item(300)))
                .isInstanceOf(CambioPuntajeEvaluacionAsesorFinalizadaException.class);
    }

    @Test
    void debeLanzarExcede_cuandoPuntajeSuperaElValorDelItem() {
        // Arrange
        var cambio = cambio(asesor, 301);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, contexto(asesor, EstadoEvaluacion.EN_PROGRESO), item(300)))
                .isInstanceOf(PuntajeEvaluacionCuantitativaAsesorExcedeValorItemException.class);
    }

    @Test
    void debePriorizarExistencia_sobrePropiedad() {
        // Arrange
        var cambio = cambio(UtilUUID.generarNuevoUUID(), 301);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, ContextoEvaluacionCuantitativaAsesorDomain.VACIO, ItemCuantitativoAsesorDomain.VACIO))
                .isInstanceOf(EvaluacionCuantitativaAsesorInexistenteException.class);
    }

    @Test
    void debePriorizarPropiedad_sobreEstado() {
        // Arrange
        var cambio = cambio(UtilUUID.generarNuevoUUID(), 300);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, contexto(asesor, EstadoEvaluacion.FINALIZADA), item(300)))
                .isInstanceOf(EvaluacionAsesorNoPerteneceAsesorException.class);
    }

    @Test
    void debePriorizarEstado_sobreExcedeValor() {
        // Arrange
        var cambio = cambio(asesor, 301);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, contexto(asesor, EstadoEvaluacion.FINALIZADA), item(300)))
                .isInstanceOf(CambioPuntajeEvaluacionAsesorFinalizadaException.class);
    }

    private CambioPuntajeEvaluacionCuantitativaAsesorDomain cambio(UUID solicitante, int puntaje) {
        return CambioPuntajeEvaluacionCuantitativaAsesorDomain.crear(evaluacionCuantitativaAsesor, solicitante, puntaje);
    }

    private ContextoEvaluacionCuantitativaAsesorDomain contexto(UUID duenio, EstadoEvaluacion estado) {
        return ContextoEvaluacionCuantitativaAsesorDomain.reconstruir(
                evaluacionCuantitativaAsesor, evaluacionAsesor, duenio, estado);
    }

    private ItemCuantitativoAsesorDomain item(int valor) {
        return ItemCuantitativoAsesorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "Rigor", "Descripción", UtilUUID.generarNuevoUUID(), valor);
    }
}
