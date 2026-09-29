package com.arquisoft.mapas_ruta.application.maparuta.command.validator.impl;

import com.arquisoft.mapas_ruta.domain.maparuta.AgregacionMapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.maparuta.exception.MapaRutaDuplicadoException;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.mapas_ruta.domain.proyectogrado.exception.ProyectoGradoNoEnProcesoException;
import com.arquisoft.mapas_ruta.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.mapas_ruta.domain.proyectogrado.exception.ProyectoGradoNoPropietarioException;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarMapaRutaValidatorImplTest {

    private final AgregarMapaRutaValidatorImpl validator = new AgregarMapaRutaValidatorImpl();

    private static AgregacionMapaRutaDomain agregacion(UUID proyectoGrado, UUID coordinador) {
        var mapaRuta = MapaRutaDomain.crear(proyectoGrado, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1));
        return AgregacionMapaRutaDomain.crear(mapaRuta, coordinador);
    }

    private static ProyectoGradoDomain proyecto(UUID id, UUID coordinador, EstadoProyectoGrado estado) {
        return ProyectoGradoDomain.reconstruir(id, estado, coordinador, UtilUUID.generarNuevoUUID(), "Titulo");
    }

    @Test
    void debePasar_cuandoTodasLasReglasSeCumplen() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var proyecto = proyecto(proyectoGrado, coordinador, EstadoProyectoGrado.EN_PROCESO);

        // Act & Assert
        assertThatCode(() -> validator.validar(agregacion(proyectoGrado, coordinador), proyecto, false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoElProyectoEsVacioSinEvaluarLasReglasSiguientes() {
        // Arrange
        var agregacion = agregacion(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(agregacion, ProyectoGradoDomain.VACIO, true))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoPropietario_cuandoElCoordinadorEsOtro() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var proyecto = proyecto(proyectoGrado, UtilUUID.generarNuevoUUID(), EstadoProyectoGrado.ATRASADO);
        var agregacion = agregacion(proyectoGrado, UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(agregacion, proyecto, true))
                .isInstanceOf(ProyectoGradoNoPropietarioException.class);
    }

    @Test
    void debeLanzarNoEnProceso_cuandoElProyectoEstaAtrasadoYYaTieneMapa() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var proyecto = proyecto(proyectoGrado, coordinador, EstadoProyectoGrado.ATRASADO);
        var agregacion = agregacion(proyectoGrado, coordinador);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(agregacion, proyecto, true))
                .isInstanceOf(ProyectoGradoNoEnProcesoException.class);
    }

    @Test
    void debeLanzarDuplicado_cuandoElProyectoYaTieneMapaRuta() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var proyecto = proyecto(proyectoGrado, coordinador, EstadoProyectoGrado.EN_PROCESO);
        var agregacion = agregacion(proyectoGrado, coordinador);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(agregacion, proyecto, true))
                .isInstanceOf(MapaRutaDuplicadoException.class);
    }
}
