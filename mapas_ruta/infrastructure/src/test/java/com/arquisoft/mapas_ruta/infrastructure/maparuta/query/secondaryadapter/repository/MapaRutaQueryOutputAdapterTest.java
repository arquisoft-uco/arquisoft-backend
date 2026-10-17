package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.maparuta.query.criteria.MapaRutaCriteria;
import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.mapper.ConsultarMapasRutaCoordinadorMapper;
import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.model.ConsultarMapasRutaCoordinadorQuery;
import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaReadModel;
import com.arquisoft.mapas_ruta.infrastructure.maparuta.command.secondaryadapter.entity.MapaRutaJpaEntity;
import com.arquisoft.mapas_ruta.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroInvalidoException;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({MapaRutaQueryOutputAdapter.class, MapaRutaJpaSpecification.class})
class MapaRutaQueryOutputAdapterTest {

    private final UUID coordinador = UtilUUID.generarNuevoUUID();
    private final UUID otroCoordinador = UtilUUID.generarNuevoUUID();

    private UUID idMapa1;
    private UUID idMapa2;
    private UUID idMapa3;
    private UUID idMapaAjeno;

    @Autowired
    private MapaRutaQueryOutputAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void sembrar() {
        idMapa1 = sembrarMapa(coordinador, "Proyecto uno", LocalDate.of(2026, 3, 1), LocalDate.of(2026, 6, 1));
        idMapa2 = sembrarMapa(coordinador, "Proyecto dos", LocalDate.of(2026, 3, 1), LocalDate.of(2026, 5, 1));
        idMapa3 = sembrarMapa(coordinador, "Proyecto tres", LocalDate.of(2026, 1, 10), LocalDate.of(2026, 4, 10));
        idMapaAjeno = sembrarMapa(otroCoordinador, "Proyecto ajeno", LocalDate.of(2026, 4, 1), LocalDate.of(2026, 7, 1));
        entityManager.flush();
        entityManager.clear();
    }

    private UUID sembrarMapa(UUID coordinadorProyecto, String titulo, LocalDate inicio, LocalDate fin) {
        var proyecto = UtilUUID.generarNuevoUUID();
        entityManager.persist(ProyectoGradoJpaEntity.builder()
                .id(proyecto)
                .estadoProyectoGradoId("EN_PROCESO")
                .coordinadorId(coordinadorProyecto)
                .fichaPerfilId(UtilUUID.generarNuevoUUID())
                .tituloProyecto(titulo)
                .build());
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(MapaRutaJpaEntity.builder()
                .id(id)
                .proyectoGradoId(proyecto)
                .fechaInicio(inicio)
                .fechaFin(fin)
                .build());
        return id;
    }

    private static MapaRutaCriteria criteria(UUID solicitante, List<SortOrder> orden, NodoFiltro raizCliente) {
        var criterio = ConsultaCriteriaQuery.crear(0, 10, orden, raizCliente);
        return ConsultarMapasRutaCoordinadorMapper.toCriteria(
                ConsultarMapasRutaCoordinadorQuery.crear(solicitante, criterio));
    }

    private static NodoFiltro fecha(String campo, FiltroOperador operador, String valor) {
        return NodoFiltro.predicado(campo, operador, valor);
    }

    private static List<UUID> ids(List<MapaRutaReadModel> contenido) {
        return contenido.stream().map(MapaRutaReadModel::id).toList();
    }

    @Test
    void debeRetornarSoloLosMapasDelCoordinadorConTitulo_cuandoNoHayFiltrosDelCliente() {
        // Act
        var resultado = adapter.consultarTodos(criteria(coordinador, List.of(), null));

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(3);
        assertThat(ids(resultado.getContent())).containsExactlyInAnyOrder(idMapa1, idMapa2, idMapa3);
        assertThat(resultado.getContent()).extracting(MapaRutaReadModel::tituloProyecto)
                .containsExactlyInAnyOrder("Proyecto uno", "Proyecto dos", "Proyecto tres");
        assertThat(resultado.getContent()).allSatisfy(readModel -> {
            assertThat(readModel.proyectoGrado()).isNotNull();
            assertThat(readModel.fechaInicio()).isNotNull();
            assertThat(readModel.fechaFin()).isNotNull();
        });
    }

    @Test
    void debeNoRetornarMapasAjenos_cuandoOtroCoordinadorTieneMapas() {
        // Act
        var resultado = adapter.consultarTodos(criteria(otroCoordinador, List.of(), null));

        // Assert
        assertThat(ids(resultado.getContent())).containsExactly(idMapaAjeno);
        assertThat(resultado.getTotalElements()).isEqualTo(1);
    }

