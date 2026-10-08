# Estándares de eventos: payload y logs

Referencia de `arquisoft-estandares`. Se carga cuando el plan tiene la sección 10 (Eventos
RabbitMQ): la HU publica o consume un evento.

## Payload de evento: campos fijos y prueba de contrato

Todo `{Evento}Payload` declara al menos `idEvento` (idempotencia) y **`ocurridoEn`** (`Instant` del
hecho en el origen), más lo suyo. `DomainEvent` los asigna siempre; omitirlos del record los tira en
silencio. `ocurridoEn` permite descartar eventos desordenados — ver
`arquisoft-arquitectura/references/eventos.md` → *Replicación entre contextos*.

Su prueba instancia la configuración de producción, con la que serializa el productor:

```java
private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();
```

Un mapper armado a mano puede pasar el test y fallar en el broker. Dos casos:

1. Serializar una subclase real de `DomainEvent` y deserializarla en el payload (los tipos, incluida
   la precisión del `Instant`, sobreviven el viaje).
2. Un JSON **sin** el campo nuevo deserializa con `null`: permite desplegar productor y consumidor en
   cualquier orden y fija `FAIL_ON_UNKNOWN_PROPERTIES = false`.

Ver `UsuarioCreadoPayloadTest` y `AsesorFichaCambiadoPayloadTest`.

## Estructura de logs de un flujo de evento

Un mensaje no pasa por `TrazabilidadFilter`, así que no hay línea `AUDIT`: los dos `INFO` los pone el
adaptador, no el use case.

| Punto | Nivel | Dónde |
|---|---|---|
| Encolado en el outbox | `debug` | `SpringModulithEventPublisher` (ya hecho) |
| Envelope recibido / confirmado | `debug` | `AbstractEventConsumer` (ya hecho) |
| **Evento recibido** | `info` | el `{Evento}Consumer`, tras `deserialize` — `idEvento` + identificadores de negocio |
| Cierre | `info` | el adaptador que interpreta el desenlace |
| Nack a la DLQ | `error` | `AbstractEventConsumer` (ya hecho) |

Un use case disparado por un consumidor **no añade `INFO` de entrada ni de cierre**. Cuando devuelve
una sellada de desenlace, el cierre lo hace quien la interpreta: en `notificaciones`,
`AbstractNotificacionConsumer.registrar(EnvioNotificacionResult)` con `info` para `Enviada`/`Duplicada`
y `warn` para `Fallida`. Un `{Evento}Consumer` nuevo solo aporta su `INFO` de recepción.

Todo log del consumidor va **dentro** del `withCorrelation(...)` (el `AlcanceTraza`); fuera, la línea
sale sin `correlacionId` ni `transaccionId`.
