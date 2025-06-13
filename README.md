# 🛡️ BlockBit Back

BlockBit 백엔드 서비스입니다.

Kotlin 기반으로 구축되었으며, 지갑 기능, 사용자 인증, 쿠폰 처리, 트랜잭션 기록 등 블록체인 지갑 서비스의 핵심 API들을 제공합니다.

## 🚀 기술 스택

• **Ktor** (Modular 구조)
• **Exposed ORM** with PostgreSQL  
• **Redis** (세션/캐시)
• **JWT / OAuth2** 인증
• **bitcoinj / web3j** - 블록체인 트랜잭션 처리
• **Swagger** API 문서 자동화

## 📦 주요 기능

• 사용자 인증 (지갑 서명 기반)
• 쿠폰 등록 및 사용 이력 관리
• 이더리움 및 비트코인 트랜잭션 위임 처리
• 트랜잭션 수수료 분석 및 이상 감지
• 관리자용 통계 및 모니터링 API
• Swagger 기반 API 문서 제공

## 🛠️ 개발 환경 구성

```bash
# 의존성 설치
./gradlew build

# 개발 서버 실행
./gradlew run

# 빌드
./gradlew build
```

## 🧪 테스트

```bash
# 유닛 테스트
./gradlew test

# E2E 테스트
./gradlew integrationTest
```

## ⚙️ 환경 변수 (.env 예시)

```
PORT=8080
DATABASE_URL=jdbc:postgresql://127.0.0.1:5432/postgres
JWT_SECRET=BLOCKBIT_2024_SECURE_JWT_KEY_COMPLEX_RANDOM_SEED_987!@#
REDIS_URL=redis://localhost:6379
ETH_RPC_URL=https://mainnet.infura.io/v3/your_project_id
BTC_API_URL=https://blockstream.info/api
```

## 📁 주요 폴더 구조

```
blockbit2-back/
├── src/
│   ├── main/kotlin/
│   │   ├── config/          # 애플리케이션 설정
│   │   ├── dto/             # 데이터 전송 객체
│   │   ├── exception/       # 예외 처리
│   │   ├── plugins/         # JWT 인증 플러그인
│   │   ├── repository/      # 데이터 접근 계층
│   │   ├── route/           # REST API 라우팅
│   │   ├── service/         # 비즈니스 로직 계층
│   │   └── utils/           # 유틸리티 클래스
│   └── resources/           # 설정 파일
└── test/                    # 테스트 코드
```

## 🌐 API 엔드포인트

### 👤 사용자 관리 (`/api/users`)
```
GET    /api/users              # 모든 사용자 조회
GET    /api/users/{id}         # 사용자 ID로 조회
GET    /api/users/num/{num}    # 사용자 번호로 조회
POST   /api/users              # 신규 사용자 등록
PUT    /api/users              # 사용자 정보 수정
PUT    /api/users/change-password  # 비밀번호 변경
DELETE /api/users/{usiNum}     # 사용자 삭제 (비활성화)
POST   /api/users/login        # 로그인
```

### 🏦 지갑 관리 (`/api/wal`) 🔐
```
GET    /api/wal/list           # 지갑 목록 조회
GET    /api/wal/{walNum}       # 특정 지갑 조회
POST   /api/wal                # 지갑 생성
PUT    /api/wal                # 지갑 수정
DELETE /api/wal/{walNum}       # 지갑 삭제
GET    /api/wal/wad/list/{usiNum}  # 사용자별 지갑 목록
```

### ₿ 비트코인 지갑 (`/api/wallet/bitcoin`) 🔐
```
POST   /api/wallet/bitcoin/create                    # 멀티시그 지갑 생성
POST   /api/wallet/bitcoin/transaction/create        # 트랜잭션 생성 (첫 번째 서명)
POST   /api/wallet/bitcoin/transaction/complete      # 트랜잭션 완료 (두 번째 서명)
GET    /api/wallet/bitcoin/transaction/{txId}        # 트랜잭션 상태 조회
GET    /api/wallet/bitcoin/utxos/{address}           # UTXO 목록 조회
GET    /api/wallet/bitcoin/balance/{address}         # 주소 잔액 조회
```

### 💸 거래 내역 (`/api/trx`) 🔐
```
GET    /api/trx/list           # 거래 내역 목록
GET    /api/trx/{trxNum}       # 특정 거래 조회
POST   /api/trx                # 거래 생성
PUT    /api/trx/{trxNum}       # 거래 수정
DELETE /api/trx/{trxNum}       # 거래 삭제
```

### 💰 자산 관리 (`/api/assets`) 🔐
```
GET    /api/assets/list        # 자산 목록 조회
GET    /api/assets/{assetNum}  # 특정 자산 조회
POST   /api/assets             # 자산 등록
PUT    /api/assets             # 자산 수정
DELETE /api/assets/{assetNum}  # 자산 삭제
```

### 📋 정책 관리 (`/api/policies`) 🔐
```
GET    /api/policies/list      # 정책 목록 조회
GET    /api/policies/{polNum}  # 특정 정책 조회
POST   /api/policies           # 정책 생성
PUT    /api/policies           # 정책 수정
DELETE /api/policies/{polNum}  # 정책 삭제
```

### 🔧 공통 코드 (`/api/common-codes`) 🔐
```
GET    /api/common-codes/list  # 공통 코드 목록
GET    /api/common-codes/{id}  # 특정 코드 조회
POST   /api/common-codes       # 코드 생성
PUT    /api/common-codes       # 코드 수정
DELETE /api/common-codes/{id}  # 코드 삭제
```

### 📍 지갑 주소 (`/api/wallet-addresses`) 🔐
```
GET    /api/wallet-addresses/list           # 지갑 주소 목록
GET    /api/wallet-addresses/{wadNum}       # 특정 주소 조회
POST   /api/wallet-addresses               # 주소 등록
PUT    /api/wallet-addresses               # 주소 수정
DELETE /api/wallet-addresses/{wadNum}      # 주소 삭제
```

**🔐 인증 필요**: JWT 토큰이 필요한 엔드포인트

## 📚 API 문서

• **Swagger UI**: `http://localhost:8080/swagger-ui`

## 📄 라이선스

MIT License