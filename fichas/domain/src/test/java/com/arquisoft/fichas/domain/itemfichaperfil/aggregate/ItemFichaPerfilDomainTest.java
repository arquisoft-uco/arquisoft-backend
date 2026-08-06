package com.arquisoft.fichas.domain.itemfichaperfil.aggregate;

import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasFields;
import com.arquisoft.fichas.domain.tipoitem.TipoItem;
import com.arquisoft.shared.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class ItemFichaPerfilDomainTest {

    @Test
    void debeConstruirItem_cuandoDatosValidos() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        String tipoItem = "OBJETIVO_GENERAL";
        String contenido = "Este es un objetivo general válido";

        // Act
        ItemFichaPerfilDomain aggregate = ItemFichaPerfilDomain.crear(
                fichaPerfilId,
                tipoItem,
                contenido
        );

        // Assert
        assertThat(aggregate.getId()).isNotNull();
        assertThat(aggregate.getFichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(aggregate.getTipoItem()).isEqualTo(TipoItem.OBJETIVO_GENERAL);
        assertThat(aggregate.getContenido()).isEqualTo(contenido);
    }

    @Test
    void debeLanzarExcepcion_cuandoFichaPerfilIdNulo() {
        // Arrange
        UUID fichaPerfilId = null;
        String tipoItem = "OBJETIVO_GENERAL";
        String contenido = "Contenido válido";

        // Act
        Throwable exception = catchThrowable(() -> ItemFichaPerfilDomain.crear(
                fichaPerfilId,
                tipoItem,
                contenido
        ));

        // Assert
        assertThat(exception)
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.ItemFichaPerfil.FICHA_PERFIL);
    }

    @Test
    void debeLanzarExcepcion_cuandoTipoItemCodeVacio() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        String tipoItem = "";
        String contenido = "Contenido válido";

        // Act
        Throwable exception = catchThrowable(() -> ItemFichaPerfilDomain.crear(
                fichaPerfilId,
                tipoItem,
                contenido
        ));

        // Assert
        assertThat(exception)
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.ItemFichaPerfil.TIPO_ITEM);
    }

    @Test
    void debeLanzarExcepcion_cuandoContenidoVacio() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        String tipoItem = "OBJETIVO_GENERAL";
        String contenido = "";

        // Act
        Throwable exception = catchThrowable(() -> ItemFichaPerfilDomain.crear(
                fichaPerfilId,
                tipoItem,
                contenido
        ));

        // Assert
        assertThat(exception)
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.ItemFichaPerfil.CONTENIDO);
    }

    @Test
    void debeLanzarExcepcion_cuandoContenidoMuyLargo() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        String tipoItem = "OBJETIVO_GENERAL";
        String contenido = "x".repeat(7001);

        // Act
        Throwable exception = catchThrowable(() -> ItemFichaPerfilDomain.crear(
                fichaPerfilId,
                tipoItem,
                contenido
        ));

        // Assert
        assertThat(exception)
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.ItemFichaPerfil.CONTENIDO);
    }

    @Test
    void debeLanzarExcepcion_cuandoTipoItemCodeInvalido() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        String tipoItem = "TIPO_INVALIDO";
        String contenido = "Contenido válido";

        // Act
        Throwable exception = catchThrowable(() -> ItemFichaPerfilDomain.crear(
                fichaPerfilId,
                tipoItem,
                contenido
        ));

        // Assert
        assertThat(exception)
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasCodes.ItemFichaPerfil.TIPO_ITEM_INVALIDO);
    }

    @Test
    void debeAplicarTrim_cuandoContenidoTieneEspacios() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        String tipoItem = "  OBJETIVO_GENERAL  ";
        String contenido = "  Este es un contenido con espacios  ";

        // Act
        ItemFichaPerfilDomain aggregate = ItemFichaPerfilDomain.crear(
                fichaPerfilId,
                tipoItem,
                contenido
        );

        // Assert
        assertThat(aggregate.getContenido()).isEqualTo("Este es un contenido con espacios");
        assertThat(aggregate.getTipoItem()).isEqualTo(TipoItem.OBJETIVO_GENERAL);
    }

}
