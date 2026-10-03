package com.arquisoft.usuarios.infrastructure.asesorficha.query.secondaryadapter.repository;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.asesorficha.query.criteria.AsesorFichaCriteria;
import com.arquisoft.usuarios.application.asesorficha.query.criteria.AsesorFichaVigenteCriteria;
import com.arquisoft.usuarios.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.usuarios.application.asesorficha.query.readmodel.AsesorFichaVigenteReadModel;
import com.arquisoft.usuarios.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
class AsesorFichaQueryOutputAdapterTest {

    private static final String ACTIVO = "ACTIVO";
    private static final String INACTIVO = "INACTIVO";

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AsesorFichaQueryRepository asesorFichaQueryRepository;

    @Autowired
    private AsesorFichaVigenteQueryRepository asesorFichaVigenteQueryRepository;

    private AsesorFichaQueryOutputAdapter adapter;

    private UUID vigenteActivo;
    private UUID vigenteInactivo;
    private UUID dadoDeBaja;

    @BeforeEach
    void setUp() {
        adapter = new AsesorFichaQueryOutputAdapter(
                asesorFichaQueryRepository,
                asesorFichaVigenteQueryRepository,
                new AsesorFichaJpaSpecification(),
                new AsesorFichaVigenteJpaSpecification());

        vigenteActivo = persistirUsuario("1001", "Ana Ramirez", "ana.ramirez@uco.edu.co", ACTIVO);
        persistirAsesorFicha(vigenteActivo, null);
        vigenteInactivo = persistirUsuario("1002", "Bruno Diaz", "bruno.diaz@uco.edu.co", INACTIVO);
        persistirAsesorFicha(vigenteInactivo, null);
        dadoDeBaja = persistirUsuario("1003", "Carla Vidal", "carla.vidal@uco.edu.co", ACTIVO);
        persistirAsesorFicha(dadoDeBaja, Instant.now());
        persistirUsuario("1004", "Diego Soto", "diego.soto@uco.edu.co", ACTIVO);
        entityManager.flush();
    }

    @Test
    void debeIncluirVigentesYBajasConEstadoYVigente_cuandoConsultaTodos() {
        // Act
        var resultado = adapter.consultarTodos(
                AsesorFichaCriteria.builder().pagina(0).tamanio(10).build());

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(3L);
        assertThat(resultado.getContent())
                .extracting(AsesorFichaReadModel::id, AsesorFichaReadModel::estado, AsesorFichaReadModel::vigente)
                .containsExactlyInAnyOrder(
                        tuple(vigenteActivo, ACTIVO, true),
                        tuple(vigenteInactivo, INACTIVO, true),
                        tuple(dadoDeBaja, ACTIVO, false));
    }

    @Test
    void debeRetornarSoloLasBajas_cuandoConsultaTodosFiltrandoVigenteFalse() {
        // Arrange
        var criteria = AsesorFichaCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("vigente", FiltroOperador.ES, "false"))
                .build();

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(AsesorFichaReadModel::id)
                .containsExactly(dadoDeBaja);
    }

    @Test
    void debeRetornarSoloLosInactivos_cuandoConsultaTodosFiltrandoPorEstado() {
        // Arrange
        var criteria = AsesorFichaCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("estado", FiltroOperador.ES, INACTIVO))
                .build();

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(AsesorFichaReadModel::id)
                .containsExactly(vigenteInactivo);
    }

    @Test
    void debeOrdenarPorNombreDescendente_cuandoConsultaTodos() {
        // Arrange
        var criteria = AsesorFichaCriteria.builder().pagina(0).tamanio(10)
                .ordenamiento(List.of(SortOrder.of("nombre", SortDirection.DESC)))
                .build();

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(AsesorFichaReadModel::nombre)
                .containsExactly("Carla Vidal", "Bruno Diaz", "Ana Ramirez");
    }

    @Test
    void debeExcluirLasBajasYExponerEstado_cuandoConsultaVigentes() {
        // Act
        var resultado = adapter.consultarVigentes(
                AsesorFichaVigenteCriteria.builder().pagina(0).tamanio(10).build());

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(2L);
        assertThat(resultado.getContent())
                .extracting(AsesorFichaVigenteReadModel::id, AsesorFichaVigenteReadModel::estado)
                .containsExactlyInAnyOrder(
                        tuple(vigenteActivo, ACTIVO),
                        tuple(vigenteInactivo, INACTIVO));
    }

    @Test
    void debeIncluirAlVigenteInactivo_cuandoConsultaVigentesFiltrandoPorEstado() {
        // Arrange
        var criteria = AsesorFichaVigenteCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("estado", FiltroOperador.ES, INACTIVO))
                .build();

        // Act
        var resultado = adapter.consultarVigentes(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(AsesorFichaVigenteReadModel::id)
                .containsExactly(vigenteInactivo);
    }

    @Test
    void debeOrdenarPorIdentificadorDescendente_cuandoConsultaVigentes() {
        // Arrange
        var criteria = AsesorFichaVigenteCriteria.builder().pagina(0).tamanio(10)
                .ordenamiento(List.of(SortOrder.of("identificador", SortDirection.DESC)))
                .build();

        // Act
        var resultado = adapter.consultarVigentes(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(AsesorFichaVigenteReadModel::identificador)
                .containsExactly("1002", "1001");
    }

    @Test
    void debeReportarElTotalDeVigentes_cuandoLaPaginaEsMenorQueElTotal() {
        // Arrange
        var criteria = AsesorFichaVigenteCriteria.builder().pagina(0).tamanio(1).build();

        // Act
        var resultado = adapter.consultarVigentes(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getTotalElements()).isEqualTo(2L);
    }

    private UUID persistirUsuario(String identificador, String nombre, String email, String estado) {
        var usuarioId = UtilUUID.generarNuevoUUID();
        entityManager.persist(UsuarioJpaEntity.builder()
                .id(usuarioId)
                .identificador(identificador)
                .nombre(nombre)
                .email(email)
                .contacto("300000" + identificador)
                .estadoId(estado)
                .build());
        return usuarioId;
    }

    private void persistirAsesorFicha(UUID usuarioId, Instant eliminadoEn) {
        entityManager.persist(AsesorFichaJpaEntity.builder()
                .usuarioId(usuarioId)
                .eliminadoEn(eliminadoEn)
                .build());
    }
}
