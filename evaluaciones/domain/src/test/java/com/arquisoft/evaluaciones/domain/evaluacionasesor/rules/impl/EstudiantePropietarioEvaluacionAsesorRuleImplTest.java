package com.arquisoft.evaluaciones.domain.evaluacionasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacionasesor.exception.EvaluacionAsesorNoPerteneceEstudianteException;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.model.PropiedadEvaluacionAsesorEstudiante;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudiantePropietarioEvaluacionAsesorRuleImplTest {

    private final EstudiantePropietarioEvaluacionAsesorRuleImpl rule = new EstudiantePropietarioEvaluacionAsesorRuleImpl();

    @Test
    void debePermitir_cuandoEstudianteVinculado() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var propiedad = new PropiedadEvaluacionAsesorEstudiante(
                UtilUUID.generarNuevoUUID(), List.of(UtilUUID.generarNuevoUUID(), estudiante), estudiante);

        // Act & Assert
        assertThatCode(() -> rule.validar(propiedad)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoEstudianteNoVinculado() {
        // Arrange
        var propiedad = new PropiedadEvaluacionAsesorEstudiante(
                UtilUUID.generarNuevoUUID(), List.of(UtilUUID.generarNuevoUUID()), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(propiedad))
                .isInstanceOfSatisfying(EvaluacionAsesorNoPerteneceEstudianteException.class, exception ->
                        assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.EvaluacionAsesor.NO_PERTENECE_ESTUDIANTE));
    }

    @Test
    void debeLanzar_cuandoListaVacia() {
        // Arrange
        var propiedad = new PropiedadEvaluacionAsesorEstudiante(
                UtilUUID.generarNuevoUUID(), List.of(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(propiedad))
                .isInstanceOf(EvaluacionAsesorNoPerteneceEstudianteException.class);
    }
}
