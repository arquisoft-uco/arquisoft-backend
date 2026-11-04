package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.query.secondaryadapter.repository;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.criteria.RepositorioArtefactoCriteria;
import com.arquisoft.repositorio_artefactos.infrastructure.administrador.command.secondaryadapter.entity.AdministradorJpaEntity;
import com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.secondaryadapter.entity.RepositorioArtefactoJpaEntity;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RepositorioArtefactoQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RepositorioArtefactoQueryRepository queryRepository;

    private RepositorioArtefactoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new RepositorioArtefactoQueryOutputAdapter(
                queryRepository, new RepositorioArtefactoJpaSpecification());
    }

    private UUID sembrarAdministrador(String identificador, String nombre, Instant eliminadoEn) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(AdministradorJpaEntity.builder()
                .id(id).identificador(identificador).nombre(nombre)
                .email(identificador + "@uco.edu.co")
                .ocurridoEn(Instant.now()).eliminadoEn(eliminadoEn).build());
        return id;
    }

    private UUID sembrarRepositorio(String nombre, String descripcion, UUID administrador) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(RepositorioArtefactoJpaEntity.builder()
                .id(id).nombreRepositorio(nombre).descripcion(descripcion).administrador(administrador).build());
        return id;
    }

    private RepositorioArtefactoCriteria criteria(NodoFiltro raiz, int pagina, int tamanio, List<SortOrder> orden) {
        return RepositorioArtefactoCriteria.builder()
                .pagina(pagina).tamanio(tamanio).ordenamiento(orden).raiz(raiz).build();
    }

    @Test
    void debeRetornarTodosLosRepositoriosConSuAdministrador_cuandoNoHayFiltros() {
        // Arrange
        var administrador = sembrarAdministrador("123", "Ana Perez", null);
        var id = sembrarRepositorio("Repo uno", "Primera descripcion", administrador);
        sembrarRepositorio("Repo dos", "Segunda descripcion", administrador);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarTodos(criteria(null, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(2L);
        var leido = resultado.getContent().stream().filter(r -> r.id().equals(id)).findFirst().orElseThrow();
        assertThat(leido.nombreRepositorio()).isEqualTo("Repo uno");
        assertThat(leido.descripcion()).isEqualTo("Primera descripcion");
        assertThat(leido.administrador().id()).isEqualTo(administrador);
        assertThat(leido.administrador().identificador()).isEqualTo("123");
        assertThat(leido.administrador().nombre()).isEqualTo("Ana Perez");
        assertThat(leido.administrador().email()).isEqualTo("123@uco.edu.co");
    }

    @Test
    void debeListarRepositorio_cuandoSuAdministradorEstaDadoDeBaja() {
        // Arrange
        var baja = Instant.parse("2026-09-24T10:00:00Z");
        var administrador = sembrarAdministrador("999", "Laura Gomez", baja);
        sembrarRepositorio("Repo historico", "Descripcion", administrador);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarTodos(criteria(null, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).administrador().nombre()).isEqualTo("Laura Gomez");
    }

    @Test
    void debeFiltrarPorNombreRepositorio_cuandoHayCoincidencia() {
        // Arrange
        var administrador = sembrarAdministrador("123", "Ana Perez", null);
        sembrarRepositorio("Backend base", "Descripcion", administrador);
        sembrarRepositorio("Frontend base", "Descripcion", administrador);
        entityManager.flush();
        var raiz = NodoFiltro.predicado("nombreRepositorio", FiltroOperador.CONTIENE, "back");

        // Act
        var resultado = adapter.consultarTodos(criteria(raiz, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).nombreRepositorio()).isEqualTo("Backend base");
    }

    @Test
    void debeFiltrarPorAdministradorId_cuandoHayCoincidencia() {
        // Arrange
        var ana = sembrarAdministrador("123", "Ana Perez", null);
        var luis = sembrarAdministrador("456", "Luis Diaz", null);
        sembrarRepositorio("Repo de Ana", "Descripcion", ana);
        sembrarRepositorio("Repo de Luis", "Descripcion", luis);
        entityManager.flush();
        var raiz = NodoFiltro.predicado("administradorId", FiltroOperador.ES, luis.toString());

        // Act
        var resultado = adapter.consultarTodos(criteria(raiz, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).nombreRepositorio()).isEqualTo("Repo de Luis");
    }

    @Test
    void debeFiltrarPorAdministradorNombreEIdentificador_cuandoHayCoincidencia() {
        // Arrange
        var ana = sembrarAdministrador("123", "Ana Perez", null);
        var luis = sembrarAdministrador("456", "Luis Diaz", null);
        sembrarRepositorio("Repo de Ana", "Descripcion", ana);
        sembrarRepositorio("Repo de Luis", "Descripcion", luis);
        entityManager.flush();
        var porNombre = NodoFiltro.predicado("administradorNombre", FiltroOperador.CONTIENE, "luis");
        var porIdentificador = NodoFiltro.predicado("administradorIdentificador", FiltroOperador.ES, "123");

        // Act
        var resultadoNombre = adapter.consultarTodos(criteria(porNombre, 0, 10, List.of()));
        var resultadoIdentificador = adapter.consultarTodos(criteria(porIdentificador, 0, 10, List.of()));

        // Assert
        assertThat(resultadoNombre.getContent()).extracting("nombreRepositorio").containsExactly("Repo de Luis");
        assertThat(resultadoIdentificador.getContent()).extracting("nombreRepositorio")
                .containsExactly("Repo de Ana");
    }

    @Test
    void debePaginar_cuandoHayMasRegistrosQueElTamanio() {
        // Arrange
        var administrador = sembrarAdministrador("123", "Ana Perez", null);
        sembrarRepositorio("Repo A", "Descripcion", administrador);
        sembrarRepositorio("Repo B", "Descripcion", administrador);
        sembrarRepositorio("Repo C", "Descripcion", administrador);
        entityManager.flush();
        var orden = List.of(SortOrder.of("nombreRepositorio", SortDirection.ASC));

        // Act
        var resultado = adapter.consultarTodos(criteria(null, 1, 2, orden));

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(3L);
        assertThat(resultado.getContent()).extracting("nombreRepositorio").containsExactly("Repo C");
        assertThat(resultado.getPage()).isEqualTo(1);
        assertThat(resultado.getSize()).isEqualTo(2);
    }

    @Test
    void debeOrdenarPorNombreRepositorioDescendente_cuandoSeSolicita() {
        // Arrange
        var administrador = sembrarAdministrador("123", "Ana Perez", null);
        sembrarRepositorio("Repo A", "Descripcion", administrador);
        sembrarRepositorio("Repo B", "Descripcion", administrador);
        entityManager.flush();
        var orden = List.of(SortOrder.of("nombreRepositorio", SortDirection.DESC));

        // Act
        var resultado = adapter.consultarTodos(criteria(null, 0, 10, orden));

        // Assert
        assertThat(resultado.getContent()).extracting("nombreRepositorio").containsExactly("Repo B", "Repo A");
    }

    @Test
    void debeRetornarPaginaVacia_cuandoNingunoCoincide() {
        // Arrange
        var administrador = sembrarAdministrador("123", "Ana Perez", null);
        sembrarRepositorio("Repo A", "Descripcion", administrador);
        entityManager.flush();
        var raiz = NodoFiltro.predicado("descripcion", FiltroOperador.CONTIENE, "inexistente");

        // Act
        var resultado = adapter.consultarTodos(criteria(raiz, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }
}
