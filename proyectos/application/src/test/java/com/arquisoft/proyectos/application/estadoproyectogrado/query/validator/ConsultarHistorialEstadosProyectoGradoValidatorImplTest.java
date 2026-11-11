package com.arquisoft.proyectos.application.estadoproyectogrado.query.validator;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.criteria.HistorialEstadosProyectoGradoCriteria;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.validator.impl.ConsultarHistorialEstadosProyectoGradoValidatorImpl;
import com.arquisoft.proyectos.domain.estadoproyectogrado.exception.UsuarioSinAccesoEstadosProyectoGradoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarHistorialEstadosProyectoGradoValidatorImplTest {

    private final ConsultarHistorialEstadosProyectoGradoValidatorImpl validator =
            new ConsultarHistorialEstadosProyectoGradoValidatorImpl();

    @Test
    void debePasar_cuandoUsuarioEsCoordinadorDelProyecto() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var criteria = new HistorialEstadosProyectoGradoCriteria(UtilUUID.generarNuevoUUID(), coordinador);

        // Act & Assert
        assertThatCode(() -> validator.validar(criteria, coordinador, false)).doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoUsuarioEsAsesorAsignado() {
        // Arrange
        var criteria = new HistorialEstadosProyectoGradoCriteria(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID());
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(criteria, coordinador, true)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoProyectoNoExiste() {
        // Arrange
        var criteria = new HistorialEstadosProyectoGradoCriteria(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID());
        var sinCoordinador = UtilUUID.obtenerUUIDPorDefecto();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, sinCoordinador, true))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarSinAcceso_cuandoNoEsCoordinadorNiAsesor() {
        // Arrange
        var criteria = new HistorialEstadosProyectoGradoCriteria(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID());
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, coordinador, false))
                .isInstanceOf(UsuarioSinAccesoEstadosProyectoGradoException.class);
    }

    @Test
    void debePriorizarExistencia_cuandoProyectoNoExisteYNoHayVinculo() {
        // Arrange
        var criteria = new HistorialEstadosProyectoGradoCriteria(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID());
        var sinCoordinador = UtilUUID.obtenerUUIDPorDefecto();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(criteria, sinCoordinador, false))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }
}
