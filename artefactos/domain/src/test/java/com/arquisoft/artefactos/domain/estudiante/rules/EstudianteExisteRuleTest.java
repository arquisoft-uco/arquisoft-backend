package com.arquisoft.artefactos.domain.estudiante.rules;

import com.arquisoft.artefactos.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.artefactos.domain.estudiante.model.ExistenciaEstudiante;
import com.arquisoft.artefactos.domain.estudiante.rules.impl.EstudianteExisteRuleImpl;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudianteExisteRuleTest {

    @Test
    void debeLanzar_cuandoElEstudianteNoExiste() {
        // Arrange
        var regla = new EstudianteExisteRuleImpl();
        var entrada = new ExistenciaEstudiante(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(EstudianteNoEncontradoException.class,
                        ex -> assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.Estudiante.NO_ENCONTRADO));
    }

    @Test
    void noDebeLanzar_cuandoElEstudianteExiste() {
        // Arrange
        var regla = new EstudianteExisteRuleImpl();
        var entrada = new ExistenciaEstudiante(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }
}
