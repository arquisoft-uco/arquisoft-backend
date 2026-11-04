package com.arquisoft.proyectos.application.estudianteproyectogrado.query.validator;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.validator.impl.ConsultarEstudiantesProyectoGradoCoordinadorValidatorImpl;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoPerteneceCoordinadorException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEstudiantesProyectoGradoCoordinadorValidatorImplTest {

    private final ConsultarEstudiantesProyectoGradoCoordinadorValidatorImpl validator =
            new ConsultarEstudiantesProyectoGradoCoordinadorValidatorImpl();

    @Test
    void debePasar_cuandoCoordinadorEsElAsignado() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(proyectoGrado, coordinador, coordinador))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoProyectoInexistente() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(UtilUUID.generarNuevoUUID(),
                UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.generarNuevoUUID()))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoPerteneceCoordinador_cuandoProyectoEsDeOtro() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID()))
                .isInstanceOf(ProyectoGradoNoPerteneceCoordinadorException.class);
    }
}