    @Test
    void debeRetornarPaginaVacia_cuandoElCoordinadorNoTieneProyectos() {
        // Act
        var resultado = adapter.consultarTodos(criteria(UtilUUID.generarNuevoUUID(), List.of(), null));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeRetornarSoloMapasPropios_cuandoElOrDelClienteAbarcariaMapasAjenos() {
        // Arrange
        var raizOr = NodoFiltro.grupo(FiltroConector.OR, List.of(
                fecha("fechaInicio", FiltroOperador.MAYOR_IGUAL_QUE, "2000-01-01"),
                fecha("fechaFin", FiltroOperador.MENOR_IGUAL_QUE, "2100-01-01")));

        // Act
        var resultado = adapter.consultarTodos(criteria(coordinador, List.of(), raizOr));

        // Assert
        assertThat(ids(resultado.getContent())).containsExactlyInAnyOrder(idMapa1, idMapa2, idMapa3);
        assertThat(resultado.getTotalElements()).isEqualTo(3);
    }

    @Test
    void debeSoloEstrechar_cuandoElClienteFiltraPorElCoordinadorDeOtro() {
        // Arrange
        var raiz = NodoFiltro.predicado(MapaRutaCriteria.Campo.COORDINADOR.getClave(),
                FiltroOperador.ES, otroCoordinador.toString());

        // Act
        var resultado = adapter.consultarTodos(criteria(coordinador, List.of(), raiz));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeFiltrarPorFechaInicio_cuandoElOperadorEsMayorIgualQue() {
        // Act
        var resultado = adapter.consultarTodos(criteria(coordinador, List.of(),
                fecha("fechaInicio", FiltroOperador.MAYOR_IGUAL_QUE, "2026-03-01")));

        // Assert
        assertThat(ids(resultado.getContent())).containsExactlyInAnyOrder(idMapa1, idMapa2);
        assertThat(resultado.getTotalElements()).isEqualTo(2);
    }

    @Test
    void debeFiltrarPorRango_cuandoElClienteCombinaInicioYFinConAnd() {
        // Arrange
        var raiz = NodoFiltro.grupo(FiltroConector.AND, List.of(
                fecha("fechaInicio", FiltroOperador.MAYOR_IGUAL_QUE, "2026-03-01"),
                fecha("fechaFin", FiltroOperador.MENOR_IGUAL_QUE, "2026-05-15")));

        // Act
        var resultado = adapter.consultarTodos(criteria(coordinador, List.of(), raiz));

        // Assert
        assertThat(ids(resultado.getContent())).containsExactly(idMapa2);
    }

    @Test
    void debeRetornarLosMapasVigentesEnLaFecha_cuandoElClientePideInicioMenorYFinMayor() {
        // Arrange
        var raiz = NodoFiltro.grupo(FiltroConector.AND, List.of(
                fecha("fechaInicio", FiltroOperador.MENOR_IGUAL_QUE, "2026-04-15"),
                fecha("fechaFin", FiltroOperador.MAYOR_IGUAL_QUE, "2026-04-15")));

        // Act
        var resultado = adapter.consultarTodos(criteria(coordinador, List.of(), raiz));

        // Assert
        assertThat(ids(resultado.getContent())).containsExactlyInAnyOrder(idMapa1, idMapa2);
    }

    @Test
    void debeOrdenarPorInicioDescYFinAsc_cuandoElClienteNoPideOrden() {
        // Act
        var resultado = adapter.consultarTodos(criteria(coordinador, List.of(), null));

        // Assert
        assertThat(ids(resultado.getContent())).containsExactly(idMapa2, idMapa1, idMapa3);
    }

    @Test
    void debeRespetarElOrdenExplicito_cuandoElClienteOrdenaPorFinAsc() {
        // Act
        var resultado = adapter.consultarTodos(criteria(coordinador,
                List.of(SortOrder.of("fechaFin", SortDirection.ASC)), null));

        // Assert
        assertThat(ids(resultado.getContent())).containsExactly(idMapa3, idMapa2, idMapa1);
    }

    @Test
    void debeLanzarFiltroInvalidoException_cuandoElOperadorOLaFechaSonInvalidos() {
        // Arrange
        var operadorDeTexto = criteria(coordinador, List.of(),
                fecha("fechaInicio", FiltroOperador.CONTIENE, "2026"));
        var fechaMalFormada = criteria(coordinador, List.of(),
                fecha("fechaInicio", FiltroOperador.ES, "01/03/2026"));

        // Act & Assert
        assertThatThrownBy(() -> adapter.consultarTodos(operadorDeTexto))
                .isInstanceOf(FiltroInvalidoException.class);
        assertThatThrownBy(() -> adapter.consultarTodos(fechaMalFormada))
                .isInstanceOf(FiltroInvalidoException.class);
    }
}
