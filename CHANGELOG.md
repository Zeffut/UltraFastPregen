# Changelog

All notable changes to UltraFastPregen will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-02-05

### Added
- ⚡ Ultra-fast chunk pre-generation (2.26x faster than Chunky)
- 🔲 Two generation shapes: Square (default) and Circle
- 📊 Real-time statistics (progress, speed, ETA, TPS)
- ⏸️ Full control: pause, resume, stop
- 🧠 Memory-adaptive configuration (100-500 concurrent chunks)
- 🌍 Multi-world support
- 🇫🇷 French interface
- 🔒 Production-ready: Thread-safe, no memory leaks, robust validation

### Technical Features
- RegionCache system for ultra-fast chunk detection
- Region-aligned generation (32x32 chunks)
- Batch processing (128 chunks per batch)
- Semaphore-based concurrency control
- Automatic cleanup and memory management
- ConcurrentHashMap for thread safety
- Timeout protection on all blocking operations

### Commands
- `/pregen start <radius> [world] [shape]` - Start generation
- `/pregen stop` - Stop generation
- `/pregen pause` - Pause generation
- `/pregen resume` - Resume generation
- `/pregen status` - View progress
- `/pregen help` - Show help

### Performance
- 1000 blocks radius: 2m 10s @ 95.8 chunks/sec (vs Chunky: 4m 54s @ 54.8 chunks/sec)
- 5000 blocks radius: ~24 minutes @ 213 chunks/sec (vs Chunky: ~55 minutes @ 93 chunks/sec)
- **2.26x faster than Chunky** ✅

### Compatibility
- Paper 1.21.11
- Java 21+
- Minimum 4GB RAM recommended

### License
- **All Rights Reserved** - Proprietary Software
- Free for personal use
- Commercial use requires separate license
- No modifications allowed
