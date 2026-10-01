package com.arquisoft.usuarios.infrastructure.administrador.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.administrador.query.criteria.AdministradorCriteria;
import com.arquisoft.usuarios.application.administrador.query.readmodel.AdministradorReadModel;
import com.arquisoft.usuarios.infrastructure.administrador.command.secondaryadapter.entity.AdministradorJpaEntity;
import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilFecha;
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
class AdministradorQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AdministradorQueryRepository administradorQueryRepository;

    private AdministradorQueryOutputAdapter adapter;

    private UUID vigenteUno;
    private UUID vigenteDos;
    private UUID dadoDeBaja;

    @BeforeEach
    void setUp() {
        adapter = new AdministradorQueryOutputAdapter(administradorQueryRepository, new AdministradorJpaSpecification());

        vigenteUno = persistirAdministrador("1001", "Ana Ramirez", "ana.ramirez@uco.edu.co", "ACTIVO", null);
        vigenteDos = persistirAdministrador("1002", "Bruno Diaz", "bruno.diaz@uco.edu.co", "ACTIVO", null);
        dadoDeBaja = persistirAdministrador("1003", "Carla Vidal", "carla.vidal@uco.edu.co", "ACTIVO", UtilFecha.generarInstanteActual());
        entityManager.flush();

        var usuarioSinAdministrador = UtilUUID.generarNuevoUUID();
        entityManager.persist(UsuarioJpaEntity.builder()
                .id(usuarioSinAdministrador)
                .identificador("1004")
                .nombre("Dario Leon")
                .email("dario.leon@uco.edu.co")
                .contacto("3000000004")
                .estadoId("ACTIVO")
                .build());
        entityManager.flush();
    }

    @Test
    void debeIncluirVigentesYDadosDeBaja_conVigenteCorrecto_cuandoConsultaTodos() {
        // Act
        var resultado = adapter.consultarTodos(
                AdministradorCriteria.builder().pagina(0).tamanio(10).build());

        // Assert
        assertThat(resultado.getContent()).hasSize(3);
        assertThat(resultado.getContent())
                .filteredOn(a -> a.id().equals(dadoDeBaja))
                .extracting(AdministradorReadModel::vigente, AdministradorReadModel::estado)
                .containsExactly(org.assertj.core.groups.Tuple.tuple(false, "ACTIVO"));
        assertThat(resultado.getContent())
                .filteredOn(a -> a.id().equals(vigenteUno))
                .extracting(AdministradorReadModel::vigente)
                .containsExactly(true);
    }

    @Test
    void debeFiltrarSoloLosDadosDeBaja_cuandoConsultaTodosConVigenteFalse() {
        // Arrange
        var criteria = AdministradorCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("vigente", FiltroOperador.ES, "false"))
                .build();

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).id()).isEqualTo(dadoDeBaja);
    }

    @Test
    void debeFiltrarPorNombre_cuandoConsultaTodos() {
        // Arrange
        var criteria = AdministradorCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("nombre", FiltroOperador.CONTIENE, "Bruno"))
                .build();

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).id()).isEqualTo(vigenteDos);
    }

    @Test
    void debeOrdenarPorIdentificador_cuandoConsultaTodos() {
        // Arrange
        var criteria = AdministradorCriteria.builder().pagina(0).tamanio(10)
                .ordenamiento(List.of(SortOrder.of("identificador", SortDirection.DESC)))
                .build();

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(AdministradorReadModel::identificador)
                .containsExactly("1003", "1002", "1001");
    }

    @Test
    void debeReportarTotalElementsCorrecto_cuandoPaginaTodosConTamanioUno() {
        // Arrange
        var criteria = AdministradorCriteria.builder().pagina(0).tamanio(1).build();

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getTotalElements()).isEqualTo(3L);
    }

    private UUID persistirAdministrador(String identificador, String nombre, String email,
                                         String estado, Instant eliminadoEn) {
        var usuarioId = UtilUUID.generarNuevoUUID();
        entityManager.persist(UsuarioJpaEntity.builder()
                .id(usuarioId)
                .identificador(identificador)
                .nombre(nombre)
                .email(email)
                .contacto("3000000000")
                .estadoId(estado)
                .build());
        entityManager.persist(AdministradorJpaEntity.builder()
                .usuarioId(usuarioId)
                .eliminadoEn(eliminadoEn)
                .build());
        return usuarioId;
    }
}
