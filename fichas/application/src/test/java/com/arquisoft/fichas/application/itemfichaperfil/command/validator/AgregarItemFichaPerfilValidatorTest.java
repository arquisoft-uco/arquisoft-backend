package com.arquisoft.fichas.application.itemfichaperfil.command.validator;

import com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl.AgregarItemFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilTerminalException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.itemfichaperfil.ItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaNoPropiaException;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemTipoDuplicadoException;
import com.arquisoft.fichas.domain.tipoitem.TipoItem;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarItemFichaPerfilValidatorTest {

    private final AgregarItemFichaPerfilValidatorImpl validator = new AgregarItemFichaPerfilValidatorImpl();

    private ItemFichaPerfilDomain item(UUID fichaPerfil) {
        return ItemFichaPerfilDomain.crear(
                fichaPerfil, TipoItem.OBJETIVO_GENERAL.getId(), "Contenido del item");
    }

    @Test
    void debePasar_cuandoLaFichaExisteEsPropiaYElTipoEstaLibre() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();

        // Act / Assert
        assertThatCode(() -> validator.validar(item(fichaPerfil), UUID.randomUUID(), true, true, enConstruccion(fichaPerfil), false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarFichaNoEncontrada_cuandoLaFichaNoExiste() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(item(fichaPerfil), UUID.randomUUID(), false, true, enConstruccion(fichaPerfil), false))
                .isInstanceOf(FichaPerfilNoEncontradaException.class)
                .hasMessageContaining(fichaPerfil.toString());
    }

    @Test
    void debeLanzarItemNoPropio_cuandoElEstudianteNoEsDuenoDeLaFicha() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(item(fichaPerfil), UUID.randomUUID(), true, false, enConstruccion(fichaPerfil), false))
                .isInstanceOf(ItemFichaNoPropiaException.class);
    }

    @Test
    void debeLanzarTipoDuplicado_cuandoLaFichaYaTieneEseTipoDeItem() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(item(fichaPerfil), UUID.randomUUID(), true, true, enConstruccion(fichaPerfil), true))
                .isInstanceOf(ItemTipoDuplicadoException.class);
    }

    @Test
    void debeReportarPrimeroLaAusenciaDeLaFicha_cuandoTodasLasReglasFallan() {
        // Arrange — el orden es parte del contrato: existencia, propiedad y por ultimo duplicidad
        UUID fichaPerfil = UUID.randomUUID();

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(item(fichaPerfil), UUID.randomUUID(), false, false, enConstruccion(fichaPerfil), true))
                .isInstanceOf(FichaPerfilNoEncontradaException.class);
    }

    @Test
    void debeLanzarEstadoTerminal_cuandoLaFichaEstaAprobada() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();
        var aprobada = EstadoFichaPerfilDomain.reconstruir(
                UUID.randomUUID(), fichaPerfil, EstadoFicha.APROBADA, Instant.now());

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(item(fichaPerfil), UUID.randomUUID(), true, true, aprobada, false))
                .isInstanceOf(EstadoFichaPerfilTerminalException.class);
    }

    @Test
    void debeLanzarEstadoNoEncontrado_cuandoLaFichaNoTieneEstado() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(
                item(fichaPerfil), UUID.randomUUID(), true, true, EstadoFichaPerfilDomain.VACIO, false))
                .isInstanceOf(EstadoFichaPerfilNoEncontradoException.class);
    }

    @Test
    void debeReportarEstadoTerminalAntesQueTipoDuplicado_cuandoAmbasFallan() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();
        var noAprobada = EstadoFichaPerfilDomain.reconstruir(
                UUID.randomUUID(), fichaPerfil, EstadoFicha.NO_APROBADA, Instant.now());

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(item(fichaPerfil), UUID.randomUUID(), true, true, noAprobada, true))
                .isInstanceOf(EstadoFichaPerfilTerminalException.class);
    }

    private EstadoFichaPerfilDomain enConstruccion(UUID fichaPerfil) {
        return EstadoFichaPerfilDomain.crear(fichaPerfil);
    }
}
