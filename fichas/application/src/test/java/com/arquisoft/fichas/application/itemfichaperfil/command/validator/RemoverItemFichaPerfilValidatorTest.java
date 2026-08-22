package com.arquisoft.fichas.application.itemfichaperfil.command.validator;

import com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl.RemoverItemFichaPerfilValidatorImpl;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPropietarioException;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemConRevisionesException;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaPerfilNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverItemFichaPerfilValidatorTest {

    private final RemoverItemFichaPerfilValidatorImpl validator = new RemoverItemFichaPerfilValidatorImpl();

    private final UUID item = UUID.randomUUID();
    private final UUID estudiante = UUID.randomUUID();
    private final UUID fichaPerfil = UUID.randomUUID();

    @Test
    void debePasar_cuandoElItemExisteEsPropioYNoTieneRevisiones() {
        // Act / Assert
        assertThatCode(() -> validator.validar(item, estudiante, fichaPerfil, true, true, 0L))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarItemNoEncontrado_cuandoElItemNoExiste() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(item, estudiante, fichaPerfil, false, true, 0L))
                .isInstanceOf(ItemFichaPerfilNoEncontradoException.class)
                .hasMessageContaining(item.toString());
    }

    @Test
    void debeLanzarNoPropietario_cuandoElEstudianteNoEsDuenoDeLaFicha() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(item, estudiante, fichaPerfil, true, false, 0L))
                .isInstanceOf(FichaNoPropietarioException.class);
    }

    @Test
    void debeLanzarItemConRevisiones_cuandoElItemTieneRevisiones() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(item, estudiante, fichaPerfil, true, true, 3L))
                .isInstanceOf(ItemConRevisionesException.class);
    }

    @Test
    void noDebeAplicarLasReglasDependientes_cuandoElItemNoExiste() {
        // Arrange — cuando el item no existe, el use case no pudo resolver su ficha: llega el UUID
        // por defecto y las reglas siguientes fallarian si llegaran a ejecutarse.

        // Act / Assert — gana la primera regla, prueba de que las dependientes no corren
        assertThatThrownBy(() -> validator.validar(
                item, estudiante, UtilUUID.obtenerUUIDPorDefecto(), false, false, 3L))
                .isInstanceOf(ItemFichaPerfilNoEncontradoException.class);
    }
}
