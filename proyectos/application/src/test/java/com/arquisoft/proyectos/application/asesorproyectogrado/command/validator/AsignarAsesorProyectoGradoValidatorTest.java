package com.arquisoft.proyectos.application.asesorproyectogrado.command.validator;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.validator.impl.AsignarAsesorProyectoGradoValidatorImpl;
import com.arquisoft.proyectos.domain.asesor.AsesorDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.AsignacionAsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.AsesorNoVigenteException;
import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.AsesorYaAsignadoException;
import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.CupoAsesoresExcedidoException;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoFinalizadoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoPerteneceCoordinadorException;
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

    private final ProyectoGradoDomain proyectoFinalizado = ProyectoGradoDomain.reconstruir(
            proyecto.getId(), proyecto.getFichaPerfil(), proyecto.getTituloProyecto(),
            proyecto.getCoordinador(), EstadoProyectoGrado.FINALIZADO);

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
        assertThatCode(() -> validator.validar(entrada(), proyecto, asesor, false, 1))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarProyectoNoEncontrado_antesQueCoordinadorAjeno_cuandoAmbosSeCumplen() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(asesor, UtilUUID.generarNuevoUUID()), ProyectoGradoDomain.VACIO, asesor, false, 0))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoPerteneceCoordinador_antesQueAsesorNoVigente_cuandoAmbosSeCumplen() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(asesor, UtilUUID.generarNuevoUUID()), proyecto, AsesorDomain.VACIO, false, 0))
                .isInstanceOf(ProyectoGradoNoPerteneceCoordinadorException.class);
    }

    @Test
    void debeLanzarAsesorNoVigente_cuandoElAsesorNoExisteEnLaReplica() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(), proyecto, AsesorDomain.VACIO, false, 0))
                .isInstanceOf(AsesorNoVigenteException.class);
    }

    @Test
    void debeLanzarAsesorNoVigente_cuandoElAsesorEstaDadoDeBaja() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(asesorEliminado, proyecto.getCoordinador()), proyecto, asesorEliminado, false, 0))
                .isInstanceOf(AsesorNoVigenteException.class);
    }

    @Test
    void debeLanzarAsesorNoVigente_antesQueFinalizado_cuandoAmbosSeCumplen() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(), proyectoFinalizado, AsesorDomain.VACIO, false, 0))
                .isInstanceOf(AsesorNoVigenteException.class);
    }

    @Test
    void debeLanzarFinalizado_cuandoElProyectoEstaFinalizado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(), proyectoFinalizado, asesor, false, 0))
                .isInstanceOf(ProyectoGradoFinalizadoException.class);
    }

    @Test
    void debeLanzarFinalizado_antesQueYaAsignado_cuandoAmbosSeCumplen() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(), proyectoFinalizado, asesor, true, 0))
                .isInstanceOf(ProyectoGradoFinalizadoException.class);
    }

    @Test
    void debeLanzarYaAsignado_cuandoElAsesorYaEstaAsignado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(), proyecto, asesor, true, 1))
                .isInstanceOf(AsesorYaAsignadoException.class);
    }

    @Test
    void debeLanzarYaAsignado_antesQueCupo_cuandoAmbosSeCumplen() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(), proyecto, asesor, true, 2))
                .isInstanceOf(AsesorYaAsignadoException.class);
    }

    @Test
    void debeLanzarCupoExcedido_cuandoYaHayDosAsesoresAsignados() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(), proyecto, asesor, false, 2))
                .isInstanceOf(CupoAsesoresExcedidoException.class);
    }
}
