package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.entity.EstudianteFichaPerfilJpaEntity;
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
class EstudianteFichaPerfilQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EstudianteFichaPerfilQueryRepository repository;

    private EstudianteFichaPerfilQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstudianteFichaPerfilQueryOutputAdapter(repository);
    }

    @Test
    void debeRetornarEstudiantesOrdenadosPorNombre_cuandoLaFichaTieneVinculos() {
        // Arrange
        var ficha = UUID.randomUUID();
        var pedro = persistirEstudiante("EST-001", "Pedro Zapata", "pedro@uco.edu.co");
        var ana = persistirEstudiante("EST-002", "Ana Ruiz", "ana@uco.edu.co");
        vincular(ficha, pedro);
        vincular(ficha, ana);
        entityManager.flush();

        // Act
        List<EstudianteFichaPerfilReadModel> resultado = adapter.consultarPorFicha(ficha);

        // Assert
        assertThat(resultado)
                .extracting(EstudianteFichaPerfilReadModel::nombre)
                .containsExactly("Ana Ruiz", "Pedro Zapata");
    }

    @Test
    void debeMapearNombreYEmailDesdeLaTablaEstudiante() {
        // Arrange
        var ficha = UUID.randomUUID();
        var estudiante = persistirEstudiante("EST-010", "Ana Ruiz", "ana.ruiz@uco.edu.co");
        var vinculoId = vincular(ficha, estudiante);
        entityManager.flush();

        // Act
        List<EstudianteFichaPerfilReadModel> resultado = adapter.consultarPorFicha(ficha);

        // Assert
        assertThat(resultado).singleElement().satisfies(rm -> {
            assertThat(rm.id()).isEqualTo(vinculoId);
            assertThat(rm.fichaPerfilId()).isEqualTo(ficha);
            assertThat(rm.estudianteId()).isEqualTo(estudiante);
            assertThat(rm.nombre()).isEqualTo("Ana Ruiz");
            assertThat(rm.email()).isEqualTo("ana.ruiz@uco.edu.co");
        });
    }

    @Test
    void debeFiltrarSoloLosVinculosDeLaFichaSolicitada() {
        // Arrange
        var fichaA = UUID.randomUUID();
        var fichaB = UUID.randomUUID();
        var e1 = persistirEstudiante("EST-020", "Ana Ruiz", "ana20@uco.edu.co");
        var e2 = persistirEstudiante("EST-021", "Luis Paz", "luis21@uco.edu.co");
        vincular(fichaA, e1);
        vincular(fichaB, e2);
        entityManager.flush();

        // Act
        List<EstudianteFichaPerfilReadModel> resultado = adapter.consultarPorFicha(fichaA);

        // Assert
        assertThat(resultado)
                .extracting(EstudianteFichaPerfilReadModel::estudianteId)
                .containsExactly(e1);
    }

    @Test
    void debeRetornarVacio_cuandoLaFichaNoTieneVinculos() {
        // Arrange
        var ficha = UUID.randomUUID();
        var estudiante = persistirEstudiante("EST-030", "Ana Ruiz", "ana30@uco.edu.co");
        vincular(UUID.randomUUID(), estudiante);
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.consultarPorFicha(ficha)).isEmpty();
    }

    @Test
    void debeRetornarVacio_cuandoLaFichaNoExiste() {
        // Act & Assert
        assertThat(adapter.consultarPorFicha(UUID.randomUUID())).isEmpty();
    }

    @Test
    void debeRetornarSoloElOtroEstudiante_cuandoLaFichaTieneDosVinculados() {
        // Arrange
        var ficha = UUID.randomUUID();
        var solicitante = persistirEstudiante("EST-100", "Pedro Zapata", "pedro100@uco.edu.co");
        var companero = persistirEstudiante("EST-101", "Ana Ruiz", "ana101@uco.edu.co");
        vincular(ficha, solicitante);
        vincular(ficha, companero);
        entityManager.flush();

        // Act
        List<EstudianteFichaPerfilReadModel> resultado =
                adapter.consultarCompanerosPorFichaYEstudiante(ficha, solicitante);

        // Assert
        assertThat(resultado)
                .singleElement()
                .satisfies(rm -> assertThat(rm.estudianteId()).isEqualTo(companero));
    }

    @Test
    void debeRetornarVacio_cuandoElSolicitanteEsElUnicoVinculado() {
        // Arrange
        var ficha = UUID.randomUUID();
        var solicitante = persistirEstudiante("EST-110", "Ana Ruiz", "ana110@uco.edu.co");
        vincular(ficha, solicitante);
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.consultarCompanerosPorFichaYEstudiante(ficha, solicitante)).isEmpty();
    }

    @Test
    void debeRetornarVacio_cuandoElSolicitanteNoEstaVinculadoAEsaFicha() {
        // Arrange
        var ficha = UUID.randomUUID();
        var otroEstudiante = persistirEstudiante("EST-120", "Ana Ruiz", "ana120@uco.edu.co");
        vincular(ficha, otroEstudiante);
        var solicitanteAjeno = persistirEstudiante("EST-121", "Pedro Zapata", "pedro121@uco.edu.co");
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.consultarCompanerosPorFichaYEstudiante(ficha, solicitanteAjeno)).isEmpty();
    }

    @Test
    void debeRetornarLosOtrosDosOrdenadosPorNombre_cuandoLaFichaTieneCupoMaximo() {
        // Arrange
        var ficha = UUID.randomUUID();
        var solicitante = persistirEstudiante("EST-130", "Luis Paz", "luis130@uco.edu.co");
        var zapata = persistirEstudiante("EST-131", "Pedro Zapata", "pedro131@uco.edu.co");
        var ruiz = persistirEstudiante("EST-132", "Ana Ruiz", "ana132@uco.edu.co");
        vincular(ficha, solicitante);
        vincular(ficha, zapata);
        vincular(ficha, ruiz);
        entityManager.flush();

        // Act
        List<EstudianteFichaPerfilReadModel> resultado =
                adapter.consultarCompanerosPorFichaYEstudiante(ficha, solicitante);

        // Assert
        assertThat(resultado)
                .extracting(EstudianteFichaPerfilReadModel::nombre)
                .containsExactly("Ana Ruiz", "Pedro Zapata");
    }

    @Test
    void debeIncluirAlEstudianteDadoDeBajaMarcadoNoVigente_cuandoConsultaElCoordinador() {
        // Arrange
        var ficha = UUID.randomUUID();
        var vigente = persistirEstudiante("EST-140", "Ana Ruiz", "ana140@uco.edu.co");
        var dadoDeBaja = persistirEstudiante("EST-141", "Pedro Zapata", "pedro141@uco.edu.co", Instant.now());
        vincular(ficha, vigente);
        vincular(ficha, dadoDeBaja);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarPorFicha(ficha);

        // Assert
        assertThat(resultado)
                .extracting(EstudianteFichaPerfilReadModel::estudianteId, EstudianteFichaPerfilReadModel::vigente)
                .containsExactly(tuple(vigente, true), tuple(dadoDeBaja, false));
    }

    @Test
    void debeRetornarSoloVigentes_cuandoSeConsultanLosVigentesDeLaFicha() {
        // Arrange
        var ficha = UUID.randomUUID();
        var vigente = persistirEstudiante("EST-160", "Ana Ruiz", "ana160@uco.edu.co");
        var dadoDeBaja = persistirEstudiante("EST-161", "Pedro Zapata", "pedro161@uco.edu.co", Instant.now());
        vincular(ficha, vigente);
        vincular(ficha, dadoDeBaja);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarVigentesPorFicha(ficha);

        // Assert
        assertThat(resultado)
                .extracting(EstudianteFichaPerfilReadModel::estudianteId)
                .containsExactly(vigente);
    }

    @Test
    void debeExcluirAlCompaneroDadoDeBaja_cuandoConsultaElEstudiante() {
        // Arrange
        var ficha = UUID.randomUUID();
        var solicitante = persistirEstudiante("EST-150", "Luis Paz", "luis150@uco.edu.co");
        var vigente = persistirEstudiante("EST-151", "Ana Ruiz", "ana151@uco.edu.co");
        var dadoDeBaja = persistirEstudiante("EST-152", "Pedro Zapata", "pedro152@uco.edu.co", Instant.now());
        vincular(ficha, solicitante);
        vincular(ficha, vigente);
        vincular(ficha, dadoDeBaja);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarCompanerosPorFichaYEstudiante(ficha, solicitante);

        // Assert
        assertThat(resultado)
                .extracting(EstudianteFichaPerfilReadModel::estudianteId)
                .containsExactly(vigente);
    }

    @Test
    void debeRetornarTodosLosVigentesDeSusFichasIncluidoElPropio_cuandoPerteneceAVariasFichas() {
        // Arrange
        var fichaUno = UUID.randomUUID();
        var fichaDos = UUID.randomUUID();
        var yo = persistirEstudiante("EST-200", "Luis Paz", "luis200@uco.edu.co");
        var companero = persistirEstudiante("EST-201", "Ana Ruiz", "ana201@uco.edu.co");
        var otro = persistirEstudiante("EST-202", "Pedro Zapata", "pedro202@uco.edu.co");
        vincular(fichaUno, yo);
        vincular(fichaUno, companero);
        vincular(fichaDos, yo);
        vincular(fichaDos, otro);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarVigentesDeFichasDelEstudiante(yo);

        // Assert
        assertThat(resultado)
                .extracting(EstudianteFichaPerfilReadModel::fichaPerfilId, EstudianteFichaPerfilReadModel::estudianteId)
                .containsExactlyInAnyOrder(
                        tuple(fichaUno, yo), tuple(fichaUno, companero),
                        tuple(fichaDos, yo), tuple(fichaDos, otro));
    }

    @Test
    void debeExcluirFichasAjenas_cuandoConsultaLasFichasDelEstudiante() {
        // Arrange
        var fichaPropia = UUID.randomUUID();
        var fichaAjena = UUID.randomUUID();
        var yo = persistirEstudiante("EST-210", "Luis Paz", "luis210@uco.edu.co");
        var otro = persistirEstudiante("EST-211", "Ana Ruiz", "ana211@uco.edu.co");
        vincular(fichaPropia, yo);
        vincular(fichaAjena, otro);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarVigentesDeFichasDelEstudiante(yo);

        // Assert
        assertThat(resultado)
                .extracting(EstudianteFichaPerfilReadModel::fichaPerfilId)
                .containsExactly(fichaPropia);
    }

    @Test
    void debeExcluirCompanerosDadosDeBaja_cuandoConsultaLasFichasDelEstudiante() {
        // Arrange
        var ficha = UUID.randomUUID();
        var yo = persistirEstudiante("EST-220", "Luis Paz", "luis220@uco.edu.co");
        var dadoDeBaja = persistirEstudiante("EST-221", "Pedro Zapata", "pedro221@uco.edu.co", Instant.now());
        vincular(ficha, yo);
        vincular(ficha, dadoDeBaja);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarVigentesDeFichasDelEstudiante(yo);

        // Assert
        assertThat(resultado)
                .extracting(EstudianteFichaPerfilReadModel::estudianteId)
                .containsExactly(yo);
    }

    @Test
    void debeRetornarVacio_cuandoElEstudianteFueDadoDeBaja() {
        // Arrange
        var ficha = UUID.randomUUID();
        var dadoDeBaja = persistirEstudiante("EST-230", "Luis Paz", "luis230@uco.edu.co", Instant.now());
        var companero = persistirEstudiante("EST-231", "Ana Ruiz", "ana231@uco.edu.co");
        vincular(ficha, dadoDeBaja);
        vincular(ficha, companero);
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.consultarVigentesDeFichasDelEstudiante(dadoDeBaja)).isEmpty();
    }

    @Test
    void debeRetornarVacio_cuandoElEstudianteNoPerteneceANingunaFicha() {
        // Act & Assert
        assertThat(adapter.consultarVigentesDeFichasDelEstudiante(UUID.randomUUID())).isEmpty();
    }

    @Test
    void debeOrdenarPorNombreAscendente_cuandoConsultaLasFichasDelEstudiante() {
        // Arrange
        var ficha = UUID.randomUUID();
        var zoe = persistirEstudiante("EST-240", "Zoe Mora", "zoe240@uco.edu.co");
        var ana = persistirEstudiante("EST-241", "Ana Ruiz", "ana241@uco.edu.co");
        var luis = persistirEstudiante("EST-242", "Luis Paz", "luis242@uco.edu.co");
        vincular(ficha, zoe);
        vincular(ficha, ana);
        vincular(ficha, luis);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarVigentesDeFichasDelEstudiante(zoe);

        // Assert
        assertThat(resultado)
                .extracting(EstudianteFichaPerfilReadModel::nombre)
                .containsExactly("Ana Ruiz", "Luis Paz", "Zoe Mora");
    }

    private UUID persistirEstudiante(String identificador, String nombre, String email) {
        return persistirEstudiante(identificador, nombre, email, null);
    }

    private UUID persistirEstudiante(String identificador, String nombre, String email, Instant eliminadoEn) {
        var estudiante = EstudianteJpaEntity.builder()
                .id(UUID.randomUUID())
                .identificador(identificador)
                .nombre(nombre)
                .email(email)
                .ocurridoEn(Instant.now())
                .eliminadoEn(eliminadoEn)
                .build();
        entityManager.persist(estudiante);
        return estudiante.getId();
    }

    private UUID vincular(UUID fichaPerfilId, UUID estudianteId) {
        var vinculo = EstudianteFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaPerfilId)
                .estudianteId(estudianteId)
                .build();
        entityManager.persist(vinculo);
        return vinculo.getId();
    }
}
