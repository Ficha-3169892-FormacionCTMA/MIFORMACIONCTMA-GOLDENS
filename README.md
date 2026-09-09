# Mi Formación CTMA

## Descripción del proyecto

Mi Formación CTMA es una aplicación móvil desarrollada para Android con Kotlin y Jetpack Compose. Su propósito es ayudar a los aprendices del SENA a organizar actividades, compromisos y evidencias de su proceso formativo en un solo lugar. La aplicación permitirá llevar un mejor control de las tareas asignadas, consultar fechas importantes y facilitar el seguimiento del avance académico de manera sencilla y organizada.

---

# Problema

Actualmente los aprendices administran sus actividades, enlaces, evidencias y fechas en diferentes plataformas, lo que genera desorganización, pérdida de información y dificultad para hacer seguimiento a su proceso de formación. Mi Formación CTMA busca centralizar esta información en una sola aplicación móvil para mejorar la organización, la productividad y la trazabilidad del aprendizaje.

---

# Usuarios

## 1. Aprendiz
**Necesidad:** Consultar actividades, registrar evidencias, bitácoras y hacer seguimiento a su progreso académico.

## 2. Instructor
**Necesidad:** Publicar actividades, establecer fechas de entrega y realizar seguimiento al avance de los aprendices.

---

# Historias de Usuario (Product Backlog)

### HU-01 - Visualización y Estado de Actividades (Mejorada)
**Como** aprendiz, **quiero** consultar mis actividades pendientes con estados claros (Pendiente, En Proceso, Completada), **para** organizar mejor mi tiempo y cumplir con las fechas establecidas.
- **CA-01.1:** Lista con colores según prioridad y estado.
- **CA-01.2:** Indicador visual de días restantes.

### HU-02 - Creación de Actividades (Mejorada)
**Como** instructor, **quiero** publicar nuevas actividades con fechas de inicio/fin y prioridad, **para** que los aprendices puedan consultarlas desde la aplicación.
- **CA-02.1:** Validación de fecha fin posterior a inicio.
- **CA-02.2:** Selección de prioridad (Alta, Media, Baja).

### HU-03 - Entrega de Evidencias Digitales (Mejorada)
**Como** aprendiz, **quiero** registrar las evidencias de las actividades realizadas, **para** llevar un control de mi progreso durante la formación.
- **CA-03.1:** El aprendiz puede marcar una actividad como completada al subir el link.
- **CA-03.2:** Validación de formato URL para la evidencia.

### HU-04 - Registro de Bitácora Diaria
**Como** aprendiz, **quiero** registrar mis tareas diarias y horas dedicadas, **para** cumplir con el seguimiento de mi etapa productiva.
- **CA-04.1:** Registro de fecha, descripción y horas.
- **CA-04.2:** Validación de horas mayores a cero.

### HU-05 - Control de Asistencia QR
**Como** aprendiz, **quiero** registrar mi asistencia escaneando un código QR, **para** agilizar el proceso de control en el ambiente de formación.
- **CA-05.1:** Escaneo de código QR generado por el instructor.

### HU-06 - Autenticación y Perfil SENA
**Como** usuario, **quiero** iniciar sesión con mis credenciales institucionales, **para** acceder a mi información de forma segura.

### HU-07 - Dashboard de Progreso
**Como** aprendiz, **quiero** ver un resumen gráfico de mi avance académico, **para** visualizar mis competencias alcanzadas.

### HU-08 - Notificaciones Push de Vencimiento
**Como** aprendiz, **quiero** recibir alertas sobre entregas próximas a vencer, **para** no olvidar mis compromisos.

### HU-09 - Filtros y Búsqueda Avanzada
**Como** aprendiz, **quiero** buscar actividades por competencia o estado, **para** encontrar información rápidamente.

### HU-10 - Modo Offline (Sincronización)
**Como** aprendiz, **quiero** registrar datos sin conexión a internet, **para** que se sincronicen automáticamente al recuperar el acceso.

### HU-11 - Gestión de Retroalimentación
**Como** aprendiz, **quiero** ver los comentarios del instructor sobre mis evidencias, **para** mejorar mi proceso de aprendizaje.

### HU-12 - Calendario Formativo
**Como** aprendiz, **quiero** ver mis actividades en una vista de calendario, **para** visualizar mi carga académica mensual.

### HU-13 - Descarga de Certificados
**Como** aprendiz, **quiero** descargar mis certificados de asistencia y notas, **para** utilizarlos en trámites administrativos.

### HU-14 - Reporte de Novedades e Inasistencias
**Como** aprendiz, **quiero** reportar excusas médicas o permisos, **para** justificar mis inasistencias de forma oficial.

