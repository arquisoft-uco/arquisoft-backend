package com.arquisoft.shared.logger;

import com.arquisoft.shared.message.ClaveMensaje;

public interface AppLogger {

    void debug(String message, Object... args);

    void info(String message, Object... args);

    void warn(String message, Object... args);

    void error(String message, Object... args);

    void error(String message, Throwable cause, Object... args);

    // Las sobrecargas de ClaveMensaje son la forma preferente. La variante de String obliga a
    // resolver el texto en el call site — Mensajes.obtener(...) es un GET a Redis en cada llamada,
    // y Java evalúa el argumento antes de entrar al método, así que un debug() con el nivel apagado
    // pagaba igual el viaje de red para tirar el resultado. Pasando la clave, la resolución ocurre
    // dentro del logger y solo si el nivel está activo.
    void debug(ClaveMensaje clave, Object... args);

    void info(ClaveMensaje clave, Object... args);

    void warn(ClaveMensaje clave, Object... args);

    void error(ClaveMensaje clave, Object... args);

    void error(ClaveMensaje clave, Throwable cause, Object... args);
}
