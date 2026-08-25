# shared:validation

Notification Pattern: acumular todos los errores de una petición y reportarlos juntos, en vez de
lanzar en el primero y obligar al cliente a descubrirlos de uno en uno.

## Qué contiene

| Clase | Rol |
|---|---|
| `DomainValidator` | acumula errores — formato, obligatoriedad, longitud, duplicados |
| `ValidationResult` | el acumulador; `lanzarSiInvalido()` decide cuándo se corta |
| `DomainValidationException` | 422 — reglas de negocio incumplidas, con `fieldErrors[]` |
| `ApplicationValidationException` | 400 — errores de entrada, con `fieldErrors[]` |

`GlobalAppExceptionHandler` (`shared:web`) captura las dos excepciones antes que la genérica de
`BaseException` y expande su `ValidationResult` en el arreglo `fieldErrors[]` de la respuesta.

## Por qué las cuatro clases van juntas

No es afinidad temática, es una dependencia mutua:

```
ValidationResult               → lanza  DomainValidationException, ApplicationValidationException
DomainValidationException      → recibe ValidationResult por constructor
ApplicationValidationException → recibe ValidationResult por constructor
```

Repartirlas entre dos módulos cierra un ciclo que Gradle rechaza. Antes estaban esparcidas entre
`shared:domain` (`DomainValidator`) y `shared:exception` (el resto), que es la forma en que el
problema pasaba desapercibido: dentro de un mismo módulo, Java permite dependencias circulares
entre paquetes y Gradle no las ve.

Juntas aquí, la relación con `shared:exception` queda en un solo sentido: las dos excepciones
extienden `DomainException` y `ApplicationException`, que se quedaron allí.

```
shared:util  shared:exception  shared:message      (hojas)
        ↑           ↑                ↑
        └───── shared:validation ────┘
                     ↑ api
                shared:domain
```

## Dependencias

- **`api shared:exception`** — las dos excepciones extienden tipos de ese módulo, así que forman
  parte del contrato público de este.
- **`implementation shared:message`** — `DomainValidator` resuelve los textos de sus mensajes
  contra el catálogo, pero ni `Mensajes` ni `ValidadorKey` aparecen en su firma.
- **`implementation shared:util`** — uso interno de `UtilText`, `UtilObject`, `UtilUUID`,
  `UtilCollection`.

`shared:domain` lo reexpone con `api`, así que los ~31 archivos que importan
`com.arquisoft.shared.validation` siguen compilando sin declarar nada nuevo.
