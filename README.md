# NEXUS — Crime Data and Analytics Hub

Full-stack academic project (Java Servlets + JSP backend, React frontend, MySQL).

This repository contains the backend Java webapp in `src/` and a React frontend in `frontend/`.

Quick start (build locally)

Prerequisites:
- Java 17+ (or 21)
- Maven
- Node 18+
- Docker & docker-compose (optional)

Build backend (local):
```powershell
cd <repo-root>
./mvnw.cmd -DskipTests package
```

Build frontend:
```powershell
cd frontend
npm install --legacy-peer-deps
npm run build
```

Run with Docker (recommended for reproducible environment):
```powershell
docker-compose up --build
```

What I added in branch `dev/setup-ci-docker`:
- `.gitignore` tweaks
- CI workflow and Dockerfiles

Next recommended tasks:
- Add REST JSON endpoints and JWT auth
- Integrate React frontend with backend APIs
- Add real-time updates and notification pipeline
