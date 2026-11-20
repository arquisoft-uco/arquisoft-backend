package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.entregableproyectogrado.query.criteria.EntregableDisponibleCriteria;
import com.arquisoft.biblioteca.application.entregableproyectogrado.query.primaryport.mapper.ConsultarEntregablesDisponiblesMapper;
import com.arquisoft.biblioteca.application.entregableproyectogrado.query.primaryport.model.ConsultarEntregablesDisponiblesQuery;
import com.arquisoft.biblioteca.application.entregableproyectogrado.query.readmodel.EntregableDisponibleReadModel;
import com.arquisoft.biblioteca.infrastructure.autor.command.secondaryadapter.entity.AutorJpaEntity;
import com.arquisoft.biblioteca.infrastructure.documentacion.command.secondaryadapter.entity.DocumentacionJpaEntity;
import com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.command.secondaryadapter.entity.EntregableProyectoGradoJpaEntity;
import com.arquisoft.biblioteca.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
import com.arquisoft.biblioteca.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
class EntregableDisponibleQueryOutputAdapterTest {

    private static final Instant BASE = Instant.parse("2026-11-10T10:00:00Z");
    private static final String BUCKET = "entregables";

    @Autowired
    private EntregableDisponibleQueryRepository repository;

    @Autowired
    private AutorEntregableDisponibleQueryRepository autorRepository;

    @Autowired
    private TestEntityManager entityManager;

