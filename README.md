# OpenNLP Test Application

A small Java test application demonstrating a natural-language processing pipeline based on Apache OpenNLP.

The application currently demonstrates:

- language detection,
- sentence detection,
- tokenization,
- part-of-speech (POS) tagging,
- stop-word filtering, and
- Snowball stemming

for English and German text.

## Requirements

- Java 8 or later
- Maven
- Apache OpenNLP 1.8.4 (resolved through Maven)

## Project structure

- `src/` – Java source code
- `resources/models/` – language-detection and language-specific model resources
- `resources/models/eng/stopp.txt` – project-specific English stop-word list
- `resources/models/deu/stopp.txt` – project-specific German stop-word list

The `resources` directory must be included on the runtime classpath.

## Licensing

The source code and original project files authored by Mario Kubek are licensed under the Apache License, Version 2.0. See `LICENSE`.

Third-party software and model resources are not relicensed by this repository. See `THIRD_PARTY_NOTICES.md` for attribution and provenance information.

## Third-party components

This project uses Apache OpenNLP and pre-trained OpenNLP model resources. Apache OpenNLP is developed by The Apache Software Foundation.

The English and German stop-word lists contained in this repository were created for this project.

## Status

This repository is a compact demonstration/test application and is preserved as such rather than as a production-ready NLP system.
