package com.arquisoft.usuarios.infrastructure.jurado.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.jurado.query.criteria.JuradoVigenteCriteria;
import com.arquisoft.usuarios.application.jurado.query.readmodel.JuradoVigenteReadModel;
import com.arquisoft.usuarios.infrastructure.jurado.command.secondaryadapter.entity.JuradoJpaEntity;
import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.util.UtilUUID;
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
import static org.assertj.core.groups.Tuple.tuple;

@DataJpaTest
class JuradoQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JuradoVigenteQueryRepository juradoVigenteQueryRepository;

    private JuradoQueryOutputAdapter adapter;

    private UUID vigenteUno;
    private UUID vigenteDos;
    private UUID dadoDeBaja;

    @BeforeEach
    void setUp() {
        adapter = new JuradoQueryOutputAdapter(
                juradoVigenteQueryRepository,
                new JuradoVigenteJpaSpecification());

        vigenteUno = persistirJurado("1001", "Ana Ramirez", "ana.ramirez@uco.edu.co", "ACTIVO", null);
        vigenteDos = persistirJurado("1002", "Bruno Diaz", "bruno.diaz@uco.edu.co", "ACTIVO", null);
        dadoDeBaja = persistirJurado("1003", "Carla Vidal", "carla.vidal@uco.edu.co", "INACTIVO", Instant.now());
        entityManager.flush();
    }

    @Test
    void debeExcluirLosDadosDeBaja_cuandoConsultaVigentesSinFiltro() {
        // Act
        var resultado = adapter.consultarVigentes(
                JuradoVigenteCriteria.builder().pagina(0).tamanio(10).build());

        // Assert
        assertThat(resultado.getContent()).hasSize(2);
        assertThat(resultado.getContent())
                .extracting(JuradoVigenteReadModel::id)
                .containsExactlyInAnyOrder(vigenteUno, vigenteDos);
    }

    @Test
    void debeFiltrarPorEmail_cuandoConsultaVigentes() {
        // Arrange
        var criteria = JuradoVigenteCriteria.builder().pagina(0).tamanio(10)
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
        var criteria = JuradoVigenteCriteria.builder().pagina(0).tamanio(10)
                .ordenamiento(List.of(SortOrder.of("identificador", SortDirection.DESC)))
                .build();

        // Act
        var resultado = adapter.consultarVigentes(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(JuradoVigenteReadModel::identificador)
                .containsExactly("1002", "1001");
    }

    @Test
    void debeReportarTotalElementsCorrecto_cuandoPaginaVigentesConTamanioUno() {
        // Arrange
        var criteria = JuradoVigenteCriteria.builder().pagina(0).tamanio(1).build();

        // Act
        var resultado = adapter.consultarVigentes(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getTotalElements()).isEqualTo(2L);
    }

    @Test
    void debeFiltrarPorEstadoSinIncluirBajas_cuandoConsultaVigentesConEstadoInactivo() {
        // Arrange — dadoDeBaja también es INACTIVO: el filtro por estado se suma a la vigencia
        var inactivoPeroVigente = persistirJurado("1004", "Dario Leon", "dario.leon@uco.edu.co", "INACTIVO", null);
        entityManager.flush();
        var criteria = JuradoVigenteCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("estado", FiltroOperador.ES, "INACTIVO"))
                .build();

        // Act
        var resultado = adapter.consultarVigentes(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(JuradoVigenteReadModel::id, JuradoVigenteReadModel::estado)
                .containsExactly(tuple(inactivoPeroVigente, "INACTIVO"));
    }

    private UUID persistirJurado(String identificador, String nombre, String email,
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
        entityManager.persist(JuradoJpaEntity.builder()
                .usuarioId(usuarioId)
                .eliminadoEn(eliminadoEn)
                .build());
        return usuarioId;
    }
}
