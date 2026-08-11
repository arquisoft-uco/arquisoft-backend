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

class AgregarItemFichaPerfilDomainTest {

    private static final String CONTENIDO = "Contenido del ítem";

    @Test
    void debeConstruirLaTransaccion_cuandoDatosValidos() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        // Act
        var transaccion = AgregarItemFichaPerfilDomain.crear(
                fichaId, TipoItem.OBJETIVO_GENERAL.name(), CONTENIDO, estudianteId);

        // Assert
        assertThat(transaccion.getItem().getFichaPerfilId()).isEqualTo(fichaId);
        assertThat(transaccion.getItem().getTipoItem()).isEqualTo(TipoItem.OBJETIVO_GENERAL);
        assertThat(transaccion.getItem().getContenido()).isEqualTo(CONTENIDO);
        assertThat(transaccion.getEstudiante()).isEqualTo(estudianteId);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoEstudianteEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> AgregarItemFichaPerfilDomain.crear(
                UUID.randomUUID(), TipoItem.OBJETIVO_GENERAL.name(), CONTENIDO, null))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.ItemFichaPerfil.ESTUDIANTE);
    }

    @Test
    void debeAcumularErroresDelItemYDelActor_enUnaSolaPasada() {
        // Arrange — todo inválido a la vez: el ítem y el estudiante que lo solicita
        String contenidoLargo = "x".repeat(FichasLimits.ItemFichaPerfil.CONTENIDO_MAX + 1);

        // Act & Assert — el actor no puede quedar sin reportar porque el ítem falle primero
        assertThatThrownBy(() -> AgregarItemFichaPerfilDomain.crear(
                null, "TIPO_INEXISTENTE", contenidoLargo, null))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasCodes.ItemFichaPerfil.FICHA_PERFIL_ID_REQUERIDO)
                .hasMessageContaining(FichasCodes.ItemFichaPerfil.TIPO_ITEM_INVALIDO)
                .hasMessageContaining(FichasCodes.ItemFichaPerfil.CONTENIDO_DEMASIADO_LARGO)
                .hasMessageContaining(FichasCodes.ItemFichaPerfil.ESTUDIANTE_REQUERIDO);
    }
}
