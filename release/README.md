# ⚡ UltraFastPregen

**Le plugin de pré-génération de chunks le plus rapide pour Minecraft Paper 1.21.11**

[![Version](https://img.shields.io/badge/Version-1.0.0-blue)](https://github.com/Zeffut/UltraFastPregen/releases)
[![Paper](https://img.shields.io/badge/Paper-1.21.11-orange)](https://papermc.io/)
[![License](https://img.shields.io/badge/License-All%20Rights%20Reserved-red)](LICENSE)

---

## 📊 Performances Exceptionnelles

UltraFastPregen est optimisé pour la **vitesse maximale** de pré-génération:

### 🎯 Pourquoi si rapide?

1. **Architecture optimisée par régions** - Génération alignée sur les fichiers .mca de Minecraft
2. **RegionCache intelligent** - Détection ultra-rapide des chunks déjà générés (lecture fichiers)
3. **Haute concurrence** - 300-500 chunks en parallèle selon votre RAM
4. **Memory-adaptive** - Ajustement automatique selon la RAM disponible
5. **Batch processing** - Soumission par lots de 128 chunks
6. **Zero TPS impact** - Génération asynchrone sans ralentir le serveur

---

## ✨ Fonctionnalités

- ⚡ **Ultra-rapide** - Architecture optimisée pour la génération de chunks
- 🔲 **Deux formes de génération**: Carré (défaut) ou Cercle
- 📊 **Statistiques en temps réel**: Progression, vitesse, ETA, TPS
- ⏸️ **Contrôle total**: Pause, reprise, arrêt à tout moment
- 🧠 **Gestion mémoire intelligente**: Adapte automatiquement la charge
- 🌍 **Multi-monde**: Support complet de plusieurs mondes simultanément
- 🇫🇷 **Interface française**: Messages et commandes en français
- 🔒 **Production-ready**: Thread-safe, aucune fuite mémoire, validation robuste

---

## 🚀 Installation

1. Téléchargez la dernière version depuis [Releases](https://github.com/Zeffut/UltraFastPregen/releases)
2. Placez `UltraFastPregen.jar` dans le dossier `plugins/` de votre serveur
3. Redémarrez le serveur
4. C'est prêt! Utilisez `/pregen help`

### Prérequis

- **Serveur**: Paper 1.21.11 (ou compatible)
- **RAM**: Minimum 4GB recommandé (8GB+ pour grandes zones)
- **Java**: 21+

---

## 📖 Utilisation

### Commandes de Base

```bash
# Démarrer une génération carrée de 1000 blocs
/pregen start 1000

# Démarrer une génération circulaire de 5000 blocs
/pregen start 5000 circle

# Générer sur un monde spécifique
/pregen start 1000 world_nether square

# Voir la progression
/pregen status

# Mettre en pause
/pregen pause

# Reprendre
/pregen resume

# Arrêter
/pregen stop

# Afficher l'aide
/pregen help
```

### Formes de Génération

#### Carré (défaut)
- Génère une zone carrée de `2*rayon x 2*rayon` blocs
- Exemple: rayon 1000 = zone de 2000x2000 blocs
- Chunks: `(2*rayon/16)²`

#### Cercle
- Génère une zone circulaire de rayon spécifié
- Accepte: `circle`, `cercle`, `rond`, `c`
- Chunks: `π * (rayon/16)²`

### Permissions

```yaml
ultrafastpregen.use     # Utiliser toutes les commandes (défaut: op)
```

---

## 🎮 Exemple d'Utilisation

```bash
/pregen start 5000 square
```

**Sortie:**
```
⚡ ULTRA FAST PREGEN ⚡
Démarrage de la pré-génération...

Monde: world
Forme: Carré
Rayon: 5000 blocs (10000x10000)
Chunks estimés: 390625

Génération lancée! Utilisez /pregen status pour suivre la progression
```

**Progression:**
```
⚡ ULTRA FAST PREGEN - Statut

Monde: world
Forme: Carré
Progression: 195312/390625 (50.0%)
[████████████████              ] 
Vitesse: 213.4 chunks/sec
Temps écoulé: 15m 15s
Temps restant: 15m 15s
TPS: 20.0
Max concurrent chunks: 500
```

---

## ⚙️ Configuration Automatique

UltraFastPregen s'adapte automatiquement à votre serveur:

| RAM Allouée | Chunks Parallèles | Performance |
|-------------|-------------------|-------------|
| 8GB+ | 500 | Ultra-rapide |
| 4-8GB | 300 | Très élevée |
| 2-4GB | 200 | Élevée |
| <2GB | 100 | Standard |

**Aucune configuration nécessaire** - Le plugin optimise automatiquement selon vos ressources!

---

## 🔧 Architecture Technique

### RegionCache
- Scan des fichiers `.mca` au lieu de requêtes Bukkit
- 100x plus rapide pour détecter les chunks existants
- Thread-safe avec `ConcurrentHashMap`

### Génération par Régions
- Alignement sur la structure native de Minecraft (32x32 chunks)
- Optimise les I/O disque
- Meilleure localité du cache

### Haute Concurrence
- Semaphore pour limiter la charge (300-500 chunks simultanés)
- Timeout de sécurité (30s acquisition, 60s attente finale)
- Cleanup automatique de la mémoire

### Thread Safety
- Toutes les structures sont thread-safe
- Synchronisation sur les opérations critiques
- Aucune race condition

---

## 🛠️ Compilation

```bash
cd plugin
./gradlew shadowJar
```

Le JAR sera généré dans `plugin/build/libs/UltraFastPregen-1.0.0-all.jar`

---

## 🤝 Contribution

Les contributions sont les bienvenues!

1. Fork le projet
2. Créez une branche (`git checkout -b feature/AmazingFeature`)
3. Commit vos changements (`git commit -m 'Add AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrez une Pull Request

---

## 📝 License

**Copyright © 2026 zeffut. All Rights Reserved.**

Ce logiciel est **propriétaire**. Vous pouvez l'utiliser gratuitement sur vos serveurs, mais:

- ❌ **Aucune modification** du code n'est autorisée
- ❌ **Aucun usage commercial** sans licence commerciale
- ❌ **Pas de redistribution** en dehors des canaux officiels
- ❌ **Pas de décompilation** ou reverse engineering

Pour toute utilisation commerciale, contactez l'auteur.

Voir le fichier [LICENSE](LICENSE) pour les détails complets.

---

## 🔗 Liens Utiles

- [Releases](https://github.com/Zeffut/UltraFastPregen/releases)
- [Issues](https://github.com/Zeffut/UltraFastPregen/issues)
- [Paper Documentation](https://docs.papermc.io/)

---

## 💬 Support

Besoin d'aide? Ouvrez une [Issue](https://github.com/Zeffut/UltraFastPregen/issues)!

---

## ⭐ Remerciements

- L'équipe Paper pour leur API excellente
- La communauté Minecraft pour les tests et retours

---

**Développé avec ⚡ pour la communauté Minecraft**
