package com.arquisoft.fichas.domain.itemfichaperfil;

import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.fichas.domain.tipoitem.TipoItem;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregacionItemFichaPerfilDomainTest {

    private static final String CONTENIDO = "Contenido del ítem";

    @Test
    void debeConstruirLaTransaccion_cuandoDatosValidos() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        // Act
        var item = ItemFichaPerfilDomain.crear(fichaId, TipoItem.OBJETIVO_GENERAL.name(), CONTENIDO);
        var transaccion = AgregacionItemFichaPerfilDomain.crear(item, estudianteId);

        // Assert
        assertThat(transaccion.getItem().getFichaPerfilId()).isEqualTo(fichaId);
        assertThat(transaccion.getItem().getTipoItem()).isEqualTo(TipoItem.OBJETIVO_GENERAL);
        assertThat(transaccion.getItem().getContenido()).isEqualTo(CONTENIDO);
        assertThat(transaccion.getEstudiante()).isEqualTo(estudianteId);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoEstudianteEsNulo() {
        // Arrange
        var item = ItemFichaPerfilDomain.crear(
                UUID.randomUUID(), TipoItem.OBJETIVO_GENERAL.name(), CONTENIDO);

        // Act & Assert
        assertThatThrownBy(() -> AgregacionItemFichaPerfilDomain.crear(item, null))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.ItemFichaPerfil.ESTUDIANTE);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoElItemEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> AgregacionItemFichaPerfilDomain.crear(null, UUID.randomUUID()))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasCodes.ItemFichaPerfil.ITEM_ID_REQUERIDO);
    }

    @Test
    void debeAcumularLosErroresDelItem_enUnaSolaPasada() {
        // Arrange
        String contenidoLargo = "x".repeat(FichasLimits.ItemFichaPerfil.CONTENIDO_MAX + 1);

        // Act & Assert
        assertThatThrownBy(() -> ItemFichaPerfilDomain.crear(null, "TIPO_INEXISTENTE", contenidoLargo))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasCodes.ItemFichaPerfil.FICHA_PERFIL_ID_REQUERIDO)
                .hasMessageContaining(FichasCodes.ItemFichaPerfil.TIPO_ITEM_INVALIDO)
                .hasMessageContaining(FichasCodes.ItemFichaPerfil.CONTENIDO_DEMASIADO_LARGO);
    }
}
