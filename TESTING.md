# Testing Suite - SilenceApp

## 📋 Resumen de Cobertura

Esta aplicación cuenta con una suite completa de pruebas automatizadas que cubre:

- ✅ **Tests Unitarios**: ViewModels y Repositories
- ✅ **Tests de Integración**: Room Database (DAOs)
- ✅ **Tests de UI**: Pantallas con Jetpack Compose
- ✅ **CI/CD**: GitHub Actions automatizado

## 🧪 Tipos de Tests

### 1. Tests Unitarios (`app/src/test/`)

#### ViewModels
- `SearchViewModelTest.kt` - Tests para búsqueda de usuarios y comunidades
- `AuthViewModelTest.kt` - Tests para autenticación (login/registro)

**Cobertura:**
- Estados de carga (loading, error, success)
- Manejo de excepciones
- Flujos de StateFlow/Flow
- Llamadas a repositories

**Ejemplo de ejecución:**
```bash
./gradlew test
```

#### Repositories
- `AuthRepositoryTest.kt` - Tests para el repositorio de autenticación

**Cobertura:**
- Integración con APIs (mocked)
- Almacenamiento de tokens
- Extracción de datos de JWT
- Manejo de errores de red

### 2. Tests de Integración (`app/src/androidTest/`)

#### Room Database
- `PostDaoTest.kt` - Tests para operaciones CRUD de posts
- `UserDaoTest.kt` - Tests para operaciones CRUD de usuarios

**Cobertura:**
- Insert, Update, Delete, Query
- Reactividad con Flows
- Base de datos en memoria
- Integridad de datos

**Ejemplo de ejecución:**
```bash
./gradlew connectedAndroidTest
```

### 3. Tests de UI

#### Compose Tests
- `LoginScreenTest.kt` - Tests de interfaz de login
- `PostCardTest.kt` - Tests de componente PostCard

**Cobertura:**
- Renderizado de componentes
- Interacciones de usuario
- Navegación
- Estados de UI
- Validaciones de formularios

## 🛠️ Herramientas Utilizadas

### Frameworks de Testing
```gradle
// MockK - Mocking en Kotlin
testImplementation("io.mockk:mockk:1.13.12")

// Coroutines Test - Testing de coroutines
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")

// Turbine - Testing de Flows
testImplementation("app.cash.turbine:turbine:1.1.0")

// Truth - Assertions fluidas
testImplementation("com.google.truth:truth:1.4.4")

// Robolectric - Tests con contexto Android
testImplementation("org.robolectric:robolectric:4.13")

// Room Testing
testImplementation("androidx.room:room-testing:2.6.1")

// MockWebServer - Simulación de APIs
testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
```

### Herramientas de Análisis
- **JaCoCo**: Cobertura de código
- **Android Lint**: Análisis estático
- **Compose Test**: UI testing

## 🚀 Ejecución de Tests

### Ejecutar todos los tests
```bash
# Tests unitarios
./gradlew test

# Tests instrumentados (requiere emulador/dispositivo)
./gradlew connectedAndroidTest

# Todos los tests
./gradlew test connectedAndroidTest
```

### Generar reporte de cobertura
```bash
./gradlew jacocoTestReport
```

El reporte se genera en: `app/build/reports/jacoco/jacocoTestReport/html/index.html`

### Ejecutar tests específicos
```bash
# Solo un ViewModel
./gradlew test --tests SearchViewModelTest

# Solo un método
./gradlew test --tests SearchViewModelTest.loadInitialData_should_load_users_and_communities_successfully
```

### Ver resultados en detalle
```bash
./gradlew test --info
./gradlew test --debug  # Más verboso
```

## 📊 Reportes

Los reportes se generan automáticamente en:

- **Tests unitarios**: `app/build/reports/tests/testDebugUnitTest/index.html`
- **Tests instrumentados**: `app/build/reports/androidTests/connected/index.html`
- **Cobertura**: `app/build/reports/jacoco/jacocoTestReport/html/index.html`
- **Lint**: `app/build/reports/lint-results-debug.html`

