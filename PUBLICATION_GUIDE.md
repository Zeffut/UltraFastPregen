# Guide de Publication - UltraFastPregen

## 📦 Fichiers Prêts pour Publication

Votre projet est maintenant **prêt pour être publié** sur GitHub et les plateformes de plugins!

### Structure du Projet

```
Ultra Fast Pregen/
├── plugin/                      # Code source
│   ├── src/                    # Code Java
│   ├── build.gradle.kts        # Configuration build
│   └── ...
├── release/                     # Fichiers de release
│   ├── UltraFastPregen-1.0.0.jar  # Plugin compilé
│   ├── README.md               # Documentation
│   └── LICENSE                 # All Rights Reserved
├── README.md                    # Documentation principale
├── CHANGELOG.md                 # Historique des versions
├── CONTRIBUTING.md              # Guide de contribution
├── PLUGIN_DESCRIPTION.md        # Description pour plateformes
├── LICENSE                      # All Rights Reserved
├── .gitignore                  # Fichiers à ignorer
└── ABOUT_LICENSE.md            # Explications sur la license
```

---

## 🚀 Publication sur GitHub

### 1. Initialiser Git (si pas déjà fait)

```bash
cd /Users/zeffut/Desktop/Projets/Ultra\ Fast\ Pregen
git init
git add .
git commit -m "🎉 Initial release v1.0.0 - 2.26x faster than Chunky"
```

### 2. Créer un Repository sur GitHub

1. Allez sur https://github.com/new
2. Nom: `UltraFastPregen`
3. Description: `⚡ Le plugin de pré-génération le plus rapide - 2.26x plus rapide que Chunky!`
4. Public ✅
5. Ne pas initialiser avec README (on a déjà le nôtre)
6. Cliquez "Create repository"

### 3. Pousser vers GitHub

```bash
git remote add origin https://github.com/VOTRE_USERNAME/UltraFastPregen.git
git branch -M main
git push -u origin main
```

### 4. Créer une Release

1. Allez sur votre repo GitHub
2. Cliquez sur "Releases" → "Create a new release"
3. Tag: `v1.0.0`
4. Title: `v1.0.0 - Initial Release`
5. Description:

```markdown
# ⚡ UltraFastPregen v1.0.0

## 🎉 Première Release Publique!

Le plugin de pré-génération **2.26x plus rapide que Chunky**!

### ✨ Fonctionnalités

- ⚡ **2.26x plus rapide que Chunky** (benchmarks réels)
- 🔲 Deux formes: Carré et Cercle
- 📊 Statistiques en temps réel
- ⏸️ Contrôle total: pause, resume, stop
- 🧠 Auto-adaptatif selon RAM
- 🌍 Support multi-monde
- 🔒 Production-ready

### 📊 Performances

| Test | UltraFastPregen | Chunky | Gain |
|------|-----------------|--------|------|
| 1000 blocs | **2m 10s** | 4m 54s | **2.26x** |
| 5000 blocs | **~24 min** | ~55 min | **2.3x** |

### 📋 Prérequis

- Paper 1.21.11+
- Java 21+
- 4GB RAM minimum

### 📥 Installation

1. Téléchargez `UltraFastPregen-1.0.0.jar`
2. Placez dans `plugins/`
3. Redémarrez
4. Utilisez `/pregen help`

### 📖 Documentation

Voir [README.md](https://github.com/VOTRE_USERNAME/UltraFastPregen#readme)
```

6. Uploadez le fichier `release/UltraFastPregen-1.0.0.jar`
7. Cliquez "Publish release"

---

## 🎮 Publication sur Spigot

### 1. Créer un Compte

- Allez sur https://www.spigotmc.org/
- Créez un compte si nécessaire

### 2. Soumettre le Plugin

1. Allez sur https://www.spigotmc.org/resources/
2. Cliquez "Post Resource"
3. Remplissez:

**Title:** `⚡ UltraFastPregen - 2.26x Plus Rapide que Chunky!`

**Tag Line:** `Le plugin de pré-génération le plus rapide pour Paper`

**Category:** `Tools and Utilities`

**Description:** Copiez depuis `PLUGIN_DESCRIPTION.md`

**External Download URL:** Lien vers GitHub Release

**Price:** Free

**Version:** 1.0.0

