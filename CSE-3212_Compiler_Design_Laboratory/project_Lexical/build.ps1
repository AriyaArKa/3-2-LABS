# EMOJI-FLOW Flex/Bison Build Script
param([string]$Command = "all")

$LEX_FILE = "emoji_flow.l"
$YACC_FILE = "emoji_flow.y"
$PARSER_C = "emoji_flow.tab.c"
$PARSER_H = "emoji_flow.tab.h"
$LEXER_C = "lex.yy.c"
$EXECUTABLE = "emoji_parser.exe"

function Get-FirstAvailableTool {
    param(
        [string[]]$Candidates,
        [string]$ToolLabel
    )

    foreach ($candidate in $Candidates) {
        if (Test-Path $candidate) { return $candidate }

        $cmd = Get-Command $candidate -ErrorAction SilentlyContinue
        if ($cmd) { return $cmd.Source }
    }

    Write-Host "$ToolLabel not found. Tried: $($Candidates -join ', ')" -ForegroundColor Red
    exit 1
}

function Build-Processor {
    Write-Host "Building EMOJI-FLOW Flex/Bison processor..." -ForegroundColor Green

    $BisonTool = Get-FirstAvailableTool @(
        "C:\Users\$env:USERNAME\AppData\Local\Microsoft\WinGet\Packages\WinFlexBison.win_flex_bison_Microsoft.Winget.Source_8wekyb3d8bbwe\win_bison.exe",
        "win_bison",
        "bison"
    ) "Bison"

    $FlexTool = Get-FirstAvailableTool @(
        "C:\Users\$env:USERNAME\AppData\Local\Microsoft\WinGet\Packages\WinFlexBison.win_flex_bison_Microsoft.Winget.Source_8wekyb3d8bbwe\win_flex.exe",
        "win_flex",
        "flex"
    ) "Flex"

    $GccTool = Get-FirstAvailableTool @("gcc") "GCC"

    & $BisonTool -d -o $PARSER_C $YACC_FILE
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Bison failed!" -ForegroundColor Red
        exit 1
    }

    & $FlexTool -o $LEXER_C $LEX_FILE
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Flex failed!" -ForegroundColor Red
        exit 1
    }

    & $GccTool $PARSER_C $LEXER_C -o $EXECUTABLE
    if ($LASTEXITCODE -ne 0) {
        Write-Host "GCC failed!" -ForegroundColor Red
        exit 1
    }

    Write-Host "Build successful: $EXECUTABLE" -ForegroundColor Green
}

function Generate-Output {
    if (-not (Test-Path $EXECUTABLE)) { Build-Processor }
    
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

function Run-Semantic-Tests {
    if (-not (Test-Path $EXECUTABLE)) { Build-Processor }

    $testDir = "samples\semantic_tests"
    if (-not (Test-Path $testDir)) {
        Write-Host "Semantic test directory not found: $testDir" -ForegroundColor Red
        exit 1
    }

    if (-not (Test-Path "output\semantic_tests")) {
        New-Item -ItemType Directory -Path "output\semantic_tests" -Force | Out-Null
    }

    $files = Get-ChildItem "$testDir\*.emoji"
    if ($files.Count -eq 0) {
        Write-Host "No semantic test files found in $testDir" -ForegroundColor Yellow
        return
    }

    $passed = 0
    $failed = 0

    Write-Host "Running semantic negative tests (expected to fail)..." -ForegroundColor Yellow

    foreach ($file in $files) {
        $outputFile = "output\semantic_tests\$($file.BaseName)_tokens.txt"
        $firstLine = Get-Content $file.FullName -TotalCount 1
        $expectedFragment = ""

        if ($firstLine -match "EXPECT_ERROR:\s*(.+)$") {
            $expectedFragment = $Matches[1].Trim()
        }

        Write-Host "Test: $($file.Name)" -ForegroundColor Cyan

        $runOutput = & ".\$EXECUTABLE" $file.FullName $outputFile 2>&1 | Out-String
        $exitCode = $LASTEXITCODE

        $exitFail = ($exitCode -ne 0)
        $normalizedOutput = ($runOutput -replace "\s+", "").ToLowerInvariant()
        $normalizedExpected = ($expectedFragment -replace "\s+", "").ToLowerInvariant()
        $containsExpected = ($expectedFragment -eq "" -or $normalizedOutput.Contains($normalizedExpected))

        if ($exitFail -and $containsExpected) {
            Write-Host "  ✅ PASS (failed as expected)" -ForegroundColor Green
            $passed++
        }
        else {
            Write-Host "  ❌ FAIL" -ForegroundColor Red
            if (-not $exitFail) {
                Write-Host "     Reason: test did not fail (exit code 0)." -ForegroundColor Red
            }
            if (-not $containsExpected) {
                Write-Host "     Reason: expected error fragment not found: '$expectedFragment'" -ForegroundColor Red
            }
            $failed++
        }
    }

    Write-Host "`nSemantic tests summary: $passed passed, $failed failed." -ForegroundColor Yellow
    if ($failed -gt 0) {
        exit 1
    }
}

function Clean-Files {
    Write-Host "Cleaning..." -ForegroundColor Yellow
    Remove-Item -Path $PARSER_C, $PARSER_H, $LEXER_C, $EXECUTABLE, "output" -Recurse -Force -ErrorAction SilentlyContinue
    Write-Host "Clean complete." -ForegroundColor Green
}

# Main execution
switch ($Command.ToLower()) {
    "build" { Build-Processor }
    "generate" { Generate-Output }
    "semantic-tests" { Run-Semantic-Tests }
    "clean" { Clean-Files }
    "all" { Build-Processor; Generate-Output }
    default { 
        # Default behavior: build and generate output
        Build-Processor
        Generate-Output
    }
}
