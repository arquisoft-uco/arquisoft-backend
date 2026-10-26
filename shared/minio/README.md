# shared:minio

Almacenamiento de objetos sobre MinIO/S3. El archivo **nunca pasa por el backend** al subirse: este firma
un formulario `POST` con política y el navegador sube directo a MinIO.

## API (`AlmacenamientoObjetos`)

| Método | Qué hace |
|---|---|
| `generarFormularioCarga` | `PostPolicy` con `key` exacta, `Content-Type` exacto, `content-length-range` `[1, máximo]` y un `eq` por metadato `x-amz-meta-*`. Devuelve `url` (`minio.endpoint-publico` + bucket), `campos` (firma + `key`, `Content-Type` y metadatos, que el cliente debe reenviar con el `file` al final) y `expiraEn` |
| `consultarMetadatos` | `statObject`. Tamaño, tipo, `etag` sin comillas y metadatos de usuario (en minúsculas, sin el prefijo `x-amz-meta-`). Objeto ausente → `MetadatosObjeto.VACIO` |
| `abrirLectura` | `getObject` en streaming y el `etag` del GET. Objeto ausente → `Optional.empty()`. El llamador cierra la `LecturaObjeto` |
| `copiar` | `copyObject` condicionado al ETag (`x-amz-copy-source-if-match`): si el origen cambió, MinIO responde 412 y sale `MinioOperationException` |
| `eliminar` | `removeObject` |

Todo fallo es `MinioOperationException` (503). El módulo no registra logs: loguea el adaptador del contexto.

## Lo que este módulo no hace

No crea buckets, no comprueba que existan y no lista objetos (`makeBucket`, `bucketExists`, `listObjects`):
en la nube la cuenta del backend solo tiene `GetObject`, `PutObject`, `DeleteObject` y `GetBucketLocation`
sobre `repositorio-artefactos/*` y `artefactos/*`. Un objeto ausente responde 404 `NoSuchKey` y se mapea a vacío. El bucket
y su ciclo de vida los configura `minio/configurar.sh`. TLS siempre se valida: no hay modo *trust-all*.

## Layout de los buckets `repositorio-artefactos` y `artefactos` (ambos igual)

```
pendientes/{carga}      # destino del formulario firmado; ILM lo expira a 1 día
versiones/{version}     # copia definitiva tras validar el archivo; no expira
```

## Propiedades

```yaml
minio:
  endpoint: http://minio:9000            # el que usa el backend
  endpoint-publico: http://localhost:9000 # el que alcanza el navegador (url del formulario)
  access-key: ...
  secret-key: ...
  presigned-url-expiry:
    formulario-carga-minutos: 15
```
