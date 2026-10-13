# Building Ghidra for Squirreldra

## Requirements (current master)

- JDK 25 (master rejects `source release: 25` on JDK 21)
- Gradle 9.x (wrapper provided)
- Python 3 (for some native/script tasks)

## First-time setup

```bash
git clone https://github.com/NationalSecurityAgency/ghidra.git
cd ghidra
mkdir -p dependencies/flatRepo
./gradlew -I gradle/support/fetchDependencies.gradle
# If the protobuf property is missing:
echo 'ghidra.protobuf.java.version=4.31.0' >> gradle.properties
./gradlew buildGhidra
```

## Common failures

| Symptom | Cause | Fix |
|---------|-------|-----|
| `Unrecognized VM option 'UseCompactObjectHeaders'` | Gradle 9.8 + JDK 21 | Override `org.gradle.jvmargs` in `gradle.properties` or use JDK 25 |
| `Unable to find the local maven repo` | Missing `dependencies/flatRepo` | `mkdir -p dependencies/flatRepo` |
| `Failed to locate prebuilt TLB file` | Dependencies not fetched | Run `./gradlew -I gradle/support/fetchDependencies.gradle` |
| `ghidra.protobuf.java.version` missing | Property not in local `gradle.properties` | Add `ghidra.protobuf.java.version=4.31.0` |
| `invalid source release: 25` | JDK too old for current master | Install JDK 25 or check out a 12.1.x tag |

## After the build

The distribution is a zip under `build/dist/`. Extract it:

```bash
cd build/dist
unzip ghidra_*_DEV_*.zip
chmod +x ghidra_*_DEV/ghidraRun ghidra_*_DEV/support/*.sh
# Native helpers (demangler, etc.) may also need +x
find ghidra_*_DEV -type f -name "demangler*" -exec chmod +x {} \;
```

## Applying the Win16 patch before building

```bash
git apply /path/to/squirreldra/patches/ghidra-win16-fixes.patch
./gradlew buildGhidra
```

Applying the cspec portion more than once produces duplicate prototype errors. Keep only one copy of `__pascal16far` and `__pascal16near`.
