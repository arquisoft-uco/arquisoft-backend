package com.arquisoft.proyectos.application.estudianteproyectogrado.query.validator;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.criteria.CompaneroProyectoGradoCriteria;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.validator.impl.ConsultarCompanerosProyectoGradoValidatorImpl;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.EstudianteNoVinculadoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarCompanerosProyectoGradoValidatorImplTest {

    private final ConsultarCompanerosProyectoGradoValidatorImpl validator =
            new ConsultarCompanerosProyectoGradoValidatorImpl();

    @Test
    void debePasar_cuandoProyectoExisteYEstudianteVinculado() {
        // Arrange
        var criteria = new CompaneroProyectoGradoCriteria(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatCode(() -> validator.validar(criteria, true, true)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarProyectoNoEncontrado_cuandoProyectoNoExisteAunqueNoVinculado() {
        // Arrange
        var criteria = new CompaneroProyectoGradoCriteria(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, false, false))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarEstudianteNoVinculado_cuandoProyectoExisteYNoVinculado() {
        // Arrange
        var criteria = new CompaneroProyectoGradoCriteria(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, true, false))
                .isInstanceOf(EstudianteNoVinculadoException.class);
    }
}
