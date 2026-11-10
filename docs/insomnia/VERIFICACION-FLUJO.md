# Verificación del flujo Insomnia — POO-08

Fecha: 10/11/2026. Archivo: [minecraft-poo08.json](minecraft-poo08.json).

Colección vigente: **16 etapas, 25 requests, 44 recursos** (workspace, Base Environment, Local, 16 carpetas y 25 requests). El recorrido incluye todos los endpoints y casos pedidos. El archivo existe en el árbol local del repositorio; aún no está versionado porque no se ejecutó git add, commit ni push.

## Compatibilidad y método de verificación

El importador oficial `insomnia-importers@2.4.0`, módulo Insomnia v4, aceptó los 44 recursos. Se verificaron IDs internos, referencias parentId, orden 01–16, sintaxis de todos los scripts y conservación de `preRequestScript`/`afterResponseScript` al importar. No hay UUID hardcodeados en URLs ni cuerpos.

Se ejecutaron las **25 peticiones HTTP reales** contra la aplicación arrancada desde la raíz con Java 21 y H2 en archivo. Un comprobador local JavaScript ejecutó los mismos scripts exportados sobre cada response, proporcionando las operaciones documentadas de `insomnia.response`, `insomnia.environment`, `insomnia.test` e `insomnia.expect`: **43 comprobaciones pasaron**. Los UUID se guardaron exclusivamente mediante los after-response scripts, sin inyectarlos manualmente desde el comprobador.

