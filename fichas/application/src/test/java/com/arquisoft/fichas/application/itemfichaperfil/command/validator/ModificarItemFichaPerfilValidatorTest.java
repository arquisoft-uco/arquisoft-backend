package com.arquisoft.fichas.application.itemfichaperfil.command.validator;

import com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl.ModificarItemFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaNoPropiaException;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaPerfilNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarItemFichaPerfilValidatorTest {

    private final ModificarItemFichaPerfilValidatorImpl validator =
            new ModificarItemFichaPerfilValidatorImpl();

    private final UUID item = UUID.randomUUID();
    private final UUID estudiante = UUID.randomUUID();
    private final UUID fichaPerfil = UUID.randomUUID();

    @Test
    void debePasar_cuandoElItemExisteEsPropioYElEstadoNoEsTerminal() {
        // Arrange
        var estadoActual = EstadoFichaPerfilDomain.crear(fichaPerfil);

        // Act / Assert
        assertThatCode(() -> validator.validar(item, estudiante, fichaPerfil, true, true, estadoActual))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarItemNoEncontrado_cuandoElItemNoExiste() {
        // Arrange
        var estadoActual = EstadoFichaPerfilDomain.crear(fichaPerfil);

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(item, estudiante, fichaPerfil, false, true, estadoActual))
                .isInstanceOf(ItemFichaPerfilNoEncontradoException.class)
                .hasMessageContaining(item.toString());
    }

    @Test
    void debeLanzarItemNoPropio_cuandoElEstudianteNoEsDuenoDeLaFicha() {
        // Arrange
        var estadoActual = EstadoFichaPerfilDomain.crear(fichaPerfil);

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(item, estudiante, fichaPerfil, true, false, estadoActual))
                .isInstanceOf(ItemFichaNoPropiaException.class);
    }

    @Test
    void debeLanzarEstadoNoEncontrado_cuandoLaFichaNoTieneEstado() {
        // Act / Assert — EstadoFichaPerfilDomain.VACIO representa la ausencia de estado
        assertThatThrownBy(() -> validator.validar(
                item, estudiante, fichaPerfil, true, true, EstadoFichaPerfilDomain.VACIO))
                .isInstanceOf(EstadoFichaPerfilNoEncontradoException.class)
                .hasMessageContaining(fichaPerfil.toString());
    }

    @Test
    void noDebeAplicarLasReglasDependientesDeLaFicha_cuandoElItemNoExiste() {
        // Arrange — sin item, el use case no pudo resolver su ficha: llega el UUID por defecto y
        // las reglas siguientes fallarian si llegaran a ejecutarse.

        // Act / Assert — gana la primera regla, prueba de que las dependientes no corren
        assertThatThrownBy(() -> validator.validar(item, estudiante, UtilUUID.obtenerUUIDPorDefecto(),
                false, false, EstadoFichaPerfilDomain.VACIO))
                .isInstanceOf(ItemFichaPerfilNoEncontradoException.class);
    }
}
