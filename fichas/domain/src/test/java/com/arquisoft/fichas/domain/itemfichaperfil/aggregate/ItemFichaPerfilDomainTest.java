package com.arquisoft.fichas.domain.itemfichaperfil.aggregate;

import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasFields;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.tipoitem.TipoItem;
import com.arquisoft.shared.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

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

    @Test
    void debeModificarContenido_cuandoContenidoValido() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        String tipoItem = "OBJETIVO_GENERAL";
        String contenidoInicial = "Contenido inicial";
        ItemFichaPerfilDomain aggregate = ItemFichaPerfilDomain.crear(
                fichaPerfilId,
                tipoItem,
                contenidoInicial
        );
        String nuevoContenido = "Contenido modificado";

        // Act
        aggregate.modificarContenido(nuevoContenido, EstadoFicha.EN_CONSTRUCCION);

        // Assert
        assertThat(aggregate.getContenido()).isEqualTo(nuevoContenido);
        assertThat(aggregate.getTipoItem()).isEqualTo(TipoItem.OBJETIVO_GENERAL);
        assertThat(aggregate.getFichaPerfilId()).isEqualTo(fichaPerfilId);
    }

    @Test
    void debeLanzarExcepcion_cuandoModificarConContenidoVacio() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        String tipoItem = "OBJETIVO_GENERAL";
        String contenidoInicial = "Contenido inicial";
        ItemFichaPerfilDomain aggregate = ItemFichaPerfilDomain.crear(
                fichaPerfilId,
                tipoItem,
                contenidoInicial
        );

        // Act
        Throwable exception = catchThrowable(
                () -> aggregate.modificarContenido("", EstadoFicha.EN_CONSTRUCCION));

        // Assert
        assertThat(exception)
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.ItemFichaPerfil.CONTENIDO);
    }

    @Test
    void debeLanzarExcepcion_cuandoModificarConContenidoDemasiado_largo() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        String tipoItem = "OBJETIVO_GENERAL";
        String contenidoInicial = "Contenido inicial";
        ItemFichaPerfilDomain aggregate = ItemFichaPerfilDomain.crear(
                fichaPerfilId,
                tipoItem,
                contenidoInicial
        );
        String contenidoLargo = "x".repeat(7001);

        // Act
        Throwable exception = catchThrowable(
                () -> aggregate.modificarContenido(contenidoLargo, EstadoFicha.EN_CONSTRUCCION));

        // Assert
        assertThat(exception)
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.ItemFichaPerfil.CONTENIDO);
    }

    @Test
    void debeLimpiarEspacios_cuandoModificarContenido() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        String tipoItem = "OBJETIVO_GENERAL";
        String contenidoInicial = "Contenido inicial";
        ItemFichaPerfilDomain aggregate = ItemFichaPerfilDomain.crear(
                fichaPerfilId,
                tipoItem,
                contenidoInicial
        );
        String nuevoContenido = "  Contenido con espacios  ";

        // Act
        aggregate.modificarContenido(nuevoContenido, EstadoFicha.EN_CONSTRUCCION);

        // Assert
        assertThat(aggregate.getContenido()).isEqualTo("Contenido con espacios");
    }

    // ─── HU-033: la ficha debe estar en un estado que permita modificaciones ──

    @ParameterizedTest
    @EnumSource(value = EstadoFicha.class,
            names = {"APROBADA", "APROBADA_CON_OBSERVACIONES", "NO_APROBADA"})
    void debeLanzarExcepcion_cuandoEstadoFichaEsTerminal(EstadoFicha estadoTerminal) {
        // Arrange
        ItemFichaPerfilDomain aggregate = ItemFichaPerfilDomain.reconstruir(
                UUID.randomUUID(),
                UUID.randomUUID(),
                TipoItem.OBJETIVO_GENERAL,
                "Contenido original"
        );

        // Act
        Throwable exception = catchThrowable(
                () -> aggregate.modificarContenido("Contenido nuevo", estadoTerminal));

        // Assert
        assertThat(exception)
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasCodes.ItemFichaPerfil.ESTADO_FICHA_NO_MODIFICABLE);
        assertThat(aggregate.getContenido()).isEqualTo("Contenido original");
    }

    @ParameterizedTest
    @EnumSource(value = EstadoFicha.class,
            names = {"EN_CONSTRUCCION", "DISPONIBLE_PARA_EVALUACION"})
    void debeModificarContenido_cuandoEstadoFichaNoEsTerminal(EstadoFicha estadoModificable) {
        // Arrange
        ItemFichaPerfilDomain aggregate = ItemFichaPerfilDomain.reconstruir(
                UUID.randomUUID(),
                UUID.randomUUID(),
                TipoItem.OBJETIVO_GENERAL,
                "Contenido original"
        );

        // Act
        aggregate.modificarContenido("Contenido nuevo", estadoModificable);

        // Assert
        assertThat(aggregate.getContenido()).isEqualTo("Contenido nuevo");
    }

    @Test
    void debeLanzarExcepcion_cuandoEstadoFichaEsNulo() {
        // Arrange
        ItemFichaPerfilDomain aggregate = ItemFichaPerfilDomain.reconstruir(
                UUID.randomUUID(),
                UUID.randomUUID(),
                TipoItem.OBJETIVO_GENERAL,
                "Contenido original"
        );

        // Act
        Throwable exception = catchThrowable(
                () -> aggregate.modificarContenido("Contenido nuevo", null));

        // Assert
        assertThat(exception)
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasCodes.ItemFichaPerfil.ESTADO_FICHA_REQUERIDO);
        assertThat(aggregate.getContenido()).isEqualTo("Contenido original");
    }

    // ─── HU-034: validar invariante POL-05 (no remover ítem con revisiones) ──

    @Test
    void debeLanzarDomainValidationException_cuandoItemTieneRevisiones() {
        // Arrange
        ItemFichaPerfilDomain aggregate = ItemFichaPerfilDomain.reconstruir(
                UUID.randomUUID(),
                UUID.randomUUID(),
                TipoItem.OBJETIVO_GENERAL,
                "Contenido existente"
        );

        // Act
        Throwable exception = catchThrowable(() -> aggregate.removerse(1));

        // Assert
        assertThat(exception).isInstanceOf(DomainValidationException.class);

        DomainValidationException validationException = (DomainValidationException) exception;
        assertThat(validationException.getValidationResult().getErrores()).hasSize(1);
        assertThat(validationException.getValidationResult().getErrores().get(0).campo())
                .isEqualTo(FichasFields.ItemFichaPerfil.REVISIONES);
        assertThat(validationException.getValidationResult().getErrores().get(0).codigoError())
                .isEqualTo(FichasCodes.ItemFichaPerfil.ITEM_CON_REVISIONES);
    }

    @Test
    void debePermitirRemover_cuandoSinRevisiones() {
        // Arrange
        ItemFichaPerfilDomain aggregate = ItemFichaPerfilDomain.reconstruir(
                UUID.randomUUID(),
                UUID.randomUUID(),
                TipoItem.OBJETIVO_GENERAL,
                "Contenido existente"
        );

        // Act
        Throwable exception = catchThrowable(() -> aggregate.removerse(0));

        // Assert
        assertThat(exception).isNull();
    }
}
