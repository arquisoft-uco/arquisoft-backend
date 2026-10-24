package com.arquisoft.autorizacion;

import com.arquisoft.seguridad.infrastructure.autorizacion.command.primaryadapter.web.ResolvedorPlantillaRuta;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.equivalencias.EquivalenciasProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.objenesis.ObjenesisStd;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

class CompletitudEquivalenciasAutorizacionTest {

    private static final String PREFIJO = "/api";
    private static final Path REPORTE = Path.of("build", "reports", "autorizacion", "recursos-pdp.csv");

    private static StandardEnvironment entorno;
    private static List<ResolvedorPlantillaRuta.Recurso> recursos;
    private static Set<String> filasDeLaTabla;
    private static List<String> patronesExentos;

    @BeforeAll
    static void cargar() throws IOException {
        entorno = new StandardEnvironment();
        var cargador = new YamlPropertySourceLoader();
        var resolvedor = new PathMatchingResourcePatternResolver();
        var recursosYml = new ArrayList<org.springframework.core.io.Resource>();
        recursosYml.add(resolvedor.getResource("classpath:application.yml"));
        recursosYml.add(resolvedor.getResource("classpath:autorizacion-equivalencias.yml"));
        recursosYml.addAll(List.of(resolvedor.getResources("classpath*:rutas-*.yml")));
        for (var recurso : recursosYml) {
            for (var fuente : cargador.load(recurso.getFilename() + "@" + recurso.getURL(), recurso)) {
                entorno.getPropertySources().addLast(fuente);
            }
        }

        var equivalencias = new Binder(ConfigurationPropertySources.from(entorno.getPropertySources()))
                .bind("security.authorization.role-table", EquivalenciasProperties.class)
                .orElseThrow(() -> new IllegalStateException("tabla de equivalencias vacia"));
        filasDeLaTabla = new TreeSet<>(equivalencias.entries().stream()
                .map(EquivalenciasProperties.Entrada::clave).toList());

        patronesExentos = new ArrayList<>();
        patronesExentos.addAll(lista("security.authorization.public-paths"));
        patronesExentos.addAll(lista("security.authorization.authenticated-paths"));
        var base = entorno.resolvePlaceholders("${rutas.seguridad.auth.base}");
        for (var accion : List.of("login", "refresh", "validate")) {
            patronesExentos.add(base + entorno.resolvePlaceholders("${rutas.seguridad.auth." + accion + "}"));
        }

        recursos = new ResolvedorPlantillaRuta(mapeoDeLosControladores(), "template", "").recursos();
    }

    @Test
    void debePasar_cuandoTodosLosMapeosTienenFilaOEstanExentos() {
        // Act
        var sinFila = endpointsSinFila(recursos, filasDeLaTabla, patronesExentos);
        var huerfanas = filasHuerfanas(recursos, filasDeLaTabla);

        // Assert
        assertThat(sinFila).as("endpoints sin fila en autorizacion-equivalencias.yml").isEmpty();
        assertThat(huerfanas).as("filas sin mapeo").isEmpty();
    }

    @Test
    void debeFallarNombrandoElEndpoint_cuandoFaltaFila() {
        // Arrange
        var sinUna = new TreeSet<>(filasDeLaTabla);
        var quitada = sinUna.first();
        sinUna.remove(quitada);

        // Act
        var sinFila = endpointsSinFila(recursos, sinUna, patronesExentos);

        // Assert
        assertThat(sinFila).contains(quitada);
    }

    @Test
    void debeFallar_cuandoHayFilaHuerfana() {
        // Arrange
        var conHuerfana = new TreeSet<>(filasDeLaTabla);
        conHuerfana.add("GET /api/no-existe/{id}");

        // Act
        var huerfanas = filasHuerfanas(recursos, conHuerfana);

        // Assert
        assertThat(huerfanas).containsExactly("GET /api/no-existe/{id}");
    }

    @Test
    void debeGenerarElCsvDeRecursos() throws IOException {
        // Arrange
        var lineas = new ArrayList<String>();
        lineas.add("METODO;RUTA");
        recursos.stream()
                .filter(recurso -> !esExento(PREFIJO + recurso.plantilla(), patronesExentos))
                .map(recurso -> recurso.metodo() + ";" + PREFIJO + recurso.plantilla())
                .sorted()
                .forEach(lineas::add);

        // Act
        Files.createDirectories(REPORTE.getParent());
        Files.write(REPORTE, lineas);

        // Assert
        assertThat(Files.readAllLines(REPORTE)).hasSizeGreaterThan(1).first().isEqualTo("METODO;RUTA");
    }

    static List<String> endpointsSinFila(List<ResolvedorPlantillaRuta.Recurso> recursos, Set<String> filas,
                                         List<String> exentos) {
        return recursos.stream()
                .map(recurso -> recurso.metodo() + " " + PREFIJO + recurso.plantilla())
                .filter(clave -> !filas.contains(clave))
                .filter(clave -> !esExento(clave.substring(clave.indexOf(' ') + 1), exentos))
                .sorted()
                .toList();
    }

    static List<String> filasHuerfanas(List<ResolvedorPlantillaRuta.Recurso> recursos, Set<String> filas) {
        var claves = recursos.stream()
                .map(recurso -> recurso.metodo() + " " + PREFIJO + recurso.plantilla())
                .toList();
        return filas.stream().filter(fila -> !claves.contains(fila)).sorted().toList();
    }

    private static boolean esExento(String ruta, List<String> exentos) {
        var comparador = new AntPathMatcher();
        return exentos.stream().anyMatch(patron ->
                comparador.match(patron, ruta) || comparador.match(PREFIJO + patron, ruta));
    }

    private static List<String> lista(String clave) {
        var valor = entorno.resolvePlaceholders(entorno.getProperty(clave, ""));
        return List.of(valor.split(",")).stream().map(String::trim).filter(texto -> !texto.isEmpty()).toList();
    }

    private static RequestMappingHandlerMapping mapeoDeLosControladores() {
        var contexto = new GenericApplicationContext();
        contexto.refresh();
        var mapeo = new MapeoDePrueba();
        mapeo.setApplicationContext(contexto);
        mapeo.setEmbeddedValueResolver(entorno::resolvePlaceholders);
        mapeo.afterPropertiesSet();

        var buscador = new ClassPathScanningCandidateComponentProvider(false);
        buscador.addIncludeFilter(new AnnotationTypeFilter(RestController.class));
        var instanciador = new ObjenesisStd();
        for (BeanDefinition definicion : buscador.findCandidateComponents("com.arquisoft")) {
            try {
                var clase = Class.forName(definicion.getBeanClassName());
                mapeo.registrar(instanciador.newInstance(clase));
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }
        return mapeo;
    }

    private static final class MapeoDePrueba extends RequestMappingHandlerMapping {

        void registrar(Object controlador) {
            detectHandlerMethods(controlador);
        }
    }
}
