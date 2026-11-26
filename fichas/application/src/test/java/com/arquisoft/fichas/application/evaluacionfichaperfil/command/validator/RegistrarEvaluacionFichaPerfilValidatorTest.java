package com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator.impl.RegistrarEvaluacionFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilNoDisponibleParaEvaluacionException;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.exception.EvaluacionFichaPerfilDuplicadaException;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.EvaluacionExistente;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.exception.RepresentanteComiteNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarEvaluacionFichaPerfilValidatorTest {

    private final RegistrarEvaluacionFichaPerfilValidatorImpl validator =
            new RegistrarEvaluacionFichaPerfilValidatorImpl();

    private final UUID representante = UUID.randomUUID();
    private final UUID ficha = UUID.randomUUID();
    private final EvaluacionFichaPerfilDomain evaluacion =
            EvaluacionFichaPerfilDomain.crear(representante, ficha);
    private final EvaluacionExistente enEvaluacion =
            new EvaluacionExistente(UUID.randomUUID(), EstadoEvaluacion.EN_EVALUACION);
    private final EstadoFichaPerfilDomain estadoDisponible = EstadoFichaPerfilDomain.reconstruir(
            UUID.randomUUID(), ficha, EstadoFicha.DISPONIBLE_PARA_EVALUACION, Instant.now());

    @Test
    void debePasar_cuandoFichaYRepresentanteExistenYNoHayDuplicado() {
        // Act / Assert
        assertThatCode(() -> validator.validar(evaluacion, true, true, EvaluacionExistente.VACIO, estadoDisponible))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarFichaNoEncontrada_cuandoLaFichaNoExiste() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(evaluacion, false, true, EvaluacionExistente.VACIO, estadoDisponible))
                .isInstanceOf(FichaPerfilNoEncontradaException.class)
                .hasMessageContaining(ficha.toString());
    }

    @Test
    void debeLanzarRepresentanteNoEncontrado_cuandoElRepresentanteNoExiste() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(evaluacion, true, false, EvaluacionExistente.VACIO, estadoDisponible))
                .isInstanceOf(RepresentanteComiteNoEncontradoException.class)
                .hasMessageContaining(representante.toString());
    }

    @Test
    void debeLanzarEvaluacionDuplicada_cuandoLaEvaluacionYaExiste() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(evaluacion, true, true, enEvaluacion, estadoDisponible))
                .isInstanceOf(EvaluacionFichaPerfilDuplicadaException.class);
    }

    @Test
    void debePasar_cuandoLaExistenteEstaDescartada() {
        // Arrange
        var descartada = new EvaluacionExistente(UUID.randomUUID(), EstadoEvaluacion.DESCARTADA);

        // Act / Assert
        assertThatCode(() -> validator.validar(evaluacion, true, true, descartada, estadoDisponible))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoFicha.class, names = {
            "EN_CONSTRUCCION", "APROBADA", "APROBADA_CON_OBSERVACIONES", "NO_APROBADA", "DESCARTADA"})
    void debeLanzarFichaNoDisponible_cuandoLaFichaNoEstaDisponible(EstadoFicha estado) {
        // Arrange
        var estadoFicha = EstadoFichaPerfilDomain.reconstruir(UUID.randomUUID(), ficha, estado, Instant.now());

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(evaluacion, true, true, EvaluacionExistente.VACIO, estadoFicha))
                .isInstanceOf(FichaPerfilNoDisponibleParaEvaluacionException.class);
    }


    @ParameterizedTest
    @EnumSource(value = EstadoFicha.class, names = {
            "EN_CONSTRUCCION", "APROBADA", "APROBADA_CON_OBSERVACIONES", "NO_APROBADA", "DESCARTADA"})
    void debeLanzarFichaNoDisponible_cuandoLaExistenteEstaDescartadaYLaFichaNoEstaDisponible(EstadoFicha estado) {
        // Arrange
        var descartada = new EvaluacionExistente(UtilUUID.generarNuevoUUID(), EstadoEvaluacion.DESCARTADA);
        var estadoFicha = EstadoFichaPerfilDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), ficha, estado, Instant.now());

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(evaluacion, true, true, descartada, estadoFicha))
                .isInstanceOf(FichaPerfilNoDisponibleParaEvaluacionException.class);
    }
    @Test
    void debeAplicarDuplicadaAntesQueDisponibilidad_cuandoAmbasReglasFallan() {
        // Arrange
        var estadoAprobada = EstadoFichaPerfilDomain.reconstruir(
                UUID.randomUUID(), ficha, EstadoFicha.APROBADA, Instant.now());

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(evaluacion, true, true, enEvaluacion, estadoAprobada))
                .isInstanceOf(EvaluacionFichaPerfilDuplicadaException.class);
    }

    @Test
    void debeReportarPrimeroLaAusenciaDeLaFicha_cuandoTodasLasReglasFallan() {
        // Act / Assert — el orden es parte del contrato
        assertThatThrownBy(() -> validator.validar(evaluacion, false, false, enEvaluacion, estadoDisponible))
                .isInstanceOf(FichaPerfilNoEncontradaException.class);
    }
}
