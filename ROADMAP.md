# QA Playground — plan projektu portfolio

> Dokument żywy. Aktualizujemy go po każdej ukończonej fazie.
> Data utworzenia: 2026-09-01

---

## 1. Cel

Zbudować dwa produkty, które razem tworzą portfolio Senior QA:

1. **SUT (System Under Test)** — własna aplikacja webowa zawierająca komplet mechanik,
   które realnie testuje się w pracy (i takich, które sprawiają problemy).
2. **Framework testowy** — profesjonalny framework automatyzacji w Javie + Selenium,
   z pełnym CI/CD, raportowaniem i dokumentacją. Następnie ten sam zakres w Playwright (TypeScript).

Dlaczego własny SUT, a nie gotowy `the-internet` / `saucedemo`?
- Kontrolujesz backend → możesz seedować dane przez API, resetować stan, testować bazę.
- Możesz świadomie wstawić bugi, flaky endpointy i opóźnienia → materiał do nauki.
- W portfolio pokazujesz, że rozumiesz aplikację od środka, nie tylko klikasz w cudzy UI.

---

## 2. Kompetencje, które projekt udowodni

| Obszar | Czym to pokazujemy |
|---|---|
| Automatyzacja UI | Selenium 4.46 + Playwright, wzorce projektowe, stabilne testy |
| Automatyzacja API | RestAssured, kontrakt OpenAPI |
| Architektura kodu | POM, DI, konfiguracja, brak duplikacji, czysty kod |
| CI/CD | GitHub Actions + Jenkins (dwa niezależne pipeline'y) |
| Konteneryzacja | Docker, docker-compose, Selenium Grid, Testcontainers |
| Zarządzanie danymi | seedowanie przez API, izolacja testów, Datafaker |
| Raportowanie | Allure + GitHub Pages, historia trendów |
| Jakość kodu | Checkstyle, Spotless, SonarCloud, code review |
| Testy niefunkcjonalne | k6 (wydajność), OWASP ZAP (bezpieczeństwo), axe-core (a11y) |
| Mobile | Appium na web mobilnym / aplikacji Android |
| Proces QA | strategia testów, test plan, przypadki testowe, raport bugów |

---

## 3. Stack technologiczny

### Zweryfikowane wersje (stan na 2026-09-01)

| Narzędzie | Wersja | Uwaga |
|---|---|---|
| JDK | **25 LTS** | Wydany 2025-09-16, wsparcie do 2033. Masz 20 (nie-LTS, EOL) → **wymiana konieczna** |
| Selenium | **4.46.0** | Wydany 2026-07-11 |
| JUnit Jupiter | **5.14.4** | JUnit 6.1.2 istnieje, ale ekosystem (Allure, pluginy) dojrzalszy na 5.14 |
| Playwright | **1.61.0** | Wydany 2026-06-29 |
| Node.js | 22 lub 24 LTS | Masz 18 → **po EOL, wymiana konieczna** |
| Maven | 3.9.x | Masz 3.9.1, podbijemy do najnowszej 3.9 |
| Docker Desktop | najnowszy | **Brak na maszynie — instalujemy w Fazie 0** |

Wersje Spring Boot / React / bibliotek pomocniczych weryfikujemy w momencie dodawania (nie zgaduję).

### Framework testowy — biblioteki

- **Selenium 4.46** — sterowanie przeglądarką. Selenium Manager sam pobiera drivery,
  `WebDriverManager` jest już **niepotrzebny** (częsty błąd w starych tutorialach).
- **JUnit 5.14** — runner, cykl życia, testy parametryzowane, tagi, równoległość.
- **AssertJ** — asercje fluent (`assertThat(x).isEqualTo(y)`), czytelniejsze niż JUnit.
- **RestAssured** — testy API i seedowanie danych.
- **Allure** — raporty z krokami, screenshotami, historią.
- **Owner** — konfiguracja typowana (env, przeglądarka, URL) zamiast `System.getProperty`.
- **Datafaker** — generowanie danych testowych.
- **Awaitility** — czekanie na warunki poza UI (np. na maila, na stan bazy).
- **WireMock** — mockowanie zewnętrznych zależności.
- **Testcontainers** — baza danych i Grid w kontenerach, sterowane z testu.
- **SLF4J + Logback** — logowanie.

---

## 4. Architektura repozytorium

Decyzja do podjęcia (patrz sekcja 8). Wariant rekomendowany — **monorepo**:

```
qa-playground/
├── app/                        # SUT
│   ├── backend/                # REST API + baza
│   └── frontend/               # SPA
├── tests-selenium/             # framework Java + Selenium
│   ├── src/test/java/
│   │   ├── core/               # driver factory, config, hooks, listeners
│   │   ├── pages/              # Page Objects
│   │   ├── components/         # komponenty współdzielone (navbar, modal, table)
│   │   ├── api/                # klienci API (seedowanie danych)
│   │   ├── data/               # buildery danych testowych
│   │   └── tests/              # właściwe testy
│   └── pom.xml
├── tests-playwright/           # framework TypeScript + Playwright (Faza 10)
├── tests-perf/                 # k6 (Faza 9)
├── docs/                       # strategia testów, test plan, przypadki, ADR
├── .github/workflows/          # GitHub Actions
├── jenkins/                    # Jenkinsfile + konfiguracja Jenkinsa w Dockerze
├── docker-compose.yml
└── README.md                   # wizytówka projektu
```

---

## 5. SUT — macierz funkcjonalności

To jest lista mechanik, które aplikacja musi zawierać, żeby dało się na niej nauczyć wszystkiego.
Budujemy przyrostowo — nie wszystko w Fazie 1.

### 5.1 Uwierzytelnianie i autoryzacja
- rejestracja z walidacją i potwierdzeniem e-mail
- logowanie / wylogowanie, „zapamiętaj mnie"
- reset hasła przez token w mailu (testowalny — mail trafia do Mailpita)
- role: `USER`, `ADMIN` (różne widoki, różne uprawnienia)
- blokada konta po N nieudanych próbach
- wygaśnięcie sesji
- (opcjonalnie) 2FA kodem TOTP

### 5.2 Formularze — komplet typów pól
text, textarea, password, number, email, select, multi-select, radio, checkbox,
date picker, time picker, range slider, color picker, autocomplete/typeahead,
upload pliku (pojedynczy i wielokrotny), download pliku,
walidacja po stronie klienta **i** serwera, pola zależne od siebie, formularz wielokrokowy (wizard)

### 5.3 Tabele i listy
paginacja, sortowanie po kolumnach, filtrowanie, wyszukiwanie,
zaznaczanie masowe, edycja inline, eksport do CSV, wirtualizowana lista, infinite scroll

### 5.4 Elementy dynamiczne (nauka czekania)
spinnery ładowania, treść doładowywana AJAX-em, elementy pojawiające się z opóźnieniem,
element, który znika, „stale element" (celowo), lazy loading obrazków, licznik/timer

### 5.5 Trudne mechaniki
- iframe i zagnieżdżone iframe
- Shadow DOM (Web Components)
- natywne `alert` / `confirm` / `prompt`
- modale, toasty, tooltips, popovery
- nowe okno / nowa karta
- drag & drop (lista sortowalna, upload przez przeciągnięcie)
- canvas i SVG
- edytor rich text (`contenteditable`)
- tabela w tabeli, dynamiczne ID (celowo złe selektory do nauki)
- WebSocket — powiadomienia w czasie rzeczywistym

### 5.6 Ścieżka biznesowa (E2E)
katalog produktów → szczegóły → koszyk → checkout → mock płatności → historia zamówień → faktura PDF

### 5.7 Panel administratora
CRUD użytkowników, CRUD produktów, moderacja, audit log

### 5.8 Celowe „pułapki" (materiał do nauki i do raportów bugów)
`/broken-images`, wolny endpoint (5 s), flaky endpoint (10% błędów),
błędy dostępności do znalezienia przez axe, znane bugi opisane w `docs/known-bugs.md`

### 5.9 Pozostałe
i18n (PL/EN), tryb ciemny, widok responsywny (pod Appium/mobile emulation),
REST API z dokumentacją OpenAPI/Swagger, healthcheck, endpoint resetu danych testowych

---

## 6. Fazy realizacji

Szacunki zakładają pracę po godzinach (2–3 h dziennie). „Sesja" = jedno nasze wspólne posiedzenie.

### Faza 0 — Fundamenty środowiska  ⏱️ 1–2 sesje
- [ ] JDK 25 LTS — instalacja, `JAVA_HOME`, weryfikacja
- [ ] Node 22/24 LTS (pod Playwright w Fazie 10)
- [ ] Docker Desktop + WSL2
- [ ] Maven — podbicie, konfiguracja
- [ ] IDE (IntelliJ IDEA) — pluginy, formatter, ustawienia
- [ ] Git — konfiguracja, SSH key, GPG signing commitów
- [ ] GitHub — repo, branch protection, konwencje commitów (Conventional Commits)
- [ ] `.gitignore`, `.editorconfig`, README v0

**Nauczysz się:** dlaczego LTS, jak działa Selenium Manager, po co podpisywać commity, trunk-based vs GitFlow.

### Faza 1 — SUT: aplikacja demo  ⏱️ 4–6 sesji
- [ ] Backend: REST API + baza + migracje + OpenAPI
- [ ] Frontend: SPA z routingiem
- [ ] Auth (5.1) + katalog produktów + koszyk (5.6, wersja minimalna)
- [ ] Docker: `Dockerfile` backend, `Dockerfile` frontend, `docker-compose.yml`
- [ ] Mailpit w compose (do testów resetu hasła)
- [ ] Endpoint `/api/test/reset` — czyszczenie stanu między testami
- [ ] Seed danych startowych

**Nauczysz się:** jak zbudowana jest testowana aplikacja, czym jest test data management,
dlaczego reset stanu przez API bije klikanie w UI.

### Faza 2 — Szkielet frameworka Selenium  ⏱️ 3–4 sesje
- [ ] Projekt Maven, struktura pakietów, `pom.xml` z BOM-ami
- [ ] `DriverFactory` + `ThreadLocal<WebDriver>` (podstawa równoległości)
- [ ] Konfiguracja przez Owner (`browser`, `env`, `headless`, `baseUrl`)
- [ ] Bazowy `BasePage`, `BaseTest`, JUnit 5 Extensions (hooks)
- [ ] Page Object Model + wzorzec Loadable Component
- [ ] Pierwszy test: logowanie (happy path + negatywny)
- [ ] Strategia czekania: **tylko** explicit waits, zero `Thread.sleep`
- [ ] Screenshot przy porażce

**Nauczysz się:** POM zrobiony dobrze (a nie jak w 90% tutoriali), dlaczego implicit + explicit
wait to bomba zegarowa, jak `ThreadLocal` umożliwia równoległość, czym jest fluent API w Page Object.

### Faza 3 — Pełne pokrycie UI  ⏱️ 6–8 sesji
- [ ] Testy formularzy (5.2) — parametryzowane, data-driven
- [ ] Testy tabel (5.3)
- [ ] Elementy dynamiczne (5.4) — mistrzostwo czekania
- [ ] Trudne mechaniki (5.5): iframe, Shadow DOM, alerty, okna, drag&drop
- [ ] E2E ścieżka zakupowa (5.6)
- [ ] Panel admina (5.7)
- [ ] Selenium 4 BiDi: przechwytywanie logów konsoli, sieci, błędów JS w teście
- [ ] Wzorzec Screenplay jako alternatywa dla POM (porównanie w `docs/`)

**Nauczysz się:** kiedy POM nie wystarcza, jak testować to, czego „nie da się przetestować",
jak nowe API BiDi zmienia grę względem starego Selenium.

### Faza 4 — Testy API + zarządzanie danymi  ⏱️ 3–4 sesje
- [ ] RestAssured: CRUD, autoryzacja, walidacja schematu JSON
- [ ] Warstwa klientów API w frameworku
- [ ] Seedowanie stanu przez API zamiast przez UI (**duży skok w szybkości testów**)
- [ ] Buildery danych testowych + Datafaker
- [ ] Testy kontraktowe względem OpenAPI
- [ ] Izolacja testów — każdy test tworzy własne dane

**Nauczysz się:** piramida testów w praktyce, dlaczego E2E przez UI do logowania to antywzorzec.

### Faza 4b — Warstwa BDD (Cucumber)  ⏱️ 2 sesje
- [ ] Cucumber-JVM jako **warstwa nad** gotowymi Page Objectami (nie zamiast nich)
- [ ] Gherkin: Feature, Scenario, Scenario Outline, Background, tagi
- [ ] Step definitions — cienkie, delegujące do Page Objects / klientów API
- [ ] Dependency injection w krokach (PicoContainer)
- [ ] Raport Cucumber + integracja z Allure
- [ ] `docs/bdd-kiedy-ma-sens.md` — kiedy BDD pomaga, a kiedy jest przerostem formy

**Nauczysz się:** dlaczego BDD to problem komunikacyjny, a nie techniczny; najczęstsze antywzorce
(kroki imperatywne opisujące klikanie zamiast zachowania biznesowego, Gherkin jako język programowania).

### Faza 5 — Docker, Grid, równoległość  ⏱️ 3–4 sesje
- [ ] Selenium Grid 4 w docker-compose (hub + nody Chrome/Firefox/Edge)
- [ ] `RemoteWebDriver` + przełącznik lokalny/zdalny
- [ ] Równoległość JUnit 5 — konfiguracja i pułapki
- [ ] Nagrywanie wideo z testów
- [ ] Testcontainers — baza i Grid podnoszone z kodu testu
- [ ] Cross-browser matrix

**Nauczysz się:** dlaczego równoległość ujawnia błędy w Twoim frameworku, jak nie robić testów
zależnych od kolejności, Grid vs Testcontainers — kiedy co.

### Faza 6 — GitHub Actions  ⏱️ 2–3 sesje
- [ ] Workflow: build + testy jednostkowe na każdy push
- [ ] Workflow: testy API + E2E na PR (z podniesieniem app w compose)
- [ ] Matrix: przeglądarki × systemy
- [ ] Nightly: pełna regresja
- [ ] Cache Mavena, artefakty (screeny, wideo, logi)
- [ ] Allure Report publikowany na GitHub Pages z historią trendów
- [ ] Status badge w README
- [ ] Dependabot / Renovate
- [ ] Publikacja obrazu SUT do GHCR

**Nauczysz się:** anatomia workflow, matrix strategy, cache, artefakty, sekrety,
dlaczego zielony badge w README robi robotę na rekruterze.

### Faza 7 — Jenkins  ⏱️ 3–4 sesje
- [ ] Jenkins w Dockerze (konfiguracja jako kod — JCasC)
- [ ] Declarative Pipeline — `Jenkinsfile`
- [ ] Stages: build → unit → api → e2e → report
- [ ] Równoległe stage'y
- [ ] Parametryzacja (wybór przeglądarki, środowiska, tagów)
- [ ] Plugin Allure, publikowanie raportów
- [ ] Shared Library — własna biblioteka kroków
- [ ] Trigger: cron + webhook z GitHuba
- [ ] Agenci w Dockerze

**Nauczysz się:** czym Jenkins różni się od Actions i dlaczego korporacje wciąż na nim siedzą,
pipeline as code, Groovy w praktyce, JCasC.

### Faza 8 — Jakość kodu i raportowanie  ⏱️ 2 sesje
- [ ] Spotless + Checkstyle (Google Java Style) + fail build na naruszeniu
- [ ] SonarCloud — analiza, quality gate w PR
- [ ] Pre-commit hooks
- [ ] Allure: kroki, załączniki, severity, linki do wymagań, kategorie defektów
- [ ] Mechanizm retry dla flaky + raportowanie flakiness
- [ ] Logowanie — co i na jakim poziomie

**Nauczysz się:** że kod testowy to kod produkcyjny, quality gate, jak mierzyć flakiness zamiast ją ukrywać.

### Faza 9 — Testy niefunkcjonalne  ⏱️ 3–4 sesje
- [ ] **Wydajność:** k6 — smoke, load, stress, spike; progi (thresholds) w CI
- [ ] **Bezpieczeństwo:** OWASP ZAP baseline scan w pipeline; ręczny przegląd OWASP Top 10 na SUT
- [ ] **Dostępność:** axe-core wpięty w testy Selenium; raport a11y
- [ ] **Wizualne:** regresja wizualna (Playwright screenshots)
- [ ] (opcjonalnie) Pact — testy kontraktowe konsument-dostawca

**Nauczysz się:** że senior QA to nie tylko klikanie E2E; jak wpiąć testy niefunkcjonalne w CI, żeby nie blokowały.

### Faza 10 — Playwright (TypeScript)  ⏱️ 4–5 sesji  🍒
- [ ] Setup: TS, ESLint, Prettier, struktura
- [ ] Port testów z Selenium 1:1 → **bezpośrednie porównanie w `docs/selenium-vs-playwright.md`**
- [ ] Auto-waiting, locators, web-first assertions
- [ ] Fixtures i storage state (logowanie raz, nie w każdym teście)
- [ ] Network interception i mockowanie
- [ ] Trace Viewer, debugowanie
- [ ] Regresja wizualna
- [ ] Component testing
- [ ] Playwright w GitHub Actions + raport HTML
- [ ] Playwright API testing

**Nauczysz się:** dlaczego Playwright jest szybszy i stabilniejszy, gdzie Selenium wciąż wygrywa,
jak uzasadnić wybór narzędzia na rozmowie.

### Faza 11 — Mobile / Appium  ⏱️ 3–4 sesje
- [ ] Appium 2.x + Android Studio + emulator
- [ ] Testy web mobilnego (Chrome na Androidzie) na tym samym SUT
- [ ] (opcjonalnie) prosta aplikacja Android → testy natywne
- [ ] Współdzielenie Page Objects między web a mobile
- [ ] Appium w CI

**Nauczysz się:** architektura Appium 2, różnice w lokatorach, dlaczego mobile CI jest trudne.

### Faza 12 — Portfolio polish  ⏱️ 2 sesje
- [ ] README z architekturą, GIF-ami z testów, badge'ami, instrukcją uruchomienia w 1 komendzie
- [ ] `docs/test-strategy.md` — strategia testów
- [ ] `docs/test-plan.md` — plan testów
- [ ] Przypadki testowe (Markdown lub w kodzie)
- [ ] Przykładowe raporty bugów dla „pułapek" z 5.8
- [ ] ADR — dlaczego takie, a nie inne decyzje architektoniczne
- [ ] Live demo raportu Allure na GitHub Pages
- [ ] GitHub Projects — tablica z zadaniami (pokazuje proces)
- [ ] Wpis do CV / LinkedIn

**Nauczysz się:** jak sprzedać projekt; że rekruter patrzy na README 30 sekund.

---

## 7. Jak pracujemy

Każda sesja według stałego schematu:

1. **Teoria** — wyjaśniam koncept, po co jest i jakie są alternatywy.
2. **Demo** — piszemy razem, ja tłumaczę każdą decyzję.
3. **Twoja kolej** — zadanie do samodzielnego zrobienia.
4. **Review** — sprawdzam, komentuję jak na code review w pracy.
5. **Commit** — poprawny commit, poprawny PR, zielony pipeline.
6. **Notatnik** — dopisuję omówione koncepty do `docs/notatnik.html`.

### Notatnik — `docs/notatnik.html`

Żywy dokument z wyjaśnieniami wszystkiego, co przechodzimy, pisany pod kątem rozmów
kwalifikacyjnych. Każdy temat ma trzy elementy:

- **wyjaśnienie konceptu** — czym jest i dlaczego istnieje,
- **„u nas na maszynie"** — co realnie zaszło w naszym projekcie,
- **pytanie rekrutacyjne** z rozpisaną odpowiedzią.

Uzupełniany po każdym kroku. Docelowo trafia do portfolio jako dowód, że projekt był
zrozumiany, a nie przeklikany za tutorialem.

Zasady:
- Nie idziemy dalej, dopóki nie rozumiesz poprzedniego kroku. Pytaj o wszystko.
- Każdy krok kończy się działającym kodem w repo.
- Nie kopiujemy z tutoriali — rozumiemy i piszemy.
- Nazywam antywzorce po imieniu, żebyś je rozpoznawał na rozmowach.

---

## 8. Decyzje architektoniczne (podjęte 2026-09-01)

| # | Decyzja | Wybór | Uzasadnienie |
|---|---|---|---|
| 1 | Stack SUT | **Spring Boot + React** | Backend w Javie — czytasz i rozumiesz kod testowanej aplikacji. Frontend jako SPA daje realną dynamikę (AJAX, lazy loading, Shadow DOM). REST API umożliwia RestAssured i seedowanie danych. |
| 2 | Układ repo | **Monorepo** | Prostsze CI, jeden link dla rekrutera, uruchomienie całości jedną komendą. |
| 3 | Runner | **JUnit 5.14** | Nowoczesny standard. Moduł pokazowy TestNG opcjonalnie później. |
| 4 | BDD / Cucumber | **Tak, po Fazie 4** | Cucumber ma sens tylko nad dojrzałym frameworkiem. Dodany za wcześnie staje się antywzorcem. |
| 5 | Zakres startowy | **MVP → rozbudowa przyrostowa** | Faza 1 buduje rdzeń aplikacji; mechaniki z sekcji 5 dokładamy równolegle z testami, które je pokrywają. |

Rozszerzenia tych decyzji trafiają do `docs/adr/` jako Architecture Decision Records.

---

## 9. Postęp

| Faza | Status | Data ukończenia |
|---|---|---|
| 0 — Fundamenty | ⬜ nie rozpoczęta | |
| 1 — SUT | ⬜ | |
| 2 — Szkielet Selenium | ⬜ | |
| 3 — Pokrycie UI | ⬜ | |
| 4 — API | ⬜ | |
| 4b — BDD/Cucumber | ⬜ | |
| 5 — Docker/Grid | ⬜ | |
| 6 — GitHub Actions | ⬜ | |
| 7 — Jenkins | ⬜ | |
| 8 — Jakość kodu | ⬜ | |
| 9 — Niefunkcjonalne | ⬜ | |
| 10 — Playwright | ⬜ | |
| 11 — Appium | ⬜ | |
| 12 — Portfolio | ⬜ | |
