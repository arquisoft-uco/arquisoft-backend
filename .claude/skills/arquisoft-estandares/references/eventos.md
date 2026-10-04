# Estándares de eventos: payload y logs

Referencia de `arquisoft-estandares`. Se carga cuando el plan tiene la sección 10 (Eventos
RabbitMQ): la HU publica o consume un evento.

## Payload de evento: campos fijos y prueba de contrato

Todo `{Evento}Payload` declara al menos `idEvento` (clave de idempotencia) y **`ocurridoEn`**
(`Instant`, instante del hecho en el origen), más lo suyo. Los dos viajan siempre en el JSON porque
`DomainEvent` los asigna; omitirlos del record los tira en silencio. `ocurridoEn` es lo que permite
descartar eventos viejos cuando llegan desordenados — ver `arquisoft-arquitectura/references/eventos.md` →
*Replicación entre contextos*.

Su prueba **instancia la configuración de producción**, no un mapper armado a mano:

```java
private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();
```

El productor serializa con ese mismo bean (`RabbitTemplate` usa `JacksonJsonMessageConverter(rabbitObjectMapper)`),
así que es lo único que prueba el contrato de verdad: un doble configurado a mano puede pasar el test
y fallar en el broker. Dos casos, y el segundo importa tanto como el primero:

1. Serializar una subclase real de `DomainEvent` y deserializarla en el payload — comprueba que los
   tipos sobreviven el viaje (un `Instant` incluye la precisión de nanosegundos).
2. Un JSON **sin** el campo nuevo deserializa con `null`, no revienta. Es lo que permite desplegar
   productor y consumidor en cualquier orden, y deja fijado que `FAIL_ON_UNKNOWN_PROPERTIES` en
   `false` no es casualidad.

Ver `UsuarioCreadoPayloadTest` y `AsesorFichaCambiadoPayloadTest`.


## Estructura de logs de un flujo de evento

Un flujo disparado por un mensaje **no pasa por `TrazabilidadFilter`**: no hay petición HTTP y por
tanto **no hay línea `AUDIT`**. El consumidor es lo único que puede dejar constancia de que el evento
llegó, y por eso aquí el `INFO` de entrada sí va en el adaptador y no en el use case.

| Punto | Nivel | Dónde |
|---|---|---|
| Encolado en el outbox | `debug` | `SpringModulithEventPublisher` (transversal, ya hecho) |
| Envelope recibido / confirmado | `debug` | `AbstractEventConsumer` (transversal, ya hecho) — cola y `deliveryTag` |
| **Evento recibido** | `info` | el `{Evento}Consumer`, tras `deserialize` — `idEvento` + identificadores de negocio |
| Cierre de la operación | `info` | el **adaptador**, no el use case — ver abajo |
| Nack a la DLQ | `error` | `AbstractEventConsumer` (transversal, ya hecho) |

**Dos `INFO` por mensaje**, igual que por petición, y los dos los pone el adaptador. El `INFO` de
entrada pertenece a quien es el punto de entrada del flujo: en un comando HTTP es el use case, en un
evento es el consumidor. Por eso **un use case disparado por un consumidor no añade su propio `INFO`
de entrada** — el del consumidor ya lo es.

El cierre sigue la misma lógica y por eso tampoco vive en el use case. En `notificaciones` lo emite
`AbstractNotificacionConsumer.registrar(EnvioNotificacionResult)`, con un `switch` exhaustivo que
elige nivel y clave según el desenlace: `info` para `Enviada` y `Duplicada`, **`warn` para
`Fallida`** — un envío rechazado es un 4xx de negocio, no un error del flujo. `EnviarNotificacionUseCaseImpl`
no emite ningún `INFO`: solo su `debug` de verificación previa. Poner un `INFO` de cierre ahí daría
tres líneas por mensaje y perdería el desenlace, que el use case devuelve pero no interpreta.

La regla general: **cuando el use case devuelve una sellada de desenlace, el log de cierre lo hace
quien la interpreta**, que es el adaptador. Un `{Evento}Consumer` nuevo hereda de
`AbstractEventConsumer` los tres logs transversales y de `AbstractNotificacionConsumer` el de cierre:
solo aporta su `INFO` de recepción.

Todo log del consumidor va **dentro** del `withCorrelation(...)`, es decir dentro del `AlcanceTraza`.
Fuera de él el MDC ya se restauró y la línea sale sin `correlacionId` ni `transaccionId` — que es
justo lo que permite seguir el evento hasta el productor.

