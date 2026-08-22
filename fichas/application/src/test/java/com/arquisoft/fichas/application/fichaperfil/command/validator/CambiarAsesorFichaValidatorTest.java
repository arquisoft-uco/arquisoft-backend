package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.application.fichaperfil.command.validator.impl.CambiarAsesorFichaValidatorImpl;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.CambioAsesorFichaDomain;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.fichaperfil.exception.MismoAsesorFichaException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarAsesorFichaValidatorTest {

    private final CambiarAsesorFichaValidatorImpl validator = new CambiarAsesorFichaValidatorImpl();

    private final UUID asesorActual = UUID.randomUUID();
    private final UUID nuevoAsesor = UUID.randomUUID();
    private final FichaPerfilDomain ficha = FichaPerfilDomain.crear("Titulo de prueba", asesorActual);

    private AsesorFichaDomain asesor(UUID id) {
        return AsesorFichaDomain.reconstruir(id, "A001", "Ana Asesora", "ana@arquisoft.com");
    }

    private CambioAsesorFichaDomain cambio(UUID nuevo) {
        return CambioAsesorFichaDomain.crear(ficha.getId(), nuevo);
    }

    @Test
    void debePasar_cuandoTodoExisteYElAsesorEsDistinto() {
        // Arrange
        var estadoActual = EstadoFichaPerfilDomain.crear(ficha.getId());

        // Act / Assert
        assertThatCode(() -> validator.validar(
                cambio(nuevoAsesor), ficha, asesor(nuevoAsesor), estadoActual))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarFichaNoEncontrada_cuandoLaFichaEsVacia() {
        // Arrange — VACIO es como viaja la ausencia de un agregado hasta el validator
        UUID fichaId = UUID.randomUUID();
        var cambio = CambioAsesorFichaDomain.crear(fichaId, nuevoAsesor);

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(cambio, FichaPerfilDomain.VACIO,
                AsesorFichaDomain.VACIO, EstadoFichaPerfilDomain.VACIO))
                .isInstanceOf(FichaPerfilNoEncontradaException.class)
                .hasMessageContaining(fichaId.toString());
    }

    @Test
    void debeLanzarAsesorNoEncontrado_cuandoElAsesorEsVacio() {
        // Arrange
        var estadoActual = EstadoFichaPerfilDomain.crear(ficha.getId());

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(
                cambio(nuevoAsesor), ficha, AsesorFichaDomain.VACIO, estadoActual))
                .isInstanceOf(AsesorFichaNoEncontradoException.class)
                .hasMessageContaining(nuevoAsesor.toString());
    }

    @Test
    void debeLanzarEstadoNoEncontrado_cuandoLaFichaNoTieneEstado() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(
                cambio(nuevoAsesor), ficha, asesor(nuevoAsesor), EstadoFichaPerfilDomain.VACIO))
                .isInstanceOf(EstadoFichaPerfilNoEncontradoException.class)
                .hasMessageContaining(ficha.getId().toString());
    }

    @Test
    void debeLanzarMismoAsesor_cuandoElNuevoAsesorEsElActual() {
        // Arrange
        var estadoActual = EstadoFichaPerfilDomain.crear(ficha.getId());

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(
                cambio(asesorActual), ficha, asesor(asesorActual), estadoActual))
                .isInstanceOf(MismoAsesorFichaException.class);
    }

    @Test
    void debeReportarPrimeroLaAusenciaDeLaFicha_cuandoTodasLasReglasFallan() {
        // Act / Assert — el orden es parte del contrato: ficha, asesor, estado y por ultimo comparacion
        assertThatThrownBy(() -> validator.validar(cambio(nuevoAsesor), FichaPerfilDomain.VACIO,
                AsesorFichaDomain.VACIO, EstadoFichaPerfilDomain.VACIO))
                .isInstanceOf(FichaPerfilNoEncontradaException.class);
    }
}
