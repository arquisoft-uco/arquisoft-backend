package com.arquisoft.proyectos.application.proyectogrado.command.validator;

import com.arquisoft.proyectos.application.proyectogrado.command.validator.impl.CancelarProyectoGradoValidatorImpl;
import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoproyectogrado.exception.EstadoProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.proyectogrado.CancelacionProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoCancelableException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoPerteneceCoordinadorException;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CancelarProyectoGradoValidatorImplTest {

    private final CancelarProyectoGradoValidatorImpl validator = new CancelarProyectoGradoValidatorImpl();

    private final ProyectoGradoDomain proyecto = ProyectoGradoDomain.crear(
            UtilUUID.generarNuevoUUID(), "Sistema de gestión", UtilUUID.generarNuevoUUID());
    private final EstadoProyectoGradoDomain enProceso = EstadoProyectoGradoDomain.crear(proyecto.getId());
    private final EstadoProyectoGradoDomain finalizado = EstadoProyectoGradoDomain.reconstruir(
            UtilUUID.generarNuevoUUID(), proyecto.getId(), EstadoProyecto.FINALIZADO,
            UtilFecha.generarInstanteActual(), null);

    private CancelacionProyectoGradoDomain cancelacion(UUID coordinador) {
        return CancelacionProyectoGradoDomain.crear(proyecto.getId(), coordinador, "Falta de avance");
    }

    @Test
    void debePasar_cuandoTodasLasReglasSeCumplen() {
        // Act & Assert
        assertThatCode(() -> validator.validar(cancelacion(proyecto.getCoordinador()), proyecto, enProceso))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoProyectoNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cancelacion(proyecto.getCoordinador()), ProyectoGradoDomain.VACIO, enProceso))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzar_cuandoSinEstadoActual() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cancelacion(proyecto.getCoordinador()), proyecto, EstadoProyectoGradoDomain.VACIO))
                .isInstanceOf(EstadoProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzar_cuandoOtroCoordinador() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cancelacion(UtilUUID.generarNuevoUUID()), proyecto, enProceso))
                .isInstanceOf(ProyectoGradoNoPerteneceCoordinadorException.class);
    }

    @Test
    void debeLanzar_cuandoNoEsCancelable() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cancelacion(proyecto.getCoordinador()), proyecto, finalizado))
                .isInstanceOf(ProyectoGradoNoCancelableException.class);
    }

    @Test
    void debeValidarExistenciaAntesQuePropiedadYEstado_cuandoFallanVarias() {
        // Arrange
        var otroCoordinador = cancelacion(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                otroCoordinador, ProyectoGradoDomain.VACIO, EstadoProyectoGradoDomain.VACIO))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
        assertThatThrownBy(() -> validator.validar(otroCoordinador, proyecto, EstadoProyectoGradoDomain.VACIO))
                .isInstanceOf(EstadoProyectoGradoNoEncontradoException.class);
        assertThatThrownBy(() -> validator.validar(otroCoordinador, proyecto, finalizado))
                .isInstanceOf(ProyectoGradoNoPerteneceCoordinadorException.class);
    }
}
