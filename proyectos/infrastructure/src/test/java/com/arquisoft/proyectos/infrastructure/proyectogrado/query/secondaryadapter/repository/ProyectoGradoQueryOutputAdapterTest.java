package com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoCriteria;
import com.arquisoft.proyectos.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProyectoGradoQueryOutputAdapterTest {

    private static final String EN_PROCESO = "EN_PROCESO";
    private static final String FINALIZADO = "FINALIZADO";

    @Autowired
    private ProyectoGradoQueryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private ProyectoGradoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProyectoGradoQueryOutputAdapter(repository, new ProyectoGradoJpaSpecification());
        entityManager.persist(new EstadoProyectoGradoFixtureJpaEntity(EN_PROCESO, "En proceso", "En desarrollo"));
        entityManager.persist(new EstadoProyectoGradoFixtureJpaEntity(FINALIZADO, "Finalizado", "Concluido"));
    }

    private UUID proyecto(UUID coordinador, String estado, String titulo) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ProyectoGradoJpaEntity(id, estado, coordinador, UtilUUID.generarNuevoUUID(), titulo));
        return id;
    }

    private ProyectoGradoCriteria criteria(UUID coordinador, NodoFiltro filtroCliente, int pagina, int tamanio,
                                           List<SortOrder> orden) {
        var forzado = NodoFiltro.predicado(ProyectoGradoCriteria.Campo.COORDINADOR.getClave(),
                FiltroOperador.ES, coordinador.toString());
        var raiz = filtroCliente == null
                ? forzado
                : NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, filtroCliente));
        return ProyectoGradoCriteria.builder().pagina(pagina).tamanio(tamanio).ordenamiento(orden).raiz(raiz).build();
    }

    @Test
    void debeDevolverSoloProyectosDelCoordinadorYMapearEstado_cuandoHayDeOtros() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var propio = proyecto(coordinador, EN_PROCESO, "Propio");
        proyecto(UtilUUID.generarNuevoUUID(), EN_PROCESO, "Ajeno");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarTodas(criteria(coordinador, null, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        var proyectoLeido = resultado.getContent().getFirst();
        assertThat(proyectoLeido.id()).isEqualTo(propio);
        assertThat(proyectoLeido.tituloProyecto()).isEqualTo("Propio");
        assertThat(proyectoLeido.fichaPerfil()).isNotNull();
        assertThat(proyectoLeido.estado().id()).isEqualTo(EN_PROCESO);
        assertThat(proyectoLeido.estado().nombre()).isEqualTo("En proceso");
    }

    @Test
    void debeFiltrarPorEstado_cuandoSePideEstadoProyectoGrado() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var finalizado = proyecto(coordinador, FINALIZADO, "Uno");
        proyecto(coordinador, EN_PROCESO, "Dos");
        entityManager.flush();
        entityManager.clear();
        var filtro = NodoFiltro.predicado("estadoProyectoGrado", FiltroOperador.ES, FINALIZADO);

        // Act
        var resultado = adapter.consultarTodas(criteria(coordinador, filtro, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getContent()).extracting(p -> p.id()).containsExactly(finalizado);
    }

    @Test
    void debeFiltrarPorTituloParcial_cuandoSePideContiene() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var buscado = proyecto(coordinador, EN_PROCESO, "Sistema de gestion");
        proyecto(coordinador, EN_PROCESO, "Otra cosa");
        entityManager.flush();
        entityManager.clear();
        var filtro = NodoFiltro.predicado("tituloProyecto", FiltroOperador.CONTIENE, "gestion");

        // Act
        var resultado = adapter.consultarTodas(criteria(coordinador, filtro, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getContent()).extracting(p -> p.id()).containsExactly(buscado);
    }

    @Test
    void debeFiltrarPorId_cuandoSePideId() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var buscado = proyecto(coordinador, EN_PROCESO, "Uno");
        proyecto(coordinador, EN_PROCESO, "Dos");
        entityManager.flush();
        entityManager.clear();
        var filtro = NodoFiltro.predicado("id", FiltroOperador.ES, buscado.toString());

        // Act
        var resultado = adapter.consultarTodas(criteria(coordinador, filtro, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getContent()).extracting(p -> p.id()).containsExactly(buscado);
    }

    @Test
    void debeOrdenarPorTitulo_cuandoSePideAscendenteYDescendente() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        proyecto(coordinador, EN_PROCESO, "B");
        proyecto(coordinador, EN_PROCESO, "A");
        proyecto(coordinador, EN_PROCESO, "C");
        entityManager.flush();
        entityManager.clear();

        // Act
        var ascendente = adapter.consultarTodas(criteria(coordinador, null, 0, 10,
                List.of(SortOrder.of("tituloProyecto", SortDirection.ASC))));
        var descendente = adapter.consultarTodas(criteria(coordinador, null, 0, 10,
                List.of(SortOrder.of("tituloProyecto", SortDirection.DESC))));

        // Assert
        assertThat(ascendente.getContent()).extracting(p -> p.tituloProyecto()).containsExactly("A", "B", "C");
        assertThat(descendente.getContent()).extracting(p -> p.tituloProyecto()).containsExactly("C", "B", "A");
    }

    @Test
    void debePaginar_cuandoHayMasProyectosQueElTamanio() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        proyecto(coordinador, EN_PROCESO, "A");
        proyecto(coordinador, EN_PROCESO, "B");
        proyecto(coordinador, EN_PROCESO, "C");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarTodas(criteria(coordinador, null, 1, 2,
                List.of(SortOrder.of("tituloProyecto", SortDirection.ASC))));

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(3);
        assertThat(resultado.getTotalPages()).isEqualTo(2);
        assertThat(resultado.getContent()).extracting(p -> p.tituloProyecto()).containsExactly("C");
    }

    @Test
    void debeDevolverVacio_cuandoCoordinadorSinProyectos() {
        // Arrange
        proyecto(UtilUUID.generarNuevoUUID(), EN_PROCESO, "Ajeno");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarTodas(criteria(UtilUUID.generarNuevoUUID(), null, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }
}
