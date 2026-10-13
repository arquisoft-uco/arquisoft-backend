package com.arquisoft.proyectos.application.asesorproyectogrado.query.validator;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.criteria.AsesorProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.validator.impl.ConsultarAsesoresProyectoGradoEstudianteValidatorImpl;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.EstudianteNoVinculadoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarAsesoresProyectoGradoEstudianteValidatorImplTest {

    private final ConsultarAsesoresProyectoGradoEstudianteValidatorImpl validator =
            new ConsultarAsesoresProyectoGradoEstudianteValidatorImpl();

    @Test
    void debePasar_cuandoExisteYVinculado() {
        // Arrange
        var criteria = new AsesorProyectoGradoEstudianteCriteria(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatCode(() -> validator.validar(criteria, true, true)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarProyectoNoEncontrado_cuandoNoExiste() {
        // Arrange
        var criteria = new AsesorProyectoGradoEstudianteCriteria(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, false, true))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debePriorizarExistencia_cuandoNoExisteNiVinculado() {
        // Arrange
        var criteria = new AsesorProyectoGradoEstudianteCriteria(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, false, false))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoVinculado_cuandoExisteSinVinculo() {
        // Arrange
        var criteria = new AsesorProyectoGradoEstudianteCriteria(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, true, false))
                .isInstanceOf(EstudianteNoVinculadoException.class);
    }
}
