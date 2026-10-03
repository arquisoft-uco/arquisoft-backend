package com.arquisoft.usuarios.infrastructure.bibliotecario.query.secondaryadapter.repository;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroInvalidoException;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.bibliotecario.query.criteria.BibliotecarioCriteria;
import com.arquisoft.usuarios.application.bibliotecario.query.readmodel.BibliotecarioReadModel;
import com.arquisoft.usuarios.infrastructure.administrador.command.secondaryadapter.entity.AdministradorJpaEntity;
import com.arquisoft.usuarios.infrastructure.bibliotecario.command.secondaryadapter.entity.BibliotecarioJpaEntity;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

@DataJpaTest
class BibliotecarioQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BibliotecarioQueryRepository bibliotecarioQueryRepository;

    private BibliotecarioQueryOutputAdapter adapter;

    private UUID vigenteUno;
    private UUID vigenteInactivo;
    private UUID dadoDeBaja;
    private UUID administradorSinBibliotecario;
    private UUID usuarioSinRoles;

    @BeforeEach
    void setUp() {
        adapter = new BibliotecarioQueryOutputAdapter(bibliotecarioQueryRepository, new BibliotecarioJpaSpecification());
        var baja = Instant.parse("2026-09-01T10:00:00Z");

        vigenteUno = persistirUsuario("1001", "Ana Ramirez", "ACTIVO");
        persistirBibliotecario(vigenteUno, null);

        vigenteInactivo = persistirUsuario("1002", "Bruno Diaz", "INACTIVO");
        persistirBibliotecario(vigenteInactivo, null);

        dadoDeBaja = persistirUsuario("1003", "Carla Vidal", "ACTIVO");
        persistirBibliotecario(dadoDeBaja, baja);

        administradorSinBibliotecario = persistirUsuario("1004", "Dario Leon", "ACTIVO");
        entityManager.persist(AdministradorJpaEntity.builder().usuarioId(administradorSinBibliotecario).build());

        usuarioSinRoles = persistirUsuario("1005", "Elena Roa", "ACTIVO");

        entityManager.flush();
    }

    @Test
    void debeRetornarVigentesYDadosDeBaja_cuandoSinFiltros() {
        // Arrange
        var criteria = BibliotecarioCriteria.builder().pagina(0).tamanio(10).build();

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(3L);
        assertThat(resultado.getContent())
                .extracting(BibliotecarioReadModel::id, BibliotecarioReadModel::estado, BibliotecarioReadModel::vigente)
                .containsExactlyInAnyOrder(
                        tuple(vigenteUno, "ACTIVO", true),
                        tuple(vigenteInactivo, "INACTIVO", true),
                        tuple(dadoDeBaja, "ACTIVO", false));
        assertThat(resultado.getContent())
                .filteredOn(b -> b.id().equals(vigenteUno))
                .extracting(BibliotecarioReadModel::identificador, BibliotecarioReadModel::nombre,
                        BibliotecarioReadModel::email, BibliotecarioReadModel::contacto)
                .containsExactly(tuple("1001", "Ana Ramirez", "1001@uco.edu.co", "3000000000"));
    }

    @Test
    void debeExcluirUsuariosSinFilaBibliotecario_cuandoSinFiltros() {
        // Arrange
        var criteria = BibliotecarioCriteria.builder().pagina(0).tamanio(10).build();

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(BibliotecarioReadModel::id)
                .doesNotContain(administradorSinBibliotecario, usuarioSinRoles);
    }

    @Test
    void debeFiltrarVigentes_cuandoVigenteEsTrue() {
        // Arrange
        var criteria = conFiltro(NodoFiltro.predicado("vigente", FiltroOperador.ES, "true"));

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(BibliotecarioReadModel::id)
                .containsExactlyInAnyOrder(vigenteUno, vigenteInactivo)
                .doesNotContain(dadoDeBaja);
    }

    @Test
    void debeFiltrarPorEstado_cuandoEstadoEsInactivo() {
        // Arrange
        var criteria = conFiltro(NodoFiltro.predicado("estado", FiltroOperador.ES, "INACTIVO"));

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(BibliotecarioReadModel::id)
                .containsExactly(vigenteInactivo);
    }

    @Test
    void debeOrdenarPorNombreDescendente_cuandoSeSolicitaConFiltroDeTexto() {
        // Arrange
        var criteria = BibliotecarioCriteria.builder().pagina(0).tamanio(10)
                .ordenamiento(List.of(SortOrder.of("nombre", SortDirection.DESC)))
                .raiz(NodoFiltro.predicado("email", FiltroOperador.CONTIENE, "@uco.edu.co"))
                .build();

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(BibliotecarioReadModel::nombre)
                .containsExactly("Carla Vidal", "Bruno Diaz", "Ana Ramirez");
    }

    @Test
    void debeLanzarFiltroInvalidoException_cuandoOperadorNoAplicaABooleano() {
        // Arrange
        var criteria = conFiltro(NodoFiltro.predicadoMultivalor(
                "vigente", FiltroOperador.IN, List.of("true", "false")));

        // Act
        var lanzamiento = assertThatThrownBy(() -> adapter.consultarTodos(criteria));

        // Assert
        lanzamiento.isInstanceOf(FiltroInvalidoException.class);
    }

    private BibliotecarioCriteria conFiltro(NodoFiltro raiz) {
        return BibliotecarioCriteria.builder().pagina(0).tamanio(10).raiz(raiz).build();
    }

    private UUID persistirUsuario(String identificador, String nombre, String estado) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(UsuarioJpaEntity.builder()
                .id(id)
                .identificador(identificador)
                .nombre(nombre)
                .email(identificador + "@uco.edu.co")
                .contacto("3000000000")
                .estadoId(estado)
                .build());
        return id;
    }

    private void persistirBibliotecario(UUID usuarioId, Instant eliminadoEn) {
        entityManager.persist(BibliotecarioJpaEntity.builder()
                .usuarioId(usuarioId)
                .eliminadoEn(eliminadoEn)
                .build());
    }
}
