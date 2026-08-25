package com.arquisoft.fichas.application.itemfichaperfil.command.validator;

import com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl.AgregarItemFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.itemfichaperfil.ItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaNoPropiaException;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemTipoDuplicadoException;
import com.arquisoft.fichas.domain.tipoitem.TipoItem;
import org.junit.jupiter.api.Test;

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
        assertThatCode(() -> validator.validar(item(fichaPerfil), UUID.randomUUID(), true, true, false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarFichaNoEncontrada_cuandoLaFichaNoExiste() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(item(fichaPerfil), UUID.randomUUID(), false, true, false))
                .isInstanceOf(FichaPerfilNoEncontradaException.class)
                .hasMessageContaining(fichaPerfil.toString());
    }

    @Test
    void debeLanzarItemNoPropio_cuandoElEstudianteNoEsDuenoDeLaFicha() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(item(fichaPerfil), UUID.randomUUID(), true, false, false))
                .isInstanceOf(ItemFichaNoPropiaException.class);
    }

    @Test
    void debeLanzarTipoDuplicado_cuandoLaFichaYaTieneEseTipoDeItem() {
        // Arrange
        UUID fichaPerfil = UUID.randomUUID();

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(item(fichaPerfil), UUID.randomUUID(), true, true, true))
                .isInstanceOf(ItemTipoDuplicadoException.class);
    }

    @Test
    void debeReportarPrimeroLaAusenciaDeLaFicha_cuandoTodasLasReglasFallan() {
        // Arrange — el orden es parte del contrato: existencia, propiedad y por ultimo duplicidad
        UUID fichaPerfil = UUID.randomUUID();

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(item(fichaPerfil), UUID.randomUUID(), false, false, true))
                .isInstanceOf(FichaPerfilNoEncontradaException.class);
    }
}
