# =====================================================
# Script de lancement JavaFX - LinguaLearn Forum
# =====================================================

$JAVA = "C:\Program Files\Java\jdk-21\bin\java.exe"
$JAVAC = "C:\Program Files\Java\jdk-21\bin\javac.exe"

$BASE = "$PSScriptRoot"
$SRC = "$BASE\src\main\java"
$RESOURCES = "$BASE\src\main\resources"
$OUT = "$BASE\target\classes"

# --- JARs depuis le cache Maven local ---
$M2 = "$env:USERPROFILE\.m2\repository"

$FX_BASE     = "$M2\org\openjfx\javafx-base\17.0.2\javafx-base-17.0.2-win.jar"
$FX_CTRL     = "$M2\org\openjfx\javafx-controls\17.0.2\javafx-controls-17.0.2-win.jar"
$FX_FXML     = "$M2\org\openjfx\javafx-fxml\17.0.2\javafx-fxml-17.0.2-win.jar"
$FX_GRAPHICS = "$M2\org\openjfx\javafx-graphics\17.0.2\javafx-graphics-17.0.2-win.jar"
$MYSQL       = "$M2\com\mysql\mysql-connector-j\8.0.33\mysql-connector-j-8.0.33.jar"

$CP = "$FX_BASE;$FX_CTRL;$FX_FXML;$FX_GRAPHICS;$MYSQL"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  LinguaLearn JavaFX Forum - Launcher  " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# --- Création du dossier de sortie ---
if (!(Test-Path $OUT)) { New-Item -ItemType Directory -Path $OUT | Out-Null }

# --- Copie des resources ---
Write-Host "`n[1/3] Copie des resources..." -ForegroundColor Yellow
Copy-Item -Recurse -Force "$RESOURCES\*" "$OUT\"

# --- Collecte des sources Java ---
Write-Host "[2/3] Compilation..." -ForegroundColor Yellow
$sources = Get-ChildItem -Recurse -Filter "*.java" $SRC | Select-Object -ExpandProperty FullName
$sources | Out-File "$BASE\target\sources.txt" -Encoding UTF8
$sources = Get-ChildItem -Recurse -Filter "*.java" $SRC | Select-Object -ExpandProperty FullName

# Ecrire les sources avec guillemets pour geerer les espaces
$sourcesQuoted = $sources | ForEach-Object { "`"$_`"" }
$sourcesQuoted | Out-File "$OUT\sources.txt" -Encoding ASCII

& $JAVAC `
    "--module-path" "$FX_BASE;$FX_CTRL;$FX_FXML;$FX_GRAPHICS" `
    "--add-modules" "javafx.controls,javafx.fxml" `
    "-cp" "$CP" `
    "-d" "$OUT" `
    "-encoding" "UTF-8" `
    $sources

if ($LASTEXITCODE -ne 0) {
    Write-Host "`n[ERREUR] Compilation echouee." -ForegroundColor Red
    exit 1
}

Write-Host "[3/3] Lancement de l'application..." -ForegroundColor Green

& $JAVA `
    --module-path "$FX_BASE;$FX_CTRL;$FX_FXML;$FX_GRAPHICS" `
    --add-modules javafx.controls,javafx.fxml `
    -cp "$OUT;$CP" `
    com.esprit.Main

