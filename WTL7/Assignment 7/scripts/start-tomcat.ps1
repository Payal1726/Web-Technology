param(
    [string]$TomcatHome = "C:\Users\payal\OneDrive\Desktop\Tomcat1.1\apache-tomcat-10.1.54-windows-x64\apache-tomcat-10.1.54",
    [string]$DbUrl = "jdbc:mysql://localhost:3306/ebookshop",
    [string]$DbUser = "root",
    [string]$DbPassword = ""
)

$ErrorActionPreference = "Stop"

$startupScript = Join-Path $TomcatHome "bin\startup.bat"

function Resolve-JavaHome {
    if ($env:JAVA_HOME) {
        return $env:JAVA_HOME
    }

    $installedJdks = Get-ChildItem "C:\Program Files\Java" -Directory -Filter "jdk*" -ErrorAction SilentlyContinue |
        Sort-Object Name -Descending
    if ($installedJdks) {
        return $installedJdks[0].FullName
    }

    throw "JAVA_HOME is not set and no JDK was found in C:\\Program Files\\Java."
}

if (-not (Test-Path -LiteralPath $startupScript)) {
    throw "Tomcat startup script not found: $startupScript"
}

if (-not $env:JAVA_HOME -and -not $env:JRE_HOME) {
    $env:JAVA_HOME = Resolve-JavaHome
}

$listener = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
if ($listener) {
    Write-Host "Port 8080 is already in use. If Tomcat is already running, open http://localhost:8080/ebookshop/."
    return
}

$env:EBOOKSHOP_DB_URL = $DbUrl
$env:EBOOKSHOP_DB_USER = $DbUser
$env:EBOOKSHOP_DB_PASSWORD = $DbPassword
$env:CATALINA_HOME = $TomcatHome
$env:CATALINA_BASE = $TomcatHome

Write-Host "Starting Tomcat from $TomcatHome"
Write-Host "Using JAVA_HOME=$env:JAVA_HOME"
& $startupScript

for ($attempt = 0; $attempt -lt 15; $attempt++) {
    Start-Sleep -Seconds 1
    $listener = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
    if ($listener) {
        Write-Host "Tomcat started successfully."
        Write-Host "App URL: http://localhost:8080/ebookshop/"
        exit 0
    }
}

throw "Tomcat did not start on port 8080. Check the logs in $TomcatHome\logs."
