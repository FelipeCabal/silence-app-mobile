# Script de Testing para SilenceApp
# Ejecuta diferentes tipos de tests

Write-Host "🧪 SilenceApp - Suite de Testing" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

function Show-Menu {
    Write-Host "Selecciona una opción:" -ForegroundColor Yellow
    Write-Host "1. Ejecutar todos los tests unitarios"
    Write-Host "2. Ejecutar tests de un ViewModel específico"
    Write-Host "3. Ejecutar tests de DAOs (requiere emulador)"
    Write-Host "4. Ejecutar tests de UI (requiere emulador)"
    Write-Host "5. Generar reporte de cobertura"
    Write-Host "6. Ejecutar lint"
    Write-Host "7. Ejecutar todos los tests + cobertura + lint"
    Write-Host "8. Ver reportes HTML"
    Write-Host "9. Limpiar build"
    Write-Host "0. Salir"
    Write-Host ""
}

function Run-UnitTests {
    Write-Host "▶️  Ejecutando tests unitarios..." -ForegroundColor Green
    .\gradlew test --info
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Tests unitarios completados exitosamente!" -ForegroundColor Green
        Write-Host "📊 Reporte disponible en: app/build/reports/tests/testDebugUnitTest/index.html" -ForegroundColor Cyan
    } else {
        Write-Host "❌ Algunos tests fallaron. Revisa el reporte." -ForegroundColor Red
    }
}

function Run-ViewModelTests {
    Write-Host "Selecciona el ViewModel a testear:" -ForegroundColor Yellow
    Write-Host "1. SearchViewModel"
    Write-Host "2. AuthViewModel"
    Write-Host "3. ChatViewModel"
    Write-Host "4. ProfileViewModel"
    Write-Host "5. PostViewModel"
    
    $choice = Read-Host "Opción"
    
    $testClass = switch ($choice) {
        "1" { "SearchViewModelTest" }
        "2" { "AuthViewModelTest" }
        "3" { "ChatViewModelTest" }
        "4" { "ProfileViewModelTest" }
        "5" { "PostViewModelTest" }
        default { "" }
    }
    
    if ($testClass -ne "") {
        Write-Host "▶️  Ejecutando tests de $testClass..." -ForegroundColor Green
        .\gradlew test --tests $testClass
    } else {
        Write-Host "❌ Opción inválida" -ForegroundColor Red
    }
}

function Run-DaoTests {
    Write-Host "⚠️  Asegúrate de tener un emulador o dispositivo conectado" -ForegroundColor Yellow
    Read-Host "Presiona Enter para continuar..."
    
    Write-Host "▶️  Ejecutando tests de DAOs..." -ForegroundColor Green
    .\gradlew connectedAndroidTest --info
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Tests de DAOs completados exitosamente!" -ForegroundColor Green
        Write-Host "📊 Reporte disponible en: app/build/reports/androidTests/connected/index.html" -ForegroundColor Cyan
    } else {
        Write-Host "❌ Algunos tests fallaron. Revisa el reporte." -ForegroundColor Red
    }
}

function Run-UITests {
    Write-Host "⚠️  Asegúrate de tener un emulador o dispositivo conectado" -ForegroundColor Yellow
    Read-Host "Presiona Enter para continuar..."
    
    Write-Host "▶️  Ejecutando tests de UI..." -ForegroundColor Green
    .\gradlew connectedAndroidTest --tests "*.view.*" --info
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Tests de UI completados exitosamente!" -ForegroundColor Green
    } else {
        Write-Host "❌ Algunos tests fallaron. Revisa el reporte." -ForegroundColor Red
    }
}

