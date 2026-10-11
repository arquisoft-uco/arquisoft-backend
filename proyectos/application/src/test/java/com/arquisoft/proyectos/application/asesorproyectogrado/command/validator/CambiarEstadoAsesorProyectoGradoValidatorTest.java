package com.arquisoft.proyectos.application.asesorproyectogrado.command.validator;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.validator.impl.CambiarEstadoAsesorProyectoGradoValidatorImpl;
import com.arquisoft.proyectos.domain.asesor.AsesorDomain;
import com.arquisoft.proyectos.domain.asesor.exception.AsesorNoEncontradoException;
import com.arquisoft.proyectos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.CambioEstadoAsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.AsesorProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.TransicionEstadoAsesorInvalidaException;
import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoFinalizadoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoPerteneceCoordinadorException;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarEstadoAsesorProyectoGradoValidatorTest {

    private final CambiarEstadoAsesorProyectoGradoValidatorImpl validator =
            new CambiarEstadoAsesorProyectoGradoValidatorImpl();

    private final ProyectoGradoDomain proyecto = ProyectoGradoDomain.crear(
            UtilUUID.generarNuevoUUID(), "Titulo", UtilUUID.generarNuevoUUID());

    private final ProyectoGradoDomain proyectoFinalizado = ProyectoGradoDomain.reconstruir(
            proyecto.getId(), proyecto.getFichaPerfil(), proyecto.getTituloProyecto(),
            proyecto.getCoordinador(), EstadoProyectoGrado.FINALIZADO);

    private final AsesorProyectoGradoDomain vinculoAsignado = vinculo(EstadoAsesor.ASIGNADO);

    private final AsesorDomain asesor = asesor(null);

    private AsesorProyectoGradoDomain vinculo(EstadoAsesor estado) {
        return AsesorProyectoGradoDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                proyecto.getId(), estado, UtilFecha.generarInstanteActual());
    }

    private AsesorDomain asesor(Instant eliminadoEn) {
        return AsesorDomain.reconstruir(vinculoAsignado.getAsesor(), "2020", "Luis Diaz",
                "luis.diaz@soyuco.edu.co", Instant.parse("2026-09-01T10:00:00Z"), eliminadoEn);
    }

    private CambioEstadoAsesorProyectoGradoDomain entrada(UUID coordinador, String estado) {
        return CambioEstadoAsesorProyectoGradoDomain.crear(
                proyecto.getId(), vinculoAsignado.getId(), coordinador, estado);
    }

    private CambioEstadoAsesorProyectoGradoDomain entrada() {
        return entrada(proyecto.getCoordinador(), "FINALIZADO");
    }

    @Test
    void debePasar_cuandoTodoEsValido() {
        // Act & Assert
        assertThatCode(() -> validator.validar(entrada(), proyecto, vinculoAsignado, asesor))
                .doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoElAsesorEstaDadoDeBaja() {
        // Arrange
        var asesorDeBaja = asesor(Instant.parse("2026-10-01T10:00:00Z"));

        // Act & Assert
        assertThatCode(() -> validator.validar(entrada(), proyecto, vinculoAsignado, asesorDeBaja))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarProyectoNoEncontrado_antesQueCoordinadorAjeno_cuandoAmbosSeCumplen() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(UtilUUID.generarNuevoUUID(), "FINALIZADO"), ProyectoGradoDomain.VACIO, vinculoAsignado,
                asesor))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoPerteneceCoordinador_antesQueProyectoFinalizado_cuandoAmbosSeCumplen() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(UtilUUID.generarNuevoUUID(), "FINALIZADO"), proyectoFinalizado, vinculoAsignado, asesor))
                .isInstanceOf(ProyectoGradoNoPerteneceCoordinadorException.class);
    }

    @Test
    void debeLanzarProyectoFinalizado_antesQueVinculoInexistente_cuandoAmbosSeCumplen() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(), proyectoFinalizado, AsesorProyectoGradoDomain.VACIO, AsesorDomain.VACIO))
                .isInstanceOf(ProyectoGradoFinalizadoException.class);
    }

    @Test
    void debeLanzarVinculoNoEncontrado_antesQueAsesorNoEncontrado_cuandoAmbosSonVacios() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(), proyecto, AsesorProyectoGradoDomain.VACIO, AsesorDomain.VACIO))
                .isInstanceOf(AsesorProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarAsesorNoEncontrado_cuandoElAsesorEsVacio() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(), proyecto, vinculoAsignado, AsesorDomain.VACIO))
                .isInstanceOf(AsesorNoEncontradoException.class);
    }

    @Test
    void debeLanzarAsesorNoEncontrado_antesQueTransicionInvalida_cuandoAmbosSeCumplen() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(), proyecto, vinculo(EstadoAsesor.FINALIZADO), AsesorDomain.VACIO))
                .isInstanceOf(AsesorNoEncontradoException.class);
    }

    @Test
    void debeLanzarTransicionInvalida_cuandoElVinculoYaEstaFinalizado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(), proyecto, vinculo(EstadoAsesor.FINALIZADO), asesor))
                .isInstanceOf(TransicionEstadoAsesorInvalidaException.class);
    }

    @Test
    void debeLanzarTransicionInvalida_cuandoElEstadoPedidoEsAsignado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(proyecto.getCoordinador(), "ASIGNADO"), proyecto, vinculoAsignado, asesor))
                .isInstanceOf(TransicionEstadoAsesorInvalidaException.class);
    }
}
