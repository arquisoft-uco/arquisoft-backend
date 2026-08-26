package com.arquisoft.shared.events;

import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;

public abstract class DomainEvent {

    private static final String FORMATO_TEMA = "{contexto}.{entidad}.{accion}";
    private static final String EJEMPLO_TEMA = "usuarios.usuario.creado";
    private static final Pattern PATRON_TEMA = Pattern.compile("^[a-z][a-z_]*\\.[a-z][a-z_]*\\.[a-z][a-z_]*$");
    private static final String ERROR_TEMA_INVALIDO =
            "El tema del evento '%s' no cumple el formato requerido '" + FORMATO_TEMA
            + "' (ej. '" + EJEMPLO_TEMA + "')";

    private final String idEvento;
    private final Instant ocurridoEn;
    private final String tipoEvento;
    private final String temaEvento;

    protected DomainEvent(String temaEvento, String tipoEvento) {
        validarTema(temaEvento);
        this.idEvento = UUID.randomUUID().toString();
        this.ocurridoEn = Instant.now();
        this.tipoEvento = tipoEvento;
        this.temaEvento = temaEvento;
    }

    private static void validarTema(String tema) {
        if (tema == null || !PATRON_TEMA.matcher(tema).matches()) {
            throw new IllegalArgumentException(ERROR_TEMA_INVALIDO.formatted(tema));
        }
    }

    public String getIdEvento() {
        return idEvento;
    }

    public Instant getOcurridoEn() {
        return ocurridoEn;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public final String getTemaEvento() {
        return temaEvento;
    }
}
