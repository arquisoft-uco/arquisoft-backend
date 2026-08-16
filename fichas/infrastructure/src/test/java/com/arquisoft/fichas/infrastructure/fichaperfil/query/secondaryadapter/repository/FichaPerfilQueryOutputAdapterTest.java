package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.entity.FichaPerfilEntity;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.query.exception.FiltroInvalidoException;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class FichaPerfilQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FichaPerfilQueryRepository fichaPerfilRepository;

    private FichaPerfilQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new FichaPerfilQueryOutputAdapter(
                fichaPerfilRepository,
                new FichaPerfilJpaSpecification()
        );
    }

    @Test
    void debeRetornarReadModel_cuandoExistenEnBD() {
        AsesorFichaEntity asesor = AsesorFichaEntity.builder()
                .id(UUID.randomUUID())
                .identificador("DOC-001")
                .nombre("Juan Salazar")
                .email("juan.salazar@soyuco.edu.co")
                .build();
        entityManager.persist(asesor);

        FichaPerfilEntity ficha = FichaPerfilEntity.builder()
                .id(UUID.randomUUID())
                .tituloProyecto("Arquisoft Backend")
                .asesorFicha(asesor)
                .build();
        entityManager.persist(ficha);
        entityManager.flush();

        PaginatedResult<FichaPerfilReadModel> resultado = adapter.consultarTodas(
                FichaPerfilCriteria.builder().pagina(0).tamanio(10).build());

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getTotalElements()).isEqualTo(1L);

        FichaPerfilReadModel fichaLeida = resultado.getContent().get(0);
        assertThat(fichaLeida.tituloProyecto()).isEqualTo("Arquisoft Backend");
        assertThat(fichaLeida.asesorFicha().nombre()).isEqualTo("Juan Salazar");
    }

    @Test
    void debeRetornarVacio_cuandoNoHayFichasEnBD() {
        PaginatedResult<FichaPerfilReadModel> resultado = adapter.consultarTodas(
                FichaPerfilCriteria.builder().pagina(0).tamanio(10).build());

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
        assertThat(resultado.getPage()).isEqualTo(0);
        assertThat(resultado.getSize()).isEqualTo(10);
    }

    @Test
    void debeRetornarTrue_cuandoFichaExistePorId() {
        // Arrange
        AsesorFichaEntity asesor = AsesorFichaEntity.builder()
                .id(UUID.randomUUID())
                .identificador("DOC-002")
                .nombre("Ana Ramirez")
                .email("ana.ramirez@soyuco.edu.co")
                .build();
        entityManager.persist(asesor);

        UUID fichaId = UUID.randomUUID();
        FichaPerfilEntity ficha = FichaPerfilEntity.builder()
                .id(fichaId)
                .tituloProyecto("Proyecto Existente")
                .asesorFicha(asesor)
                .build();
        entityManager.persist(ficha);
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.existePorId(fichaId)).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoFichaNoExistePorId() {
        // Act & Assert
        assertThat(adapter.existePorId(UUID.randomUUID())).isFalse();
    }

    @Test
    void debeFiltrarPorNombreDelAsesor_cuandoElCriteriaTraeEsePredicado() {
        // Arrange
        persistirFicha("Proyecto de Ana", "DOC-010", "Ana Ramirez", "ana@soyuco.edu.co");
        persistirFicha("Proyecto de Juan", "DOC-011", "Juan Salazar", "juan@soyuco.edu.co");
        entityManager.flush();

        FichaPerfilCriteria criteria = FichaPerfilCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("asesorNombre", FiltroOperador.CONTIENE, "Ramirez"))
                .build();

        // Act
        PaginatedResult<FichaPerfilReadModel> resultado = adapter.consultarTodas(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).asesorFicha().nombre()).isEqualTo("Ana Ramirez");
    }

    @Test
    void debeFiltrarPorIdDelAsesor_cuandoElCriteriaTraeEsePredicado() {
        // Arrange
        UUID asesorBuscado = persistirFicha("Proyecto uno", "DOC-020", "Carla Diaz", "carla@soyuco.edu.co");
        persistirFicha("Proyecto dos", "DOC-021", "Luis Peña", "luis@soyuco.edu.co");
        entityManager.flush();

        FichaPerfilCriteria criteria = FichaPerfilCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("asesorId", FiltroOperador.ES, asesorBuscado.toString()))
                .build();

        // Act
        PaginatedResult<FichaPerfilReadModel> resultado = adapter.consultarTodas(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).asesorFicha().id()).isEqualTo(asesorBuscado);
    }

    @Test
    void debeOrdenarPorNombreDelAsesorDescendente_cuandoElCriteriaLoPide() {
        // Arrange
        persistirFicha("Proyecto A", "DOC-030", "Ana Ramirez", "ana2@soyuco.edu.co");
        persistirFicha("Proyecto Z", "DOC-031", "Zulma Torres", "zulma@soyuco.edu.co");
        entityManager.flush();

        FichaPerfilCriteria criteria = FichaPerfilCriteria.builder()
                .pagina(0).tamanio(10)
                .ordenamiento(List.of(SortOrder.of("asesorNombre", SortDirection.DESC)))
                .build();

        // Act
        PaginatedResult<FichaPerfilReadModel> resultado = adapter.consultarTodas(criteria);

        // Assert
        assertThat(resultado.getContent())
                .extracting(ficha -> ficha.asesorFicha().nombre())
                .containsExactly("Zulma Torres", "Ana Ramirez");
    }

    @Test
    void debeFiltrarPorTituloDelProyecto_cuandoElCriteriaTraeEsePredicado() {
        // Arrange
        persistirFicha("Arquisoft Backend", "DOC-040", "Ana Ramirez", "ana3@soyuco.edu.co");
        persistirFicha("Otro Proyecto", "DOC-041", "Juan Salazar", "juan3@soyuco.edu.co");
        entityManager.flush();

        FichaPerfilCriteria criteria = FichaPerfilCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("tituloProyecto", FiltroOperador.EMPIEZA_CON, "Arquisoft"))
                .build();

        // Act
        PaginatedResult<FichaPerfilReadModel> resultado = adapter.consultarTodas(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).tituloProyecto()).isEqualTo("Arquisoft Backend");
    }

    @Test
    void debeTratarLosComodinesComoTextoLiteral_cuandoElValorDelFiltroLosContiene() {
        // Arrange
        persistirFicha("Avance 50% del proyecto", "DOC-050", "Ana Ramirez", "ana5@soyuco.edu.co");
        persistirFicha("Otro Proyecto", "DOC-051", "Juan Salazar", "juan5@soyuco.edu.co");
        entityManager.flush();

        FichaPerfilCriteria comodinSolo = FichaPerfilCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("tituloProyecto", FiltroOperador.CONTIENE, "%"))
                .build();

        FichaPerfilCriteria comodinLiteral = FichaPerfilCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("tituloProyecto", FiltroOperador.CONTIENE, "50%"))
                .build();

        // Act & Assert
        assertThat(adapter.consultarTodas(comodinSolo).getContent())
                .as("'%%' debe buscarse literal: solo la ficha que lo contiene, no toda la tabla")
                .extracting(FichaPerfilReadModel::tituloProyecto)
                .containsExactly("Avance 50% del proyecto");

        assertThat(adapter.consultarTodas(comodinLiteral).getContent())
                .extracting(FichaPerfilReadModel::tituloProyecto)
                .containsExactly("Avance 50% del proyecto");
    }

    @Test
    void debeReportarUuidInvalido_cuandoElFiltroPorAsesorNoTraeUnUuid() {
        // Arrange
        FichaPerfilCriteria criteria = FichaPerfilCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("asesorId", FiltroOperador.ES, "no-es-un-uuid"))
                .build();

        // Act & Assert
        assertThatThrownBy(() -> adapter.consultarTodas(criteria))
                .isInstanceOf(FiltroInvalidoException.class)
                .hasMessageContaining("UUID inválido: 'no-es-un-uuid'");
    }

    private UUID persistirFicha(String titulo, String identificador, String nombre, String email) {
        AsesorFichaEntity asesor = AsesorFichaEntity.builder()
                .id(UUID.randomUUID())
                .identificador(identificador)
                .nombre(nombre)
                .email(email)
                .build();
        entityManager.persist(asesor);

        entityManager.persist(FichaPerfilEntity.builder()
                .id(UUID.randomUUID())
                .tituloProyecto(titulo)
                .asesorFicha(asesor)
                .build());

        return asesor.getId();
    }
}
