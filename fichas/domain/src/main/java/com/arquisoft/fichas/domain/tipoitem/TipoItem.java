package com.arquisoft.fichas.domain.tipoitem;

import com.arquisoft.fichas.domain.tipoitem.exception.TipoItemNoEncontradoException;
import com.arquisoft.shared.util.UtilEnum;

public enum TipoItem {

    OBJETIVO_GENERAL("Objetivo General"),
    OBJETIVO_ESPECIFICO("Objetivo Especifico"),
    ESTADO_DEL_ARTE("Estado Del Arte"),
    ANTECEDENTES("Antecedentes"),
    JUSTIFICACION("Justificacion"),
    REFERENCIAS("Referencias");

    private final String id;
    private final String nombre;

    TipoItem(String nombre) {
        this.id = this.name();
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public static TipoItem desde(String id) {
        return UtilEnum.desde(TipoItem.class, id)
                .orElseThrow(() -> new TipoItemNoEncontradoException(id));
    }

    public static boolean esValido(String id) {
        return UtilEnum.esValido(TipoItem.class, id);
    }
}
