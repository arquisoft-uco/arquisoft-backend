package com.arquisoft.mapas_ruta.application.planestimado.command.validator.impl;

import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.maparuta.exception.MapaRutaNoEncontradoException;
import com.arquisoft.mapas_ruta.domain.planestimado.PlanEstimadoDomain;
import com.arquisoft.mapas_ruta.domain.planestimado.exception.PlanEstimadoDuplicadoException;
import com.arquisoft.mapas_ruta.domain.planestimado.exception.TiempoPlanesExcedePlazoException;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.exception.TareaNoEncontradaException;
import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarPlanEstimadoValidatorImplTest {

    private final AgregarPlanEstimadoValidatorImpl validator = new AgregarPlanEstimadoValidatorImpl();

    private static MapaRutaDomain mapa() {
        return MapaRutaDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 11));
    }

    private static TareaDomain tarea(MapaRutaDomain mapa) {
        return TareaDomain.reconstruir(UtilUUID.generarNuevoUUID(), mapa.getId(), "Redactar capitulo",
                UtilUUID.generarNuevoUUID(), EstadoItem.NUEVA);
    }

    private static PlanEstimadoDomain plan(TareaDomain tarea, int dias) {
        return PlanEstimadoDomain.crear(tarea.getId(), dias, null);
    }

    @Test
    void debePasar_cuandoTodoEsValido() {
        // Arrange
        var mapa = mapa();
        var tarea = tarea(mapa);
        var plan = plan(tarea, 4);

        // Act & Assert
        assertThatCode(() -> validator.validar(plan, tarea, mapa, false, 6)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarTareaNoEncontrada_cuandoTareaVacia() {
        // Arrange
        var plan = PlanEstimadoDomain.crear(UtilUUID.generarNuevoUUID(), 2, null);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(plan, TareaDomain.VACIO, MapaRutaDomain.VACIO, false, 0))
                .isInstanceOf(TareaNoEncontradaException.class);
    }

    @Test
    void debeLanzarMapaRutaNoEncontrado_cuandoMapaVacio() {
        // Arrange
        var tarea = tarea(mapa());
        var plan = plan(tarea, 2);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(plan, tarea, MapaRutaDomain.VACIO, false, 0))
                .isInstanceOf(MapaRutaNoEncontradoException.class);
    }

    @Test
    void debeLanzarDuplicado_cuandoPlanExiste() {
        // Arrange
        var mapa = mapa();
        var tarea = tarea(mapa);
        var plan = plan(tarea, 2);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(plan, tarea, mapa, true, 0))
                .isInstanceOf(PlanEstimadoDuplicadoException.class);
    }

    @Test
    void debeLanzarExcedePlazo_cuandoAcumuladoSuperaPlazo() {
        // Arrange
        var mapa = mapa();
        var tarea = tarea(mapa);
        var plan = plan(tarea, 5);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(plan, tarea, mapa, false, 6))
                .isInstanceOf(TiempoPlanesExcedePlazoException.class);
    }

    @Test
    void debeAplicarElOrden_cuandoFallanVariasReglas() {
        // Arrange
        var mapa = mapa();
        var tarea = tarea(mapa);
        var plan = plan(tarea, 50);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(plan, TareaDomain.VACIO, MapaRutaDomain.VACIO, true, 99))
                .isInstanceOf(TareaNoEncontradaException.class);
        assertThatThrownBy(() -> validator.validar(plan, tarea, MapaRutaDomain.VACIO, true, 99))
                .isInstanceOf(MapaRutaNoEncontradoException.class);
        assertThatThrownBy(() -> validator.validar(plan, tarea, mapa, true, 99))
                .isInstanceOf(PlanEstimadoDuplicadoException.class);
    }
}
