package com.arquisoft.proyectos.application.asesorproyectogrado.command.validator;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.validator.impl.CambiarEstadoAsesorProyectoGradoValidatorImpl;
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

    private AsesorProyectoGradoDomain vinculo(EstadoAsesor estado) {
        return AsesorProyectoGradoDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                proyecto.getId(), estado, UtilFecha.generarInstanteActual());
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
        assertThatCode(() -> validator.validar(entrada(), proyecto, vinculoAsignado)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarProyectoNoEncontrado_antesQueCoordinadorAjeno_cuandoAmbosSeCumplen() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(UtilUUID.generarNuevoUUID(), "FINALIZADO"), ProyectoGradoDomain.VACIO, vinculoAsignado))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoPerteneceCoordinador_antesQueProyectoFinalizado_cuandoAmbosSeCumplen() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(UtilUUID.generarNuevoUUID(), "FINALIZADO"), proyectoFinalizado, vinculoAsignado))
                .isInstanceOf(ProyectoGradoNoPerteneceCoordinadorException.class);
    }

    @Test
    void debeLanzarProyectoFinalizado_antesQueVinculoInexistente_cuandoAmbosSeCumplen() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(), proyectoFinalizado, AsesorProyectoGradoDomain.VACIO))
                .isInstanceOf(ProyectoGradoFinalizadoException.class);
    }

    @Test
    void debeLanzarVinculoNoEncontrado_cuandoElVinculoEsVacio() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(), proyecto, AsesorProyectoGradoDomain.VACIO))
                .isInstanceOf(AsesorProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarTransicionInvalida_cuandoElVinculoYaEstaFinalizado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(entrada(), proyecto, vinculo(EstadoAsesor.FINALIZADO)))
                .isInstanceOf(TransicionEstadoAsesorInvalidaException.class);
    }

    @Test
    void debeLanzarTransicionInvalida_cuandoElEstadoPedidoEsAsignado() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                entrada(proyecto.getCoordinador(), "ASIGNADO"), proyecto, vinculoAsignado))
                .isInstanceOf(TransicionEstadoAsesorInvalidaException.class);
    }
}
