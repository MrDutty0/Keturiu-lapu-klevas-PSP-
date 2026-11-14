# **Implementation scope**

**Team:** Keturiu lapu klevas

**Capacity:** about 50 hours per week for the whole team (200 hours in total)

**Period:** 16 Nov – 14 Dec 2026

## **Summary**

The full specification (Must, Should and Could have) is larger than 200 hours. We therefore commit to a working Android prototype of the core product: plan a journey in Vilnius, see live departures and vehicles, board a bus and get live in-ride assistance (next stops, transfer-risk warning, get-off alert).

We commit to the Core Must-have flows of TrafiK, with the explicit simplifications and limitations described in this document. This means we implement the core product prototype (journey planning, live departures, vehicle map and manual in-ride assistance), while intentionally narrowing scope where the full specification exceeds the project's time and capacity. We include the 19 Must-have requirements in their Core form, 9 of the 13 Should-have requirements in Tier 2 (S-1 to S-5 and S-10 to S-13), and the remaining 4 Should-have requirements (S-6 to S-9, Tier 3) as a stretch goal: they are started only if the week 2 checkpoint shows that the schedule holds. We include none of the Could-have requirements. Accounts, community reports, ticketing and iOS are out of scope. This project does not claim full compliance with the original specification; it claims delivery of the defined Core prototype, plus Tier 2 and Tier 3 where the schedule allows.

Planned work is about 160 hours, including about 13 hours for the Tier 3 stretch items, which are done only if the checkpoint allows. About 40 hours (20%) are kept as buffer. The scope is split into three tiers (Core, Tier 2, Tier 3), so that if the team's real hours turn out lower, whole tiers are dropped instead of features being half-finished.

## **Technology stack**

| Area | Choice |
| :---- | :---- |
| Mobile app | Kotlin with Jetpack Compose, native Android 10 or later; MapLibre for the map. Foreground service for location while the screen is locked. Tested on an emulator and on two physical phones; iOS is not tested. |
| Server | Kotlin with Ktor, one application; each module of section 5.1 is a package. Using one language for app and server lets the entities of section 7 be shared. |
| Data | GTFS from stops.lt/vilnius/vilnius/gtfs.zip (GitHub mirror vilnius/transportas as fallback). Live positions from stops.lt/vilnius/gps_full.txt, which includes measurement time, GTFS trip id, direction and deviation. Recorded feed and GPS traces for replay tests and demo fallback. |
| Map services | Map tiles from a free-tier tile provider that permits app use (not the public OSM tile servers). Tier 2: self-hosted Photon and GraphHopper in Docker with the Lithuania extract; if setup takes more than 8 hours, the straight-line fallback stays. |
| Server hosting | A small cloud virtual machine running the server in Docker during the test period, behind HTTPS. Development and replay tests run on the team's laptops. |
| Testing and CI | Unit tests for server modules and the in-ride engine, replay tests with recorded GPS traces, GitHub Actions. |

In-ride logic runs on the phone, as in the specification. The server runs on the cloud virtual machine, so that the phone can reach it during real test rides outside the laptop's network. There is no production release.

## **Scope tiers**

| Tier | Content | Hours | Cut rule |
| :---- | :---- | ----- | :---- |
| Core | M-1 to M-19 and BF-04: planning with 1 transfer, departures, live map, manual vehicle selection, transfer warning, get-off alert, replan | \~100 | Always delivered |
| Tier 2 | S-1 to S-5, S-10 to S-13, addresses (Photon), walking routes (GraphHopper), up to 3 transfers | \~35 | Dropped second, last item first: GraphHopper, then Photon, then S-11 |
| Tier 3 (stretch) | S-6 to S-9: automatic detection, automatic end, route-change notices | \~25 | Dropped first. Started only if the week 2 checkpoint shows the schedule holds; otherwise not started |

## **Functional requirements**

