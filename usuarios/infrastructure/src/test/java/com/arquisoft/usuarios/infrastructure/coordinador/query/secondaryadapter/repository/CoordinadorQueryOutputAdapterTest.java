package com.arquisoft.usuarios.infrastructure.coordinador.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.coordinador.query.criteria.CoordinadorCriteria;
import com.arquisoft.usuarios.application.coordinador.query.criteria.CoordinadorVigenteCriteria;
import com.arquisoft.usuarios.application.coordinador.query.readmodel.CoordinadorReadModel;
import com.arquisoft.usuarios.application.coordinador.query.readmodel.CoordinadorVigenteReadModel;
import com.arquisoft.usuarios.infrastructure.coordinador.command.secondaryadapter.entity.CoordinadorJpaEntity;
import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
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
class CoordinadorQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CoordinadorQueryRepository coordinadorQueryRepository;

    @Autowired
    private CoordinadorVigenteQueryRepository coordinadorVigenteQueryRepository;

    private CoordinadorQueryOutputAdapter adapter;

    private UUID vigenteUno;
    private UUID vigenteDos;
    private UUID dadoDeBaja;

    @BeforeEach
    void setUp() {
        adapter = new CoordinadorQueryOutputAdapter(
                coordinadorQueryRepository,
                coordinadorVigenteQueryRepository,
                new CoordinadorJpaSpecification(),
                new CoordinadorVigenteJpaSpecification());

        vigenteUno = persistirCoordinador("1001", "Ana Ramirez", "ana.ramirez@uco.edu.co", "ACTIVO", null);
        vigenteDos = persistirCoordinador("1002", "Bruno Diaz", "bruno.diaz@uco.edu.co", "ACTIVO", null);
        dadoDeBaja = persistirCoordinador("1003", "Carla Vidal", "carla.vidal@uco.edu.co", "INACTIVO", Instant.now());
        entityManager.flush();
    }

    @Test
    void debeIncluirVigentesYDadosDeBaja_conVigenteCorrecto_cuandoConsultaTodos() {
        // Act
        var resultado = adapter.consultarTodos(
                CoordinadorCriteria.builder().pagina(0).tamanio(10).build());

        // Assert
        assertThat(resultado.getContent()).hasSize(3);
        assertThat(resultado.getContent())
                .filteredOn(c -> c.id().equals(dadoDeBaja))
                .extracting(CoordinadorReadModel::vigente)
                .containsExactly(false);
        assertThat(resultado.getContent())
                .filteredOn(c -> c.id().equals(vigenteUno))
                .extracting(CoordinadorReadModel::vigente)
                .containsExactly(true);
    }

    @Test
    void debeFiltrarSoloLosDadosDeBaja_cuandoConsultaTodosConVigenteFalse() {
        // Arrange
        var criteria = CoordinadorCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("vigente", FiltroOperador.ES, "false"))
                .build();

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).id()).isEqualTo(dadoDeBaja);
    }

    @Test
    void debeFiltrarPorEstadoInactivo_cuandoConsultaTodos() {
        // Arrange
        var criteria = CoordinadorCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("estado", FiltroOperador.ES, "INACTIVO"))
                .build();

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).id()).isEqualTo(dadoDeBaja);
    }

    @Test
    void debeOrdenarPorNombreDescendente_cuandoConsultaTodos() {
        // Arrange
        var criteria = CoordinadorCriteria.builder().pagina(0).tamanio(10)
                .ordenamiento(List.of(SortOrder.of("nombre", SortDirection.DESC)))
                .build();

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(CoordinadorReadModel::nombre)
                .containsExactly("Carla Vidal", "Bruno Diaz", "Ana Ramirez");
    }

    @Test
    void debeExcluirLosDadosDeBaja_cuandoConsultaVigentesSinFiltro() {
        // Act
        var resultado = adapter.consultarVigentes(
                CoordinadorVigenteCriteria.builder().pagina(0).tamanio(10).build());

        // Assert
        assertThat(resultado.getContent()).hasSize(2);
        assertThat(resultado.getContent())
                .extracting(CoordinadorVigenteReadModel::id)
                .containsExactlyInAnyOrder(vigenteUno, vigenteDos);
    }

    @Test
    void debeFiltrarPorEmail_cuandoConsultaVigentes() {
        // Arrange
        var criteria = CoordinadorVigenteCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("email", FiltroOperador.CONTIENE, "bruno"))
                .build();

        // Act
        var resultado = adapter.consultarVigentes(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).id()).isEqualTo(vigenteDos);
    }

    @Test
    void debeOrdenarPorIdentificador_cuandoConsultaVigentes() {
        // Arrange
        var criteria = CoordinadorVigenteCriteria.builder().pagina(0).tamanio(10)
                .ordenamiento(List.of(SortOrder.of("identificador", SortDirection.DESC)))
                .build();

        // Act
        var resultado = adapter.consultarVigentes(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(CoordinadorVigenteReadModel::identificador)
                .containsExactly("1002", "1001");
    }

    @Test
    void debeReportarTotalElementsCorrecto_cuandoPaginaVigentesConTamanioUno() {
        // Arrange
        var criteria = CoordinadorVigenteCriteria.builder().pagina(0).tamanio(1).build();

        // Act
        var resultado = adapter.consultarVigentes(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getTotalElements()).isEqualTo(2L);
    }

    private UUID persistirCoordinador(String identificador, String nombre, String email,
                                       String estado, Instant eliminadoEn) {
        var usuarioId = UUID.randomUUID();
        entityManager.persist(UsuarioJpaEntity.builder()
                .id(usuarioId)
                .identificador(identificador)
                .nombre(nombre)
                .email(email)
                .contacto("3000000000")
                .estadoId(estado)
                .build());
        entityManager.persist(CoordinadorJpaEntity.builder()
                .usuarioId(usuarioId)
                .eliminadoEn(eliminadoEn)
                .build());
        return usuarioId;
    }
}
