# Configuration Java 21 pour le Projet

## Installation de Java 21

### Sur macOS (avec Homebrew)

```bash
# Installer Java 21
brew install openjdk@21

# Configurer JAVA_HOME pour cette session
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# Ajouter à votre ~/.zshrc ou ~/.bash_profile pour rendre permanent
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 21)' >> ~/.zshrc
source ~/.zshrc
```

### Vérification

```bash
java -version
# Devrait afficher: openjdk version "21.x.x"
```

### Alternative: Utiliser SDKMAN

```bash
# Installer SDKMAN
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Installer Java 21
sdk install java 21.0.1-tem

# Utiliser Java 21
sdk use java 21.0.1-tem
```

## Configuration Maven

Le projet est déjà configuré pour utiliser Java 21 dans `pom.xml`:

```xml
<properties>
    <java.version>21</java.version>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
</properties>
```

## Vérifier que Maven utilise Java 21

```bash
mvn -version
# Devrait afficher: Java version: 21.x.x
```

## Exécuter les tests avec Java 21

Une fois Java 21 installé et configuré:

```bash
mvn clean test -pl services/identity-service
```

