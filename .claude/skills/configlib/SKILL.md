---
description: Load ConfigLib API from the Gradle source cache and generate or explain ConfigLib code. Usage: /configlib <request>
allowed-tools: Bash Read Glob Grep
---

## Step 1 — Extract ConfigLib sources

Run the extraction script:

```bash
bash .claude/skills/configlib/scripts/extract-sources.sh
```

The script prints one of:
- A JAR path → sources were extracted to `/tmp/configlib-sources/`
- `LOCAL_BUILD` → ConfigLib source tree is available at the project root

## Step 2 — Read the key source files

**If extracted JAR** (base path: `/tmp/configlib-sources/`):

- `README.md`

Core:
- `net/kunmc/lab/configlib/CommonBaseConfig.java`
- `net/kunmc/lab/configlib/Value.java`
- `net/kunmc/lab/configlib/SingleValue.java`
- `net/kunmc/lab/configlib/CollectionValue.java`
- `net/kunmc/lab/configlib/MapValue.java`
- `net/kunmc/lab/configlib/NumericValue.java`
- `net/kunmc/lab/configlib/ConfigCommandBuilder.java`

Value types:
- All `.java` files under `net/kunmc/lab/configlib/value/`
- All `.java` files under `net/kunmc/lab/configlib/value/collection/`
- All `.java` files under `net/kunmc/lab/configlib/value/map/`
- All `.java` files under `net/kunmc/lab/configlib/value/tuple/`

**If LOCAL_BUILD** (base path: project root):

- `README.md`

Core:
- `common/src/main/java/net/kunmc/lab/configlib/CommonBaseConfig.java`
- `bukkit/src/main/java/net/kunmc/lab/configlib/BaseConfig.java`
- `common/src/main/java/net/kunmc/lab/configlib/Value.java`
- `common/src/main/java/net/kunmc/lab/configlib/SingleValue.java`
- `common/src/main/java/net/kunmc/lab/configlib/CollectionValue.java`
- `common/src/main/java/net/kunmc/lab/configlib/MapValue.java`
- `common/src/main/java/net/kunmc/lab/configlib/NumericValue.java`
- `common/src/main/java/net/kunmc/lab/configlib/ConfigCommandBuilder.java`

Value types:
- All `.java` files under `common/src/main/java/net/kunmc/lab/configlib/value/`
- All `.java` files under `common/src/main/java/net/kunmc/lab/configlib/value/collection/`
- All `.java` files under `common/src/main/java/net/kunmc/lab/configlib/value/map/`
- All `.java` files under `common/src/main/java/net/kunmc/lab/configlib/value/tuple/`
- All `.java` files under `bukkit/src/main/java/net/kunmc/lab/configlib/value/`
- All `.java` files under `bukkit/src/main/java/net/kunmc/lab/configlib/value/collection/`
- All `.java` files under `bukkit/src/main/java/net/kunmc/lab/configlib/value/map/`
- All `.java` files under `bukkit/src/main/java/net/kunmc/lab/configlib/value/tuple/`

> Only read what the request actually needs. For simple usage questions the core files and
> one `value/` directory are usually sufficient.

## Step 3 — Apply conventions

Read and apply the conventions in `.claude/skills/configlib/references/conventions.md`.

## Step 4 — Respond to the request

Respond in the same language as the user's request.
If asked to generate code, output a complete working implementation.
If asked how to do something, explain with a code example.

$ARGUMENTS