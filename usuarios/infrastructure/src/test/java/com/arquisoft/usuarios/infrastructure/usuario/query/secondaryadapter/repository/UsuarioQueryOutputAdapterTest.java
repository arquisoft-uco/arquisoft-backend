package com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository;

import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroInvalidoException;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.usuario.query.criteria.UsuarioCriteria;
import com.arquisoft.usuarios.application.usuario.query.readmodel.UsuarioReadModel;
import com.arquisoft.usuarios.infrastructure.administrador.command.secondaryadapter.entity.AdministradorJpaEntity;
import com.arquisoft.usuarios.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.usuarios.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import com.arquisoft.usuarios.infrastructure.coordinador.command.secondaryadapter.entity.CoordinadorJpaEntity;
import com.arquisoft.usuarios.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

@DataJpaTest
class UsuarioQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UsuarioQueryRepository usuarioQueryRepository;

    private UsuarioQueryOutputAdapter adapter;

    private UUID estudiante;
    private UUID estudianteAsesor;
    private UUID coordinadorAsesorFicha;
    private UUID estudianteDadoDeBaja;
    private UUID eliminado;
    private UUID sinRolesInactivo;
    private UUID administrador;
    private UUID administradorDadoDeBaja;

    @BeforeEach
    void setUp() {
        adapter = new UsuarioQueryOutputAdapter(usuarioQueryRepository, new UsuarioJpaSpecification());
        var baja = Instant.parse("2026-09-01T10:00:00Z");

        estudiante = persistirUsuario("1001", "Ana Ramirez", "ACTIVO", null);
        entityManager.persist(EstudianteJpaEntity.builder().usuarioId(estudiante).build());

        estudianteAsesor = persistirUsuario("1002", "Bruno Diaz", "ACTIVO", null);
        entityManager.persist(EstudianteJpaEntity.builder().usuarioId(estudianteAsesor).build());
        entityManager.persist(AsesorJpaEntity.builder().usuarioId(estudianteAsesor).build());

        coordinadorAsesorFicha = persistirUsuario("1003", "Carla Vidal", "ACTIVO", null);
        entityManager.persist(CoordinadorJpaEntity.builder().usuarioId(coordinadorAsesorFicha).build());
        entityManager.persist(AsesorFichaJpaEntity.builder().usuarioId(coordinadorAsesorFicha).build());
        entityManager.persist(RepresentanteComiteJpaEntity.builder().usuarioId(coordinadorAsesorFicha).build());

        estudianteDadoDeBaja = persistirUsuario("1004", "Dario Solano", "ACTIVO", null);
        entityManager.persist(EstudianteJpaEntity.builder().usuarioId(estudianteDadoDeBaja).eliminadoEn(baja).build());
        entityManager.persist(RepresentanteComiteJpaEntity.builder().usuarioId(estudianteDadoDeBaja).eliminadoEn(baja).build());

        eliminado = persistirUsuario("1005", "Elena Roa", "INACTIVO", baja);
        entityManager.persist(AsesorJpaEntity.builder().usuarioId(eliminado).eliminadoEn(baja).build());

        sinRolesInactivo = persistirUsuario("1006", "Felipe Mora", "INACTIVO", null);

        administrador = persistirUsuario("1007", "Gina Paz", "ACTIVO", null);
        entityManager.persist(AdministradorJpaEntity.builder().usuarioId(administrador).build());

        administradorDadoDeBaja = persistirUsuario("1008", "Hugo Tello", "ACTIVO", null);
        entityManager.persist(AdministradorJpaEntity.builder().usuarioId(administradorDadoDeBaja).eliminadoEn(baja).build());

        entityManager.flush();
    }

    @Test
    void debeRetornarTodosConFlagsCorrectos_cuandoSinFiltros() {
        // Arrange
        var criteria = UsuarioCriteria.builder().pagina(0).tamanio(10).build();

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(8L);
        assertThat(resultado.getContent())
                .extracting(UsuarioReadModel::id, UsuarioReadModel::vigente, UsuarioReadModel::esEstudiante,
                        UsuarioReadModel::esAsesor, UsuarioReadModel::esAsesorFicha, UsuarioReadModel::esCoordinador,
                        UsuarioReadModel::esRepresentanteComite, UsuarioReadModel::esAdministrador)
                .containsExactlyInAnyOrder(
                        tuple(estudiante, true, true, false, false, false, false, false),
                        tuple(estudianteAsesor, true, true, true, false, false, false, false),
                        tuple(coordinadorAsesorFicha, true, false, false, true, true, true, false),
                        tuple(estudianteDadoDeBaja, true, false, false, false, false, false, false),
                        tuple(eliminado, false, false, false, false, false, false, false),
                        tuple(sinRolesInactivo, true, false, false, false, false, false, false),
                        tuple(administrador, true, false, false, false, false, false, true),
                        tuple(administradorDadoDeBaja, true, false, false, false, false, false, false));
        assertThat(resultado.getContent())
                .filteredOn(u -> u.id().equals(estudiante))
                .extracting(UsuarioReadModel::identificador, UsuarioReadModel::nombre, UsuarioReadModel::email,
                        UsuarioReadModel::contacto, UsuarioReadModel::estado)
                .containsExactly(tuple("1001", "Ana Ramirez", "1001@uco.edu.co", "3000000000", "ACTIVO"));
    }

    @Test
    void debeFiltrarEstudiantesVigentes_cuandoEsEstudianteEsTrue() {
        // Arrange
        var criteria = conFiltro(NodoFiltro.predicado("esEstudiante", FiltroOperador.ES, "true"));

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(UsuarioReadModel::id)
                .containsExactlyInAnyOrder(estudiante, estudianteAsesor)
                .doesNotContain(estudianteDadoDeBaja);
    }

    @Test
    void debeFiltrarRepresentantesVigentes_cuandoEsRepresentanteComiteEsTrue() {
        // Arrange
        var criteria = conFiltro(NodoFiltro.predicado("esRepresentanteComite", FiltroOperador.ES, "true"));

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(UsuarioReadModel::id)
                .containsExactly(coordinadorAsesorFicha)
                .doesNotContain(estudianteDadoDeBaja);
    }

    @Test
    void debeFiltrarAdministradoresVigentes_cuandoEsAdministradorEsTrue() {
        // Arrange — administradorDadoDeBaja tiene la fila de administrador dada de baja: no debe contar como vigente
        var criteria = conFiltro(NodoFiltro.predicado("esAdministrador", FiltroOperador.ES, "true"));

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(UsuarioReadModel::id)
                .containsExactly(administrador)
                .doesNotContain(administradorDadoDeBaja);
    }

    @Test
    void debeRetornarNuncaAdministradosYDadosDeBaja_cuandoEsAdministradorEsFalse() {
        // Arrange
        var criteria = conFiltro(NodoFiltro.predicado("esAdministrador", FiltroOperador.ES, "false"));

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(7L);
        assertThat(resultado.getContent())
                .extracting(UsuarioReadModel::id)
                .contains(estudiante, sinRolesInactivo, administradorDadoDeBaja)
                .doesNotContain(administrador);
    }

    @Test
    void debeRetornarUnaSolaFila_cuandoElUsuarioEsAsesorYAdministradorALaVez() {
        // Arrange
        var asesorAdministrador = persistirUsuario("1009", "Ivan Rios", "ACTIVO", null);
        entityManager.persist(AsesorJpaEntity.builder().usuarioId(asesorAdministrador).build());
        entityManager.persist(AdministradorJpaEntity.builder().usuarioId(asesorAdministrador).build());
        entityManager.flush();
        var criteria = conFiltro(NodoFiltro.grupo(FiltroConector.OR, List.of(
                NodoFiltro.predicado("esAsesor", FiltroOperador.ES, "true"),
                NodoFiltro.predicado("esAdministrador", FiltroOperador.ES, "true"))));

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(3L);
        assertThat(resultado.getContent())
                .extracting(UsuarioReadModel::id, UsuarioReadModel::esAsesor, UsuarioReadModel::esAdministrador)
                .containsExactlyInAnyOrder(
                        tuple(estudianteAsesor, true, false),
                        tuple(administrador, false, true),
                        tuple(asesorAdministrador, true, true));
    }

    @Test
    void debeRetornarUnaSolaFila_cuandoOrRepresentanteComiteCoordinadorSobreElMismoUsuario() {
        // Arrange
        var criteria = conFiltro(NodoFiltro.grupo(FiltroConector.OR, List.of(
                NodoFiltro.predicado("esRepresentanteComite", FiltroOperador.ES, "true"),
                NodoFiltro.predicado("esCoordinador", FiltroOperador.ES, "true"))));

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(1L);
        assertThat(resultado.getContent())
                .extracting(UsuarioReadModel::id)
                .containsExactly(coordinadorAsesorFicha);
    }

    @Test
    void debeRetornarUnionSinDuplicados_cuandoOrEstudianteAsesor() {
        // Arrange
        var criteria = conFiltro(NodoFiltro.grupo(FiltroConector.OR, List.of(
                NodoFiltro.predicado("esEstudiante", FiltroOperador.ES, "true"),
                NodoFiltro.predicado("esAsesor", FiltroOperador.ES, "true"))));

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(2L);
        assertThat(resultado.getContent())
                .extracting(UsuarioReadModel::id)
                .containsExactlyInAnyOrder(estudiante, estudianteAsesor);
    }

    @Test
    void debeRetornarInterseccion_cuandoAndEstudianteAsesor() {
        // Arrange
        var criteria = conFiltro(NodoFiltro.grupo(FiltroConector.AND, List.of(
                NodoFiltro.predicado("esEstudiante", FiltroOperador.ES, "true"),
                NodoFiltro.predicado("esAsesor", FiltroOperador.ES, "true"))));

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(UsuarioReadModel::id)
                .containsExactly(estudianteAsesor);
    }

    @Test
    void debeRetornarEliminadosSinRoles_cuandoVigenteEsFalse() {
        // Arrange
        var criteria = conFiltro(NodoFiltro.predicado("vigente", FiltroOperador.ES, "false"));

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(UsuarioReadModel::id, UsuarioReadModel::esEstudiante, UsuarioReadModel::esAsesor,
                        UsuarioReadModel::esAsesorFicha, UsuarioReadModel::esCoordinador)
                .containsExactly(tuple(eliminado, false, false, false, false));
    }

    @Test
    void debeRetornarVacio_cuandoRolTrueYVigenteFalse() {
        // Arrange
        var criteria = conFiltro(NodoFiltro.grupo(FiltroConector.AND, List.of(
                NodoFiltro.predicado("esAsesor", FiltroOperador.ES, "true"),
                NodoFiltro.predicado("vigente", FiltroOperador.ES, "false"))));

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeResolverCadaCampoFiltrable_cuandoSeCombinanEnUnAnd() {
        // Arrange
        var criteria = conFiltro(NodoFiltro.grupo(FiltroConector.AND, List.of(
                NodoFiltro.predicado("esAsesorFicha", FiltroOperador.ES, "true"),
                NodoFiltro.predicado("esCoordinador", FiltroOperador.ES, "true"),
                NodoFiltro.predicado("esRepresentanteComite", FiltroOperador.ES, "true"),
                NodoFiltro.predicado("identificador", FiltroOperador.ES, "1003"),
                NodoFiltro.predicado("nombre", FiltroOperador.CONTIENE, "Carla"),
                NodoFiltro.predicado("email", FiltroOperador.CONTIENE, "1003@"),
                NodoFiltro.predicado("contacto", FiltroOperador.ES, "3000000000"))));

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(UsuarioReadModel::id)
                .containsExactly(coordinadorAsesorFicha);
    }

    @Test
    void debeFiltrarPorEstado_cuandoEstadoEsInactivo() {
        // Arrange
        var criteria = conFiltro(NodoFiltro.predicado("estado", FiltroOperador.ES, "INACTIVO"));

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(UsuarioReadModel::id, UsuarioReadModel::vigente)
                .containsExactlyInAnyOrder(tuple(eliminado, false), tuple(sinRolesInactivo, true));
    }

    @Test
    void debeOrdenarPorNombreDescendente_cuandoSeSolicita() {
        // Arrange
        var criteria = UsuarioCriteria.builder().pagina(0).tamanio(3)
                .ordenamiento(List.of(SortOrder.of("nombre", SortDirection.DESC)))
                .build();

        // Act
        var resultado = adapter.consultarTodos(criteria);

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(8L);
        assertThat(resultado.getContent())
                .extracting(UsuarioReadModel::nombre)
                .containsExactly("Hugo Tello", "Gina Paz", "Felipe Mora");
    }

    @Test
    void debeLanzarFiltroInvalidoException_cuandoOperadorNoAplicaABooleano() {
        // Arrange
        var criteria = conFiltro(NodoFiltro.predicadoMultivalor(
                "esAsesor", FiltroOperador.IN, List.of("true", "false")));

        // Act
        var lanzamiento = assertThatThrownBy(() -> adapter.consultarTodos(criteria));

        // Assert
        lanzamiento.isInstanceOf(FiltroInvalidoException.class);
    }

    private UsuarioCriteria conFiltro(NodoFiltro raiz) {
        return UsuarioCriteria.builder().pagina(0).tamanio(10).raiz(raiz).build();
    }

    private UUID persistirUsuario(String identificador, String nombre, String estado, Instant eliminadoEn) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(UsuarioJpaEntity.builder()
                .id(id)
                .identificador(identificador)
                .nombre(nombre)
                .email(identificador + "@uco.edu.co")
                .contacto("3000000000")
                .estadoId(estado)
                .eliminadoEn(eliminadoEn)
                .build());
        return id;
    }
}
