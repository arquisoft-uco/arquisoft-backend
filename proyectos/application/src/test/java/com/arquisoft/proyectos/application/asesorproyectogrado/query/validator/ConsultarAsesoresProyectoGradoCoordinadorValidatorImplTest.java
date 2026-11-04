package com.arquisoft.proyectos.application.asesorproyectogrado.query.validator;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.validator.impl.ConsultarAsesoresProyectoGradoCoordinadorValidatorImpl;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoPerteneceCoordinadorException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarAsesoresProyectoGradoCoordinadorValidatorImplTest {

    private final ConsultarAsesoresProyectoGradoCoordinadorValidatorImpl validator =
            new ConsultarAsesoresProyectoGradoCoordinadorValidatorImpl();

    @Test
    void debePasar_cuandoCoordinadorEsElDuenio() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(proyectoGrado, coordinador, coordinador))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoProyectoInexistenteYSolicitanteDistinto() {
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
