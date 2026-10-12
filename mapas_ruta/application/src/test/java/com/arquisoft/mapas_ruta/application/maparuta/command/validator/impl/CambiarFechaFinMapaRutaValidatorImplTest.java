package com.arquisoft.mapas_ruta.application.maparuta.command.validator.impl;

import com.arquisoft.mapas_ruta.domain.maparuta.CambioFechaFinMapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.maparuta.exception.FechaFinMapaRutaNoPosteriorException;
import com.arquisoft.mapas_ruta.domain.maparuta.exception.MapaRutaNoEncontradoException;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.mapas_ruta.domain.proyectogrado.exception.ProyectoGradoNoEnProcesoException;
import com.arquisoft.mapas_ruta.domain.proyectogrado.exception.ProyectoGradoNoPropietarioException;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarFechaFinMapaRutaValidatorImplTest {

    private static final LocalDate INICIO = LocalDate.of(2026, 10, 1);
    private static final LocalDate FIN_ACTUAL = LocalDate.of(2026, 12, 1);

    private final CambiarFechaFinMapaRutaValidatorImpl validator = new CambiarFechaFinMapaRutaValidatorImpl();

    private static MapaRutaDomain mapa(UUID proyectoGrado) {
        return MapaRutaDomain.reconstruir(UtilUUID.generarNuevoUUID(), proyectoGrado, INICIO, FIN_ACTUAL);
    }

    private static ProyectoGradoDomain proyecto(UUID id, UUID coordinador, EstadoProyectoGrado estado) {
        return ProyectoGradoDomain.reconstruir(id, estado, coordinador, UtilUUID.generarNuevoUUID(), "Titulo");
    }

    private static CambioFechaFinMapaRutaDomain cambio(MapaRutaDomain mapa, UUID coordinador, LocalDate fechaFin) {
        return CambioFechaFinMapaRutaDomain.crear(mapa.getId(), fechaFin, coordinador);
    }

    @Test
    void debePasar_cuandoTodoValido() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var mapa = mapa(proyectoGrado);
        var proyecto = proyecto(proyectoGrado, coordinador, EstadoProyectoGrado.EN_PROCESO);
        var cambio = cambio(mapa, coordinador, FIN_ACTUAL.plusDays(10));

        // Act & Assert
        assertThatCode(() -> validator.validar(cambio, mapa, proyecto)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarMapaNoEncontrado_cuandoElMapaEsVacio() {
        // Arrange
        var cambio = CambioFechaFinMapaRutaDomain.crear(
                UtilUUID.generarNuevoUUID(), FIN_ACTUAL, UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, MapaRutaDomain.VACIO, ProyectoGradoDomain.VACIO))
                .isInstanceOf(MapaRutaNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoPropietario_cuandoElCoordinadorEsOtro() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var mapa = mapa(proyectoGrado);
        var proyecto = proyecto(proyectoGrado, UtilUUID.generarNuevoUUID(), EstadoProyectoGrado.EN_PROCESO);
        var cambio = cambio(mapa, UtilUUID.generarNuevoUUID(), FIN_ACTUAL.plusDays(10));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, mapa, proyecto))
                .isInstanceOf(ProyectoGradoNoPropietarioException.class);
    }

    @Test
    void debeLanzarNoEnProceso_cuandoElProyectoEstaAtrasado() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var mapa = mapa(proyectoGrado);
        var proyecto = proyecto(proyectoGrado, coordinador, EstadoProyectoGrado.ATRASADO);
        var cambio = cambio(mapa, coordinador, FIN_ACTUAL.plusDays(10));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, mapa, proyecto))
                .isInstanceOf(ProyectoGradoNoEnProcesoException.class);
    }

    @Test
    void debeLanzarFechaFinNoPosterior_cuandoFinNoSuperaElInicio() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var mapa = mapa(proyectoGrado);
        var proyecto = proyecto(proyectoGrado, coordinador, EstadoProyectoGrado.EN_PROCESO);
        var cambio = cambio(mapa, coordinador, INICIO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, mapa, proyecto))
                .isInstanceOf(FechaFinMapaRutaNoPosteriorException.class);
    }

    @Test
    void debePriorizarMapaNoEncontradoSobreElResto_cuandoTodoFalla() {
        // Arrange
        var cambio = CambioFechaFinMapaRutaDomain.crear(
                UtilUUID.generarNuevoUUID(), INICIO, UtilUUID.generarNuevoUUID());
        var proyecto = proyecto(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), EstadoProyectoGrado.ATRASADO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, MapaRutaDomain.VACIO, proyecto))
                .isInstanceOf(MapaRutaNoEncontradoException.class);
    }
}