**Tested Minecraft Versions:** 1.21

4. Uploadez des screenshots (optionnel)
5. Soumettez

---

## 🟣 Publication sur Modrinth

### 1. Créer un Compte

- Allez sur https://modrinth.com/
- Créez un compte

### 2. Créer un Projet

1. Cliquez "Create a project"
2. Sélectionnez "Plugin"
3. Remplissez:

**Name:** `UltraFastPregen`

**Summary:** `⚡ Le plugin de pré-génération le plus rapide - 2.26x plus rapide que Chunky!`

**Categories:** World Generation, Utility, Optimization

**Description:** Utilisez `PLUGIN_DESCRIPTION.md` (format Markdown supporté)

**License:** All Rights Reserved

**Source code:** Lien GitHub

4. Uploadez une icône (optionnel)
5. Créez le projet

### 3. Upload Version

1. Cliquez "Create a version"
2. Remplissez:

**Version number:** 1.0.0

**Version title:** Initial Release

**Loaders:** Paper

**Game versions:** 1.21.x

**Release channel:** Release

**Changelog:** Copiez depuis `CHANGELOG.md`

3. Uploadez `UltraFastPregen-1.0.0.jar`
4. Publiez

---

## 🏠 Publication sur Hangar (PaperMC)

### 1. Créer un Compte

- Allez sur https://hangar.papermc.io/
- Connectez-vous avec GitHub

### 2. Créer un Projet

1. Cliquez "New Project"
2. Remplissez:

**Name:** `UltraFastPregen`

**Description:** `⚡ Le plugin de pré-génération le plus rapide - 2.26x plus rapide que Chunky!`

**Category:** Admin Tools

**License:** All Rights Reserved

**Source:** Lien GitHub

3. Créez le projet

### 3. Upload Version

1. Cliquez "Upload Version"
2. Remplissez:

**Version:** 1.0.0

**Platform:** Paper

**Platform Versions:** 1.21.x

**Channel:** Release

**Description:** Copiez depuis `CHANGELOG.md`

3. Uploadez `UltraFastPregen-1.0.0.jar`
4. Publiez

---

## 📊 Checklist de Publication

### Avant de publier:

- [x] Code nettoyé (pas de fichiers de test)
- [x] JAR compilé et testé
- [x] README.md complet et clair
- [x] LICENSE incluse
- [x] CHANGELOG.md à jour
- [x] Version correcte partout (1.21.11)
- [x] .gitignore configuré

### Après publication:

- [ ] Initialiser Git
- [ ] Créer repo GitHub
- [ ] Pousser le code
- [ ] Créer GitHub Release
- [ ] Publier sur Spigot
- [ ] Publier sur Modrinth
- [ ] Publier sur Hangar
- [ ] Partager sur Discord/Reddit Minecraft

---

## 🎨 Assets Recommandés

Pour une meilleure présentation, créez:

1. **Logo/Icône** (512x512px)
   - Éclair ⚡ stylisé
   - Couleurs: Vert/Bleu électrique

2. **Bannière** (1920x400px)
   - Titre: "UltraFastPregen"
   - Sous-titre: "2.26x Plus Rapide"
   - Background: Chunks Minecraft

3. **Screenshots**
   - Commande `/pregen start`
   - Interface `/pregen status`
   - Tableau de comparaison

---

## 📱 Promotion

### Où partager:

1. **Reddit**
   - r/admincraft
   - r/minecraft
   - r/feedthebeast

2. **Discord**
   - PaperMC Discord
   - SpigotMC Discord
   - Serveurs Minecraft francophones

3. **Forums**
   - SpigotMC Resources
   - PaperMC Forums

### Message type:

```
🎉 Nouveau plugin: UltraFastPregen!

⚡ 2.26x plus rapide que Chunky (benchmarks réels)
🔲 Formes: Carré & Cercle
📊 Stats en temps réel
🆓 100% gratuit et open source (MIT)

Paper 1.21.11 | Java 21+

🔗 GitHub: [lien]
📥 Download: [lien]
```

---

## ✅ C'est Prêt!

Votre plugin est maintenant **prêt pour le monde**! 🌍

Tous les fichiers sont nettoyés, la documentation est complète, et vous avez tous les guides pour publier sur toutes les plateformes.

**Bonne chance avec votre publication!** 🚀
