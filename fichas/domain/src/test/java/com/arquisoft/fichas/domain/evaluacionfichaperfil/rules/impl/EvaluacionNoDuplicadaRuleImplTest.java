package com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.exception.EvaluacionFichaPerfilDuplicadaException;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.DisponibilidadEvaluacionFicha;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.EvaluacionExistente;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionNoDuplicadaRuleImplTest {

    private final EvaluacionNoDuplicadaRuleImpl regla = new EvaluacionNoDuplicadaRuleImpl();

    @ParameterizedTest
    @EnumSource(value = EstadoEvaluacion.class, names = {
            "EN_EVALUACION", "APROBADA", "APROBADA_CON_OBSERVACIONES", "NO_APROBADA"})
    void debeLanzarDuplicada_cuandoLaExistenteNoEstaDescartada(EstadoEvaluacion estado) {
        // Arrange
        var existente = new EvaluacionExistente(UtilUUID.generarNuevoUUID(), estado);
        var disponibilidad = new DisponibilidadEvaluacionFicha(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), existente);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(disponibilidad))
                .isInstanceOf(EvaluacionFichaPerfilDuplicadaException.class);
    }

    @Test
    void debePasar_cuandoNoHayEvaluacionExistente() {
        // Arrange
        var disponibilidad = new DisponibilidadEvaluacionFicha(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), EvaluacionExistente.VACIO);

        // Act & Assert
        assertThatCode(() -> regla.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoLaExistenteEstaDescartada() {
        // Arrange
        var existente = new EvaluacionExistente(UtilUUID.generarNuevoUUID(), EstadoEvaluacion.DESCARTADA);
        var disponibilidad = new DisponibilidadEvaluacionFicha(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), existente);

        // Act & Assert
        assertThatCode(() -> regla.validar(disponibilidad)).doesNotThrowAnyException();
    }
}