| ID | Requirement | Decision | Tier | Scope and notes |
| :---- | :---- | :---- | :---- | :---- |
| M-1 | Single search field | Will do | Core / 2 | Core: stops and lines; a destination can also be set with a map pin. Tier 2: addresses and places through Photon. |
| M-2 | Suggestions while typing | Will do | Core | Dynamic search suggestions with support for names without Lithuanian characters (4.3.4). |
| M-3 | Line stops and timetable | Will do | Core | Ordered stops and scheduled departures for each direction. |
| M-4 | Journey planning with transfers | Simplified | Core / 2 | Custom timetable-based search, rules 1–7 of 5.4. Core: shows at most 1 transfer. Tier 2: can show up to 3 transfers (Table 1). |
| M-5 | Sorted journey options | Will do | Core | Sorted by arrival time, all fields of M-5. Latest-arrival sorting comes with S-5 (Tier 2). |
| M-6 | "My location" | Will do | Core | Uses exact device GPS coordinates for journey planning. |
| M-7 | Pin on map | Will do | Core | Allows starting point and destination selection using exact map coordinates. |
| M-8 | Default start and time | Will do | Core | Defaults to the current location and departure time. |
| M-9 | Location unavailable | Will do | Core | Prompts for manual starting point selection. |
| M-10 | Walking parts | Simplified | Core / 2 | Core: estimated distance using a 1.3 multiplier and 4.5 km/h walking speed. Tier 2: GraphHopper walking routes; the straight-line fallback stays. |
| M-11 | Live vehicles on map | Will do | Core | Server updates vehicle positions every 10 seconds from gps_full.txt, which carries the measurement time. Positions older than two minutes are not shown as live. |
| M-12 | Lithuanian and English | Will do | Core | Interface and notifications use LT/EN string resources and follow the device's language settings. |
| M-13 | Stop departures | Will do | Core | Departures sorted by expected time, with scheduled times and delays displayed. |
| M-14 | Manual vehicle selection | Will do | Core | Selection based on proximity or line number, direction and upcoming stop. |
| M-15 | Known vehicle as start | Will do | Core | Automatically uses the active vehicle as the starting point for journey planning. |
| M-16 | Transfer-risk warning | Will do | Core | Evaluates transfer feasibility every 30 seconds using a one-minute boarding margin. |
| M-17 | Replan at risk | Will do | Core | Recalculates from the vehicle's position and lists the options. |
| M-18 | Upcoming stops and get-off alert | Will do | Core | GPS-based stop tracking. Sound, vibration and notifications within 10 seconds, including when the screen is locked. |
| M-19 | End assistance manually | Will do | Core | Terminates active ride tracking and associated alerts at any time by user request. |
| S-1 | Favourites | Will do | Tier 2 | Stops, places, lines and journeys stored locally without an account. |
| S-2, S-3 | Recent searches, first suggestions | Will do | Tier 2 | Stores the 20 most recent searches and prioritises them alongside favourites. |
| S-4 | Nearest stops with departures | Will do | Tier 2 | Nearby stops sorted by walking distance, with upcoming departures. |
| S-5 | Departure and latest-arrival time | Will do | Tier 2 | Supports custom departure and latest-arrival times with corresponding result sorting. |
| S-10 | Notification switches | Will do | Tier 2 | Individual notification categories configurable through settings. |
| S-11 | Faster alternatives during a ride | Will do | Tier 2 | Suggests alternatives saving at least three minutes, at most once every two minutes. |
| S-12 | Step-by-step guidance | Will do | Tier 2 | Detailed boarding, walking, transfer and exit instructions for each journey segment. |
| S-13 | Lock-screen widget | Simplified | Tier 2 | Android ongoing notification only, excluding iOS Live Activity. |
| S-6, S-7 | Automatic vehicle detection and confirmation | Stretch | Tier 3 | Built only if the schedule holds. Accuracy checked on 5 test rides instead of 20. |
| S-8 | Automatic end | Stretch | Tier 3 | Built only if the schedule holds. Terminates assistance based on movement and distance thresholds defined in section 4.2.6. |
| S-9 | Route-change notices | Stretch | Tier 3 | Built only if the schedule holds. Detects changes between GTFS versions; notices limited to favourites and active journeys. |
| C-1 – C-8 | All Could have | Won't do | – | No accounts, reports, tickets or step-free routing. The modules stay as dashed boxes in the architecture. C-8 is the first stretch item if the buffer remains. |
| BF-04 | Data processing | Will do | Core | Hourly GTFS checks, encoding conversion, data validation and vehicle feed updates every 10 seconds. |

## **Scope decisions**

| Question | Decision |
| :---- | :---- |
| Platform and delivery | Mobile app only; no web app or admin panel. Android only. Prototype runs on a cloud virtual machine during the test period; no production release. |
| Product quality | Clean, usable UI built from the wireframes of section 6; not pixel-perfect or production-grade. |
| City and modes | Vilnius only, one GTFS feed, buses and trolleybuses (W-1, W-2). Modular packages, but no support for other cities. |
| Data | Real GTFS and real live feed. A recorded feed and recorded GPS traces are used for tests and as demo fallback. |
| Alerts | Get-off and transfer alerts only while a journey is active. Unguided tracking shows upcoming stops without alerts (BF-02). |
| Offline | Journey planning shows a message (W-3). Favourites and recent searches work offline. |
| Error handling | Missing GPS (M-9), no internet, no live data (4.1.4) and no results are handled with messages. |
| Accessibility | Text scaling to 200%, text status tags (never colour alone), TalkBack check of the main flow. A full WCAG 2.1 AA audit is not committed. |
| Deliverables | Working prototype; updated package and class diagrams (5.1 to 5.3); test and benchmark report; usability test report; demo of the scenario in 6.4. |
| Three essential features | Journey planning with transfers; live departures with the vehicle map; in-ride warnings (transfer risk and get-off alert). |

