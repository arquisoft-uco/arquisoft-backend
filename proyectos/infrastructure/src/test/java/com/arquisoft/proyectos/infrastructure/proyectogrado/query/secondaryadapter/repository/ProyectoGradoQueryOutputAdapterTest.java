package com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.infrastructure.estadoproyectogrado.command.secondaryadapter.entity.EstadoProyectoGradoJpaEntity;
import com.arquisoft.proyectos.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
import com.arquisoft.proyectos.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
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
class ProyectoGradoQueryOutputAdapterTest {

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

    private UUID proyecto(UUID coordinador, String estado, String titulo) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ProyectoGradoJpaEntity(id, coordinador, UtilUUID.generarNuevoUUID(), titulo));
        entityManager.persist(new EstadoProyectoGradoJpaEntity(
                UtilUUID.generarNuevoUUID(), id, estado, Instant.parse("2026-10-01T10:00:00Z")));
        return id;
    }

    private void cambiarEstado(UUID proyectoGrado, String estado, String fecha) {
        entityManager.persist(new EstadoProyectoGradoJpaEntity(
                UtilUUID.generarNuevoUUID(), proyectoGrado, estado, Instant.parse(fecha)));
    }

    private ProyectoGradoCriteria criteria(UUID coordinador, NodoFiltro filtroCliente, int pagina, int tamanio,
                                           List<SortOrder> orden) {
        var forzado = NodoFiltro.predicado(ProyectoGradoCriteria.Campo.COORDINADOR.getClave(),
                FiltroOperador.ES, coordinador.toString());
        var raiz = filtroCliente == null
                ? forzado
                : NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, filtroCliente));
        return ProyectoGradoCriteria.builder().pagina(pagina).tamanio(tamanio).ordenamiento(orden).raiz(raiz).build();
    }

    @Test
    void debeDevolverSoloProyectosDelCoordinadorYMapearEstado_cuandoHayDeOtros() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var propio = proyecto(coordinador, EN_PROCESO, "Propio");
        proyecto(UtilUUID.generarNuevoUUID(), EN_PROCESO, "Ajeno");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarTodas(criteria(coordinador, null, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        var proyectoLeido = resultado.getContent().getFirst();
        assertThat(proyectoLeido.id()).isEqualTo(propio);
        assertThat(proyectoLeido.tituloProyecto()).isEqualTo("Propio");
        assertThat(proyectoLeido.fichaPerfil()).isNotNull();
        assertThat(proyectoLeido.estado().id()).isEqualTo(EN_PROCESO);
        assertThat(proyectoLeido.estado().nombre()).isEqualTo("En proceso");
    }

    @Test
    void debeFiltrarPorEstado_cuandoSePideEstadoProyectoGrado() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var finalizado = proyecto(coordinador, FINALIZADO, "Uno");
        proyecto(coordinador, EN_PROCESO, "Dos");
        entityManager.flush();
        entityManager.clear();
        var filtro = NodoFiltro.predicado("estadoProyectoGrado", FiltroOperador.ES, FINALIZADO);

        // Act
        var resultado = adapter.consultarTodas(criteria(coordinador, filtro, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getContent()).extracting(p -> p.id()).containsExactly(finalizado);
    }

    @Test
    void debeExponerElEstadoMasRecienteSinDuplicar_cuandoElProyectoTieneVariosEstados() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var id = proyecto(coordinador, EN_PROCESO, "Uno");
        cambiarEstado(id, FINALIZADO, "2026-10-05T10:00:00Z");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarTodas(criteria(coordinador, null, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        var proyectoLeido = resultado.getContent().getFirst();
        assertThat(proyectoLeido.id()).isEqualTo(id);
        assertThat(proyectoLeido.estado().id()).isEqualTo(FINALIZADO);
        assertThat(proyectoLeido.estado().nombre()).isEqualTo("Finalizado");
    }

    @Test
    void debeFiltrarPorEstadoVigente_cuandoUnEstadoAnteriorCoincideConElFiltro() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var yaFinalizado = proyecto(coordinador, EN_PROCESO, "Uno");
        cambiarEstado(yaFinalizado, FINALIZADO, "2026-10-05T10:00:00Z");
        var enProceso = proyecto(coordinador, EN_PROCESO, "Dos");
        entityManager.flush();
        entityManager.clear();
        var filtro = NodoFiltro.predicado("estadoProyectoGrado", FiltroOperador.ES, EN_PROCESO);

        // Act
        var resultado = adapter.consultarTodas(criteria(coordinador, filtro, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getContent()).extracting(p -> p.id()).containsExactly(enProceso);
    }

    @Test
    void debeFiltrarPorTituloParcial_cuandoSePideContiene() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var buscado = proyecto(coordinador, EN_PROCESO, "Sistema de gestion");
        proyecto(coordinador, EN_PROCESO, "Otra cosa");
        entityManager.flush();
        entityManager.clear();
        var filtro = NodoFiltro.predicado("tituloProyecto", FiltroOperador.CONTIENE, "gestion");

        // Act
        var resultado = adapter.consultarTodas(criteria(coordinador, filtro, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getContent()).extracting(p -> p.id()).containsExactly(buscado);
    }

    @Test
    void debeFiltrarPorId_cuandoSePideId() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var buscado = proyecto(coordinador, EN_PROCESO, "Uno");
        proyecto(coordinador, EN_PROCESO, "Dos");
        entityManager.flush();
        entityManager.clear();
        var filtro = NodoFiltro.predicado("id", FiltroOperador.ES, buscado.toString());

        // Act
        var resultado = adapter.consultarTodas(criteria(coordinador, filtro, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getContent()).extracting(p -> p.id()).containsExactly(buscado);
    }

    @Test
    void debeOrdenarPorTitulo_cuandoSePideAscendenteYDescendente() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        proyecto(coordinador, EN_PROCESO, "B");
        proyecto(coordinador, EN_PROCESO, "A");
        proyecto(coordinador, EN_PROCESO, "C");
        entityManager.flush();
        entityManager.clear();

        // Act
        var ascendente = adapter.consultarTodas(criteria(coordinador, null, 0, 10,
                List.of(SortOrder.of("tituloProyecto", SortDirection.ASC))));
        var descendente = adapter.consultarTodas(criteria(coordinador, null, 0, 10,
                List.of(SortOrder.of("tituloProyecto", SortDirection.DESC))));

        // Assert
        assertThat(ascendente.getContent()).extracting(p -> p.tituloProyecto()).containsExactly("A", "B", "C");
        assertThat(descendente.getContent()).extracting(p -> p.tituloProyecto()).containsExactly("C", "B", "A");
    }

    @Test
    void debePaginar_cuandoHayMasProyectosQueElTamanio() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        proyecto(coordinador, EN_PROCESO, "A");
        proyecto(coordinador, EN_PROCESO, "B");
        proyecto(coordinador, EN_PROCESO, "C");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarTodas(criteria(coordinador, null, 1, 2,
                List.of(SortOrder.of("tituloProyecto", SortDirection.ASC))));

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(3);
        assertThat(resultado.getTotalPages()).isEqualTo(2);
        assertThat(resultado.getContent()).extracting(p -> p.tituloProyecto()).containsExactly("C");
    }

    private UUID estudiante(Instant eliminadoEn) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new EstudianteJpaEntity(id, id.toString().substring(0, 20), "Nombre",
                "e@uco.edu.co", Instant.parse("2026-10-01T10:00:00Z"), eliminadoEn));
        return id;
    }

    private void vincular(UUID estudiante, UUID proyectoGrado) {
        entityManager.persist(new EstudianteProyectoGradoJpaEntity(
                UtilUUID.generarNuevoUUID(), estudiante, proyectoGrado));
    }

    @Test
    void debeRetornarProyectosDelEstudiante_cuandoTieneVinculos() {
        // Arrange
        var estudiante = estudiante(null);
        var id = proyecto(UtilUUID.generarNuevoUUID(), EN_PROCESO, "Propio");
        vincular(estudiante, id);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorEstudiante(new ProyectoGradoEstudianteCriteria(estudiante));

        // Assert
        assertThat(resultado).hasSize(1);
        var leido = resultado.getFirst();
        assertThat(leido.id()).isEqualTo(id);
        assertThat(leido.fichaPerfil()).isNotNull();
        assertThat(leido.tituloProyecto()).isEqualTo("Propio");
        assertThat(leido.estado().id()).isEqualTo(EN_PROCESO);
        assertThat(leido.estado().nombre()).isEqualTo("En proceso");
    }

    @Test
    void debeRetornarEstadoMasReciente_cuandoHayVariasFilasDeEstado() {
        // Arrange
        var estudiante = estudiante(null);
        var id = proyecto(UtilUUID.generarNuevoUUID(), EN_PROCESO, "Uno");
        cambiarEstado(id, FINALIZADO, "2026-10-05T10:00:00Z");
        vincular(estudiante, id);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorEstudiante(new ProyectoGradoEstudianteCriteria(estudiante));

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().estado().id()).isEqualTo(FINALIZADO);
        assertThat(resultado.getFirst().estado().nombre()).isEqualTo("Finalizado");
    }

    @Test
    void debeExcluirProyectosAjenos_cuandoOtroEstudianteEstaVinculado() {
        // Arrange
        var estudiante = estudiante(null);
        var otro = estudiante(null);
        var propio = proyecto(UtilUUID.generarNuevoUUID(), EN_PROCESO, "Propio");
        var ajeno = proyecto(UtilUUID.generarNuevoUUID(), EN_PROCESO, "Ajeno");
        vincular(estudiante, propio);
        vincular(otro, ajeno);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorEstudiante(new ProyectoGradoEstudianteCriteria(estudiante));

        // Assert
        assertThat(resultado).extracting(p -> p.id()).containsExactly(propio);
    }

    @Test
    void debeRetornarVacio_cuandoEstudianteDadoDeBaja() {
        // Arrange
        var estudiante = estudiante(Instant.parse("2026-10-02T10:00:00Z"));
        vincular(estudiante, proyecto(UtilUUID.generarNuevoUUID(), EN_PROCESO, "Uno"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorEstudiante(new ProyectoGradoEstudianteCriteria(estudiante));

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRetornarVacio_cuandoNoTieneVinculos() {
        // Arrange
        var estudiante = estudiante(null);
        proyecto(UtilUUID.generarNuevoUUID(), EN_PROCESO, "Sin vinculo");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorEstudiante(new ProyectoGradoEstudianteCriteria(estudiante));

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeOrdenarPorTitulo_cuandoTieneVariosProyectos() {
        // Arrange
        var estudiante = estudiante(null);
        vincular(estudiante, proyecto(UtilUUID.generarNuevoUUID(), EN_PROCESO, "B"));
        vincular(estudiante, proyecto(UtilUUID.generarNuevoUUID(), EN_PROCESO, "A"));
        vincular(estudiante, proyecto(UtilUUID.generarNuevoUUID(), EN_PROCESO, "C"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorEstudiante(new ProyectoGradoEstudianteCriteria(estudiante));

        // Assert
        assertThat(resultado).extracting(p -> p.tituloProyecto()).containsExactly("A", "B", "C");
    }

    @Test
    void debeDevolverVacio_cuandoCoordinadorSinProyectos() {
        // Arrange
        proyecto(UtilUUID.generarNuevoUUID(), EN_PROCESO, "Ajeno");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarTodas(criteria(UtilUUID.generarNuevoUUID(), null, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }
}
