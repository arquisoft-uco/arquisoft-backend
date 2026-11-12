package com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.mapper;

import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FiltroCierreProyectoEntregableTest {

    private static final NodoFiltro CANCELADO_FALSO =
            NodoFiltro.predicado("proyectoCancelado", FiltroOperador.ES, "false");
    private static final NodoFiltro FINALIZADO_FALSO =
            NodoFiltro.predicado("proyectoFinalizado", FiltroOperador.ES, "false");

    @Test
    void debeAgregarFiltrosPorDefecto_cuandoNoHayRaizDelCliente() {
        // Arrange
        var forzado = NodoFiltro.predicado("asesorId", FiltroOperador.ES, "x");

        // Act
        var raiz = FiltroCierreProyectoEntregable.combinar(List.of(forzado), null);

        // Assert
        assertThat(raiz).isEqualTo(
                NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, CANCELADO_FALSO, FINALIZADO_FALSO)));
    }

    @Test
    void debeNoAgregarProyectoFinalizadoPorDefecto_cuandoElClienteLoNombra() {
        // Arrange
        var cliente = NodoFiltro.predicado("proyectoFinalizado", FiltroOperador.ES, "true");

        // Act
        var raiz = FiltroCierreProyectoEntregable.combinar(List.of(), cliente);

        // Assert
        assertThat(raiz).isEqualTo(NodoFiltro.grupo(FiltroConector.AND, List.of(CANCELADO_FALSO, cliente)));
    }

    @Test
    void debeDetectarProyectoFinalizado_cuandoEstaAnidadoEnUnGrupo() {
        // Arrange
        var finalizado = NodoFiltro.predicado("proyectoFinalizado", FiltroOperador.NO_ES, "false");
        var cliente = NodoFiltro.grupo(FiltroConector.OR, List.of(
                NodoFiltro.predicado("asesorNombre", FiltroOperador.CONTIENE, "Ana"),
                NodoFiltro.grupo(FiltroConector.AND, List.of(finalizado))));

        // Act
        var raiz = FiltroCierreProyectoEntregable.combinar(List.of(), cliente);

        // Assert
        assertThat(raiz).isEqualTo(NodoFiltro.grupo(FiltroConector.AND, List.of(CANCELADO_FALSO, cliente)));
    }

    @Test
    void debeAgregarProyectoFinalizadoPorDefecto_cuandoElClienteNombraOtroCampoMultivalor() {
        // Arrange
        var cliente = NodoFiltro.predicadoMultivalor(
                "estadoEntregable", FiltroOperador.IN, List.of("APROBADO", "CERRADO"));

        // Act
        var raiz = FiltroCierreProyectoEntregable.combinar(List.of(), cliente);

        // Assert
        assertThat(raiz).isEqualTo(
                NodoFiltro.grupo(FiltroConector.AND, List.of(CANCELADO_FALSO, FINALIZADO_FALSO, cliente)));
    }

    @Test
    void debeSiempreForzarProyectoCanceladoEnFalse_aunqueElClienteFiltreCanceladoEnTrue() {
        // Arrange
        var cliente = NodoFiltro.predicado("proyectoCancelado", FiltroOperador.ES, "true");

        // Act
        var raiz = FiltroCierreProyectoEntregable.combinar(List.of(), cliente);

        // Assert
        assertThat(raiz).isEqualTo(
                NodoFiltro.grupo(FiltroConector.AND, List.of(CANCELADO_FALSO, FINALIZADO_FALSO, cliente)));
    }
}