## **Non-functional requirements**

•     **Performance (4.1):** measured and reported, not guaranteed. Response times are measured with a load script of 200 simulated clients; the 5,000-user target is not tested. Degraded operation (4.1.4) is implemented.

•     **In-ride assistance (4.2):** 4.2.1 to 4.2.4 are met in Core and Tier 2. 4.2.5 and 4.2.6 (automatic detection and end) are Tier 3 stretch goals; if built, detection accuracy is checked on 5 test rides.

•     **Usability (4.3):** a test with 5 first-time users, including 2 who do not know Vilnius; task times and the SUS score are reported, not guaranteed.

•     **Device and platforms (4.4):** Android 10 or later. Battery and mobile data use are measured once on a real phone and reported.

•     **Security and privacy (4.5):** no accounts; coordinates are not stored or logged on the server and journeys are never saved. The cloud server is reached only over HTTPS; plain HTTP is used only in local debug builds.

•     **Licences:** OSM attribution is shown on the map; raid.lt is not used. The stops.lt data licence is confirmed in Week 1, before the prototype is shown outside the team.

## **Plan**

•     **Week 1 (16–22 Nov), about 38 hours:** repository, CI, API contract (endpoints and entities of section 7), updated diagrams; GTFS importer and TimetableStore; Search Service for stops and lines; app skeleton, LT/EN string resources and Home map; record a sample feed and two GPS traces; map and foreground-service spikes; confirm the data licence and tile provider.

•     **Week 2 (23–29 Nov), about 42 hours:** Realtime Service (gps_full.txt feed, trip matching, delays); RideTracker with replay tests; stop departures and Line screen; first Trip Planner (direct and 1 transfer); Search and Journey options screens. **Checkpoint at the end of week 2** decides whether Tier 3 is started.

•     **Week 3 (30 Nov – 6 Dec), about 42 hours:** Journey details; Select vehicle screen; InRideSession; get-off alert, transfer-risk warning and replan (end of Core); start of Tier 2 (favourites, recents, nearest stops, time selection).

•     **Week 4 (7–14 Dec), about 38 hours:** rest of Tier 2 (alternatives, notification switches, ongoing notification, Photon, GraphHopper, 3 transfers); Tier 3 only if the checkpoint allowed it; replay tests, real test ride from the cloud server, load script, usability test, demo and documentation.

Total planned: about 160 hours (setup 10, GTFS 12, search 8, departures 8, realtime 12, trip planner 22, mobile screens 28, in-ride engine 26, automatic detection 8 and route-change notices 5 as Tier 3 stretch, testing 14, documentation 7). Buffer: about 40 hours, used first on the in-ride engine and the Trip Planner. **Checkpoint:** at the end of week 2, the team compares hours actually spent with the plan and drops tiers if it is more than 15% behind.

## **Work split**

Five areas, each with one owner: **data** (GTFS importer, TimetableStore), **realtime and search** (Realtime Service, Search Service, map services), **planning** (Trip Planner), **mobile app** (screens, favourites, language) and **in-ride engine** (InRideSession, RideTracker, alerts; automatic detection and route-change diff as Tier 3 stretch). The owner of each area writes its tests. Every change is reviewed by another team member before merging.

## **Risks**

| Risk | Mitigation |
| :---- | :---- |
| Live feed format, freshness, or vehicle-to-trip matching is unsuitable | Validate in Week 1; save permitted samples; support deterministic replay; label replay data. |
| Planner work exceeds the estimate | Time-box the OpenTripPlanner spike; constrain the supported cases; test calendars, dates, transfers, and realtime fallback early. |
| GPS matching produces missed or false get-off alerts | Test recorded traces and physical-device rides; handle uncertainty explicitly; do not alert when stop progress is ambiguous. |
| Server unreachable during a physical test ride (laptop asleep, no Wi-Fi, IP change) | Server runs on a cloud virtual machine with HTTPS, not on a laptop; check the connection before each ride; replay traces as fallback. |
| Map tiles or map services have unsuitable terms or setup cost | Confirm the tile source and attribution in Week 1; do not rely on public OSM tiles; defer geocoding and walking routers. |
| Background location behaves differently across Android versions/devices | Test the foreground service and permission flow on emulator and physical devices; prioritize a working foreground demo before optional features. |
| Team falls behind | Keep the 40-hour contingency; drop Tier 3 first, then Tier 2 items; preserve a complete, tested Core demo rather than starting additional partial features. |
