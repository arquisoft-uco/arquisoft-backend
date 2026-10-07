package com.arquisoft.usuarios.infrastructure.estudiante.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.estudiante.query.criteria.EstudianteVigenteCriteria;
import com.arquisoft.usuarios.application.estudiante.query.readmodel.EstudianteVigenteReadModel;
import com.arquisoft.usuarios.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@DataJpaTest
class EstudianteQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EstudianteVigenteQueryRepository estudianteVigenteQueryRepository;

    private EstudianteQueryOutputAdapter adapter;

    private UUID vigenteUno;
    private UUID vigenteDos;
    private UUID dadoDeBaja;

    @BeforeEach
    void setUp() {
        adapter = new EstudianteQueryOutputAdapter(
                estudianteVigenteQueryRepository,
                new EstudianteVigenteJpaSpecification());

        vigenteUno = persistirEstudiante("1001", "Ana Ramirez", "ana.ramirez@uco.edu.co", "ACTIVO", null);
        vigenteDos = persistirEstudiante("1002", "Bruno Diaz", "bruno.diaz@uco.edu.co", "ACTIVO", null);
        dadoDeBaja = persistirEstudiante("1003", "Carla Vidal", "carla.vidal@uco.edu.co", "INACTIVO", Instant.now());
        persistirUsuarioSinRolEstudiante("1004", "Dario Solano", "dario.solano@uco.edu.co");
        entityManager.flush();
    }

    @Test
    void debeExcluirLosDadosDeBaja_cuandoConsultaVigentesSinFiltro() {
        // Act
        var resultado = adapter.consultarVigentes(
                EstudianteVigenteCriteria.builder().pagina(0).tamanio(10).build());

        // Assert
        assertThat(resultado.getContent()).hasSize(2);
        assertThat(resultado.getContent())
                .extracting(EstudianteVigenteReadModel::id)
                .containsExactlyInAnyOrder(vigenteUno, vigenteDos);
    }

    @Test
    void debeFiltrarPorNombre_cuandoConsultaVigentes() {
        // Arrange
        var criteria = EstudianteVigenteCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("nombre", FiltroOperador.CONTIENE, "Bruno"))
                .build();

        // Act
        var resultado = adapter.consultarVigentes(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).id()).isEqualTo(vigenteDos);
    }

    @Test
    void debeFiltrarPorEstadoSinIncluirBajas_cuandoConsultaVigentesConEstadoInactivo() {
        // Arrange — dadoDeBaja también es INACTIVO: el filtro por estado se suma a la vigencia
        var inactivoPeroVigente = persistirEstudiante("1005", "Elena Roa", "elena.roa@uco.edu.co", "INACTIVO", null);
        entityManager.flush();
        var criteria = EstudianteVigenteCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("estado", FiltroOperador.ES, "INACTIVO"))
                .build();

        // Act
        var resultado = adapter.consultarVigentes(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(EstudianteVigenteReadModel::id, EstudianteVigenteReadModel::estado)
                .containsExactly(tuple(inactivoPeroVigente, "INACTIVO"));
    }

    private UUID persistirEstudiante(String identificador, String nombre, String email,
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
        entityManager.persist(EstudianteJpaEntity.builder()
                .usuarioId(usuarioId)
                .eliminadoEn(eliminadoEn)
                .build());
        return usuarioId;
    }

    private void persistirUsuarioSinRolEstudiante(String identificador, String nombre, String email) {
        entityManager.persist(UsuarioJpaEntity.builder()
                .id(UUID.randomUUID())
                .identificador(identificador)
                .nombre(nombre)
                .email(email)
                .contacto("3000000000")
                .estadoId("ACTIVO")
                .build());
    }
}
