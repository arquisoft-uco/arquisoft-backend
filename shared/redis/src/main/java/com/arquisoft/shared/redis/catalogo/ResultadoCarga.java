package com.arquisoft.shared.redis.catalogo;

import java.util.List;

/**
 * Lo que Redis tenía del catálogo en un intento de carga.
 *
 * <p>Separa los dos fallos posibles porque no se tratan igual. Una clave <em>faltante</em> no tiene
 * texto y su mensaje degrada al respaldo genérico; un <em>desajuste</em> de aridad sí tiene texto,
 * pero con un número de parámetros que no es el que el código va a pasarle, así que
 * {@code String.formatted} fallaría en el momento de usarlo — típicamente construyendo la respuesta
 * de un error que ya ocurrió.
 *
 * <p>Quien decide qué hacer con cada uno es el llamador: el arranque aborta ante cualquiera de los
 * dos, mientras que el monitor en caliente solo exige que no falten claves, porque a esas alturas
 * abortar no es una opción y un desajuste ya degrada de forma controlada.
 */
public record ResultadoCarga(int cargadas, int declaradas, List<String> faltantes, List<String> desajustes) {

    /**
     * Indica si todas las claves declaradas tienen texto.
     *
     * @return {@code true} si no falta ninguna
     */
    public boolean esCompleto() {
        return faltantes.isEmpty();
    }

    /**
     * Indica si todo patrón admite los parámetros que su clave declara.
     *
     * @return {@code true} si no hay desajustes de aridad
     */
    public boolean esConsistente() {
        return desajustes.isEmpty();
    }
}
