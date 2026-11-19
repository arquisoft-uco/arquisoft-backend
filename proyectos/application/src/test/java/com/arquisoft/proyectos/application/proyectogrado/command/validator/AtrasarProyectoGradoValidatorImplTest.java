package com.arquisoft.proyectos.application.proyectogrado.command.validator;

import com.arquisoft.proyectos.application.proyectogrado.command.validator.impl.AtrasarProyectoGradoValidatorImpl;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
import com.arquisoft.proyectos.domain.coordinador.exception.CoordinadorNoEncontradoException;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoproyectogrado.exception.EstadoProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.proyectogrado.AtrasoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AtrasarProyectoGradoValidatorImplTest {

    private final AtrasarProyectoGradoValidatorImpl validator = new AtrasarProyectoGradoValidatorImpl();

    private final ProyectoGradoDomain proyecto = ProyectoGradoDomain.crear(
            UtilUUID.generarNuevoUUID(), "Sistema de gestión", UtilUUID.generarNuevoUUID());
    private final EstadoProyectoGradoDomain estado = EstadoProyectoGradoDomain.crear(proyecto.getId());
    private final CoordinadorDomain coordinador = CoordinadorDomain.reconstruir(proyecto.getCoordinador(), "1020",
            "Laura Mesa", "laura.mesa@uco.edu.co", Instant.parse("2026-09-01T10:00:00Z"), null);
    private final AtrasoProyectoGradoDomain atraso = AtrasoProyectoGradoDomain.crear(proyecto.getId());

    @Test
    void debePasar_cuandoTodoExiste() {
        // Act & Assert
        assertThatCode(() -> validator.validar(atraso, proyecto, estado, coordinador)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarYPriorizarEnOrden_cuandoFaltanProyectoEstadoOCoordinador() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(atraso, ProyectoGradoDomain.VACIO,
                EstadoProyectoGradoDomain.VACIO, CoordinadorDomain.VACIO))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
        assertThatThrownBy(() -> validator.validar(atraso, proyecto, EstadoProyectoGradoDomain.VACIO,
                CoordinadorDomain.VACIO))
                .isInstanceOf(EstadoProyectoGradoNoEncontradoException.class);
        assertThatThrownBy(() -> validator.validar(atraso, proyecto, estado, CoordinadorDomain.VACIO))
                .isInstanceOf(CoordinadorNoEncontradoException.class);
    }
}