    private EntregableDisponibleQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EntregableDisponibleQueryOutputAdapter(
                repository, autorRepository, new EntregableDisponibleJpaSpecification());
    }

    private static UUID idFijo(int numero) {
        return UUID.fromString(String.format("00000000-0000-4000-8000-%012d", numero));
    }

    private UUID sembrarProyecto(String titulo) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(id).titulo(titulo).ocurridoEn(BASE).build());
        return id;
    }

    private UUID sembrarEntregable(UUID id, UUID proyecto, int version, String bucket, String clave) {
        entityManager.persist(EntregableProyectoGradoJpaEntity.builder()
                .id(id).proyectoGradoId(proyecto).versionEntregable(version)
                .estadoEntregableNombre("Aprobado")
                .bucket(bucket).claveObjeto(clave).nombreArchivo("entregable-v" + version + ".zip").build());
        return id;
    }

    private UUID sembrarEntregable(UUID proyecto, int version) {
        return sembrarEntregable(UtilUUID.generarNuevoUUID(), proyecto, version, BUCKET, "clave/" + proyecto);
    }

    private void sembrarDocumentacion(UUID entregable, String estado) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(DocumentacionJpaEntity.builder()
                .id(id).url("https://biblioteca.test/" + id).fechaPublicacion(BASE)
                .entregableProyectoGradoId(entregable).bibliotecarioId(UtilUUID.generarNuevoUUID())
                .estadoDocumentacion(estado).build());
    }

    private UUID sembrarAutor(UUID id, String nombre) {
        entityManager.persist(AutorJpaEntity.builder()
                .id(id).identificador("ID-" + nombre).nombre(nombre).email(nombre + "@uco.edu.co")
                .ocurridoEn(BASE).build());
        return id;
    }

    private UUID sembrarAutor(String nombre) {
        return sembrarAutor(UtilUUID.generarNuevoUUID(), nombre);
    }

    private void vincular(UUID proyecto, UUID autor, Instant eliminadoEn) {
        entityManager.persist(EstudianteProyectoGradoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).proyectoGradoId(proyecto).autorId(autor)
                .ocurridoEn(BASE).eliminadoEn(eliminadoEn).build());
    }

    private void confirmar() {
        entityManager.flush();
        entityManager.clear();
    }

    private static EntregableDisponibleCriteria criteria(int pagina, int tamanio, String titulo) {
        return ConsultarEntregablesDisponiblesMapper.toCriteria(
                ConsultarEntregablesDisponiblesQuery.crear(pagina, tamanio, titulo));
    }

    private List<UUID> idsDe(int pagina, int tamanio, String titulo) {
        return adapter.consultar(criteria(pagina, tamanio, titulo)).getContent().stream()
                .map(EntregableDisponibleReadModel::id)
                .toList();
    }

    private Statistics estadisticas() {
        var statistics = entityManager.getEntityManager().getEntityManagerFactory()
                .unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        return statistics;
    }

    @Test
    void debeListarSoloCandidatosConTodasLasColumnas_cuandoHayDocumentadosYSinArchivo() {
        // Arrange
        var proyecto = sembrarProyecto("Sistema de Gestión Académica");
        var candidato = sembrarEntregable(proyecto, 2);
        var otroProyecto = sembrarProyecto("Documentado");
        sembrarDocumentacion(sembrarEntregable(otroProyecto, 1), "PUBLICADO");
        var sinArchivo = sembrarProyecto("Sin archivo");
        sembrarEntregable(UtilUUID.generarNuevoUUID(), sinArchivo, 1, null, null);
        var autor = sembrarAutor("Ana");
        vincular(proyecto, autor, null);
        confirmar();

        // Act
        var resultado = adapter.consultar(criteria(0, 10, ""));

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(1L);
        var leido = resultado.getContent().get(0);
        assertThat(leido.id()).isEqualTo(candidato);
        assertThat(leido.proyectoGrado()).isEqualTo(proyecto);
        assertThat(leido.tituloProyecto()).isEqualTo("Sistema de Gestión Académica");
        assertThat(leido.versionEntregable()).isEqualTo(2);
        assertThat(leido.nombreArchivo()).isEqualTo("entregable-v2.zip");
        assertThat(leido.autores()).hasSize(1);
        assertThat(leido.autores().get(0).id()).isEqualTo(autor);
        assertThat(leido.autores().get(0).identificador()).isEqualTo("ID-Ana");
        assertThat(leido.autores().get(0).nombre()).isEqualTo("Ana");
    }

    @Test
    void debeExcluirElEntregable_cuandoTieneDocumentacionEnCualquierEstado() {
        // Arrange
        var disponible = sembrarEntregable(sembrarProyecto("Disponible"), 1);
        sembrarDocumentacion(sembrarEntregable(sembrarProyecto("Pendiente"), 1), "PENDIENTE");
        sembrarDocumentacion(sembrarEntregable(sembrarProyecto("Publicado"), 1), "PUBLICADO");
        sembrarDocumentacion(sembrarEntregable(sembrarProyecto("No publicado"), 1), "NO_PUBLICADO");
        confirmar();

        // Act
        var ids = idsDe(0, 10, "");

        // Assert
        assertThat(ids).containsExactly(disponible);
    }

    @Test
    void debeExcluirElEntregable_cuandoBucketOClaveObjetoSonNulosVaciosOEnBlanco() {
        // Arrange
        var proyecto = sembrarProyecto("Proyecto");
        var disponible = sembrarEntregable(UtilUUID.generarNuevoUUID(), proyecto, 1, BUCKET, "clave");
        sembrarEntregable(UtilUUID.generarNuevoUUID(), proyecto, 2, null, "clave");
        sembrarEntregable(UtilUUID.generarNuevoUUID(), proyecto, 3, "", "clave");
        sembrarEntregable(UtilUUID.generarNuevoUUID(), proyecto, 4, "   ", "clave");
        sembrarEntregable(UtilUUID.generarNuevoUUID(), proyecto, 5, BUCKET, null);
        sembrarEntregable(UtilUUID.generarNuevoUUID(), proyecto, 6, BUCKET, "");
        sembrarEntregable(UtilUUID.generarNuevoUUID(), proyecto, 7, BUCKET, "  ");
        confirmar();

        // Act
        var ids = idsDe(0, 10, "");

        // Assert
        assertThat(ids).containsExactly(disponible);
    }

    @Test
    void debeDevolverTituloNuloYAutoresVacios_cuandoElProyectoNoEstaEnLaReplica() {
        // Arrange
        var entregable = sembrarEntregable(UtilUUID.generarNuevoUUID(), 1);
        confirmar();

        // Act
        var resultado = adapter.consultar(criteria(0, 10, ""));

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).id()).isEqualTo(entregable);
        assertThat(resultado.getContent().get(0).tituloProyecto()).isNull();
        assertThat(resultado.getContent().get(0).autores()).isNotNull().isEmpty();
    }

    @Test
    void debeIncluirSoloAutoresVigentesYListaVaciaSinVinculos_cuandoHayVinculoRetirado() {
        // Arrange
        var conRetirado = sembrarProyecto("Con retirado");
        var soloRetirado = sembrarProyecto("Solo retirado");
        var vigente = sembrarAutor("Ana");
        var retirado = sembrarAutor("Beto");
        vincular(conRetirado, vigente, null);
        vincular(conRetirado, retirado, BASE);
        vincular(soloRetirado, retirado, BASE);
        sembrarEntregable(conRetirado, 1);
        sembrarEntregable(soloRetirado, 1);
        confirmar();

        // Act
        var resultado = adapter.consultar(criteria(0, 10, ""));

        // Assert
        assertThat(resultado.getContent()).extracting(EntregableDisponibleReadModel::tituloProyecto)
                .containsExactly("Con retirado", "Solo retirado");
        assertThat(resultado.getContent().get(0).autores()).extracting(a -> a.id()).containsExactly(vigente);
        assertThat(resultado.getContent().get(1).autores()).isNotNull().isEmpty();
    }

    @Test
    void debeOrdenarAutoresPorNombreYLuegoPorId_cuandoHayNombresIguales() {
        // Arrange
        var proyecto = sembrarProyecto("Proyecto");
        var carla = sembrarAutor(idFijo(1), "Carla");
        var anaB = sembrarAutor(idFijo(3), "Ana");
        var anaA = sembrarAutor(idFijo(2), "Ana");
        vincular(proyecto, carla, null);
        vincular(proyecto, anaB, null);
        vincular(proyecto, anaA, null);
        sembrarEntregable(proyecto, 1);
        confirmar();

        // Act
        var resultado = adapter.consultar(criteria(0, 10, ""));

        // Assert
        assertThat(resultado.getContent().get(0).autores()).extracting(a -> a.id())
                .containsExactly(anaA, anaB, carla);
    }

    @Test
    void debeCompartirAutores_cuandoDosEntregablesSonDelMismoProyecto() {
        // Arrange
        var proyecto = sembrarProyecto("Proyecto");
        var autor = sembrarAutor("Ana");
        vincular(proyecto, autor, null);
        sembrarEntregable(proyecto, 1);
        sembrarEntregable(proyecto, 2);
        confirmar();

        // Act
        var resultado = adapter.consultar(criteria(0, 10, ""));

        // Assert
        assertThat(resultado.getContent()).hasSize(2);
        assertThat(resultado.getContent()).allSatisfy(
                e -> assertThat(e.autores()).extracting(a -> a.id()).containsExactly(autor));
    }

    @Test
    void debeFiltrarPorTituloSinDistinguirMayusculas_cuandoVariaLaCapitalizacion() {
        // Arrange
        var gestion = sembrarEntregable(sembrarProyecto("Sistema de Gestión Académica"), 1);
        sembrarEntregable(sembrarProyecto("Otro proyecto"), 1);
        confirmar();

        // Act
        var enMayusculas = idsDe(0, 10, "GESTIÓN");
        var enMinusculas = idsDe(0, 10, "gestión");
        var recortado = idsDe(0, 10, "  gestión  ");

        // Assert
        assertThat(enMayusculas).containsExactly(gestion);
        assertThat(enMinusculas).containsExactly(gestion);
        assertThat(recortado).containsExactly(gestion);
    }

    @Test
    void debeTratarPorcentajeComoTextoLiteral_cuandoElTituloLoContiene() {
        // Arrange
        var conPorcentaje = sembrarEntregable(sembrarProyecto("Análisis al 100% con Python"), 1);
        sembrarEntregable(sembrarProyecto("Plataforma para 100 usuarios"), 1);
        confirmar();

        // Act
        var ids = idsDe(0, 10, "100%");

        // Assert
        assertThat(ids).containsExactly(conPorcentaje);
    }

    @Test
    void debeTratarGuionBajoComoTextoLiteral_cuandoElTituloLoContiene() {
        // Arrange
        var conGuion = sembrarEntregable(sembrarProyecto("Modelo_predictivo de deserción"), 1);
        sembrarEntregable(sembrarProyecto("Modelo predictivo de ventas"), 1);
        confirmar();

        // Act
        var ids = idsDe(0, 10, "modelo_pred");

        // Assert
        assertThat(ids).containsExactly(conGuion);
    }

    @Test
    void debeTratarBarraInvertidaComoTextoLiteral_cuandoElTituloLaContiene() {
        // Arrange
        var conBarra = sembrarEntregable(sembrarProyecto("Ruta a\\b del sistema"), 1);
        sembrarEntregable(sembrarProyecto("Ruta ab del sistema"), 1);
        confirmar();

        // Act
        var ids = idsDe(0, 10, "a\\b");

        // Assert
        assertThat(ids).containsExactly(conBarra);
    }

    @Test
    void debeOmitirElFiltroYExcluirTituloNuloSoloConFiltro_cuandoVariaElTitulo() {
        // Arrange
        var conTitulo = sembrarEntregable(sembrarProyecto("Proyecto"), 1);
        var sinTitulo = sembrarEntregable(UtilUUID.generarNuevoUUID(), 1);
        confirmar();

        // Act
        var sinFiltro = idsDe(0, 10, "");
        var conFiltro = idsDe(0, 10, "proyecto");

        // Assert
        assertThat(sinFiltro).containsExactly(conTitulo, sinTitulo);
        assertThat(conFiltro).containsExactly(conTitulo);
    }

    @Test
    void debeOrdenarPorTituloVersionEIdConNulosAlFinal_cuandoHayVariosCandidatos() {
        // Arrange
        var beta = sembrarEntregable(sembrarProyecto("Beta"), 1);
        var proyectoAlfa = sembrarProyecto("Alfa");
        var alfaV2 = sembrarEntregable(proyectoAlfa, 2);
        var alfaV1 = sembrarEntregable(proyectoAlfa, 1);
        var deltaB = sembrarEntregable(idFijo(2), sembrarProyecto("Delta"), 1, BUCKET, "clave-b");
        var deltaA = sembrarEntregable(idFijo(1), sembrarProyecto("Delta"), 1, BUCKET, "clave-a");
        var sinTitulo = sembrarEntregable(UtilUUID.generarNuevoUUID(), 1);
        confirmar();

        // Act
        var ids = idsDe(0, 10, "");

        // Assert
        assertThat(ids).containsExactly(alfaV1, alfaV2, beta, deltaA, deltaB, sinTitulo);
    }

    @Test
    void debePaginarSinDuplicarNiPerder_cuandoHayVariosAutoresPorEntregable() {
        // Arrange
        var esperados = new ArrayList<UUID>();
        for (var i = 0; i < 5; i++) {
            var proyecto = sembrarProyecto("Proyecto " + i);
            for (var j = 0; j <= i % 3; j++) {
                vincular(proyecto, sembrarAutor("Autor " + i + "-" + j), null);
            }
            esperados.add(sembrarEntregable(proyecto, 1));
        }
        confirmar();

        // Act
        var primera = adapter.consultar(criteria(0, 2, ""));
        var segunda = adapter.consultar(criteria(1, 2, ""));
        var tercera = adapter.consultar(criteria(2, 2, ""));

        // Assert
        var unidos = new ArrayList<UUID>();
        for (var pagina : List.of(primera, segunda, tercera)) {
            pagina.getContent().forEach(e -> unidos.add(e.id()));
        }
        assertThat(unidos).containsExactlyElementsOf(esperados);
        assertThat(primera.getTotalElements()).isEqualTo(5L);
        assertThat(tercera.getContent()).hasSize(1);
    }

    @Test
    void debeConsultarElMismoNumeroDeSentencias_cuandoCambiaElTamanioDePagina() {
        // Arrange
        for (var i = 0; i < 8; i++) {
            var proyecto = sembrarProyecto("Proyecto " + i);
            vincular(proyecto, sembrarAutor("Autor " + i), null);
            sembrarEntregable(proyecto, 1);
        }
        confirmar();
        var estadisticas = estadisticas();

        // Act
        estadisticas.clear();
        adapter.consultar(criteria(0, 2, ""));
        var conTamanioDos = estadisticas.getPrepareStatementCount();
        estadisticas.clear();
        adapter.consultar(criteria(0, 5, ""));
        var conTamanioCinco = estadisticas.getPrepareStatementCount();

        // Assert
        assertThat(conTamanioDos).isEqualTo(3L);
        assertThat(conTamanioCinco).isEqualTo(3L);
    }

    @Test
    void debeNoConsultarAutores_cuandoLaPaginaEstaVacia() {
        // Arrange
        for (var i = 0; i < 3; i++) {
            var proyecto = sembrarProyecto("Proyecto " + i);
            vincular(proyecto, sembrarAutor("Autor " + i), null);
            sembrarEntregable(proyecto, 1);
        }
        confirmar();
        var estadisticas = estadisticas();

        // Act
        estadisticas.clear();
        var fueraDeRango = adapter.consultar(criteria(5, 2, ""));
        var sentenciasFueraDeRango = estadisticas.getPrepareStatementCount();
        estadisticas.clear();
        var sinResultados = adapter.consultar(criteria(0, 10, "inexistente"));
        var sentenciasSinResultados = estadisticas.getPrepareStatementCount();

        // Assert
        assertThat(fueraDeRango.getContent()).isEmpty();
        assertThat(sentenciasFueraDeRango).isEqualTo(2L);
        assertThat(sinResultados.getContent()).isEmpty();
        assertThat(sentenciasSinResultados).isEqualTo(1L);
    }

    @Test
    void debeDejarDeListarElEntregable_cuandoSeInsertaSuDocumentacion() {
        // Arrange
        var entregable = sembrarEntregable(sembrarProyecto("Proyecto"), 1);
        confirmar();
        var antes = idsDe(0, 10, "");

        // Act
        sembrarDocumentacion(entregable, "PENDIENTE");
        confirmar();
        var despues = idsDe(0, 10, "");

        // Assert
        assertThat(antes).containsExactly(entregable);
        assertThat(despues).isEmpty();
    }
}
