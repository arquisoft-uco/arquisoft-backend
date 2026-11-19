package com.arquisoft.proyectos.application.proyectogrado.command.validator;

import com.arquisoft.proyectos.application.proyectogrado.command.validator.impl.ReanudarProyectoGradoValidatorImpl;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
import com.arquisoft.proyectos.domain.coordinador.exception.CoordinadorNoEncontradoException;
import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoproyectogrado.exception.EstadoProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.ReanudacionProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReanudarProyectoGradoValidatorImplTest {

    private final ReanudarProyectoGradoValidatorImpl validator = new ReanudarProyectoGradoValidatorImpl();

    private final ProyectoGradoDomain proyecto = ProyectoGradoDomain.crear(
            UtilUUID.generarNuevoUUID(), "Sistema de gestión", UtilUUID.generarNuevoUUID());
    private final EstadoProyectoGradoDomain estado = EstadoProyectoGradoDomain.crearConEstado(
            proyecto.getId(), EstadoProyecto.ATRASADO);
    private final CoordinadorDomain coordinador = CoordinadorDomain.reconstruir(proyecto.getCoordinador(), "1020",
            "Laura Mesa", "laura.mesa@uco.edu.co", Instant.parse("2026-09-01T10:00:00Z"), null);
    private final ReanudacionProyectoGradoDomain reanudacion = ReanudacionProyectoGradoDomain.crear(
            proyecto.getId(), LocalDate.of(2026, 12, 1), LocalDate.of(2026, 11, 19));

    @Test
    void debePasar_cuandoTodoExiste() {
        // Act & Assert
        assertThatCode(() -> validator.validar(reanudacion, proyecto, estado, coordinador))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarYPriorizarEnOrden_cuandoFaltanProyectoEstadoOCoordinador() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(reanudacion, ProyectoGradoDomain.VACIO,
                EstadoProyectoGradoDomain.VACIO, CoordinadorDomain.VACIO))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
        assertThatThrownBy(() -> validator.validar(reanudacion, proyecto, EstadoProyectoGradoDomain.VACIO,
                CoordinadorDomain.VACIO))
                .isInstanceOf(EstadoProyectoGradoNoEncontradoException.class);
        assertThatThrownBy(() -> validator.validar(reanudacion, proyecto, estado, CoordinadorDomain.VACIO))
                .isInstanceOf(CoordinadorNoEncontradoException.class);
    }
}
