package com.arquisoft.usuarios.infrastructure.representantecomite.query.secondaryadapter.repository;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.representantecomite.query.criteria.RepresentanteComiteVigenteCriteria;
import com.arquisoft.usuarios.application.representantecomite.query.readmodel.RepresentanteComiteVigenteReadModel;
import com.arquisoft.usuarios.infrastructure.representantecomite.command.secondaryadapter.entity.RepresentanteComiteJpaEntity;
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
class RepresentanteComiteQueryOutputAdapterTest {

    private static final String ACTIVO = "ACTIVO";
    private static final String INACTIVO = "INACTIVO";

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RepresentanteComiteVigenteQueryRepository representanteComiteVigenteQueryRepository;

    private RepresentanteComiteQueryOutputAdapter adapter;

    private UUID vigenteActivo;
    private UUID vigenteInactivo;
    private UUID dadoDeBaja;

    @BeforeEach
    void setUp() {
        adapter = new RepresentanteComiteQueryOutputAdapter(
                representanteComiteVigenteQueryRepository,
                new RepresentanteComiteVigenteJpaSpecification());

        vigenteActivo = persistirUsuario("1001", "Ana Ramirez", "ana.ramirez@uco.edu.co", ACTIVO);
        persistirRepresentanteComite(vigenteActivo, null);
        vigenteInactivo = persistirUsuario("1002", "Bruno Diaz", "bruno.diaz@uco.edu.co", INACTIVO);
        persistirRepresentanteComite(vigenteInactivo, null);
        dadoDeBaja = persistirUsuario("1003", "Carla Vidal", "carla.vidal@uco.edu.co", ACTIVO);
        persistirRepresentanteComite(dadoDeBaja, Instant.now());
        persistirUsuario("1004", "Diego Soto", "diego.soto@uco.edu.co", ACTIVO);
        entityManager.flush();
    }

    @Test
    void debeExcluirLasBajasYExponerEstado_cuandoConsultaVigentes() {
        // Act
        var resultado = adapter.consultarVigentes(
                RepresentanteComiteVigenteCriteria.builder().pagina(0).tamanio(10).build());

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(2L);
        assertThat(resultado.getContent())
                .extracting(RepresentanteComiteVigenteReadModel::id, RepresentanteComiteVigenteReadModel::estado)
                .containsExactlyInAnyOrder(
                        tuple(vigenteActivo, ACTIVO),
                        tuple(vigenteInactivo, INACTIVO));
    }

    @Test
    void debeRetornarSoloLaCoincidencia_cuandoConsultaVigentesFiltrandoPorNombre() {
        // Arrange
        var criteria = RepresentanteComiteVigenteCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("nombre", FiltroOperador.CONTIENE, "Bruno"))
                .build();

        // Act
        var resultado = adapter.consultarVigentes(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(RepresentanteComiteVigenteReadModel::id)
                .containsExactly(vigenteInactivo);
    }

    @Test
    void debeOrdenarPorIdentificadorDescendente_cuandoConsultaVigentes() {
        // Arrange
        var criteria = RepresentanteComiteVigenteCriteria.builder().pagina(0).tamanio(10)
                .ordenamiento(List.of(SortOrder.of("identificador", SortDirection.DESC)))
                .build();

        // Act
        var resultado = adapter.consultarVigentes(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(RepresentanteComiteVigenteReadModel::identificador)
                .containsExactly("1002", "1001");
    }

    @Test
    void debeReportarElTotalDeVigentes_cuandoLaPaginaEsMenorQueElTotal() {
        // Arrange
        var criteria = RepresentanteComiteVigenteCriteria.builder().pagina(0).tamanio(1).build();

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

    private void persistirRepresentanteComite(UUID usuarioId, Instant eliminadoEn) {
        entityManager.persist(RepresentanteComiteJpaEntity.builder()
                .usuarioId(usuarioId)
                .eliminadoEn(eliminadoEn)
                .build());
    }
}
