package com.arquisoft.proyectos.application.proyectogrado.query.validator;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.DetalleProyectoGradoAsesorCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.validator.impl.ConsultarDetalleProyectoGradoAsesorValidatorImpl;
import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.AsesorProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarDetalleProyectoGradoAsesorValidatorImplTest {

    private final ConsultarDetalleProyectoGradoAsesorValidatorImpl validator =
            new ConsultarDetalleProyectoGradoAsesorValidatorImpl();

    @Test
    void debeValidar_cuandoProyectoExisteYAsesorAsignado() {
        // Arrange
        var criteria = new DetalleProyectoGradoAsesorCriteria(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatCode(() -> validator.validar(criteria, true, true)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarProyectoNoEncontrado_cuandoProyectoNoExiste() {
        // Arrange
        var criteria = new DetalleProyectoGradoAsesorCriteria(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, false, true))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarAsesorNoEncontrado_cuandoAsesorNoAsignado() {
        // Arrange
        var criteria = new DetalleProyectoGradoAsesorCriteria(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, true, false))
                .isInstanceOf(AsesorProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarProyectoNoEncontrado_cuandoAmbosFallan() {
        // Arrange
        var criteria = new DetalleProyectoGradoAsesorCriteria(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, false, false))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }
}