### HU-15 - Preferencias y Accesibilidad
**Como** usuario, **quiero** ajustar el tema (oscuro/claro) y el tamaño de fuente, **para** mejorar mi experiencia de uso.

---

# Tecnologías utilizadas

- Kotlin, Jetpack Compose, Clean Architecture, MVVM, UDF, Coroutines, Flow, StateFlow, Room, DataStore, Retrofit 2, OkHttp 4, Gson, MockWebServer, Espresso.

---

# Evolución del Proyecto

## Semana 5: Implementación Integral (Clean Architecture & UDF)
- **Agile Coaching:** Definición y estructuración de 15 Historias de Usuario con estándares profesionales.
- **Testing Estratégico:** Implementación de pruebas unitarias y de UI automatizadas para el flujo de Bitácora y Actividades.
- **Arquitectura Robusta:** Refactorización a Clean Architecture con manejo de estado unidireccional (UDF).

## Semana 7: Concurrencia y Estado Reactivo
- **Reactividad Total:** Implementación de `Flow` y `StateFlow` desde Room/DataStore hasta la UI.
- **Main-Safety:** Migración de toda la lógica de persistencia a `Dispatchers.IO` en el Repositorio, garantizando que el hilo principal nunca se bloquee.
- **Gestión de UI State:** Implementación de `sealed interfaces` para estados complejos de carga, contenido, error y validación de operaciones asíncronas.
- **Optimización de Búsqueda:** Uso de `flatMapLatest` y `debounce` para realizar consultas eficientes y cancelar peticiones obsoletas.

## Semana 8: Integración con Servicio REST (Actual)
- **Capa de Red:** Integración de Retrofit con interceptores de seguridad para tokens `Bearer`.
- **Estrategia Offline-First:** Implementación de la "Regla de Oro": API -> DTO -> Mapeo -> Room -> Flow -> UI. El caché local es la fuente única de verdad.
- **Resiliencia:** Clasificación detallada de errores de red (401, 404, Timeout) sin interrumpir el acceso a datos locales.
- **Sincronización:** Implementación de refresco manual e indicadores de estado de red con marcas de tiempo de última actualización.

---

## 🏗️ Arquitectura y Red (Semana 8)

La aplicación sigue un flujo de datos unidireccional donde la base de datos local (Room) actúa como fuente única de verdad para la UI.

### 🚀 Contrato de la API
| Método | Endpoint | Descripción |
|:---|:---|:---|
| **GET** | `/v1/actividades` | Sincroniza todas las actividades con el caché local. |
| **GET** | `/v1/actividades/{id}` | Obtiene detalle remoto de una actividad específica. |
| **POST** | `/v1/actividades` | Registra una nueva actividad en el servidor. |
| **PUT** | `/v1/actividades/{id}` | Actualiza el estado o progreso de una actividad. |

### 🔒 Decisiones de Seguridad y Caché
- **Inyección de Token:** Se utiliza un `TokenProvider` y un Interceptor de OkHttp para adjuntar la cabecera `Authorization: Bearer <token>` de forma transparente.
- **Privacidad de Logs:** Configuración de niveles de log para no exponer tokens ni información sensible en Logcat.
- **Persistencia Atómica:** Las respuestas exitosas de la API se guardan en Room; los errores remotos no reemplazan ni eliminan el caché existente.

---

## ✅ Casos de Aceptación (Semana 8)

| ID | Caso de Prueba | Resultado |
|:---|:---|:---:|
| **CA-01** | Respuesta 200 OK con actividades | **PASÓ** (Room se actualiza y la UI refleja los cambios) |
| **CA-02** | Respuesta 200 OK con arreglo vacío | **PASÓ** (Se trata como vacío válido, no error) |
| **CA-03** | Timeout con caché existente | **PASÓ** (Caché permanece visible, banner de error) |
| **CA-04** | Sin red y sin caché previo | **PASÓ** (Estado de error recuperable con botón Sincronizar) |
| **CA-05** | Error 401 (No Autorizado) | **PASÓ** (Pide renovar sesión de forma segura) |
| **CA-06** | Error 500 o JSON inválido | **PASÓ** (Error clasificado, Room conserva datos previos) |
| **CA-07** | Refresh rápidos consecutivos | **PASÓ** (Sin corrupción ni estados imposibles) |
| **CA-08** | Cancelación del ViewModel | **PASÓ** (La petición de red se cancela al salir de la pantalla) |

---

## 🧪 Cómo correr las pruebas
Ejecutar `./gradlew test` para validar la lógica de negocio y la integración con servidores simulados mediante MockWebServer.

---

> **Nota Técnica:** Este proyecto utiliza estándares modernos de Android (UDF, Offline-First). Se utilizó asistencia de IA para la generación de mappers, optimización de flujos reactivos y resolución de errores de compilación y deprecaciones en la UI.
