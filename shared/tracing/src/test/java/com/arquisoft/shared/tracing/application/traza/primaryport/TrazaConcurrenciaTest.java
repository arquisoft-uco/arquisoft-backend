package com.arquisoft.shared.tracing.application.traza.primaryport;

import com.arquisoft.shared.tracing.application.traza.primaryport.impl.GestorTrazaImpl;
import com.arquisoft.shared.tracing.domain.traza.model.SolicitudTraza;
import com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc.MdcContextoDiagnosticoOutputAdapter;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class TrazaConcurrenciaTest {

    private static final int TAREAS = 2_000;

    private final GestorTraza gestor =
            new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false);

    @Test
    void debeAislarElContextoPorHilo_cuandoMilesDeHilosVirtualesConcurren() throws Exception {
        // Arrange
        var fallos = new ConcurrentLinkedQueue<String>();
        var listos = new CountDownLatch(TAREAS);

        // Act
        try (ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, TAREAS).forEach(i -> pool.execute(() -> {
                String esperada = "correlacion-" + i;
                try (var alcance = gestor.abrir(
                        SolicitudTraza.paraHttp(esperada, null, "203.0.113.25", "GET", "/api/" + i))) {

                    Thread.yield();
                    if (!esperada.equals(gestor.correlacionActual())) {
                        fallos.add(esperada + " vio " + gestor.correlacionActual());
                    }
                    gestor.registrarUsuario("usuario-" + i);
                    Thread.yield();
                    if (!("usuario-" + i).equals(gestor.usuarioActual())) {
                        fallos.add("usuario de " + i + " contaminado");
                    }
                    if (!esperada.equals(alcance.correlacionId())) {
                        fallos.add("alcance de " + i + " incoherente");
                    }
                } catch (RuntimeException ex) {
                    fallos.add("excepcion en " + i + ": " + ex);
                } finally {
                    listos.countDown();
                }
            }));
            assertThat(listos.await(60, TimeUnit.SECONDS)).isTrue();
        }

        // Assert
        assertThat(fallos).isEmpty();
    }

    @Test
    void debeDejarLimpioElHilo_cuandoElPoolDePlataformaLoReutiliza() throws Exception {
        // Arrange — los listeners de RabbitMQ corren sobre hilos de plataforma reutilizados
        var contaminados = new ConcurrentLinkedQueue<String>();
        var listos = new CountDownLatch(TAREAS);

        // Act
        ExecutorService pool = Executors.newFixedThreadPool(4);
        try {
            IntStream.range(0, TAREAS).forEach(i -> pool.execute(() -> {
                try {
                    var previo = MDC.getCopyOfContextMap();
                    if (previo != null && !previo.isEmpty()) {
                        contaminados.add("tarea " + i + " heredo " + previo);
                    }
                    try (var alcance = gestor.abrir(SolicitudTraza.paraEvento("evento-" + i, "cola-" + i, "padre-" + i))) {
                        gestor.registrarUsuario("usuario-" + i);
                        assertThat(alcance.correlacionId()).isEqualTo("evento-" + i);
                    }
                } finally {
                    listos.countDown();
                }
            }));
            assertThat(listos.await(60, TimeUnit.SECONDS)).isTrue();
        } finally {
            pool.shutdownNow();
        }

        // Assert
        assertThat(contaminados).isEmpty();
    }

    @Test
    void debeLimpiarElHilo_cuandoLaTareaLanzaUnaExcepcion() throws Exception {
        // Arrange
        var residuo = new AtomicReference<Object>();
        ExecutorService pool = Executors.newFixedThreadPool(1);

        // Act — la primera tarea revienta dentro del alcance; la segunda reutiliza el hilo
        try {
            pool.submit(() -> {
                try (var alcance = gestor.abrir(SolicitudTraza.paraEvento("evento-roto", "cola-rota", "padre-roto"))) {
                    assertThat(alcance.correlacionId()).isNotBlank();
                    throw new IllegalStateException("fallo simulado");
                }
            });
            pool.submit(() -> residuo.set(MDC.getCopyOfContextMap())).get(30, TimeUnit.SECONDS);
        } finally {
            pool.shutdownNow();
        }

        // Assert
        assertThat((java.util.Map<?, ?>) residuo.get()).isNullOrEmpty();
    }

    @Test
    void debeNoHeredarElContexto_cuandoSeLanzaUnHiloHijoDentroDelAlcance() throws Exception {
        // Arrange
        var visto = new AtomicReference<String>("no-ejecutado");

        // Act
        try (var alcance = gestor.abrir(SolicitudTraza.paraHttp("padre", null, "203.0.113.25", "GET", "/api"))) {
            assertThat(alcance.correlacionId()).isEqualTo("padre");
            Thread hijo = Thread.ofVirtual().start(() -> visto.set(gestor.correlacionActual()));
            hijo.join(TimeUnit.SECONDS.toMillis(10));
        }

        // Assert — Logback 1.3+ usa ThreadLocal, no InheritableThreadLocal: el hijo no hereda.
        // Es lo que evita fugas, y a la vez la razon de que el trabajo asincrono pierda el contexto.
        assertThat(visto.get()).isNull();
    }

    @Test
    void debeRestaurarCadaNivel_cuandoLosAlcancesSeAnidanEnParalelo() throws Exception {
        // Arrange
        var fallos = new ConcurrentLinkedQueue<String>();
        var listos = new CountDownLatch(500);

        // Act
        try (ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 500).forEach(i -> pool.execute(() -> {
                try (var externo = gestor.abrir(SolicitudTraza.paraHttp("ext-" + i, null, "1.1.1.1", "GET", "/x"))) {
                    try (var interno = gestor.abrir(SolicitudTraza.paraEvento("int-" + i, "cola-" + i, "padre-" + i))) {
                        Thread.yield();
                        if (!("int-" + i).equals(gestor.correlacionActual())) {
                            fallos.add("anidado " + i + " incorrecto");
                        }
                        assertThat(interno.correlacionId()).isEqualTo("int-" + i);
                    }
                    Thread.yield();
                    if (!("ext-" + i).equals(gestor.correlacionActual())) {
                        fallos.add("restauracion " + i + " incorrecta");
                    }
                    assertThat(externo.correlacionId()).isEqualTo("ext-" + i);
                } finally {
                    listos.countDown();
                }
            }));
            assertThat(listos.await(60, TimeUnit.SECONDS)).isTrue();
        }

        // Assert
        assertThat(List.copyOf(fallos)).isEmpty();
    }
}
