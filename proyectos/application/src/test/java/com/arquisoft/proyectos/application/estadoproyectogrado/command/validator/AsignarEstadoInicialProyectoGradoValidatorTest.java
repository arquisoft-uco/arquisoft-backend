package com.arquisoft.proyectos.application.estadoproyectogrado.command.validator;

import com.arquisoft.proyectos.application.estadoproyectogrado.command.validator.impl.AsignarEstadoInicialProyectoGradoValidatorImpl;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignarEstadoInicialProyectoGradoValidatorTest {

    private final AsignarEstadoInicialProyectoGradoValidatorImpl validator =
            new AsignarEstadoInicialProyectoGradoValidatorImpl();

    @Test
    void debePasar_cuandoElProyectoExiste() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(proyectoGrado, true)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarProyectoNoEncontrado_cuandoElProyectoNoExiste() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(proyectoGrado, false))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class)
                .extracting("codigoError")
                .isEqualTo(ProyectosCodes.ProyectoGrado.NO_ENCONTRADO);
    }
}
