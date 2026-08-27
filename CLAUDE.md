# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this project is

**smp-mate** ("SMP MAintenance TEchnology") is a Java CLI bulk-provisioning tool for a Peppol Service Metadata Publisher (SMP). It reads a JSON *task file* and provisions Participant Identifiers (with optional Business Card XML) into an SMP via REST. Single-module Maven project, Java 25 source/target, JUnit 4 tests.

The README (`README.md`) is German-only.

## Build, run, test

```bash
mvn clean install                                        # build + tests; produces target/smp-mate-<v>-app.jar
java -jar target/smp-mate-<version>-app.jar <task.json>  # primary run mode
mvn test -Dtest=SmpServiceTest                           # single test class
mvn test -Dtest=SmpServiceTest#methodName                # single test method
```

The `make-package` assembly execution also produces a distribution ZIP with sample config.

## User-facing contract — keep backwards-compatible

The JSON task file is the user-facing contract. When editing the config layer, all three must stay in sync, or the shipped template breaks for users:

- `com.helger.smpmate.config.SPTask` — the in-memory model
- `com.helger.smpmate.config.SPReader` — JSON → SPTask parsing (Gson)
- `src/main/resources/default.task.json` — the template users start from

Renaming or removing a JSON field is a breaking change; add fields as optional.

## Project layout

```
src/main/java/com/helger/smpmate/
  Main.java          # entry point (main class declared in pom.xml)
  args/              # CLI argument parsing (SPArg*, SPParser, ESPArgOption)
  business/          # SmpService, UserConfigurator, Statistics
  config/            # SPTask, SPReader, SPPaths, SPServiceMetadata, ValidationException
  log/               # MyLog
  util/              # Tokenizer
src/main/resources/  # default.task.json, smp-mate-version.properties (Maven-filtered)
src/main/assembly/   # cmd.xml (executable JAR), zip.xml (distribution ZIP)
src/test/resources/  # sample task JSON files + XML templates for tests
```

## Conventions specific to this repo

- Apache 2.0 license header is required at the top of every `.java` file — copy verbatim from any existing source file (the current header reads "Copyright (C) 2022-204 Philip Helger"; do not "correct" the year).
- Nullability annotations are `javax.annotation.*` (JSR-305: `@Nonnull`, `@Nullable`, `@Nonnegative`) — this project does NOT use ph-commons in the runtime classpath; only Gson + JSR-305.
- Tests are JUnit 4 (`org.junit.Test`, `org.junit.Assert.*`) — do not introduce JUnit 5 here.
- The `/bin/` directory at the repo root is Eclipse output and is gitignored. Never edit anything inside it; all source lives under `src/`.

## Releases

Releases go through `maven-release-plugin` (`mvn release:prepare release:perform`) inherited from `com.helger:parent-pom`. Version bumps appear as standalone commits ("Verison bump", "Version bump"); do not bump the version yourself unless explicitly asked.

## Global rules

The personal global rules in `~/.claude/rules/` (Hungarian notation, naming conventions, logging style, truth/non-guessing, working behaviour) apply to all work in this repo and are not duplicated here.
