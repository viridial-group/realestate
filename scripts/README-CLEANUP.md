# Script de Nettoyage VPS Ubuntu

Ce script nettoie automatiquement les fichiers temporaires, logs anciens, caches et autres fichiers inutiles sur un VPS Ubuntu.

## Usage

```bash
# Mode normal (supprime réellement les fichiers)
./scripts/cleanup-vps.sh

# Mode dry-run (simulation, ne supprime rien)
./scripts/cleanup-vps.sh --dry-run

# Mode agressif (nettoyage plus approfondi)
./scripts/cleanup-vps.sh --aggressive

# Combinaison des deux
./scripts/cleanup-vps.sh --dry-run --aggressive
```

## Ce qui est nettoyé

### 1. Packages
- Cache apt (`apt-get clean`)
- Packages inutilisés (`apt-get autoremove`)
- Packages obsolètes (`apt-get autoclean`)

### 2. Logs système
- Logs journald de plus de 7 jours
- Logs compressés de plus de 30 jours dans `/var/log`
- Logs de plus de 7 jours (mode agressif uniquement)

### 3. Fichiers temporaires
- Fichiers dans `/tmp` et `/var/tmp` de plus de 7 jours
- Caches utilisateur de plus de 7 jours
- Répertoires vides

### 4. Logs de l'application
- Logs de plus de 30 jours dans les répertoires de logs de l'application
- Fichiers PID orphelins

### 5. Docker
- Conteneurs arrêtés
- Images non utilisées
- Système Docker (volumes, réseaux en mode agressif)

### 6. Snapshots
- Snapshots ZFS anciens (si ZFS est disponible)

### 7. Caches utilisateur
- Cache npm
- Cache Maven (versions SNAPSHOT de plus de 30 jours)
- Cache Gradle

### 8. Core dumps
- Fichiers core de plus de 7 jours
- Fichiers crash de plus de 7 jours

## Configuration automatique (Cron)

Pour exécuter le nettoyage automatiquement chaque semaine :

```bash
# Éditer le crontab
crontab -e

# Ajouter cette ligne (exécution chaque dimanche à 2h du matin)
0 2 * * 0 /opt/source/realestate/scripts/cleanup-vps.sh >> /var/log/cleanup.log 2>&1
```

## Sécurité

- Le script utilise `set -e` pour s'arrêter en cas d'erreur
- Les suppressions sont vérifiées avant exécution
- Mode dry-run disponible pour tester sans risque
- Les erreurs de permissions sont gérées gracieusement

## Notes

- Certaines opérations nécessitent des privilèges sudo (apt-get, journalctl)
- Le mode agressif peut supprimer plus de fichiers (attention aux logs récents)
- Les snapshots ZFS sont supprimés sans confirmation (utilisez --dry-run d'abord)

## Exemples

```bash
# Voir ce qui serait supprimé
./scripts/cleanup-vps.sh --dry-run

# Nettoyage normal
sudo ./scripts/cleanup-vps.sh

# Nettoyage agressif (supprime plus de fichiers)
sudo ./scripts/cleanup-vps.sh --aggressive
```

