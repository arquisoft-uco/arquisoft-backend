package com.arquisoft.proyectos.application.estudianteproyectogrado.query.validator;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.criteria.EstudianteAsesoradoProyectoGradoCriteria;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.validator.impl.ConsultarEstudiantesAsesoradosProyectoGradoValidatorImpl;
import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.AsesorProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEstudiantesAsesoradosProyectoGradoValidatorImplTest {

    private final ConsultarEstudiantesAsesoradosProyectoGradoValidatorImpl validator =
            new ConsultarEstudiantesAsesoradosProyectoGradoValidatorImpl();

    @Test
    void debeValidar_cuandoProyectoExisteYAsesorAsignado() {
        // Arrange
        var criteria = new EstudianteAsesoradoProyectoGradoCriteria(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatCode(() -> validator.validar(criteria, true, true)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarProyectoNoEncontrado_cuandoProyectoNoExiste() {
        // Arrange
        var criteria = new EstudianteAsesoradoProyectoGradoCriteria(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, false, true))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarAsesorNoEncontrado_cuandoAsesorNoAsignado() {
        // Arrange
        var criteria = new EstudianteAsesoradoProyectoGradoCriteria(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, true, false))
                .isInstanceOf(AsesorProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarProyectoNoEncontrado_cuandoAmbosFallan() {
        // Arrange
        var criteria = new EstudianteAsesoradoProyectoGradoCriteria(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, false, false))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }
}