## 🔄 CI/CD

### GitHub Actions

El workflow se ejecuta automáticamente en:
- Push a `main` o `develop`
- Pull Requests hacia `main` o `develop`

**Jobs configurados:**
1. **Test** - Ejecuta tests unitarios e instrumentados
2. **Lint** - Análisis estático del código
3. **Build** - Compilación del APK

**Artefactos generados:**
- Reportes de tests
- Reportes de lint
- APK debug
- Cobertura (Codecov)

### Ver estado del CI
[![Android CI](https://github.com/FelipeCabal/silence-app-mobile/actions/workflows/android-ci.yml/badge.svg)](https://github.com/FelipeCabal/silence-app-mobile/actions/workflows/android-ci.yml)

## 📝 Escribir Nuevos Tests

### Test Unitario para ViewModel

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class MyViewModelTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `test should do something`() = runTest {
        // Given
        val viewModel = MyViewModel()
        
        // When
        viewModel.doSomething()
        advanceUntilIdle()
        
        // Then
        viewModel.state.test {
            assertThat(awaitItem()).isEqualTo(expectedState)
        }
    }
}
```

### Test de UI con Compose

```kotlin
class MyScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun screen_displays_content() {
        // Given
        composeTestRule.setContent {
            MyScreen()
        }
        
        // Then
        composeTestRule.onNodeWithText("Expected Text").assertExists()
    }
}
```

### Test de DAO

```kotlin
@RunWith(AndroidJUnit4::class)
class MyDaoTest {
    
    private lateinit var database: AppDatabase
    private lateinit var dao: MyDao
    
    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.myDao()
    }
    
    @After
    fun tearDown() {
        database.close()
    }
    
    @Test
    fun insert_and_retrieve() = runTest {
        // Given
        val entity = MyEntity(...)
        
        // When
        dao.insert(entity)
        
        // Then
        val retrieved = dao.getById(entity.id)
        assertThat(retrieved).isEqualTo(entity)
    }
}
```

## 🎯 Objetivos de Cobertura

### Metas Actuales
- ✅ ViewModels principales: **100%**
- ✅ Repositories críticos: **100%**
- ✅ DAOs: **90%+**
- 🔄 UI Screens: **60%+** (en progreso)

### Próximos Pasos
1. Agregar tests para más ViewModels (PostViewModel, ProfileViewModel)
2. Tests de integración para más DAOs
3. Tests E2E con flujos completos
4. Performance testing
5. Accessibility testing

## 🐛 Debugging Tests

### Test Falla en CI pero Pasa Local
1. Verificar versión de Android en emulador
2. Revisar timeouts (pueden ser más lentos en CI)
3. Comprobar dependencias de tiempo (`advanceUntilIdle()`)

### Tests Flaky
- Usar `composeTestRule.waitUntil()` en vez de delays
- Asegurar limpieza en `@After`
- Evitar dependencias de orden de ejecución

### Memory Leaks en Tests
- Siempre llamar `database.close()` en DAOs tests
- Usar `unmockkAll()` después de tests con MockK
- Resetear Main dispatcher: `Dispatchers.resetMain()`

## 📚 Referencias

- [MockK Documentation](https://mockk.io/)
- [Turbine by Cash App](https://github.com/cashapp/turbine)
- [Compose Testing](https://developer.android.com/jetpack/compose/testing)
- [Room Testing](https://developer.android.com/training/data-storage/room/testing-db)
- [Coroutines Testing](https://developer.android.com/kotlin/coroutines/test)

## 💡 Tips

1. **Usa `advanceUntilIdle()`** después de operaciones asíncronas en tests
2. **Mock solo lo necesario** - no mockear toda la cadena
3. **Tests descriptivos** - usa nombres como `when_x_then_y`
4. **One assertion per test** cuando sea posible
5. **Given-When-Then** structure para claridad
6. **Turbine para Flows** - más limpio que collectAsState en tests
7. **Truth assertions** - más legibles que JUnit assertions

---

**Última actualización**: Diciembre 2025
**Mantenido por**: Equipo SilenceApp
