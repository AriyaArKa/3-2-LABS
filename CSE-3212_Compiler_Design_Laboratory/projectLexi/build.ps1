# ============================================
# EMOJI-FLOW Lexer Build Script (PowerShell)
# Author: Arka Braja Prasad Nath (Roll: 2107055)
# ============================================

param(
    [Parameter(Position=0)]
    [string]$Command = "all"
)

$LEX_FILE = "emoji_flow.l"
$LEX_OUTPUT = "lex.yy.c"
$EXECUTABLE = "emoji_lexer.exe"
$OUTPUT_DIR = "output"

function Show-Banner {
    Write-Host "============================================" -ForegroundColor Cyan
    Write-Host "  EMOJI-FLOW Lexer Build System" -ForegroundColor Yellow
    Write-Host "============================================" -ForegroundColor Cyan
}

function Build-Lexer {
    Write-Host "`n[1/2] Generating C code from Flex..." -ForegroundColor Green
    flex $LEX_FILE
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Error: Flex failed!" -ForegroundColor Red
        exit 1
    }
    
    Write-Host "[2/2] Compiling lexer..." -ForegroundColor Green
    gcc $LEX_OUTPUT -o $EXECUTABLE
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Error: GCC compilation failed!" -ForegroundColor Red
        exit 1
    }
    
    Write-Host "`nBuild successful: $EXECUTABLE" -ForegroundColor Green
}

function Run-AllSamples {
    if (-not (Test-Path $EXECUTABLE)) {
        Write-Host "Lexer not found. Building first..." -ForegroundColor Yellow
        Build-Lexer
    }
    
    if (-not (Test-Path $OUTPUT_DIR)) {
        New-Item -ItemType Directory -Path $OUTPUT_DIR | Out-Null
    }
    
    Write-Host "`nRunning lexer on all samples..." -ForegroundColor Green
    Write-Host "============================================" -ForegroundColor Cyan
    
    $samples = @("hello", "loop", "condition", "variables")
    
    foreach ($sample in $samples) {
        $inputFile = "samples\$sample.emoji"
        $outputFile = "$OUTPUT_DIR\${sample}_tokens.txt"
        
        Write-Host "`n--- Processing $sample.emoji ---" -ForegroundColor Yellow
        & ".\$EXECUTABLE" $inputFile $outputFile
    }
    
    Write-Host "`n============================================" -ForegroundColor Cyan
    Write-Host "All samples processed!" -ForegroundColor Green
    Write-Host "Output files in: $OUTPUT_DIR\" -ForegroundColor Green
}

function Clean-Files {
    Write-Host "Cleaning build files..." -ForegroundColor Yellow
    
    if (Test-Path $LEX_OUTPUT) { Remove-Item $LEX_OUTPUT -Force }
    if (Test-Path $EXECUTABLE) { Remove-Item $EXECUTABLE -Force }
    if (Test-Path $OUTPUT_DIR) { Remove-Item $OUTPUT_DIR -Recurse -Force }
    if (Test-Path "*.txt" -Exclude "*.md") { 
        Get-ChildItem -Filter "*_tokens.txt" | Remove-Item -Force 
    }
    
    Write-Host "Clean complete." -ForegroundColor Green
}

function Show-Outputs {
    Write-Host "`n============================================" -ForegroundColor Cyan
    Write-Host "  Token Output Files" -ForegroundColor Yellow
    Write-Host "============================================" -ForegroundColor Cyan
    
    if (Test-Path $OUTPUT_DIR) {
        Get-ChildItem "$OUTPUT_DIR\*.txt" | ForEach-Object {
            Write-Host "`n--- $($_.Name) ---" -ForegroundColor Yellow
            Get-Content $_.FullName -Encoding UTF8
        }
    } else {
        Write-Host "No output files found. Run 'build.ps1 run' first." -ForegroundColor Red
    }
}

function Show-Help {
    Write-Host @"

============================================
  EMOJI-FLOW Lexer - Build Script Help
============================================

Usage: .\build.ps1 [command]

Commands:
  all        - Clean, build, and run all samples (default)
  build      - Build the lexer only
  run        - Run lexer on all samples
  clean      - Remove generated files
  show       - Display all token outputs
  help       - Show this help message

Examples:
  .\build.ps1              # Build and run everything
  .\build.ps1 build        # Just build the lexer
  .\build.ps1 run          # Run on all samples
  .\build.ps1 clean        # Clean up files
  .\build.ps1 show         # View token outputs

============================================
"@
}

# Main execution
Show-Banner

switch ($Command.ToLower()) {
    "all" {
        Clean-Files
        Build-Lexer
        Run-AllSamples
    }
    "build" {
        Build-Lexer
    }
    "run" {
        Run-AllSamples
    }
    "clean" {
        Clean-Files
    }
    "show" {
        Show-Outputs
    }
    "help" {
        Show-Help
    }
    default {
        Write-Host "Unknown command: $Command" -ForegroundColor Red
        Show-Help
    }
}
