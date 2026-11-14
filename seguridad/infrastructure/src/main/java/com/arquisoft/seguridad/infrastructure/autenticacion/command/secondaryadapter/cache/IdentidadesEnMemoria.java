package com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.cache;

import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.model.ResolucionSesion;
import com.arquisoft.shared.util.UtilObjeto;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

public final class IdentidadesEnMemoria {

    private record Entrada(ResolucionSesion.Resuelta identidad, Instant expiraEn) {}

    private final ReentrantLock cerrojo = new ReentrantLock();
    private final Map<String, Entrada> entradas;
    private final Duration ttl;
    private final Clock reloj;

    public IdentidadesEnMemoria(int maxEntradas, Duration ttl, Clock reloj) {
        this.ttl = ttl;
        this.reloj = reloj;
        this.entradas = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Entrada> masAntigua) {
                return size() > maxEntradas;
            }
        };
    }

    public Optional<ResolucionSesion.Resuelta> obtener(String huella) {
        cerrojo.lock();
        try {
            var entrada = entradas.get(huella);
            if (UtilObjeto.esNulo(entrada)) {
                return Optional.empty();
            }
            if (!reloj.instant().isBefore(entrada.expiraEn())) {
                entradas.remove(huella);
                return Optional.empty();
            }
            return Optional.of(entrada.identidad());
        } finally {
            cerrojo.unlock();
        }
    }

    public void guardar(String huella, ResolucionSesion.Resuelta identidad) {
        cerrojo.lock();
        try {
            entradas.put(huella, new Entrada(identidad, reloj.instant().plus(ttl)));
        } finally {
            cerrojo.unlock();
        }
    }

    public int tamanio() {
        cerrojo.lock();
        try {
            return entradas.size();
        } finally {
            cerrojo.unlock();
        }
    }
}
