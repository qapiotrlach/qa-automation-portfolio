# QA Playground

Projekt portfolio Senior QA — własna aplikacja webowa (SUT) wraz z kompletnym
frameworkiem automatyzacji testów.

> 🚧 Projekt w budowie. Aktualny etap: **Faza 0 — fundamenty środowiska**.
> Pełny plan: [`ROADMAP.md`](ROADMAP.md)

---

## O projekcie

Repozytorium zawiera dwa produkty:

1. **Aplikacja demo (SUT)** — Spring Boot + React, celowo zawierająca komplet mechanik,
   które realnie testuje się w pracy: formularze, tabele, elementy dynamiczne, iframe,
   Shadow DOM, drag & drop, WebSocket, a także świadomie wprowadzone błędy i flaky endpointy.
2. **Framework testowy** — Java + Selenium 4, następnie ten sam zakres w Playwright,
   z pełnym CI/CD, raportowaniem i dokumentacją.

Aplikacja jest własna, a nie gotowa (`saucedemo`, `the-internet`), ponieważ kontrola nad
backendem pozwala seedować dane przez API, resetować stan między testami i świadomie
wprowadzać defekty jako materiał do nauki.

## Stack

| Obszar | Technologie |
|---|---|
| Aplikacja | Spring Boot, React, PostgreSQL |
| Testy UI | Selenium 4.46, Playwright 1.61 |
| Runner | JUnit 5.14, AssertJ |
| Testy API | RestAssured |
| Raportowanie | Allure |
| CI/CD | GitHub Actions, Jenkins |
| Konteneryzacja | Docker, Selenium Grid, Testcontainers |
| Niefunkcjonalne | k6, OWASP ZAP, axe-core |

## Dokumentacja

- [`ROADMAP.md`](ROADMAP.md) — plan projektu, decyzje architektoniczne
- [`docs/notatnik.html`](docs/notatnik.html) — notatnik techniczny z omówieniem
  napotkanych zagadnień

## Wymagania

- JDK 25 LTS
- Maven 3.9+
- Node.js 24 LTS
- Docker

---

Autor: **Piotr Lach** · [github.com/qapiotrlach](https://github.com/qapiotrlach)