Esta verificación no equivale a abrir la interfaz de Insomnia. El uso de los scripts se apoya en la [API oficial](https://developer.konghq.com/insomnia/scripts/) y en el [campo de exportación v4](https://github.com/Kong/insomnia/discussions/8526); el README incluye la alternativa manual para versiones que no ejecuten scripts. Seleccionar **Local** y recorrer las carpetas y subrequests en orden.

## UUID capturados durante esta ejecución

Estos UUID son evidencia histórica de esta ejecución, no valores para pegar en la colección. Cada nuevo recorrido captura sus propios IDs.

| Variable | UUID devuelto por POST |
| --- | --- |
| `entidad_id` | `6f9e6850-a4cc-4c19-bad8-8b0715438cb8` |
| `objetivo_id` | `6f9e6850-a4cc-4c19-bad8-8b0715438cb8` |
| `creeper_id` | `0596c743-fa8d-40f7-8f97-d77382167f17` |
| `cerdo_id` | `fdf7c395-7d67-4bc0-a3dc-2a84a0f0f01f` |
| `inexistente_id` | `6a235553-3d17-4efc-a54e-38001593ed9d` |

`entidad_id` y `objetivo_id` son el mismo Jugador creado en 02. Se reutilizó en GET, PUT, explosión, DELETE y GET posterior. `inexistente_id` proviene del POST auxiliar de 12.1 y su DELETE de 12.2: no se inventó un UUID para producir el 404.

## Resultados de las 25 requests

Base de las rutas siguientes: `http://localhost:8080`. Las variables representan los UUID reales de la tabla anterior.

| Request | Método y ruta | HTTP | JSON/cuerpo resumido |
| --- | --- | --- | --- |
| 01.1 | `GET /` | 200 | `"API REST - Sofia Torres - Minecraft"` |
| 02.1 | `POST /api/entidades` | 201 | `{"nombre":"JugadorFlujo","tipo":"JUGADOR","salud":18.0}` |
| 03.1 | `POST /api/entidades` | 201 | `{"nombre":"CreeperFlujo","tipo":"CREEPER","salud":20.0,"fuseTime":30,"encendido":false}` |
| 04.1 | `POST /api/entidades` | 201 | `{"nombre":"CerdoFlujo","tipo":"CERDO","salud":10.0}` |
| 05.1 | `GET /api/entidades` | 200 | `{"cantidad":6}` |
| 06.1 | `GET /api/entidades/{{ _.entidad_id }}` | 200 | `{"nombre":"JugadorFlujo","tipo":"JUGADOR","salud":18.0}` |
| 07.1 | `PUT /api/entidades/{{ _.entidad_id }}` | 200 | `{"nombre":"JugadorActualizado","tipo":"JUGADOR","salud":20.0}` |
| 07.2 | `GET /api/entidades/{{ _.entidad_id }}` | 200 | `{"nombre":"JugadorActualizado","tipo":"JUGADOR","salud":20.0}` |
| 08.1 | `GET /api/sonidos` | 200 | `{"resultados":[{"tipo":"Zombie","mensaje":"Grrr..."},{"tipo":"Cerdo","mensaje":"Oink, oink!"}]}` |
| 09.1 | `POST /api/explosiones` | 200 | `{"saludCreeper":0.0,"saludObjetivos":[5.0,0.0]}` |
| 10.1 | `GET /api/entidades/{{ _.objetivo_id }}` | 200 | `{"nombre":"JugadorActualizado","tipo":"JUGADOR","salud":5.0}` |
| 10.2 | `GET /api/entidades/{{ _.creeper_id }}` | 200 | `{"nombre":"CreeperFlujo","tipo":"CREEPER","salud":0.0,"fuseTime":0,"encendido":false}` |
| 10.3 | `GET /api/entidades/{{ _.cerdo_id }}` | 200 | `{"nombre":"CerdoFlujo","tipo":"CERDO","salud":0.0}` |
| 11.1 | `GET /api/entidades` | 200 | `{"cantidad":6}` |
| 11.2 | `POST /api/entidades` | 400 | `{"codigo":"DATOS_INVALIDOS","error":"La velocidad debe ser finita y no negativa"}` |
| 11.3 | `GET /api/entidades` | 200 | `{"cantidad":6}` |
| 12.1 | `POST /api/entidades` | 201 | `{"nombre":"Auxiliar404Flujo","tipo":"OTRO","salud":1.0}` |
| 12.2 | `DELETE /api/entidades/{{ _.inexistente_id }}` | 204 | `"(sin cuerpo)"` |
| 12.3 | `GET /api/entidades/{{ _.inexistente_id }}` | 404 | `{"codigo":"ENTIDAD_NO_ENCONTRADA","error":"No existe una entidad con id 6a235553-3d17-4efc-a54e-38001593ed9d"}` |
| 13.1 | `POST /api/explosiones` | 404 | `{"codigo":"ENTIDAD_NO_ENCONTRADA","error":"No existe una entidad con id 6a235553-3d17-4efc-a54e-38001593ed9d"}` |
| 14.1 | `POST /api/explosiones` | 409 | `{"codigo":"EXPLOSION_INVALIDA","error":"Un creeper muerto no puede explotar"}` |
| 14.2 | `GET /api/entidades/{{ _.objetivo_id }}` | 200 | `{"nombre":"JugadorActualizado","tipo":"JUGADOR","salud":5.0}` |
| 15.1 | `DELETE /api/entidades/{{ _.entidad_id }}` | 204 | `"(sin cuerpo)"` |
| 16.1 | `GET /api/entidades/{{ _.entidad_id }}` | 404 | `{"codigo":"ENTIDAD_NO_ENCONTRADA","error":"No existe una entidad con id 6f9e6850-a4cc-4c19-bad8-8b0715438cb8"}` |
| 16.2 | `GET /api/entidades` | 200 | `{"cantidad":5,"jugador_eliminado":true,"auxiliar_eliminado":true,"invalido_ausente":true}` |

Antes y después del POST ilegal hubo **6 y 6 registros**, respectivamente: el inválido no se guardó. Al finalizar quedaron **5 registros**: los tres de la evidencia anterior y el Creeper/Cerdo nuevos. Se eliminaron solamente el auxiliar de 12 y el Jugador de este recorrido, mediante la API.

Los GET posteriores a la simulación devolvieron Jugador=5, Creeper=0 y Cerdo=0. Repetirla devolvió 409 y el Jugador conservó salud 5. DELETE del mismo Jugador devolvió 204 y GET posterior devolvió 404.

El servidor de prueba se detuvo al terminar. No se cambió código funcional ni configuración, no se modificó H2 manualmente y no se ejecutó staging, commit ni push.
