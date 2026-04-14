param(
    [string]$TomcatHome = "C:\Users\payal\OneDrive\Desktop\Tomcat1.1\apache-tomcat-10.1.54-windows-x64\apache-tomcat-10.1.54",
    [string]$ContextPath = "ebookshop"
)

$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
$sourceServlet = Join-Path $projectRoot "BookServlet.java"
$sourceWebInf = Join-Path $projectRoot "WEB-INF"
$sourceClasses = Join-Path $sourceWebInf "classes"
$servletApiJar = Join-Path $TomcatHome "lib\servlet-api.jar"
$webappsDir = Join-Path $TomcatHome "webapps"
$targetDir = Join-Path $webappsDir $ContextPath
$targetWar = Join-Path $webappsDir ($ContextPath + ".war")
$buildRoot = Join-Path $projectRoot "build"
$stagingDir = Join-Path $buildRoot "tomcat-deploy\$ContextPath"

function Assert-PathExists {
    param(
        [string]$PathToCheck,
        [string]$Description
    )

    if (-not (Test-Path -LiteralPath $PathToCheck)) {
        throw "$Description not found: $PathToCheck"
    }
}

function Assert-ChildPath {
    param(
        [string]$ChildPath,
        [string]$ParentPath,
        [string]$Description
    )

    $resolvedChild = [System.IO.Path]::GetFullPath($ChildPath)
    $resolvedParent = [System.IO.Path]::GetFullPath($ParentPath)

    if (-not $resolvedParent.EndsWith([System.IO.Path]::DirectorySeparatorChar)) {
        $resolvedParent += [System.IO.Path]::DirectorySeparatorChar
    }

    if (-not $resolvedChild.StartsWith($resolvedParent, [System.StringComparison]::OrdinalIgnoreCase)) {
        throw "$Description must stay inside $resolvedParent but resolved to $resolvedChild"
    }
}

Assert-PathExists -PathToCheck $TomcatHome -Description "Tomcat home"
Assert-PathExists -PathToCheck $servletApiJar -Description "Servlet API JAR"
Assert-PathExists -PathToCheck $sourceServlet -Description "Servlet source file"
Assert-PathExists -PathToCheck $sourceWebInf -Description "WEB-INF folder"

New-Item -ItemType Directory -Force -Path $sourceClasses | Out-Null

Write-Host "Compiling BookServlet.java against $servletApiJar"
& javac -cp $servletApiJar -d $sourceClasses $sourceServlet
if ($LASTEXITCODE -ne 0) {
    throw "Compilation failed."
}

if (Test-Path -LiteralPath $stagingDir) {
    Assert-ChildPath -ChildPath $stagingDir -ParentPath $buildRoot -Description "Staging directory"
    Remove-Item -LiteralPath $stagingDir -Recurse -Force
}

New-Item -ItemType Directory -Force -Path $stagingDir | Out-Null
Copy-Item -LiteralPath (Join-Path $projectRoot "index.html") -Destination (Join-Path $stagingDir "index.html")
Copy-Item -LiteralPath $sourceWebInf -Destination $stagingDir -Recurse

if (Test-Path -LiteralPath $targetDir) {
    Assert-ChildPath -ChildPath $targetDir -ParentPath $webappsDir -Description "Deployment directory"
    Remove-Item -LiteralPath $targetDir -Recurse -Force
}

if (Test-Path -LiteralPath $targetWar) {
    Assert-ChildPath -ChildPath $targetWar -ParentPath $webappsDir -Description "Deployment WAR"
    Remove-Item -LiteralPath $targetWar -Force
}

Copy-Item -LiteralPath $stagingDir -Destination $targetDir -Recurse

Write-Host "Deployment complete."
Write-Host "Application path: $targetDir"
