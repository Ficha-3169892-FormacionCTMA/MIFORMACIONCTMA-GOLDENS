# Mi Formación CTMA

## Descripción del proyecto
Mi Formación CTMA es una aplicación móvil desarrollada para Android con Kotlin y Jetpack Compose. Su propósito es ayudar a los aprendices del SENA a organizar actividades, compromisos y evidencias de su proceso formativo en un solo lugar. La aplicación permitirá llevar un mejor control de las tareas asignadas, consultar fechas importantes y facilitar el seguimiento del avance académico de manera sencilla y organizada.

## Problema
Actualmente los aprendices administran sus actividades, enlaces, evidencias y fechas en diferentes plataformas, lo que genera desorganización, pérdida de información y dificultad para hacer seguimiento a su proceso de formación. Mi Formación CTMA busca centralizar esta información en una sola aplicación móvil para mejorar la organización, la productividad y la trazabilidad del aprendizaje.

## Usuarios
1. **Aprendiz**: Necesidad de consultar actividades, registrar evidencias y hacer seguimiento a su progreso académico.
2. **Instructor**: Necesidad de publicar actividades, establecer fechas de entrega y realizar seguimiento al avance de los aprendices.

## Historias de Usuario
### Historia 1
**Como** aprendiz, **quiero** consultar mis actividades pendientes, **para** organizar mejor mi tiempo y cumplir con las fechas establecidas.
* **Criterio de aceptación**: La aplicación debe mostrar una lista con las actividades registradas. El aprendiz puede visualizar el nombre y la fecha de cada actividad.

### Historia 2
**Como** instructor, **quiero** publicar nuevas actividades, **para** que los aprendices puedan consultarlas desde la aplicación.
* **Criterio de aceptación**: El instructor puede crear una nueva actividad. La actividad debe quedar disponible para ser consultada por los aprendices.

### Historia 3
**Como** aprendiz, **quiero** registrar las evidencias de las actividades realizadas, **para** llevar un control de mi progreso durante la formación.
* **Criterio de aceptación**: El aprendiz puede marcar una actividad como completada. La aplicación debe reflejar el cambio de estado de la actividad.

## Tecnologías utilizadas
* Kotlin
* Android Studio
* Jetpack Compose
* Material Design 3
* Gradle
* Git
* GitHub

## Estado del proyecto
🚧 **Proyecto en desarrollo**

### Semana 2 - Cambios realizados
* Se creó el paquete `model`.
* Se implementó la clase `ActividadFormativa`.
* Se creó el enum `Prioridad`.
* Se desarrollaron las reglas de negocio.
* Se implementó la búsqueda de actividades.
* Se calcula el promedio de progreso.
* Se muestran datos calculados en la pantalla inicial.

### Semana 3 - Cambios realizados
1. **Interfaz de Usuario con Material 3**: Centralización de colores, tipografías y formas en el paquete `ui/theme`. Uso de `ColorScheme`, `Typography` y `Shapes`.
2. **Componentes Reutilizables**:
    * `EncabezadoFormacion`: Muestra el saludo y resumen de actividades.
    * `TarjetaActividad`: Componente stateless para detalles de actividad.
    * **Accesibilidad**: Uso de `contentDescription` y modificadores semánticos.
3. **Pantalla Principal y Layout Adaptable**:
    * Diseño Adaptable con `BoxWithConstraints` (alternando entre `LazyColumn` y `LazyVerticalGrid`).
    * Gestión de estados de lista vacía y optimización con `key`.

---

# Semana 7: Concurrencia y Estado Reactivo

## 🏗️ Arquitectura de Datos Reactiva
La aplicación implementa un flujo de datos unidireccional (UDF) y reactivo siguiendo este esquema:

**Room (DB) / DataStore (Prefs)**  
⬇️  
**Repository** (Transforma Entidades -> Modelos de Dominio)  
⬇️  
**ViewModel** (Combina Flows con `combine` y `flatMapLatest`)  
⬇️  
**Compose UI** (Recolecta con `collectAsStateWithLifecycle`)

### ⚡ Decisión de Dispatchers
- **Dispatchers.Main:** Utilizado en el ViewModel para la recolección de estados y actualización de la UI.
- **Dispatchers.IO:** Utilizado exclusivamente en el `AppRepository` mediante `withContext(Dispatchers.IO)` para todas las operaciones de persistencia (Room y DataStore), garantizando que las funciones sean **main-safe**.

---

## ✅ Casos de Aceptación (Semana 7)

| ID | Caso de Prueba | Resultado |
|:---|:---|:---:|
| **CA-01** | Abrir app sin datos previos | **PASÓ** (Muestra Cargando -> Estado Vacío) |
| **CA-02** | Insertar actividad nueva | **PASÓ** (Room emite flujo y UI se actualiza reactivamente) |
| **CA-03** | Cambio de filtro de prioridad | **PASÓ** (DataStore persiste y `combine` recalcula el flujo) |
| **CA-04** | Búsquedas rápidas (Debounce/Cancel) | **PASÓ** (Se cancela consulta previa, prevalece última búsqueda) |
| **CA-05** | Error forzado en Repository | **PASÓ** (Captura excepción, muestra ErrorUiState con Reintentar) |
| **CA-06** | Cancelación por ciclo de vida | **PASÓ** (El Job se cancela automáticamente al cerrar el ViewModel) |
| **CA-07** | Rotación de pantalla | **PASÓ** (StateFlow conserva estado, no se repiten inserciones) |
| **CA-08** | Suite de pruebas (RunTest) | **PASÓ** (Ejecución asíncrona validada sin Thread.sleep) |

---

## 🛠️ Tecnologías Implementadas (Semana 7)
- **Kotlin Coroutines:** Manejo de asincronía y main-safety.
- **Flow / StateFlow:** Flujos de datos reactivos y conservación de estado.
- **DataStore:** Persistencia de preferencias de usuario reactiva.
- **Room + Flow:** Consultas a base de datos que notifican cambios automáticamente.
- **Lifecycle Runtime Compose:** Recolección de flujo optimizada para Compose.
