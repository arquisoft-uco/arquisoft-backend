package com.arquisoft.mapas_ruta.application.tarea.command.validator.impl;

import com.arquisoft.mapas_ruta.domain.asignacionasesor.exception.AsesorNoAsignadoProyectoGradoException;
import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.maparuta.exception.MapaRutaNoEncontradoException;
import com.arquisoft.mapas_ruta.domain.tarea.ModificacionTareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.exception.TareaDuplicadaException;
import com.arquisoft.mapas_ruta.domain.tarea.exception.TareaNoEncontradaException;
import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarInformacionTareaValidatorImplTest {

    private final ModificarInformacionTareaValidatorImpl validator = new ModificarInformacionTareaValidatorImpl();

    private static MapaRutaDomain mapa() {
        return MapaRutaDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1));
    }

    private static TareaDomain tarea(MapaRutaDomain mapa) {
        return TareaDomain.reconstruir(UtilUUID.generarNuevoUUID(), mapa.getId(), "Anterior",
                UtilUUID.generarNuevoUUID(), EstadoItem.NUEVA);
    }

    private static ModificacionTareaDomain modificacion(TareaDomain tarea) {
        return ModificacionTareaDomain.crear(tarea.getId(), "Redactar capitulo", UtilUUID.generarNuevoUUID());
    }

    @Test
    void debePasar_cuandoTodoEsValido() {
        // Arrange
        var mapa = mapa();
        var tarea = tarea(mapa);
        var modificacion = modificacion(tarea);

        // Act & Assert
        assertThatCode(() -> validator.validar(modificacion, tarea, mapa, false, true)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarTareaNoEncontrada_cuandoTareaVacia() {
        // Arrange
        var modificacion = ModificacionTareaDomain.crear(
                UtilUUID.generarNuevoUUID(), "Redactar capitulo", UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, TareaDomain.VACIO, MapaRutaDomain.VACIO,
                false, false)).isInstanceOf(TareaNoEncontradaException.class);
    }

    @Test
    void debeLanzarMapaRutaNoEncontrado_cuandoMapaVacio() {
        // Arrange
        var tarea = tarea(mapa());
        var modificacion = modificacion(tarea);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, tarea, MapaRutaDomain.VACIO, false, false))
                .isInstanceOf(MapaRutaNoEncontradoException.class);
    }

    @Test
    void debeLanzarTareaDuplicada_cuandoDescripcionEnUso() {
        // Arrange
        var mapa = mapa();
        var tarea = tarea(mapa);
        var modificacion = modificacion(tarea);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, tarea, mapa, true, true))
                .isInstanceOf(TareaDuplicadaException.class);
    }

    @Test
    void debeLanzarAsesorNoAsignado_cuandoNoEstaAsignado() {
        // Arrange
        var mapa = mapa();
        var tarea = tarea(mapa);
        var modificacion = modificacion(tarea);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, tarea, mapa, false, false))
                .isInstanceOf(AsesorNoAsignadoProyectoGradoException.class);
    }

    @Test
    void debePriorizarSegunElOrden_cuandoFallanVarias() {
        // Arrange
        var mapa = mapa();
        var tarea = tarea(mapa);
        var modificacion = modificacion(tarea);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, TareaDomain.VACIO, MapaRutaDomain.VACIO,
                true, false)).isInstanceOf(TareaNoEncontradaException.class);
        assertThatThrownBy(() -> validator.validar(modificacion, tarea, MapaRutaDomain.VACIO, true, false))
                .isInstanceOf(MapaRutaNoEncontradoException.class);
        assertThatThrownBy(() -> validator.validar(modificacion, tarea, mapa, true, false))
                .isInstanceOf(TareaDuplicadaException.class);
    }
}
