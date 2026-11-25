package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilRepresentanteCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilRepresentanteReadModel;
import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.command.secondaryadapter.entity.EstadoEvaluacionJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.secondaryadapter.entity.EstadoEvaluacionFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoficha.command.secondaryadapter.entity.EstadoFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.command.secondaryadapter.entity.EstadoFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.entity.EvaluacionFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.secondaryadapter.entity.FichaPerfilJpaEntity;
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
class FichaPerfilRepresentanteQueryOutputAdapterTest {

    private static final String DISPONIBLE = "DISPONIBLE_PARA_EVALUACION";
    private static final String EN_CONSTRUCCION = "EN_CONSTRUCCION";
    private static final String DESCARTADA = "DESCARTADA";
    private static final String APROBADA = "APROBADA";
    private static final String EN_EVALUACION = "EN_EVALUACION";
    private static final Instant T0 = Instant.parse("2026-11-01T10:00:00Z");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FichaPerfilRepresentanteQueryRepository repository;

    private FichaPerfilRepresentanteQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new FichaPerfilRepresentanteQueryOutputAdapter(
                repository, new FichaPerfilRepresentanteJpaSpecification());
        for (var estado : List.of(DISPONIBLE, EN_CONSTRUCCION, DESCARTADA, APROBADA,
                "APROBADA_CON_OBSERVACIONES", "NO_APROBADA")) {
            entityManager.persist(EstadoFichaJpaEntity.builder()
                    .id(estado).nombre(estado).descripcion(estado).build());
        }
        entityManager.persist(EstadoEvaluacionJpaEntity.builder()
                .id(EN_EVALUACION).nombre("En Evaluacion").descripcion("").build());
        entityManager.persist(EstadoEvaluacionJpaEntity.builder()
                .id(DESCARTADA).nombre("Descartada").descripcion("").build());
    }

    @Test
    void debeIncluirLaFichaConEvaluacionYEstados_cuandoElRepresentanteParticipa() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var ficha = sembrarFicha("Arquisoft Backend", "Laura Gomez", "laura@soyuco.edu.co");
        sembrarEstado(ficha, APROBADA, T0);
        var evaluacion = sembrarEvaluacion(ficha, representante, EN_EVALUACION);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarParticipacionRepresentante(criteria(representante).build());

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        var leida = resultado.getContent().get(0);
        assertThat(leida.ficha().id()).isEqualTo(ficha);
        assertThat(leida.ficha().tituloProyecto()).isEqualTo("Arquisoft Backend");
        assertThat(leida.ficha().asesorFicha().nombre()).isEqualTo("Laura Gomez");
        assertThat(leida.ficha().estado().id()).isEqualTo(APROBADA);
        assertThat(leida.evaluacion().id()).isEqualTo(evaluacion);
        assertThat(leida.evaluacion().estadoId()).isEqualTo(EN_EVALUACION);
        assertThat(leida.evaluacion().estadoNombre()).isEqualTo("En Evaluacion");
    }

    @Test
    void debeExcluirLaFicha_cuandoSoloOtroRepresentanteLaEvalua() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var ajena = sembrarFicha("Ajena", "Laura Gomez", "laura@soyuco.edu.co");
        var propia = sembrarFicha("Propia", "Laura Gomez", "laura@soyuco.edu.co");
        sembrarEstado(ajena, DISPONIBLE, T0);
        sembrarEstado(propia, DISPONIBLE, T0);
        sembrarEvaluacion(ajena, UtilUUID.generarNuevoUUID(), EN_EVALUACION);
        sembrarEvaluacion(propia, representante, EN_EVALUACION);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarParticipacionRepresentante(criteria(representante).build());

        // Assert
        assertThat(titulos(resultado.getContent())).containsExactly("Propia");
        assertThat(resultado.getTotalElements()).isEqualTo(1L);
    }

    @Test
    void debeIncluirLaFichaConEstadoDescartada_cuandoLaEvaluacionEstaDescartada() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var ficha = sembrarFicha("Evaluacion descartada", "Laura Gomez", "laura@soyuco.edu.co");
        sembrarEstado(ficha, DISPONIBLE, T0);
        sembrarEvaluacion(ficha, representante, DESCARTADA);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarParticipacionRepresentante(criteria(representante).build());

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).evaluacion().estadoId()).isEqualTo(DESCARTADA);
    }

    @Test
    void debeMostrarEnEvaluacion_cuandoLaEvaluacionFueReactivada() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var ficha = sembrarFicha("Reactivada", "Laura Gomez", "laura@soyuco.edu.co");
        sembrarEstado(ficha, DISPONIBLE, T0);
        var evaluacion = sembrarEvaluacion(ficha, representante, EN_EVALUACION);
        sembrarEstadoEvaluacion(evaluacion, DESCARTADA, T0.plusSeconds(60));
        sembrarEstadoEvaluacion(evaluacion, EN_EVALUACION, T0.plusSeconds(120));
        entityManager.flush();

        // Act
        var resultado = adapter.consultarParticipacionRepresentante(criteria(representante).build());

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).evaluacion().estadoId()).isEqualTo(EN_EVALUACION);
        assertThat(resultado.getTotalElements()).isEqualTo(1L);
    }

    @Test
    void debeIncluirLaFicha_cuandoVolvioAEnConstruccionOQuedoDescartada() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var enConstruccion = sembrarFicha("Volvio a construccion", "Laura Gomez", "laura@soyuco.edu.co");
        var descartada = sembrarFicha("Ficha descartada", "Laura Gomez", "laura@soyuco.edu.co");
        sembrarEstado(enConstruccion, DISPONIBLE, T0);
        sembrarEstado(enConstruccion, EN_CONSTRUCCION, T0.plusSeconds(60));
        sembrarEstado(descartada, DISPONIBLE, T0);
        sembrarEstado(descartada, DESCARTADA, T0.plusSeconds(60));
        sembrarEvaluacion(enConstruccion, representante, EN_EVALUACION);
        sembrarEvaluacion(descartada, representante, EN_EVALUACION);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarParticipacionRepresentante(criteria(representante).build());

        // Assert
        assertThat(titulos(resultado.getContent()))
                .containsExactlyInAnyOrder("Volvio a construccion", "Ficha descartada");
        assertThat(resultado.getContent()).extracting(r -> r.ficha().estado().id())
                .containsExactlyInAnyOrder(EN_CONSTRUCCION, DESCARTADA);
    }

    @Test
    void debeUsarElEstadoActualDeLaFicha_noElHistorial() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var ficha = sembrarFicha("Con historial", "Laura Gomez", "laura@soyuco.edu.co");
        sembrarEstado(ficha, DISPONIBLE, T0);
        sembrarEstado(ficha, APROBADA, T0.plusSeconds(60));
        sembrarEvaluacion(ficha, representante, EN_EVALUACION);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarParticipacionRepresentante(criteria(representante).build());

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).ficha().estado().id()).isEqualTo(APROBADA);
        assertThat(resultado.getTotalElements()).isEqualTo(1L);
    }

    @Test
    void debeFiltrarPorEstadoFicha_cuandoSePideDisponibleOUnoDeDecision() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var disponible = sembrarFicha("Disponible", "Laura Gomez", "laura@soyuco.edu.co");
        var aprobada = sembrarFicha("Aprobada", "Laura Gomez", "laura@soyuco.edu.co");
        var noAprobada = sembrarFicha("No aprobada", "Laura Gomez", "laura@soyuco.edu.co");
        sembrarEstado(disponible, DISPONIBLE, T0);
        sembrarEstado(aprobada, APROBADA, T0);
        sembrarEstado(noAprobada, "NO_APROBADA", T0);
        for (var ficha : List.of(disponible, aprobada, noAprobada)) {
            sembrarEvaluacion(ficha, representante, EN_EVALUACION);
        }
        entityManager.flush();

        // Act
        var porDisponible = adapter.consultarParticipacionRepresentante(
                criteria(representante).raiz(estadoEs(DISPONIBLE)).build());
        var porAprobada = adapter.consultarParticipacionRepresentante(
                criteria(representante).raiz(estadoEs(APROBADA)).build());
        var porNoAprobada = adapter.consultarParticipacionRepresentante(
                criteria(representante).raiz(estadoEs("NO_APROBADA")).build());

        // Assert
        assertThat(titulos(porDisponible.getContent())).containsExactly("Disponible");
        assertThat(titulos(porAprobada.getContent())).containsExactly("Aprobada");
        assertThat(titulos(porNoAprobada.getContent())).containsExactly("No aprobada");
    }

    @Test
    void debeAcotarSinAmpliar_cuandoFiltraPorTituloAsesorNombreOEmail() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var web = sembrarFicha("Plataforma web", "Laura Gomez", "laura@soyuco.edu.co");
        var movil = sembrarFicha("App movil", "Pedro Ruiz", "pedro@soyuco.edu.co");
        var ajena = sembrarFicha("Web ajena", "Laura Gomez", "laura@soyuco.edu.co");
        for (var ficha : List.of(web, movil, ajena)) {
            sembrarEstado(ficha, DISPONIBLE, T0);
        }
        sembrarEvaluacion(web, representante, EN_EVALUACION);
        sembrarEvaluacion(movil, representante, EN_EVALUACION);
        sembrarEvaluacion(ajena, UtilUUID.generarNuevoUUID(), EN_EVALUACION);
        entityManager.flush();

        // Act
        var porTitulo = adapter.consultarParticipacionRepresentante(
                criteria(representante).raiz(contiene("tituloProyecto", "web")).build());
        var porNombre = adapter.consultarParticipacionRepresentante(
                criteria(representante).raiz(contiene("asesorNombre", "Pedro")).build());
        var porEmail = adapter.consultarParticipacionRepresentante(
                criteria(representante).raiz(contiene("asesorEmail", "laura@")).build());

        // Assert
        assertThat(titulos(porTitulo.getContent())).containsExactly("Plataforma web");
        assertThat(titulos(porNombre.getContent())).containsExactly("App movil");
        assertThat(titulos(porEmail.getContent())).containsExactly("Plataforma web");
    }

    @Test
    void debePaginarYOrdenarPorTitulo_cuandoHayVariasFichas() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        for (var titulo : List.of("Charlie", "Alfa", "Bravo")) {
            var ficha = sembrarFicha(titulo, "Laura Gomez", "laura@soyuco.edu.co");
            sembrarEstado(ficha, DISPONIBLE, T0);
            sembrarEvaluacion(ficha, representante, EN_EVALUACION);
        }
        entityManager.flush();
        var orden = List.of(SortOrder.of("tituloProyecto", SortDirection.ASC));

        // Act
        var primera = adapter.consultarParticipacionRepresentante(
                criteria(representante).pagina(0).tamanio(2).ordenamiento(orden).build());
        var segunda = adapter.consultarParticipacionRepresentante(
                criteria(representante).pagina(1).tamanio(2).ordenamiento(orden).build());

        // Assert
        assertThat(titulos(primera.getContent())).containsExactly("Alfa", "Bravo");
        assertThat(titulos(segunda.getContent())).containsExactly("Charlie");
        assertThat(primera.getTotalElements()).isEqualTo(3L);
    }

    @Test
    void debeOrdenarEstablePorIdDeEvaluacion_cuandoNoSeEnviaOrden() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        for (var indice = 0; indice < 4; indice++) {
            var ficha = sembrarFicha("Ficha " + indice, "Laura Gomez", "laura@soyuco.edu.co");
            sembrarEstado(ficha, DISPONIBLE, T0);
            sembrarEvaluacion(ficha, representante, EN_EVALUACION);
        }
        entityManager.flush();

        // Act
        var completa = adapter.consultarParticipacionRepresentante(
                criteria(representante).pagina(0).tamanio(10).build());
        var porPaginas = List.of(0, 1, 2, 3).stream()
                .flatMap(pagina -> adapter.consultarParticipacionRepresentante(
                        criteria(representante).pagina(pagina).tamanio(1).build()).getContent().stream())
                .toList();

        // Assert
        assertThat(evaluaciones(porPaginas)).containsExactlyElementsOf(evaluaciones(completa.getContent()));
        assertThat(evaluaciones(porPaginas)).doesNotHaveDuplicates().hasSize(4);
    }

    @Test
    void debeDevolverPaginaVacia_cuandoElRepresentanteNoEvaluoNinguna() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var ficha = sembrarFicha("De otro", "Laura Gomez", "laura@soyuco.edu.co");
        sembrarEstado(ficha, DISPONIBLE, T0);
        sembrarEvaluacion(ficha, UtilUUID.generarNuevoUUID(), EN_EVALUACION);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarParticipacionRepresentante(criteria(representante).build());

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    private FichaPerfilRepresentanteCriteria.Builder criteria(UUID representante) {
        return FichaPerfilRepresentanteCriteria.builder()
                .pagina(0).tamanio(10).representanteComite(representante);
    }

    private NodoFiltro contiene(String campo, String valor) {
        return NodoFiltro.predicado(campo, FiltroOperador.CONTIENE, valor);
    }

    private NodoFiltro estadoEs(String estado) {
        return NodoFiltro.predicado("estadoFicha", FiltroOperador.ES, estado);
    }

    private List<String> titulos(List<FichaPerfilRepresentanteReadModel> fichas) {
        return fichas.stream().map(f -> f.ficha().tituloProyecto()).toList();
    }

    private List<UUID> evaluaciones(List<FichaPerfilRepresentanteReadModel> fichas) {
        return fichas.stream().map(f -> f.evaluacion().id()).toList();
    }

    private UUID sembrarFicha(String titulo, String asesorNombre, String asesorEmail) {
        var asesor = AsesorFichaJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .identificador("DOC-" + UtilUUID.generarNuevoUUID().toString().substring(0, 8))
                .nombre(asesorNombre)
                .email(asesorEmail)
                .ocurridoEn(T0)
                .build();
        entityManager.persist(asesor);
        var ficha = FichaPerfilJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .tituloProyecto(titulo)
                .asesorFicha(asesor)
                .build();
        entityManager.persist(ficha);
        return ficha.getId();
    }

    private void sembrarEstado(UUID ficha, String estadoId, Instant fecha) {
        var estado = entityManager.getEntityManager().getReference(EstadoFichaJpaEntity.class, estadoId);
        entityManager.persist(EstadoFichaPerfilJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .fichaPerfilId(ficha)
                .estadoFicha(estado)
                .fechaActualizacion(fecha)
                .build());
    }

    private UUID sembrarEvaluacion(UUID ficha, UUID representante, String estadoEvaluacionId) {
        var evaluacion = EvaluacionFichaPerfilJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .fichaPerfilId(ficha)
                .representanteComiteId(representante)
                .fechaCreacion(T0)
                .build();
        entityManager.persist(evaluacion);
        sembrarEstadoEvaluacion(evaluacion.getId(), estadoEvaluacionId, T0);
        return evaluacion.getId();
    }

    private void sembrarEstadoEvaluacion(UUID evaluacionId, String estadoEvaluacionId, Instant fecha) {
        var evaluacion = entityManager.getEntityManager()
                .getReference(EvaluacionFichaPerfilJpaEntity.class, evaluacionId);
        var estado = entityManager.getEntityManager()
                .getReference(EstadoEvaluacionJpaEntity.class, estadoEvaluacionId);
        entityManager.persist(EstadoEvaluacionFichaJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .evaluacionFichaPerfil(evaluacion)
                .estadoEvaluacion(estado)
                .fechaActualizacion(fecha)
                .build());
    }
}
