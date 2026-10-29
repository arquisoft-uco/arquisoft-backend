package com.arquisoft.entregables.application.entregableproyectogrado.query.validator.impl;

import com.arquisoft.entregables.domain.proyectogrado.exception.EstudianteNoPerteneceProyectoGradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEntregablesProyectoGradoEstudianteValidatorImplTest {

    private final ConsultarEntregablesProyectoGradoEstudianteValidatorImpl validator =
            new ConsultarEntregablesProyectoGradoEstudianteValidatorImpl();

    @Test
    void noDebeLanzar_cuandoPertenece() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(estudiante, proyecto, true)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarEstudianteNoPertenece_cuandoNoPertenece() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(estudiante, proyecto, false))
                .isInstanceOf(EstudianteNoPerteneceProyectoGradoException.class);
    }
}
