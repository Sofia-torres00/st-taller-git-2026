# Bitácora de asistencia con IA

## Herramientas usadas

1. Codex

   - Marca: Codex / OpenAI
   - Modelo exacto:  GPT-6.1 Sol

## Resumen de uso

Usé IA para revisar el enunciado y la rúbrica de POO-06, comparar los requisitos
con la estructura que ya tenía mi proyecto de Minecraft y detectar qué faltaba.

Con esa ayuda reorganicé el dominio en `py.edu.uc.lp3.domain`, los controllers
en `py.edu.uc.lp3.rest.controller` y Application en `py.edu.uc.lp3`, actualizando
los packages e imports sin perder funcionalidad.

También agregué el constructor sobrecargado de Zombie y la sobrecarga de
`Entidad.mover`. Revisé la sobreescritura que ya existía de
`describirComportamiento()` en Zombie y Creeper y cómo el controller las usa
como Entidad. Después actualicé el README y el Mermaid según el código final.

Usé IA para detectar y corregir los problemas de configuración de Java 21 y
del Maven Wrapper. Verifiqué compilación, tests, arranque y endpoints REST,
incluyendo que una velocidad negativa devuelva HTTP 400 y que la validación
siga en el dominio.

## Prompts resumidos

- "Revisar el proyecto contra el enunciado POO-06 y detectar qué faltaba."
- "Reorganizar los paquetes según el template sin perder funcionalidad."
- "Agregar constructores y métodos sobrecargados manteniendo objetos válidos."
- "Verificar sobreescritura y polimorfismo con Entidad, Zombie y Creeper."
- "Corregir la respuesta REST para valores inválidos sin duplicar la validación."
- "Probar Maven Wrapper, Java 21, tests y endpoints REST."
- "Actualizar README y Mermaid según el código final."

Nota: revisé las decisiones y las verificaciones finales antes de versionar
los cambios. La IA fue una ayuda para el trabajo; la revisión final quedó a mi cargo.

## POO-09 - 23/11/2026

Se publico OpenAPI/Swagger de las operaciones de Sofi y se consultaron los
OpenAPI reales de Nico y Amir para los clientes WebClient. El escenario
coordinado consulta disponibilidad de respaldo en Nico, solicita la explosion
de Amir y persiste en H2 la demolicion confirmada con su resultado remoto.
No se copio codigo de los companeros.

En este cierre se identifican los errores con fuente A2/A3, servicio,
mensaje y status, conservando los errores locales existentes. Las pruebas
comparan entidades, salud, inventario e historial antes y despues del rechazo.
Se documentaron los dos participantes independientes y que Amir aplica efectos
reales sin rollback distribuido; su previsualizacion integrada exige un contrato
distinto y no es un reemplazo directo del escenario actual.

Se preparo la coleccion Insomnia con nueve solicitudes y ejemplos offline
capturados en pruebas (A1 real, A2/A3 simulados, H2 en memoria). Se valido su
esquema oficial; queda pendiente la importacion grafica. Se actualizo el contrato
exportado, README, referencias de commits proporcionadas por el grupo y pasos
de demostracion. No se afirma haber ejecutado el trio completo en vivo.

Validacion: Maven Wrapper clean test, JDK 21, 160 pruebas, cero fallos,
cero errores y cero omitidas. No se hizo git add, commit ni push.

Asistente: Codex / OpenAI. Se conserva sin cambios el registro historico del
modelo de la etapa anterior; no se atribuye un identificador de modelo no
confirmado a esta etapa. La revision final y la publicacion quedan a cargo de
la autora.


## POO-09 - cierre documental final - 23/11/2026

El mapa de esta entrega queda fijado como A1 Sofi, A2 Nico y A3 Amir.
Cada integrante asume A1 en su propia entrega. Los commits acordados/publicados
son Sofi `06a6fb297a5e9f9186d23b6dee7086fdaba94e30`, Nico
`e9db16a93c925928734932e2759459a08747a1dd` y Amir
`a6e6b14a3626f1cf6cfa48547fb4b1d5596a03fb`. Sus repositorios, bases y
OpenAPI quedan enlazados en la tabla POO-09 del README. No se afirma que se
hayan clonado o ejecutado esos commits ni que sean los procesos encendidos.

La evidencia aportada por Amir y contrastada con su README registra el consumo
de `POST /api/explosiones/validar` de Sofi como su A2: valida participantes
Creeper, Jugador y Cerdo con UUID distintos, tipos correctos y Creeper vivo,
sin dano, escrituras en H2 ni consumo del Creeper. Con este respaldo, el round
robin esta completo documentalmente; la demostracion coordinada en vivo sigue
pendiente y requiere los servicios Tailscale disponibles.

OpenAPI y consumo A2/A3 estan completos. El escenario de Sofi consulta primero
la disponibilidad del respaldo de Nico y despues solicita la explosion a Amir;
solo guarda la demolicion confirmada y su resultado remoto en H2. La
persistencia y los rechazos sin cambios locales estan verificados en pruebas.
Amir aplica efectos reales mediante `/api/lp3/explosiones/simular`. Su
previsualizacion sin efectos requiere otro contrato y no es un reemplazo
equivalente. A1 garantiza que su propia H2 no cambia ante un rechazo;
una operacion remota ya ejecutada no puede deshacerse mediante la transaccion
local de A1. Se documenta esta limitacion, sin presentarla como un bug corregido.

La coleccion Insomnia conserva el escenario feliz, GET posterior, rechazos A2/A3
y GET despues de cada rechazo. El archivo fue validado estructuralmente.
La importacion grafica en Insomnia se verifica manualmente y aun no se ha
confirmado. Los ejemplos offline proceden de pruebas con servicios simulados,
no acreditan una demostracion coordinada en vivo. Ambas verificaciones son
pendientes de evidencia, no codigo faltante.

Se conserva el resultado tecnico previamente registrado: 160 tests, 0 failures,
0 errors, 0 skipped. Este cierre modifica solamente la seccion POO-09 del README
y agrega esta entrada; no cambia Java, logica ni tests y no los vuelve a ejecutar.
Asistente utilizado: Codex / OpenAI. Se mantienen los registros historicos de
modelos sin atribuir a esta etapa un identificador no confirmado. No se hizo
git add, commit ni push.
