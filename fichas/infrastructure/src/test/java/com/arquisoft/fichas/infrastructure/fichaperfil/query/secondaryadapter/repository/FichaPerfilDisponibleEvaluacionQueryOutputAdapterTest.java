package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilDisponibleEvaluacionCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
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
class FichaPerfilDisponibleEvaluacionQueryOutputAdapterTest {

    private static final String DISPONIBLE = "DISPONIBLE_PARA_EVALUACION";
    private static final String EN_CONSTRUCCION = "EN_CONSTRUCCION";
    private static final Instant T0 = Instant.parse("2026-11-01T10:00:00Z");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FichaPerfilDisponibleEvaluacionQueryRepository repository;

    private FichaPerfilDisponibleEvaluacionQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new FichaPerfilDisponibleEvaluacionQueryOutputAdapter(
                repository, new FichaPerfilDisponibleEvaluacionJpaSpecification());
        for (var estado : List.of(DISPONIBLE, EN_CONSTRUCCION, "DESCARTADA", "APROBADA")) {
            entityManager.persist(EstadoFichaJpaEntity.builder()
                    .id(estado).nombre(estado).descripcion(estado).build());
        }
        entityManager.persist(EstadoEvaluacionJpaEntity.builder()
                .id("EN_EVALUACION").nombre("En Evaluacion").descripcion("").build());
        entityManager.persist(EstadoEvaluacionJpaEntity.builder()
                .id("DESCARTADA").nombre("Descartada").descripcion("").build());
    }

    @Test
    void debeIncluirLaFichaConSuAsesorYEstado_cuandoEstaDisponibleYSinEvaluacionDelRepresentante() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var ficha = sembrarFicha("Arquisoft Backend", "Laura Gomez", "laura@soyuco.edu.co");
        sembrarEstado(ficha, DISPONIBLE, T0);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarDisponiblesParaEvaluar(criteria(representante).build());

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        var leida = resultado.getContent().get(0);
        assertThat(leida.id()).isEqualTo(ficha);
        assertThat(leida.tituloProyecto()).isEqualTo("Arquisoft Backend");
        assertThat(leida.asesorFicha().nombre()).isEqualTo("Laura Gomez");
        assertThat(leida.estado().id()).isEqualTo(DISPONIBLE);
    }

    @Test
    void debeIncluirLaFicha_cuandoSoloOtroRepresentanteLaEvalua() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var ficha = sembrarFicha("Ficha compartida", "Laura Gomez", "laura@soyuco.edu.co");
        sembrarEstado(ficha, DISPONIBLE, T0);
        sembrarEvaluacion(ficha, UtilUUID.generarNuevoUUID(), "EN_EVALUACION");
        entityManager.flush();

        // Act
        var resultado = adapter.consultarDisponiblesParaEvaluar(criteria(representante).build());

        // Assert
        assertThat(titulos(resultado.getContent())).containsExactly("Ficha compartida");
    }

    @Test
    void debeExcluirLaFicha_cuandoElRepresentanteTieneEvaluacionEnCualquierEstado() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var enCurso = sembrarFicha("Evaluacion en curso", "Laura Gomez", "laura@soyuco.edu.co");
        var descartada = sembrarFicha("Evaluacion descartada", "Laura Gomez", "laura@soyuco.edu.co");
        var libre = sembrarFicha("Sin evaluacion", "Laura Gomez", "laura@soyuco.edu.co");
        for (var ficha : List.of(enCurso, descartada, libre)) {
            sembrarEstado(ficha, DISPONIBLE, T0);
        }
        sembrarEvaluacion(enCurso, representante, "EN_EVALUACION");
        sembrarEvaluacion(descartada, representante, "DESCARTADA");
        entityManager.flush();

        // Act
        var resultado = adapter.consultarDisponiblesParaEvaluar(criteria(representante).build());

        // Assert
        assertThat(titulos(resultado.getContent())).containsExactly("Sin evaluacion");
        assertThat(resultado.getTotalElements()).isEqualTo(1L);
    }

    @Test
    void debeExcluirLaFicha_cuandoSuEstadoActualNoEsDisponible() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var enConstruccion = sembrarFicha("En construccion", "Laura Gomez", "laura@soyuco.edu.co");
        var aprobada = sembrarFicha("Aprobada", "Laura Gomez", "laura@soyuco.edu.co");
        var estuvoDisponible = sembrarFicha("Ya no disponible", "Laura Gomez", "laura@soyuco.edu.co");
        sembrarEstado(enConstruccion, EN_CONSTRUCCION, T0);
        sembrarEstado(aprobada, "APROBADA", T0);
        sembrarEstado(estuvoDisponible, DISPONIBLE, T0);
        sembrarEstado(estuvoDisponible, "DESCARTADA", T0.plusSeconds(60));
        entityManager.flush();

        // Act
        var resultado = adapter.consultarDisponiblesParaEvaluar(criteria(representante).build());

        // Assert
        assertThat(resultado.getContent()).isEmpty();
    }

    @Test
    void debeIncluirLaFichaUnaSolaVez_cuandoVolvioAEstarDisponible() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var ficha = sembrarFicha("Reactivada", "Laura Gomez", "laura@soyuco.edu.co");
        sembrarEstado(ficha, DISPONIBLE, T0);
        sembrarEstado(ficha, "DESCARTADA", T0.plusSeconds(60));
        sembrarEstado(ficha, DISPONIBLE, T0.plusSeconds(120));
        entityManager.flush();

        // Act
        var resultado = adapter.consultarDisponiblesParaEvaluar(criteria(representante).build());

        // Assert
        assertThat(titulos(resultado.getContent())).containsExactly("Reactivada");
        assertThat(resultado.getTotalElements()).isEqualTo(1L);
    }

    @Test
    void debeAcotarSinAmpliar_cuandoFiltraPorTituloAsesorNombreOEmail() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var web = sembrarFicha("Plataforma web", "Laura Gomez", "laura@soyuco.edu.co");
        var movil = sembrarFicha("App movil", "Pedro Ruiz", "pedro@soyuco.edu.co");
        var evaluada = sembrarFicha("Web ya evaluada", "Laura Gomez", "laura@soyuco.edu.co");
        for (var ficha : List.of(web, movil, evaluada)) {
            sembrarEstado(ficha, DISPONIBLE, T0);
        }
        sembrarEvaluacion(evaluada, representante, "EN_EVALUACION");
        entityManager.flush();

        // Act
        var porTitulo = adapter.consultarDisponiblesParaEvaluar(
                criteria(representante).raiz(contiene("tituloProyecto", "web")).build());
        var porNombre = adapter.consultarDisponiblesParaEvaluar(
                criteria(representante).raiz(contiene("asesorNombre", "Pedro")).build());
        var porEmail = adapter.consultarDisponiblesParaEvaluar(
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
            sembrarEstado(sembrarFicha(titulo, "Laura Gomez", "laura@soyuco.edu.co"), DISPONIBLE, T0);
        }
        entityManager.flush();
        var orden = List.of(SortOrder.of("tituloProyecto", SortDirection.ASC));

        // Act
        var primera = adapter.consultarDisponiblesParaEvaluar(
                criteria(representante).pagina(0).tamanio(2).ordenamiento(orden).build());
        var segunda = adapter.consultarDisponiblesParaEvaluar(
                criteria(representante).pagina(1).tamanio(2).ordenamiento(orden).build());

        // Assert
        assertThat(titulos(primera.getContent())).containsExactly("Alfa", "Bravo");
        assertThat(titulos(segunda.getContent())).containsExactly("Charlie");
        assertThat(primera.getTotalElements()).isEqualTo(3L);
    }

    private FichaPerfilDisponibleEvaluacionCriteria.Builder criteria(UUID representante) {
        return FichaPerfilDisponibleEvaluacionCriteria.builder()
                .pagina(0).tamanio(10).representanteComite(representante);
    }

    private NodoFiltro contiene(String campo, String valor) {
        return NodoFiltro.predicado(campo, FiltroOperador.CONTIENE, valor);
    }

    private List<String> titulos(List<FichaPerfilReadModel> fichas) {
        return fichas.stream().map(FichaPerfilReadModel::tituloProyecto).toList();
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

    private void sembrarEvaluacion(UUID ficha, UUID representante, String estadoEvaluacionId) {
        var evaluacion = EvaluacionFichaPerfilJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .fichaPerfilId(ficha)
                .representanteComiteId(representante)
                .fechaCreacion(T0)
                .build();
        entityManager.persist(evaluacion);
        var estado = entityManager.getEntityManager()
                .getReference(EstadoEvaluacionJpaEntity.class, estadoEvaluacionId);
        entityManager.persist(EstadoEvaluacionFichaJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .evaluacionFichaPerfil(evaluacion)
                .estadoEvaluacion(estado)
                .fechaActualizacion(T0)
                .build());
    }
}
