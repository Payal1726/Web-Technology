param(
    [string]$TomcatHome = "C:\Users\payal\OneDrive\Desktop\Tomcat1.1\apache-tomcat-10.1.54-windows-x64\apache-tomcat-10.1.54"
)

$ErrorActionPreference = "Stop"

$shutdownScript = Join-Path $TomcatHome "bin\shutdown.bat"

function Resolve-JavaHome {
    if ($env:JAVA_HOME) {
        return $env:JAVA_HOME
    }

    $installedJdks = Get-ChildItem "C:\Program Files\Java" -Directory -Filter "jdk*" -ErrorAction SilentlyContinue |
        Sort-Object Name -Descending
    if ($installedJdks) {
        return $installedJdks[0].FullName
    }

    return $null
}

if (-not (Test-Path -LiteralPath $shutdownScript)) {
    throw "Tomcat shutdown script not found: $shutdownScript"
}

if (-not $env:JAVA_HOME -and -not $env:JRE_HOME) {
    $resolvedJavaHome = Resolve-JavaHome
    if ($resolvedJavaHome) {
        $env:JAVA_HOME = $resolvedJavaHome
    }
}

$env:CATALINA_HOME = $TomcatHome
$env:CATALINA_BASE = $TomcatHome

Write-Host "Stopping Tomcat from $TomcatHome"
& $shutdownScript
