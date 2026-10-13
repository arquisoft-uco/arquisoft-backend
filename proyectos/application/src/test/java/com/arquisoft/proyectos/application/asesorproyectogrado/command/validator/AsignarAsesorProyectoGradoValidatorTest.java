package com.arquisoft.proyectos.application.asesorproyectogrado.command.validator;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.validator.impl.AsignarAsesorProyectoGradoValidatorImpl;
import com.arquisoft.proyectos.domain.asesor.AsesorDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.AsignacionAsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.AsesorNoVigenteException;
import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.AsesorYaAsignadoException;
import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.CupoAsesoresExcedidoException;
import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoproyectogrado.exception.EstadoProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoFinalizadoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoPerteneceCoordinadorException;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignarAsesorProyectoGradoValidatorTest {

    private final AsignarAsesorProyectoGradoValidatorImpl validator = new AsignarAsesorProyectoGradoValidatorImpl();

    private final ProyectoGradoDomain proyecto = ProyectoGradoDomain.crear(
            UtilUUID.generarNuevoUUID(), "Titulo", UtilUUID.generarNuevoUUID());

    private final EstadoProyectoGradoDomain estadoEnProceso = EstadoProyectoGradoDomain.crear(proyecto.getId());

    private final EstadoProyectoGradoDomain estadoFinalizado = EstadoProyectoGradoDomain.reconstruir(
            UtilUUID.generarNuevoUUID(), proyecto.getId(), EstadoProyecto.FINALIZADO,
            UtilFecha.generarInstanteActual());

    private final AsesorDomain asesor = asesor(null);

    private final AsesorDomain asesorEliminado = asesor(Instant.parse("2026-09-10T10:00:00Z"));

    private static AsesorDomain asesor(Instant eliminadoEn) {
        return AsesorDomain.reconstruir(UtilUUID.generarNuevoUUID(), "2020", "Luis Diaz",
                "luis.diaz@soyuco.edu.co", Instant.parse("2026-09-01T10:00:00Z"), eliminadoEn);
    }

    private AsignacionAsesorProyectoGradoDomain entrada(AsesorDomain delAsesor, UUID coordinador) {
        return AsignacionAsesorProyectoGradoDomain.crear(
                AsesorProyectoGradoDomain.crear(delAsesor.getId(), proyecto.getId()), coordinador);
    }

    private AsignacionAsesorProyectoGradoDomain entrada() {
        return entrada(asesor, proyecto.getCoordinador());
    }

    @Test
    void debePasar_cuandoTodoEsValido() {
        // Act & Assert
        assertThatCode(() -> validator.validar(entrada(), proyecto, estadoEnProceso, asesor, false, 0))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarProyectoNoEncontrado_antesQueCoordinadorAjeno_cuandoAmbosSeCumplen() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(asesor, UtilUUID.generarNuevoUUID()), ProyectoGradoDomain.VACIO, estadoEnProceso, asesor, false, 0))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoPerteneceCoordinador_antesQueAsesorNoVigente_cuandoAmbosSeCumplen() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(asesor, UtilUUID.generarNuevoUUID()), proyecto, estadoEnProceso, AsesorDomain.VACIO, false, 0))
                .isInstanceOf(ProyectoGradoNoPerteneceCoordinadorException.class);
    }

    @Test
    void debeLanzarAsesorNoVigente_cuandoElAsesorNoExisteEnLaReplica() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(), proyecto, estadoEnProceso, AsesorDomain.VACIO, false, 0))
                .isInstanceOf(AsesorNoVigenteException.class);
    }

    @Test
    void debeLanzarAsesorNoVigente_cuandoElAsesorEstaDadoDeBaja() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(asesorEliminado, proyecto.getCoordinador()), proyecto, estadoEnProceso, asesorEliminado, false, 0))
                .isInstanceOf(AsesorNoVigenteException.class);
    }

    @Test
    void debeLanzarAsesorNoVigente_antesQueFinalizado_cuandoAmbosSeCumplen() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(), proyecto, estadoFinalizado, AsesorDomain.VACIO, false, 0))
                .isInstanceOf(AsesorNoVigenteException.class);
    }

    @Test
    void debeLanzarFinalizado_cuandoElProyectoEstaFinalizado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(), proyecto, estadoFinalizado, asesor, false, 0))
                .isInstanceOf(ProyectoGradoFinalizadoException.class);
    }

    @Test
    void debeLanzarFinalizado_antesQueYaAsignado_cuandoAmbosSeCumplen() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(), proyecto, estadoFinalizado, asesor, true, 0))
                .isInstanceOf(ProyectoGradoFinalizadoException.class);
    }

    @Test
    void debeLanzarYaAsignado_cuandoElAsesorYaEstaAsignado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(), proyecto, estadoEnProceso, asesor, true, 1))
                .isInstanceOf(AsesorYaAsignadoException.class);
    }

    @Test
    void debeLanzarYaAsignado_antesQueCupo_cuandoAmbosSeCumplen() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(), proyecto, estadoEnProceso, asesor, true, 1))
                .isInstanceOf(AsesorYaAsignadoException.class);
    }

    @Test
    void debeLanzarCupoExcedido_cuandoElProyectoYaTieneUnAsesorAsignado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(), proyecto, estadoEnProceso, asesor, false, 1))
                .isInstanceOf(CupoAsesoresExcedidoException.class);
    }

    @Test
    void debeLanzarEstadoNoEncontrado_antesQueCoordinadorAjeno_cuandoElProyectoNoTieneEstado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(asesor, UtilUUID.generarNuevoUUID()), proyecto, EstadoProyectoGradoDomain.VACIO, asesor, false, 0))
                .isInstanceOf(EstadoProyectoGradoNoEncontradoException.class)
                .extracting("codigoError")
                .isEqualTo(ProyectosCodes.EstadoProyectoGrado.NO_ENCONTRADO);
    }

    @Test
    void debeLanzarProyectoNoEncontrado_antesQueEstadoNoEncontrado_cuandoAmbosSonVacios() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(), ProyectoGradoDomain.VACIO, EstadoProyectoGradoDomain.VACIO, asesor, false, 0))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }
}
