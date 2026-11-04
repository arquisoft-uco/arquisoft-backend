package com.arquisoft.mapas_ruta.application.tarea.command.validator.impl;

import com.arquisoft.mapas_ruta.domain.asignacionasesor.exception.AsesorNoAsignadoProyectoGradoException;
import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.maparuta.exception.MapaRutaNoEncontradoException;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.mapas_ruta.domain.proyectogrado.exception.ProyectoGradoNoEnProcesoException;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.exception.TareaDuplicadaException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignarTareaValidatorImplTest {

    private final AsignarTareaValidatorImpl validator = new AsignarTareaValidatorImpl();

    private static MapaRutaDomain mapa() {
        return MapaRutaDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1));
    }

    private static ProyectoGradoDomain proyecto(MapaRutaDomain mapa, EstadoProyectoGrado estado) {
        return ProyectoGradoDomain.reconstruir(mapa.getProyectoGrado(), estado,
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Titulo");
    }

    private static TareaDomain tarea(MapaRutaDomain mapa) {
        return TareaDomain.crear(mapa.getId(), "Redactar capitulo", UtilUUID.generarNuevoUUID());
    }

    @Test
    void noDebeLanzar_cuandoTodoValido() {
        // Arrange
        var mapa = mapa();
        var proyecto = proyecto(mapa, EstadoProyectoGrado.EN_PROCESO);
        var tarea = tarea(mapa);

        // Act & Assert
        assertThatCode(() -> validator.validar(tarea, mapa, proyecto, false, true)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarMapaNoEncontrado_cuandoElMapaEsVacio() {
        // Arrange
        var tarea = tarea(mapa());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(tarea, MapaRutaDomain.VACIO, ProyectoGradoDomain.VACIO,
                false, false)).isInstanceOf(MapaRutaNoEncontradoException.class);
    }

    @Test
    void debeLanzarTareaDuplicada_cuandoYaExiste() {
        // Arrange
        var mapa = mapa();
        var proyecto = proyecto(mapa, EstadoProyectoGrado.EN_PROCESO);
        var tarea = tarea(mapa);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(tarea, mapa, proyecto, true, true))
                .isInstanceOf(TareaDuplicadaException.class);
    }

    @Test
    void debeLanzarNoEnProceso_cuandoElProyectoNoEstaEnProceso() {
        // Arrange
        var mapa = mapa();
        var proyecto = proyecto(mapa, EstadoProyectoGrado.ATRASADO);
        var tarea = tarea(mapa);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(tarea, mapa, proyecto, false, true))
                .isInstanceOf(ProyectoGradoNoEnProcesoException.class);
    }

    @Test
    void debeLanzarAsesorNoAsignado_cuandoElAsesorNoEstaEnElProyecto() {
        // Arrange
        var mapa = mapa();
        var proyecto = proyecto(mapa, EstadoProyectoGrado.EN_PROCESO);
        var tarea = tarea(mapa);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(tarea, mapa, proyecto, false, false))
                .isInstanceOf(AsesorNoAsignadoProyectoGradoException.class);
    }

    @Test
    void debePriorizarCadaRegla_segunElOrdenDeValidacion() {
        // Arrange
        var mapa = mapa();
        var enProceso = proyecto(mapa, EstadoProyectoGrado.EN_PROCESO);
        var atrasado = proyecto(mapa, EstadoProyectoGrado.ATRASADO);
        var tarea = tarea(mapa);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(tarea, MapaRutaDomain.VACIO, ProyectoGradoDomain.VACIO,
                true, false)).isInstanceOf(MapaRutaNoEncontradoException.class);
        assertThatThrownBy(() -> validator.validar(tarea, mapa, atrasado, true, false))
                .isInstanceOf(TareaDuplicadaException.class);
        assertThatThrownBy(() -> validator.validar(tarea, mapa, atrasado, false, false))
                .isInstanceOf(ProyectoGradoNoEnProcesoException.class);
        assertThatThrownBy(() -> validator.validar(tarea, mapa, enProceso, false, false))
                .isInstanceOf(AsesorNoAsignadoProyectoGradoException.class);
    }
}
