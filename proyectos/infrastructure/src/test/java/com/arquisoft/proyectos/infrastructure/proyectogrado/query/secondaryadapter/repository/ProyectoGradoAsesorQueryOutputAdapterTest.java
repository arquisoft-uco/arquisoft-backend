package com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoAsesorCriteria;
import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
import com.arquisoft.proyectos.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.secondaryadapter.entity.AsesorProyectoGradoJpaEntity;
import com.arquisoft.proyectos.infrastructure.estadoproyectogrado.command.secondaryadapter.entity.EstadoProyectoGradoJpaEntity;
import com.arquisoft.proyectos.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilObjeto;
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
class ProyectoGradoAsesorQueryOutputAdapterTest {

    private static final String EN_PROCESO = "EN_PROCESO";
    private static final String FINALIZADO = "FINALIZADO";

    @Autowired
    private ProyectoGradoQueryRepository repository;

    @Autowired
    private ProyectoGradoEstudianteQueryRepository estudianteRepository;

    @Autowired
    private ProyectoGradoAsesorQueryRepository asesorRepository;

    @Autowired
    private TestEntityManager entityManager;

    private ProyectoGradoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProyectoGradoQueryOutputAdapter(
                repository, new ProyectoGradoJpaSpecification(), estudianteRepository,
                asesorRepository, new ProyectoGradoAsesorJpaSpecification());
        entityManager.persist(new EstadoProyectoFixtureJpaEntity(EN_PROCESO, "En proceso", "En desarrollo"));
        entityManager.persist(new EstadoProyectoFixtureJpaEntity(FINALIZADO, "Finalizado", "Concluido"));
    }

    private UUID asesor(Instant eliminadoEn) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new AsesorJpaEntity(id, id.toString().substring(0, 20), "Nombre",
                "a@uco.edu.co", Instant.parse("2026-10-01T10:00:00Z"), eliminadoEn));
        return id;
    }

    private UUID proyecto(String titulo, String estado) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ProyectoGradoJpaEntity(
                id, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), titulo));
        entityManager.persist(new EstadoProyectoGradoJpaEntity(
                UtilUUID.generarNuevoUUID(), id, estado, Instant.parse("2026-10-01T10:00:00Z")));
        return id;
    }

    private void cambiarEstadoProyecto(UUID proyectoGrado, String estado, String fecha) {
        entityManager.persist(new EstadoProyectoGradoJpaEntity(
                UtilUUID.generarNuevoUUID(), proyectoGrado, estado, Instant.parse(fecha)));
    }

    private void asignar(UUID asesor, UUID proyectoGrado, EstadoAsesor estado, String fecha) {
        entityManager.persist(new AsesorProyectoGradoJpaEntity(
                UtilUUID.generarNuevoUUID(), asesor, proyectoGrado, estado.getId(), Instant.parse(fecha)));
    }

    private ProyectoGradoAsesorCriteria criteria(UUID asesor, NodoFiltro filtroCliente, int pagina, int tamanio,
                                                 List<SortOrder> orden) {
        var forzadoAsesor = NodoFiltro.predicado(ProyectoGradoAsesorCriteria.Campo.ASESOR.getClave(),
                FiltroOperador.ES, asesor.toString());
        var forzadoEstado = NodoFiltro.predicado(ProyectoGradoAsesorCriteria.Campo.ESTADO_ASESOR.getClave(),
                FiltroOperador.ES, EstadoAsesor.ASIGNADO.getId());
        var nodos = UtilObjeto.esNulo(filtroCliente)
                ? List.of(forzadoAsesor, forzadoEstado)
                : List.of(forzadoAsesor, forzadoEstado, filtroCliente);
        return ProyectoGradoAsesorCriteria.builder().pagina(pagina).tamanio(tamanio).ordenamiento(orden)
                .raiz(NodoFiltro.grupo(FiltroConector.AND, nodos)).build();
    }

    @Test
    void debeRetornarSoloProyectosVigentesDelAsesorConIdDelProyecto_cuandoHayAjenosYFinalizados() {
        // Arrange
        var asesor = asesor(null);
        var otro = asesor(null);
        var vigente = proyecto("Vigente", EN_PROCESO);
        var finalizado = proyecto("Finalizado", EN_PROCESO);
        var ajeno = proyecto("Ajeno", EN_PROCESO);
        asignar(asesor, vigente, EstadoAsesor.ASIGNADO, "2026-10-01T10:00:00Z");
        asignar(asesor, finalizado, EstadoAsesor.ASIGNADO, "2026-10-01T10:00:00Z");
        asignar(asesor, finalizado, EstadoAsesor.FINALIZADO, "2026-10-03T10:00:00Z");
        asignar(otro, ajeno, EstadoAsesor.ASIGNADO, "2026-10-01T10:00:00Z");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorAsesor(criteria(asesor, null, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        var leido = resultado.getContent().getFirst();
        assertThat(leido.id()).isEqualTo(vigente);
        assertThat(leido.fichaPerfil()).isNotNull();
        assertThat(leido.tituloProyecto()).isEqualTo("Vigente");
        assertThat(leido.estado().id()).isEqualTo(EN_PROCESO);
        assertThat(leido.estado().nombre()).isEqualTo("En proceso");
    }

    @Test
    void debeRetornarVacio_cuandoAsesorDadoDeBaja() {
        // Arrange
        var asesor = asesor(Instant.parse("2026-10-02T10:00:00Z"));
        asignar(asesor, proyecto("Uno", EN_PROCESO), EstadoAsesor.ASIGNADO, "2026-10-01T10:00:00Z");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorAsesor(criteria(asesor, null, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeExponerEstadoMasRecienteSinDuplicar_cuandoElProyectoTieneVariosEstados() {
        // Arrange
        var asesor = asesor(null);
        var id = proyecto("Uno", EN_PROCESO);
        cambiarEstadoProyecto(id, FINALIZADO, "2026-10-05T10:00:00Z");
        asignar(asesor, id, EstadoAsesor.ASIGNADO, "2026-10-01T10:00:00Z");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorAsesor(criteria(asesor, null, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().getFirst().estado().id()).isEqualTo(FINALIZADO);
        assertThat(resultado.getContent().getFirst().estado().nombre()).isEqualTo("Finalizado");
    }

    @Test
    void debeFiltrarPorTituloYPorEstadoDelProyecto_cuandoElClienteLosEnvia() {
        // Arrange
        var asesor = asesor(null);
        var buscado = proyecto("Sistema de gestion", FINALIZADO);
        var otroTitulo = proyecto("Otra cosa", FINALIZADO);
        var otroEstado = proyecto("Gestion academica", EN_PROCESO);
        asignar(asesor, buscado, EstadoAsesor.ASIGNADO, "2026-10-01T10:00:00Z");
        asignar(asesor, otroTitulo, EstadoAsesor.ASIGNADO, "2026-10-01T10:00:00Z");
        asignar(asesor, otroEstado, EstadoAsesor.ASIGNADO, "2026-10-01T10:00:00Z");
        entityManager.flush();
        entityManager.clear();
        var filtroTitulo = NodoFiltro.predicado("tituloProyecto", FiltroOperador.CONTIENE, "gestion");
        var filtroEstado = NodoFiltro.predicado("estadoProyectoGrado", FiltroOperador.ES, FINALIZADO);

        // Act
        var porTitulo = adapter.consultarPorAsesor(criteria(asesor, filtroTitulo, 0, 10, List.of()));
        var porEstado = adapter.consultarPorAsesor(criteria(asesor, filtroEstado, 0, 10, List.of()));
        var porAmbos = adapter.consultarPorAsesor(criteria(asesor,
                NodoFiltro.grupo(FiltroConector.AND, List.of(filtroTitulo, filtroEstado)), 0, 10, List.of()));

        // Assert
        assertThat(porTitulo.getContent()).extracting(p -> p.id()).containsExactlyInAnyOrder(buscado, otroEstado);
        assertThat(porEstado.getContent()).extracting(p -> p.id()).containsExactlyInAnyOrder(buscado, otroTitulo);
        assertThat(porAmbos.getContent()).extracting(p -> p.id()).containsExactly(buscado);
    }

    @Test
    void debeOrdenarPorTituloYPaginar_cuandoHayMasProyectosQueElTamanio() {
        // Arrange
        var asesor = asesor(null);
        asignar(asesor, proyecto("B", EN_PROCESO), EstadoAsesor.ASIGNADO, "2026-10-01T10:00:00Z");
        asignar(asesor, proyecto("A", EN_PROCESO), EstadoAsesor.ASIGNADO, "2026-10-01T10:00:00Z");
        asignar(asesor, proyecto("C", EN_PROCESO), EstadoAsesor.ASIGNADO, "2026-10-01T10:00:00Z");
        entityManager.flush();
        entityManager.clear();
        var ascendente = List.of(SortOrder.of("tituloProyecto", SortDirection.ASC));
        var descendente = List.of(SortOrder.of("tituloProyecto", SortDirection.DESC));

        // Act
        var completaAsc = adapter.consultarPorAsesor(criteria(asesor, null, 0, 10, ascendente));
        var completaDesc = adapter.consultarPorAsesor(criteria(asesor, null, 0, 10, descendente));
        var segundaPagina = adapter.consultarPorAsesor(criteria(asesor, null, 1, 2, ascendente));

        // Assert
        assertThat(completaAsc.getContent()).extracting(p -> p.tituloProyecto()).containsExactly("A", "B", "C");
        assertThat(completaDesc.getContent()).extracting(p -> p.tituloProyecto()).containsExactly("C", "B", "A");
        assertThat(segundaPagina.getTotalElements()).isEqualTo(3);
        assertThat(segundaPagina.getTotalPages()).isEqualTo(2);
        assertThat(segundaPagina.getContent()).extracting(p -> p.tituloProyecto()).containsExactly("C");
    }
}
