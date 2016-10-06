# OmegaT ReStracturedText plugin

## Where you should change?

- Source code: `src/main/java/*`
- Test code: `src/test/java/*` and `src/test/resources/*`
- Project name in `settings.gradle`
- Plugin Main class name in``gradle.properties`.
- Integration test code: `src/integration-test/java/*` and `src/integration-test/resources/*`
- Coding rules: `config/checkstyle/checkstyle.xml
- Source file Header rule: `config/checkstyle/header.txt`

## Build system

The ReStructuredTest filter plugin for OmegaT use Gralde for build system
as same as OmegaT version 4.0 and later.

## Dependency

OmegaT and dependencies are located on remote maven repositories.
It is nessesary to connect the internet at least first time to compile.

## Test report

You can see test result report at `build/reports/` with your favorit web browser.

## Install

Please download zip file from Github release. You can get jar file from zip distribution.
OmegaT plugin should be placed in `$HOME/.omegat/plugin` or `C:\Program Files\OmegaT\plugin`
depending on your operating system.

## License

This project is distributed under the GNU general public license version 3 or later.

