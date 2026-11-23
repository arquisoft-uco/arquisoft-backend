package com.arquisoft.proyectos.application.proyectogrado.command.validator;

import com.arquisoft.proyectos.application.proyectogrado.command.validator.impl.CancelarProyectoGradoValidatorImpl;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
import com.arquisoft.proyectos.domain.coordinador.exception.CoordinadorNoEncontradoException;
import com.arquisoft.proyectos.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoproyectogrado.exception.EstadoProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.proyectogrado.CancelacionProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoConEntregableVigenteException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoCancelableException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoPerteneceCoordinadorException;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CancelarProyectoGradoValidatorImplTest {

    private final CancelarProyectoGradoValidatorImpl validator = new CancelarProyectoGradoValidatorImpl();

    private final ProyectoGradoDomain proyecto = ProyectoGradoDomain.crear(
            UtilUUID.generarNuevoUUID(), "Sistema de gestión", UtilUUID.generarNuevoUUID());
    private final EstadoProyectoGradoDomain enProceso = EstadoProyectoGradoDomain.crear(proyecto.getId());
    private final CoordinadorDomain coordinador = CoordinadorDomain.reconstruir(proyecto.getCoordinador(), "1020",
            "Laura Mesa", "laura.mesa@uco.edu.co", Instant.parse("2026-09-01T10:00:00Z"), null);
    private final EstadoProyectoGradoDomain finalizado = EstadoProyectoGradoDomain.reconstruir(
            UtilUUID.generarNuevoUUID(), proyecto.getId(), EstadoProyecto.FINALIZADO,
            UtilFecha.generarInstanteActual(), null);

    private final EntregableProyectoGradoDomain sinEntregable = EntregableProyectoGradoDomain.VACIO;
    private final EntregableProyectoGradoDomain entregableAprobado = EntregableProyectoGradoDomain.reconstruir(
            UtilUUID.generarNuevoUUID(), proyecto.getId(), 2, EstadoEntregable.APROBADO,
            Instant.parse("2026-11-01T10:00:00Z"));

    private CancelacionProyectoGradoDomain cancelacion(UUID coordinador) {
        return CancelacionProyectoGradoDomain.crear(proyecto.getId(), coordinador, "Falta de avance");
    }

    @Test
    void debePasar_cuandoTodasLasReglasSeCumplen() {
        // Act & Assert
        assertThatCode(() -> validator.validar(cancelacion(proyecto.getCoordinador()), proyecto, enProceso, coordinador, sinEntregable))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoProyectoNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cancelacion(proyecto.getCoordinador()), ProyectoGradoDomain.VACIO, enProceso, coordinador, sinEntregable))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzar_cuandoSinEstadoActual() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cancelacion(proyecto.getCoordinador()), proyecto, EstadoProyectoGradoDomain.VACIO, coordinador, sinEntregable))
                .isInstanceOf(EstadoProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzar_cuandoOtroCoordinador() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cancelacion(UtilUUID.generarNuevoUUID()), proyecto, enProceso, coordinador, sinEntregable))
                .isInstanceOf(ProyectoGradoNoPerteneceCoordinadorException.class);
    }

    @Test
    void debeLanzar_cuandoNoEsCancelable() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cancelacion(proyecto.getCoordinador()), proyecto, finalizado, coordinador, sinEntregable))
                .isInstanceOf(ProyectoGradoNoCancelableException.class);
    }

    @Test
    void debeValidarExistenciaAntesQuePropiedadYEstado_cuandoFallanVarias() {
        // Arrange
        var otroCoordinador = cancelacion(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                otroCoordinador, ProyectoGradoDomain.VACIO, EstadoProyectoGradoDomain.VACIO, coordinador, sinEntregable))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
        assertThatThrownBy(() -> validator.validar(otroCoordinador, proyecto, EstadoProyectoGradoDomain.VACIO, coordinador, sinEntregable))
                .isInstanceOf(EstadoProyectoGradoNoEncontradoException.class);
        assertThatThrownBy(() -> validator.validar(otroCoordinador, proyecto, finalizado, coordinador, sinEntregable))
                .isInstanceOf(ProyectoGradoNoPerteneceCoordinadorException.class);
    }

    @Test
    void debeLanzarCoordinadorNoEncontrado_cuandoElCoordinadorEsVacio() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cancelacion(proyecto.getCoordinador()), proyecto, enProceso, CoordinadorDomain.VACIO, sinEntregable))
                .isInstanceOf(CoordinadorNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoPertenece_antesQueCoordinadorNoEncontrado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cancelacion(UtilUUID.generarNuevoUUID()), proyecto, enProceso, CoordinadorDomain.VACIO, sinEntregable))
                .isInstanceOf(ProyectoGradoNoPerteneceCoordinadorException.class);
    }

    @Test
    void debeLanzarConEntregableVigente_cuandoElUltimoEstaAprobado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cancelacion(proyecto.getCoordinador()), proyecto, enProceso, coordinador, entregableAprobado))
                .isInstanceOf(ProyectoGradoConEntregableVigenteException.class);
    }

    @Test
    void debeAplicarLaReglaDeEntregableAlFinal_cuandoElProyectoNoEsCancelable() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cancelacion(proyecto.getCoordinador()), proyecto, finalizado, coordinador, entregableAprobado))
                .isInstanceOf(ProyectoGradoNoCancelableException.class);
    }
}
