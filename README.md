# 🇿🇼 Zim Mobile Money

A mobile money / agency banking system built for Zimbabwe. Supports multi-currency wallets (USD & ZIG), agent cash-in/cash-out, P2P transfers, double-entry ledger, and RBZ-compliant audit trails.

## Tech Stack

| Layer | Tech |
|-------|------|
| Backend | Java 17, Spring Boot 3.3, PostgreSQL 16, Redis 7 |
| Frontend | Vue 3, Vite, Pinia, Axios |
| Infra | Docker, Docker Compose, Flyway |

## Quick Start

### 1. Clone & Configure
```bash
git clone <repo-url>
cd zim-mobile-money
```

Create a `.env` file (optional — defaults are provided):
```bash
JWT_SECRET=your-256-bit-secret-here
```

### 2. Run with Docker Compose
```bash
docker-compose up --build
```

Services will be available at:
- **Frontend** → http://localhost:3000
- **Backend API** → http://localhost:8080/api
- **PostgreSQL** → localhost:5432
- **Redis** → localhost:6379

### 3. Run Locally (Dev Mode)

**Backend:**
```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev
```

The Vite dev server proxies `/api` to `http://localhost:8080` automatically.



## Key Features

- **Multi-currency wallets** — USD & ZIG with configurable daily/monthly limits
- **Agent network** — Cash-in / cash-out with float tracking
- **P2P transfers** — Phone-number based with 0.5% fee
- **Double-entry ledger** — Every completed transaction posts immutable debit & credit entries
- **Idempotency** — SHA-256 keys in Redis + PostgreSQL prevent double-posting
- **Distributed locking** — Redis-based pessimistic locks on wallets & agent float
- **Audit trail** — Async audit logs for all financial actions
- **RBZ-ready** — Immutable ledger, reference numbers, and status tracking

## Default Users

After registration, the system auto-creates a **USD wallet** for every user. Use the `/auth/login` endpoint (or the frontend login page) with your phone number and PIN.

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `momo_db` | Database name |
| `DB_USER` | `momo` | Database user |
| `DB_PASS` | `momo` | Database password |
| `REDIS_HOST` | `localhost` | Redis host |
| `REDIS_PORT` | `6379` | Redis port |
| `JWT_SECRET` | `change-me-in-production...` | JWT signing key (min 256 bits) |

## License

MIT — Built for Zimbabwe 🇿🇼
