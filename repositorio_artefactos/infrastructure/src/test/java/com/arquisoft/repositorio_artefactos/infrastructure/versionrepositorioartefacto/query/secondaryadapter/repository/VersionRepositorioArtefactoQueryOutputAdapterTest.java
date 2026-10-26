package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.secondaryadapter.repository;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.criteria.VersionRepositorioArtefactoAsesorCriteria;
import com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.secondaryadapter.entity.RepositorioArtefactoJpaEntity;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.entity.VersionRepositorioArtefactoJpaEntity;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VersionRepositorioArtefactoQueryOutputAdapterTest {

    @Autowired
    private VersionRepositorioArtefactoQueryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VersionRepositorioArtefactoAsesorQueryRepository asesorRepository;

    private VersionRepositorioArtefactoQueryOutputAdapter adapter() {
        return new VersionRepositorioArtefactoQueryOutputAdapter(
                repository, asesorRepository, new VersionRepositorioArtefactoAsesorJpaSpecification());
    }

    private UUID sembrar(UUID repositorio, String documento, int numero, String hash) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(VersionRepositorioArtefactoJpaEntity.builder()
                .id(id)
                .nombreDocumento(documento)
                .numeroVersion(numero)
                .fechaCreacion(Instant.parse("2026-10-20T10:00:00Z"))
                .repositorioArtefacto(repositorio)
                .uriArchivo("versiones/" + id)
                .tipoContenido("application/pdf")
                .tamanoBytes(2048L)
                .nombreArchivo("formato.pdf")
                .hashSha256(hash)
                .build());
        return id;
    }

    @Test
    void debeOrdenarPorNumeroVersionDescendente_cuandoHayVarias() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var v1 = sembrar(repositorio, "Formato A", 1, null);
        var v3 = sembrar(repositorio, "Formato A", 3, null);
        var v2 = sembrar(repositorio, "Formato A", 2, null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter().consultarPorRepositorio(repositorio);

        // Assert
        assertThat(resultado).extracting("id").containsExactly(v3, v2, v1);
    }

    @Test
    void debeDesempatarPorNombreDocumentoAscendente_cuandoMismoNumero() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var b = sembrar(repositorio, "Formato B", 2, null);
        var a = sembrar(repositorio, "Formato A", 2, null);
        var alto = sembrar(repositorio, "Formato Z", 5, null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter().consultarPorRepositorio(repositorio);

        // Assert
        assertThat(resultado).extracting("id").containsExactly(alto, a, b);
    }

    @Test
    void debeExcluirVersionesDeOtroRepositorio_cuandoHayVarios() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var propia = sembrar(repositorio, "Formato A", 1, null);
        sembrar(UtilUUID.generarNuevoUUID(), "Formato A", 1, null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter().consultarPorRepositorio(repositorio);

        // Assert
        assertThat(resultado).extracting("id").containsExactly(propia);
    }

    @Test
    void debeRetornarListaVacia_cuandoRepositorioNoTieneVersiones() {
        // Arrange
        sembrar(UtilUUID.generarNuevoUUID(), "Formato A", 1, null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter().consultarPorRepositorio(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeMapearTodosLosCampos_cuandoHashEsNulo() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var id = sembrar(repositorio, "Formato A", 4, null);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter().consultarPorRepositorio(repositorio);

        // Assert
        assertThat(resultado).hasSize(1);
        var version = resultado.get(0);
        assertThat(version.id()).isEqualTo(id);
        assertThat(version.nombreDocumento()).isEqualTo("Formato A");
        assertThat(version.numeroVersion()).isEqualTo(4);
        assertThat(version.fechaCreacion()).isEqualTo(Instant.parse("2026-10-20T10:00:00Z"));
        assertThat(version.tipoContenido()).isEqualTo("application/pdf");
        assertThat(version.tamanoBytes()).isEqualTo(2048L);
        assertThat(version.nombreArchivo()).isEqualTo("formato.pdf");
        assertThat(version.hashSha256()).isNull();
    }

    private UUID sembrarRepositorio(String nombre) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(RepositorioArtefactoJpaEntity.builder()
                .id(id)
                .nombreRepositorio(nombre)
                .descripcion("Descripcion " + nombre)
                .administrador(UtilUUID.generarNuevoUUID())
                .build());
        return id;
    }

    private UUID sembrarVersion(UUID repositorio, String documento, int numero, Instant fecha) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(VersionRepositorioArtefactoJpaEntity.builder()
                .id(id)
                .nombreDocumento(documento)
                .numeroVersion(numero)
                .fechaCreacion(fecha)
                .repositorioArtefacto(repositorio)
                .uriArchivo("versiones/" + id)
                .tipoContenido("application/pdf")
                .tamanoBytes(2048L)
                .nombreArchivo(documento + ".pdf")
                .hashSha256(null)
                .build());
        return id;
    }

    private VersionRepositorioArtefactoAsesorCriteria criterio(NodoFiltro raiz, int pagina, int tamanio,
                                                               List<SortOrder> orden) {
        return VersionRepositorioArtefactoAsesorCriteria.builder()
                .pagina(pagina).tamanio(tamanio).ordenamiento(orden).raiz(raiz).build();
    }

    @Test
    void debeListarVersionesDeTodosLosRepositorios_conSuRepositorio() {
        // Arrange
        var repoUno = sembrarRepositorio("Repo uno");
        var repoDos = sembrarRepositorio("Repo dos");
        var fecha = Instant.parse("2026-10-20T10:00:00Z");
        var v1 = sembrarVersion(repoUno, "Formato A", 1, fecha);
        sembrarVersion(repoDos, "Formato B", 1, fecha);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter().consultarParaAsesor(criterio(null, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(2L);
        var leida = resultado.getContent().stream().filter(v -> v.id().equals(v1)).findFirst().orElseThrow();
        assertThat(leida.nombreDocumento()).isEqualTo("Formato A");
        assertThat(leida.numeroVersion()).isEqualTo(1);
        assertThat(leida.fechaCreacion()).isEqualTo(fecha);
        assertThat(leida.tipoContenido()).isEqualTo("application/pdf");
        assertThat(leida.tamanoBytes()).isEqualTo(2048L);
        assertThat(leida.nombreArchivo()).isEqualTo("Formato A.pdf");
        assertThat(leida.hashSha256()).isNull();
        assertThat(leida.repositorioArtefacto().id()).isEqualTo(repoUno);
        assertThat(leida.repositorioArtefacto().nombreRepositorio()).isEqualTo("Repo uno");
        assertThat(leida.repositorioArtefacto().descripcion()).isEqualTo("Descripcion Repo uno");
    }

    @Test
    void debeFiltrarPorNombreRepositorio_cuandoHayCoincidencia() {
        // Arrange
        var backend = sembrarRepositorio("Backend base");
        var frontend = sembrarRepositorio("Frontend base");
        var fecha = Instant.parse("2026-10-20T10:00:00Z");
        var esperada = sembrarVersion(backend, "Formato A", 1, fecha);
        sembrarVersion(frontend, "Formato B", 1, fecha);
        entityManager.flush();
        var raiz = NodoFiltro.predicado("nombreRepositorio", FiltroOperador.CONTIENE, "back");

        // Act
        var resultado = adapter().consultarParaAsesor(criterio(raiz, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getContent()).extracting("id").containsExactly(esperada);
    }

    @Test
    void debeFiltrarPorRepositorioId_cuandoHayCoincidencia() {
        // Arrange
        var uno = sembrarRepositorio("Repo uno");
        var dos = sembrarRepositorio("Repo dos");
        var fecha = Instant.parse("2026-10-20T10:00:00Z");
        sembrarVersion(uno, "Formato A", 1, fecha);
        var esperada = sembrarVersion(dos, "Formato B", 1, fecha);
        entityManager.flush();
        var raiz = NodoFiltro.predicado("repositorioId", FiltroOperador.ES, dos.toString());

        // Act
        var resultado = adapter().consultarParaAsesor(criterio(raiz, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getContent()).extracting("id").containsExactly(esperada);
    }

    @Test
    void debeFiltrarPorNumeroVersion_cuandoHayCoincidencia() {
        // Arrange
        var repo = sembrarRepositorio("Repo uno");
        var fecha = Instant.parse("2026-10-20T10:00:00Z");
        sembrarVersion(repo, "Formato A", 1, fecha);
        var esperada = sembrarVersion(repo, "Formato A", 3, fecha);
        entityManager.flush();
        var raiz = NodoFiltro.predicado("numeroVersion", FiltroOperador.ES, "3");

        // Act
        var resultado = adapter().consultarParaAsesor(criterio(raiz, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getContent()).extracting("id").containsExactly(esperada);
        assertThat(resultado.getContent().get(0).numeroVersion()).isEqualTo(3);
    }

    @Test
    void debeFiltrarPorFechaCreacion_cuandoHayCoincidencia() {
        // Arrange
        var repo = sembrarRepositorio("Repo uno");
        sembrarVersion(repo, "Formato A", 1, Instant.parse("2026-09-01T10:00:00Z"));
        var esperada = sembrarVersion(repo, "Formato B", 1, Instant.parse("2026-10-20T10:00:00Z"));
        entityManager.flush();
        var raiz = NodoFiltro.predicado("fechaCreacion", FiltroOperador.MAYOR_QUE, "2026-10-01T00:00:00");

        // Act
        var resultado = adapter().consultarParaAsesor(criterio(raiz, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getContent()).extracting("id").containsExactly(esperada);
    }

    @Test
    void debePaginar_cuandoTamanioEsMenorAlTotal() {
        // Arrange
        var repo = sembrarRepositorio("Repo uno");
        var fecha = Instant.parse("2026-10-20T10:00:00Z");
        sembrarVersion(repo, "Formato A", 1, fecha);
        sembrarVersion(repo, "Formato B", 1, fecha);
        sembrarVersion(repo, "Formato C", 1, fecha);
        entityManager.flush();
        var orden = List.of(SortOrder.of("nombreDocumento", SortDirection.ASC));

        // Act
        var resultado = adapter().consultarParaAsesor(criterio(null, 1, 2, orden));

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(3L);
        assertThat(resultado.getContent()).extracting("nombreDocumento").containsExactly("Formato C");
    }

    @Test
    void debeOrdenarPorNombreRepositorio_cuandoElCriterioLoPide() {
        // Arrange
        var zeta = sembrarRepositorio("Zeta");
        var alfa = sembrarRepositorio("Alfa");
        var fecha = Instant.parse("2026-10-20T10:00:00Z");
        var deZeta = sembrarVersion(zeta, "Formato A", 1, fecha);
        var deAlfa = sembrarVersion(alfa, "Formato B", 1, fecha);
        entityManager.flush();
        var orden = List.of(SortOrder.of("nombreRepositorio", SortDirection.ASC));

        // Act
        var resultado = adapter().consultarParaAsesor(criterio(null, 0, 10, orden));

        // Assert
        assertThat(resultado.getContent()).extracting("id").containsExactly(deAlfa, deZeta);
    }

    @Test
    void debeDevolverVacio_cuandoNoHayVersiones() {
        // Act
        var resultado = adapter().consultarParaAsesor(criterio(null, 0, 10, List.of()));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }
}
