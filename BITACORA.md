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
esquema oficial; la autora confirma la importacion manual correcta en
Insomnia y la verificacion de su carga. Se actualizo el contrato
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
La autora confirma que `docs/insomnia/sofi-poo09.yaml` se importo
manualmente en Insomnia y que se verifico su carga correctamente. Los ejemplos offline proceden de pruebas con servicios simulados,
no acreditan una demostracion coordinada en vivo. Esa demostracion sigue
pendiente de evidencia; no es codigo faltante.

Se conserva el resultado tecnico previamente registrado: 160 tests, 0 failures,
0 errors, 0 skipped. Este cierre modifica solamente la seccion POO-09 del README
y agrega esta entrada; no cambia Java, logica ni tests y no los vuelve a ejecutar.
Asistente utilizado: Codex / OpenAI. Se mantienen los registros historicos de
modelos sin atribuir a esta etapa un identificador no confirmado. No se hizo
git add, commit ni push.


### Confirmacion final de documentacion

La importacion manual de la coleccion Insomnia quedo confirmada por la autora.
La demostracion coordinada del trio no se registra como ejecutada. Se conserva
el resultado confirmado: 160 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS.
Se preparo `docs/POO-09-Sofia-Torres.md` para Classroom. El commit publicado de
Sofi anterior a este ajuste es `269ff2081b70ed7bd9f02e4514ff61e0e6b04de5`;
el hash final se completara despues del commit documental. No se modifico
codigo ni se regenero OpenAPI. No se hizo git add, commit ni push.

## POO-09 - registro de la sesion grupal del 24/11/2026 - 25/11/2026

Se actualizan exclusivamente README.md, BITACORA.md y
docs/POO-09-Sofia-Torres.md a partir de la confirmacion de los participantes.
Se conservan las entradas del 23/11/2026 como antecedentes del estado previo;
sus menciones de demostracion pendiente corresponden a ese cierre.

En la sesion del 24/11/2026 se realizaron pruebas manuales de integracion en
vivo con los tres servidores conectados mediante Tailscale:

- Sofia como A1: caso exitoso, rechazo de Nico y rechazo de Amir realizados
  correctamente, segun confirmacion de los participantes.
- Nicolas como A1: caso exitoso, rechazo de Amir y rechazo de Sofia realizados
  correctamente, segun confirmacion de los participantes.
- Amir como A1: pendiente de confirmacion final.

Durante las pruebas de Nicolas aparecio inicialmente un HTTP 503. Los
participantes confirmaron que se comprobo mediante curl que ambas APIs remotas
respondian correctamente y que la integracion funciono posteriormente.
No se atribuye una causa al 503.

Es una confirmacion manual, separada del resultado previo de las 160 pruebas
automatizadas y de los ejemplos offline con servicios simulados. No se
aportaron capturas, logs, respuestas HTTP completas, hashes de los procesos
ejecutados ni resultados SQL de esa sesion. No se declara finalizada la
demostracion de Amir como A1 ni se afirman verificaciones adicionales.

Se mantiene la tabla A1/A2/A3 y los commits originales acordados del README.
Para Sofia se distinguen el acuerdo original
`06a6fb297a5e9f9186d23b6dee7086fdaba94e30`, el commit documental intermedio
`269ff2081b70ed7bd9f02e4514ff61e0e6b04de5` y el commit final entregado
`7915cb50709770ef9eb6f01dd8880679e68f7c41`. Este ultimo completa la referencia
que habia quedado anunciada en la entrada anterior; ninguno de esos hashes
se atribuye a los procesos de la sesion sin evidencia de su version.

No se modifican Java, tests, configuraciones, contratos OpenAPI, archivos de
Insomnia ni bases de datos. No se ejecutaron pruebas ni llamadas a los
servidores para esta actualizacion. No se hizo git add, commit ni push.
Asistente: Codex / OpenAI.

## POO-09 - cierre de la demostracion grupal del 24/11/2026 - 25/11/2026

Los tres integrantes confirmaron que completaron exitosamente los nueve casos
de integracion manual en vivo durante la sesion del 24/11/2026, con los tres
servidores conectados mediante Tailscale:

- Sofia como A1: caso exitoso, rechazo A2 (Nico) y rechazo A3 (Amir).
- Nicolas como A1: caso exitoso, rechazo A2 (Amir) y rechazo A3 (Sofia).
- Amir como A1: caso exitoso, rechazo A2 (Sofia) y rechazo A3 (Nico).

Esta confirmacion final cierra la demostracion grupal. La entrada anterior
conserva la confirmacion parcial recibida, incluido el pendiente de Amir que
ahora queda cerrado y el antecedente del HTTP 503 en las pruebas de Nicolas.

Los nueve casos manuales confirmados por los integrantes son independientes
de las 160 pruebas automatizadas anteriores y de los ejemplos offline con
servicios simulados. No se aportaron capturas, logs, respuestas HTTP completas,
hashes de los procesos ejecutados ni resultados SQL de la sesion.

Se actualizan unicamente README.md, BITACORA.md y docs/POO-09-Sofia-Torres.md.
Se conservan los antecedentes, las tablas round robin y la distincion entre
commits acordados, intermedio y entregado. No se modifican Java,
configuraciones, tests, contratos OpenAPI, archivos de Insomnia ni bases de
datos. No se ejecutaron pruebas ni llamadas a servidores para esta actualizacion.
No se hizo git add, commit ni push. Asistente: Codex / OpenAI.
