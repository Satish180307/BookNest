# BookNest Backend Runner
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "Starting BookNest Spring Boot Backend (Port 8080)" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host ""

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location "$scriptDir\backend"

if (-not $env:DB_PASSWORD) {
    Write-Host "Note: DB_PASSWORD environment variable is not set." -ForegroundColor Yellow
    $inputPassword = Read-Host "Enter MySQL Root Password (press Enter if no password)"
    $env:DB_PASSWORD = $inputPassword
}

Write-Host "Starting Spring Boot application..." -ForegroundColor Green
java -jar target\booknest-backend-1.0.0.jar
