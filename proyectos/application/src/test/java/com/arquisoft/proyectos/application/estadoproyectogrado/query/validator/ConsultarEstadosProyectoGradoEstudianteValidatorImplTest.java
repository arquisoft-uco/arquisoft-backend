package com.arquisoft.proyectos.application.estadoproyectogrado.query.validator;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.criteria.EstadoProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.validator.impl.ConsultarEstadosProyectoGradoEstudianteValidatorImpl;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.EstudianteNoVinculadoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEstadosProyectoGradoEstudianteValidatorImplTest {

    private final ConsultarEstadosProyectoGradoEstudianteValidatorImpl validator =
            new ConsultarEstadosProyectoGradoEstudianteValidatorImpl();

    private EstadoProyectoGradoEstudianteCriteria criteria() {
        return new EstadoProyectoGradoEstudianteCriteria(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
    }

    @Test
    void debePasar_cuandoExisteYEstaVinculado() {
        // Arrange
        var criteria = criteria();

        // Act & Assert
        assertThatCode(() -> validator.validar(criteria, true, true)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarProyectoNoEncontrado_cuandoNoExiste() {
        // Arrange
        var criteria = criteria();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, false, true))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarEstudianteNoVinculado_cuandoNoEstaVinculado() {
        // Arrange
        var criteria = criteria();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, true, false))
                .isInstanceOf(EstudianteNoVinculadoException.class);
    }

    @Test
    void debePriorizarExistencia_cuandoNoExisteNiEstaVinculado() {
        // Arrange
        var criteria = criteria();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, false, false))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }
}
