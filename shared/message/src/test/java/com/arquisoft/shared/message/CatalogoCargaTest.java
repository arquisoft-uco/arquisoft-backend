package com.arquisoft.shared.message;

import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.shared.message.prueba.CatalogoMensajesPrueba;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Red de seguridad del catálogo cargado en Redis.
 *
 * <p>Los textos viven fuera del código, en {@code catalogo/*.properties}, y con el fail-fast de
 * arranque una clave declarada sin texto no degrada un mensaje: impide levantar el despliegue. Esta
 * es la compuerta que lo detecta en el build, donde el diagnóstico es inmediato y gratis.
 *
 * <p>Los enums de {@code key.*} se descubren escaneando el árbol de fuentes, no listándolos a mano:
 * la lista manual está en producción ({@link ClavesCatalogo}) porque el arranque la necesita, y este
 * escaneo es precisamente lo que garantiza que esté completa.
 */
class CatalogoCargaTest {

    private static final String SEPARADOR = Pattern.quote(".");
    private static final int SEGMENTO_TIPO = 3;

    // El cuarto segmento de la clave. Los cuatro primeros son los nombres de CategoriaMensaje, que
    // deciden el texto de respaldo; los otros son segmentos propios de un grupo de claves cuyo
    // respaldo genérico es el de error, y están aquí para que la lista sea exhaustiva y cerrada.
    private static final Set<String> SEGMENTOS_ACEPTADOS =
            Set.of("error", "validacion", "log", "api", "tipo", "valor",
                    "asunto", "cuerpo", "pie", "mensaje");

    private static final Path FUENTES_CLAVES = Path.of("src/main/java/com/arquisoft/shared/message/key");
    private static final Path RAIZ_PROYECTO = Path.of("../..");
    private static final Set<String> DIRECTORIOS_IGNORADOS = Set.of("build", ".git", ".gradle", "worktrees");
    private static final String EXTENSION_FUENTE = ".java";
    private static final String PAQUETE_CLAVES = "com.arquisoft.shared.message.key";
    private static final String SUFIJO_FUENTE = "Key.java";
    private static final String RUTA_CATALOGO = "/catalogo/%s.properties";

    // Cualquier barra invertida que no sea la de \n. Es lo unico en lo que printf '%b' (cargar.sh)
    // y Properties.load (los tests) coinciden; el resto de escapes divergen o solo existen en uno.
    private static final Pattern ESCAPE_NO_SOPORTADO = Pattern.compile("\\\\(?!n)");

    private final CatalogoMensajesPrueba catalogo = CatalogoMensajesPrueba.porDefecto();

    @Test
    @DisplayName("el escaneo encuentra los enums de claves — si fallara, el resto pasaría en vacío")
    void debeEncontrarLosEnumsDeClaves_cuandoSeEscaneaElPaquete() {
        assertThat(clavesDeclaradas())
                .as("sin claves descubiertas las demás pruebas serían tautológicas")
                .isNotEmpty();
    }

    @Test
    @DisplayName("ClavesCatalogo.TODAS registra todos los enums que existen")
    void debeRegistrarTodosLosEnums_cuandoSeComparaConElEscaneo() {
        Set<Class<?>> escaneados = new LinkedHashSet<>(enumsDeClaves());
        Set<Class<?>> registrados = new LinkedHashSet<>(ClavesCatalogo.ENUMS);

        assertThat(escaneados)
                .as("enums de key/ que ClavesCatalogo.ENUMS no registra: el arranque no pediría "
                        + "sus claves a Redis y el fail-fast las dejaría pasar sin texto")
                .containsExactlyInAnyOrderElementsOf(registrados);
    }

    @Test
    @DisplayName("toda clave declarada resuelve a un texto del catálogo")
    void debeResolverTodaClaveDeclarada_cuandoSeRecorrenLosEnums() {
        List<String> sinTexto = clavesDeclaradas().stream()
                .filter(clave -> !catalogo.contiene(clave))
                .map(ClaveMensaje::clave)
                .toList();

        assertThat(sinTexto)
                .as("claves declaradas en Java sin línea en catalogo/*.properties: con el fail-fast "
                        + "de arranque, cada una impide levantar la aplicación")
                .isEmpty();
    }

    @Test
    @DisplayName("la aridad declarada coincide con los marcadores del patrón")
    void debeCoincidirLaAridad_cuandoSeCuentanLosMarcadores() {
        List<String> discrepancias = new ArrayList<>();

        for (ClaveMensaje clave : clavesDeclaradas()) {
            if (!catalogo.contiene(clave)) {
                continue;
            }

            int encontrados = AridadClave.marcadores(clave, catalogo.patron(clave));
            if (encontrados != clave.parametros()) {
                discrepancias.add("%s (declara %d, el patrón tiene %d)"
                        .formatted(clave.clave(), clave.parametros(), encontrados));
            }
        }

        assertThat(discrepancias)
                .as("String.formatted lanza si faltan argumentos y los ignora en silencio si sobran, y "
                        + "SLF4J no protesta en ninguno de los dos casos: se come los sobrantes e "
                        + "imprime el {} literal cuando faltan. La aridad declarada es lo que convierte "
                        + "los cuatro casos en un fallo del build")
                .isEmpty();
    }

    @Test
    @DisplayName("el cuarto segmento de toda clave está en la lista de segmentos aceptados")
    void debeTenerSegmentoAceptado_cuandoSeParseaLaClave() {
        List<String> inesperados = clavesDeclaradas().stream()
                .map(ClaveMensaje::clave)
                .filter(clave -> !SEGMENTOS_ACEPTADOS.contains(segmentoDeTipo(clave)))
                .toList();

        assertThat(inesperados)
                .as("CategoriaMensaje.desde degrada en silencio a ERROR cuando no reconoce el "
                        + "cuarto segmento, así que un 'erorr' mal escrito pasaría inadvertido y su "
                        + "texto de respaldo sería el equivocado. Un segmento nuevo tiene que ser "
                        + "una decisión: o se le da su CategoriaMensaje, o se añade a esta lista")
                .isEmpty();
    }

    @Test
    @DisplayName("el primer segmento de toda clave es un contexto declarado")
    void debeCoincidirElPrefijoConElContexto_cuandoSeRecorrenLasClaves() {
        List<String> inesperados = clavesDeclaradas().stream()
                .map(ClaveMensaje::clave)
                .filter(clave -> !ContextosCatalogo.TODOS.contains(clave.split(SEPARADOR, 2)[0]))
                .toList();

        assertThat(inesperados)
                .as("el script de carga recorre ContextosCatalogo.TODOS: una clave con otro prefijo "
                        + "no tendría archivo donde vivir y el fail-fast la echaría en falta")
                .isEmpty();
    }

    @Test
    @DisplayName("ningún texto del catálogo queda huérfano — todo par tiene su clave en Java")
    void debeTenerClave_cuandoSeRecorrenLosTextosDelCatalogo() {
        Set<String> declaradas = new LinkedHashSet<>();
        clavesDeclaradas().forEach(clave -> declaradas.add(clave.clave()));

        List<String> huerfanas = new ArrayList<>();
        for (String contexto : ContextosCatalogo.TODOS) {
            leer(RUTA_CATALOGO.formatted(contexto)).stringPropertyNames().stream()
                    .filter(clave -> !declaradas.contains(clave))
                    .forEach(huerfanas::add);
        }

        assertThat(huerfanas)
                .as("textos que se cargan en Redis y ninguna clave de Java referencia")
                .isEmpty();
    }

    @Test
    @DisplayName("ninguna clave está declarada en dos archivos del catálogo")
    void debeNoDeclararClaveEnDosArchivos_cuandoSeComparanTodos() {
        Map<String, List<String>> archivosPorClave = new LinkedHashMap<>();

        for (String contexto : ContextosCatalogo.TODOS) {
            leer(RUTA_CATALOGO.formatted(contexto)).stringPropertyNames().forEach(clave ->
                    archivosPorClave.computeIfAbsent(clave, k -> new ArrayList<>()).add(contexto));
        }

        List<String> duplicadas = archivosPorClave.entrySet().stream()
                .filter(entrada -> entrada.getValue().size() > 1)
                .map(entrada -> entrada.getKey() + " → " + entrada.getValue())
                .toList();

        assertThat(duplicadas)
                .as("el script de carga emite un SET por par, así que el último archivo gana en "
                        + "silencio y el otro texto no se usa nunca")
                .isEmpty();
    }

    @Test
    @DisplayName("formatear sustituye los parámetros del patrón")
    void debeSustituirParametros_cuandoElPatronLosDeclara() {
        String resultado = catalogo.formatear(FichaPerfilKey.ERROR_TITULO_DUPLICADO, "Mi proyecto");

        assertThat(resultado).isEqualTo("El título ya existe: Mi proyecto");
    }

    @Test
    @DisplayName("los patrones de log conservan los marcadores {} de SLF4J")
    void debeConservarMarcadoresSlf4j_cuandoSeObtieneUnPatronDeLog() {
        String patron = catalogo.obtener(FichaPerfilKey.LOG_REGISTRADA);

        assertThat(patron).contains("{}");
    }

    @Test
    @DisplayName("El catálogo solo usa el escape \\n")
    void debeUsarSoloElEscapeDeSaltoDeLinea_cuandoSeLeeElTextoCrudo() {
        List<String> invalidos = new ArrayList<>();

        for (String contexto : ContextosCatalogo.TODOS) {
            String ruta = RUTA_CATALOGO.formatted(contexto);
            ESCAPE_NO_SOPORTADO.matcher(leerCrudo(ruta)).results()
                    .forEach(coincidencia -> invalidos.add(ruta + " -> " + coincidencia.group()));
        }

        assertThat(invalidos)
                .as("cargar.sh escribe el valor con printf '%%b', que interpreta los escapes de la "
                        + "shell, mientras que los tests leen el mismo archivo con Properties.load, "
                        + "que interpreta los de Java: los dos coinciden en \\n y divergen en el "
                        + "resto. Un texto multilínea se escribe con \\n; para cualquier otro "
                        + "carácter, ponlo literal en el .properties")
                .isEmpty();
    }

    // -------------------------------------------------------------------------

    @Test
    @DisplayName("ninguna clave declarada queda sin usar — toda clave la referencia algún .java")
    void debeReferenciarseEnElCodigo_cuandoSeRecorrenLasClavesDeclaradas() {
        Set<String> referencias = referenciasEnElCodigo();

        List<String> sinUso = clavesDeclaradas().stream()
                .filter(clave -> !referencias.contains(nombreDeConstante(clave)))
                .map(ClaveMensaje::clave)
                .toList();

        assertThat(sinUso)
                .as("una clave que nadie invoca sigue exigiendo su texto al fail-fast de arranque y "
                        + "se carga en Redis en cada despliegue, así que el catálogo no delata que "
                        + "sobra. Peor: cuando el comportamiento cambia —un filtro que pasa de "
                        + "fail-closed a fail-open y deja de emitir su 503— la clave del mensaje "
                        + "retirado queda viva y aparenta seguir describiendo lo que hace el código")
                .isEmpty();
    }

    @Test
    @DisplayName("el escaneo del código encuentra referencias — si fallara, el test anterior pasaría en vacío")
    void debeEncontrarReferenciasEnElCodigo_cuandoSeEscaneaElProyecto() {
        assertThat(referenciasEnElCodigo())
                .as("sin referencias descubiertas la prueba de claves sin uso sería tautológica")
                .isNotEmpty();
    }

    private static String segmentoDeTipo(String clave) {
        String[] segmentos = clave.split(SEPARADOR);
        return segmentos.length > SEGMENTO_TIPO ? segmentos[SEGMENTO_TIPO] : "";
    }


    private static String leerCrudo(String ruta) {
        try (InputStream entrada = CatalogoCargaTest.class.getResourceAsStream(ruta)) {
            assertThat(entrada).as("no está en el classpath de test: %s", ruta).isNotNull();

            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(ruta, e);
        }
    }
    private static Properties leer(String ruta) {
        try (InputStream entrada = CatalogoCargaTest.class.getResourceAsStream(ruta)) {
            assertThat(entrada).as("no está en el classpath de test: %s", ruta).isNotNull();

            var propiedades = new Properties();
            propiedades.load(new InputStreamReader(entrada, StandardCharsets.UTF_8));
            return propiedades;
        } catch (IOException e) {
            throw new IllegalStateException(ruta, e);
        }
    }

    private static String nombreDeConstante(ClaveMensaje clave) {
        return ((Enum<?>) clave).getDeclaringClass().getSimpleName() + "." + ((Enum<?>) clave).name();
    }

    private static Set<String> referenciasEnElCodigo() {
        var referencias = new LinkedHashSet<String>();
        Pattern uso = Pattern.compile("\\b([A-Za-z0-9_]+Key)\\.([A-Z][A-Z0-9_]*)\\b");

        try (Stream<Path> archivos = Files.walk(RAIZ_PROYECTO)) {
            archivos.filter(CatalogoCargaTest::esFuenteDelProyecto).forEach(ruta -> {
                try {
                    uso.matcher(Files.readString(ruta, StandardCharsets.UTF_8)).results()
                            .forEach(c -> referencias.add(c.group(1) + "." + c.group(2)));
                } catch (IOException e) {
                    throw new IllegalStateException(ruta.toString(), e);
                }
            });
        } catch (IOException e) {
            throw new IllegalStateException(RAIZ_PROYECTO.toString(), e);
        }

        return referencias;
    }

    private static boolean esFuenteDelProyecto(Path ruta) {
        if (!Files.isRegularFile(ruta) || !ruta.getFileName().toString().endsWith(EXTENSION_FUENTE)) {
            return false;
        }
        for (Path segmento : ruta) {
            if (DIRECTORIOS_IGNORADOS.contains(segmento.toString())) {
                return false;
            }
        }
        return true;
    }

    private List<ClaveMensaje> clavesDeclaradas() {
        List<ClaveMensaje> claves = new ArrayList<>();
        for (Class<?> tipo : enumsDeClaves()) {
            for (Object constante : tipo.getEnumConstants()) {
                claves.add((ClaveMensaje) constante);
            }
        }
        return claves;
    }

    // Se escanea el árbol de FUENTES y no el classpath: java-test-fixtures pone el jar del propio
    // módulo en el classpath de test, así que el paquete resuelve a una URL "jar:" — y, peor, a un jar
    // que puede estar obsoleto, con lo que un enum recién añadido no aparecería y el test pasaría en
    // falso. El directorio de fuentes es la verdad que este test tiene que vigilar.
    private List<Class<?>> enumsDeClaves() {
        assertThat(FUENTES_CLAVES)
                .as("el test se ejecuta desde el directorio del módulo shared:message")
                .isDirectory();

        try (Stream<Path> archivos = Files.walk(FUENTES_CLAVES)) {
            return archivos
                    .filter(ruta -> ruta.getFileName().toString().endsWith(SUFIJO_FUENTE))
                    .map(CatalogoCargaTest::nombreDeClase)
                    .map(CatalogoCargaTest::cargar)
                    .filter(Class::isEnum)
                    .filter(ClaveMensaje.class::isAssignableFrom)
                    .toList();
        } catch (IOException e) {
            throw new IllegalStateException(FUENTES_CLAVES.toString(), e);
        }
    }

    private static String nombreDeClase(Path archivo) {
        String relativa = FUENTES_CLAVES.relativize(archivo).toString().replace(File.separatorChar, '/');
        String sinExtension = relativa.substring(0, relativa.length() - ".java".length());
        return PAQUETE_CLAVES + "." + sinExtension.replace('/', '.');
    }

    private static Class<?> cargar(String nombre) {
        try {
            return Class.forName(nombre);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(nombre, e);
        }
    }
}
