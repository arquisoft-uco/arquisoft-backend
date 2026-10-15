package com.arquisoft.proyectos.application.estudianteproyectogrado.command.validator;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.validator.impl.DestituirEstudianteProyectoGradoValidatorImpl;
import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoproyectogrado.exception.EstadoProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.DestitucionEstudianteProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.EstudianteNoVinculadoException;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoFinalizadoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoPerteneceCoordinadorException;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DestituirEstudianteProyectoGradoValidatorTest {

    private final DestituirEstudianteProyectoGradoValidatorImpl validator =
            new DestituirEstudianteProyectoGradoValidatorImpl();

    private final ProyectoGradoDomain proyecto = ProyectoGradoDomain.crear(
            UtilUUID.generarNuevoUUID(), "Titulo", UtilUUID.generarNuevoUUID());

    private final EstadoProyectoGradoDomain estadoEnProceso = EstadoProyectoGradoDomain.crear(proyecto.getId());

    private final EstadoProyectoGradoDomain estadoFinalizado = EstadoProyectoGradoDomain.reconstruir(
            UtilUUID.generarNuevoUUID(), proyecto.getId(), EstadoProyecto.FINALIZADO,
            UtilFecha.generarInstanteActual());

    private DestitucionEstudianteProyectoGradoDomain entrada(UUID coordinador) {
        return DestitucionEstudianteProyectoGradoDomain.crear(
                proyecto.getId(), UtilUUID.generarNuevoUUID(), coordinador);
    }

    @Test
    void noDebeLanzar_cuandoTodoEsValido() {
        // Act & Assert
        assertThatCode(() -> validator.validar(entrada(proyecto.getCoordinador()), proyecto, estadoEnProceso, true))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarProyectoNoEncontrado_cuandoElProyectoNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(proyecto.getCoordinador()),
                ProyectoGradoDomain.VACIO, estadoEnProceso, true))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoPerteneceCoordinador_cuandoElSolicitanteNoEsElCoordinador() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(UtilUUID.generarNuevoUUID()), proyecto, estadoEnProceso, true))
                .isInstanceOf(ProyectoGradoNoPerteneceCoordinadorException.class);
    }

    @Test
    void debeLanzarNoVinculado_cuandoElEstudianteNoEstaVinculado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(proyecto.getCoordinador()), proyecto, estadoEnProceso, false))
                .isInstanceOf(EstudianteNoVinculadoException.class);
    }

    @Test
    void debeLanzarFinalizado_cuandoElProyectoEstaFinalizado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(proyecto.getCoordinador()), proyecto, estadoFinalizado, true))
                .isInstanceOf(ProyectoGradoFinalizadoException.class);
    }

    @Test
    void debePriorizarPropiedad_cuandoNoEsCoordinadorYNoEstaVinculado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(UtilUUID.generarNuevoUUID()), proyecto, estadoEnProceso, false))
                .isInstanceOf(ProyectoGradoNoPerteneceCoordinadorException.class);
    }

    @Test
    void debePriorizarNoVinculado_cuandoNoEstaVinculadoYElProyectoEstaFinalizado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(proyecto.getCoordinador()), proyecto, estadoFinalizado, false))
                .isInstanceOf(EstudianteNoVinculadoException.class);
    }

    @Test
    void debeLanzarEstadoNoEncontrado_antesQueCoordinadorAjeno_cuandoElProyectoNoTieneEstado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(UtilUUID.generarNuevoUUID()), proyecto, EstadoProyectoGradoDomain.VACIO, true))
                .isInstanceOf(EstadoProyectoGradoNoEncontradoException.class)
                .extracting("codigoError")
                .isEqualTo(ProyectosCodes.EstadoProyectoGrado.NO_ENCONTRADO);
    }

    @Test
    void debeLanzarProyectoNoEncontrado_antesQueEstadoNoEncontrado_cuandoAmbosSonVacios() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(proyecto.getCoordinador()), ProyectoGradoDomain.VACIO, EstadoProyectoGradoDomain.VACIO, true))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }
}