function Generate-Coverage {
    Write-Host "▶️  Generando reporte de cobertura..." -ForegroundColor Green
    .\gradlew test jacocoTestReport
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Reporte de cobertura generado!" -ForegroundColor Green
        Write-Host "📊 Reporte disponible en: app/build/reports/jacoco/jacocoTestReport/html/index.html" -ForegroundColor Cyan
        
        # Intentar abrir el reporte automáticamente
        $reportPath = "app\build\reports\jacoco\jacocoTestReport\html\index.html"
        if (Test-Path $reportPath) {
            $open = Read-Host "¿Deseas abrir el reporte ahora? (s/n)"
            if ($open -eq "s") {
                Start-Process $reportPath
            }
        }
    } else {
        Write-Host "❌ Error al generar el reporte de cobertura" -ForegroundColor Red
    }
}

function Run-Lint {
    Write-Host "▶️  Ejecutando análisis de lint..." -ForegroundColor Green
    .\gradlew lint
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Lint completado!" -ForegroundColor Green
        Write-Host "📊 Reporte disponible en: app/build/reports/lint-results-debug.html" -ForegroundColor Cyan
    } else {
        Write-Host "⚠️  Se encontraron problemas. Revisa el reporte." -ForegroundColor Yellow
    }
}

function Run-AllTests {
    Write-Host "▶️  Ejecutando suite completa de tests..." -ForegroundColor Green
    Write-Host ""
    
    Write-Host "1️⃣  Tests Unitarios..." -ForegroundColor Cyan
    .\gradlew test
    
    Write-Host ""
    Write-Host "2️⃣  Reporte de Cobertura..." -ForegroundColor Cyan
    .\gradlew jacocoTestReport
    
    Write-Host ""
    Write-Host "3️⃣  Análisis de Lint..." -ForegroundColor Cyan
    .\gradlew lint
    
    Write-Host ""
    Write-Host "✅ Suite completa ejecutada!" -ForegroundColor Green
    Write-Host ""
    Write-Host "📊 Reportes disponibles:" -ForegroundColor Cyan
    Write-Host "   - Tests: app/build/reports/tests/testDebugUnitTest/index.html"
    Write-Host "   - Cobertura: app/build/reports/jacoco/jacocoTestReport/html/index.html"
    Write-Host "   - Lint: app/build/reports/lint-results-debug.html"
}

function Show-Reports {
    Write-Host "Abriendo reportes..." -ForegroundColor Green
    
    $reports = @(
        @{Name="Tests Unitarios"; Path="app\build\reports\tests\testDebugUnitTest\index.html"},
        @{Name="Cobertura"; Path="app\build\reports\jacoco\jacocoTestReport\html\index.html"},
        @{Name="Lint"; Path="app\build\reports\lint-results-debug.html"}
    )
    
    foreach ($report in $reports) {
        if (Test-Path $report.Path) {
            Write-Host "✓ $($report.Name): $($report.Path)" -ForegroundColor Green
            Start-Process $report.Path
            Start-Sleep -Milliseconds 500
        } else {
            Write-Host "✗ $($report.Name): No encontrado. Ejecuta los tests primero." -ForegroundColor Yellow
        }
    }
}

function Clean-Build {
    Write-Host "🧹 Limpiando build..." -ForegroundColor Yellow
    .\gradlew clean
    Write-Host "✅ Build limpiado!" -ForegroundColor Green
}

# Main loop
do {
    Show-Menu
    $choice = Read-Host "Opción"
    Write-Host ""
    
    switch ($choice) {
        "1" { Run-UnitTests }
        "2" { Run-ViewModelTests }
        "3" { Run-DaoTests }
        "4" { Run-UITests }
        "5" { Generate-Coverage }
        "6" { Run-Lint }
        "7" { Run-AllTests }
        "8" { Show-Reports }
        "9" { Clean-Build }
        "0" { 
            Write-Host "👋 ¡Hasta luego!" -ForegroundColor Cyan
            exit 
        }
        default { Write-Host "❌ Opción inválida" -ForegroundColor Red }
    }
    
    Write-Host ""
    Write-Host "Presiona Enter para continuar..."
    Read-Host
    Clear-Host
    
} while ($true)
