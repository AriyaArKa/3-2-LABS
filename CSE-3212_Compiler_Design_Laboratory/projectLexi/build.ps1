# EMOJI-FLOW Lexer Build Script
param([string]$Command = "all")

$LEX_FILE = "emoji_flow.l"
$EXECUTABLE = "emoji_lexer.exe"

function Build-Lexer {
    Write-Host "Building EMOJI-FLOW Lexer..." -ForegroundColor Green
    
    flex $LEX_FILE
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Flex failed!" -ForegroundColor Red
        exit 1
    }
    
    gcc lex.yy.c -o $EXECUTABLE
    if ($LASTEXITCODE -ne 0) {
        Write-Host "GCC failed!" -ForegroundColor Red
        exit 1
    }
    
    Write-Host "Build successful: $EXECUTABLE" -ForegroundColor Green
}

function Generate-Output {
    if (-not (Test-Path $EXECUTABLE)) { Build-Lexer }
    
    Write-Host "Generating output from sample files..." -ForegroundColor Yellow
    
    if (-not (Test-Path "output")) { New-Item -ItemType Directory -Path "output" | Out-Null }
    
    Get-ChildItem "samples\*.emoji" | ForEach-Object {
        $outputFile = "output\$($_.BaseName)_tokens.txt"
        Write-Host "Processing $($_.Name) -> $($_.BaseName)_tokens.txt" -ForegroundColor Cyan
        & ".\$EXECUTABLE" $_.FullName $outputFile
        Write-Host "  ✅ Generated: $outputFile" -ForegroundColor Green
    }
    
    Write-Host "`nOutput generation complete! Check 'output' folder." -ForegroundColor Green
}

function Clean-Files {
    Write-Host "Cleaning..." -ForegroundColor Yellow
    Remove-Item -Path "lex.yy.c", $EXECUTABLE, "output" -Recurse -Force -ErrorAction SilentlyContinue
    Write-Host "Clean complete." -ForegroundColor Green
}

# Main execution
switch ($Command.ToLower()) {
    "build" { Build-Lexer }
    "generate" { Generate-Output }
    "clean" { Clean-Files }
    "all" { Build-Lexer; Generate-Output }
    default { 
        # Default behavior: build and generate output
        Build-Lexer
        Generate-Output
    }
}
