# Mi Formación CTMA - Semana 7: Concurrencia y Estado Reactivo

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

## 🛠️ Tecnologías Implementadas
- **Kotlin Coroutines:** Manejo de asincronía y main-safety.
- **Flow / StateFlow:** Flujos de datos reactivos y conservación de estado.
- **DataStore:** Persistencia de preferencias de usuario reactiva.
- **Room + Flow:** Consultas a base de datos que notifican cambios automáticamente.
- **Lifecycle Runtime Compose:** Recolección de flujo optimizada para Compose.
