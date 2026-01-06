# 🛍️ Moamart - 픽업 서비스 중심 온라인 마켓

> 편리한 온라인 주문, 가까운 매장에서 픽업하세요!

## 📋 프로젝트 소개

Moamart는 온라인으로 주문하고 원하는 시간에 매장에서 픽업할 수 있는 편리한 쇼핑 플랫폼입니다.

### ✨ 주요 특징

- 🏪 **픽업 서비스**: 원하는 매장과 시간을 선택하여 편리하게 픽업
- 🛒 **실시간 장바구니**: 재고 확인 및 수량 조절
- 📦 **상품 관리**: 카테고리별 상품 분류 및 검색
- 💳 **간편 주문**: 비회원도 이용 가능한 간단한 주문 프로세스
- 🔄 **유연한 주문 관리**: 부분 반품/교환 지원
- ♻️ **종량제 봉투**: 환경을 생각하는 봉투 선택 옵션

---

## 🛠️ 기술 스택

### Backend
- ☕ **Java 17** - 최신 LTS 버전
- 🍃 **Spring Boot 3.x** - 핵심 프레임워크
- 🔐 **Spring Security** - 인증/인가 관리
- 💾 **Spring Data JPA** - ORM 및 데이터 접근
- 🔍 **QueryDSL** - 동적 쿼리 처리
- 🏗️ **Gradle 9.2.1** - 빌드 도구

### Frontend
- 🎨 **Thymeleaf** - 서버 사이드 템플릿 엔진
- 📝 **Markdown** - 상품 상세 설명 지원 (marked.js, SimpleMDE)
- 💅 **Vanilla CSS** - 커스텀 스타일링

### Database
- 🗄️ **H2** - 개발 환경
- 🐬 **MySQL** - 운영 환경

### Infrastructure
- ☁️ **AWS S3** - 이미지 스토리지
- 🔑 **AWS Secrets Manager** - 민감 정보 관리
- 🚀 **AWS EC2** - 서버 호스팅

### 이미지 처리
- 🖼️ **Thumbnailator** - 이미지 리사이징 및 최적화

---

## 📁 프로젝트 구조

```
moamart/
├── 📂 src/main/java/com/kobe/moamart/
│   ├── 🎮 controller/           # 컨트롤러 계층
│   │   ├── admin/              # 관리자 컨트롤러
│   │   ├── api/                # REST API
│   │   └── view/               # 뷰 컨트롤러
│   ├── 💼 service/              # 비즈니스 로직
│   ├── 🏛️ domain/               # 엔티티 및 리포지토리
│   │   ├── product/            # 상품 도메인
│   │   ├── order/              # 주문 도메인
│   │   ├── member/             # 회원 도메인
│   │   ├── store/              # 매장 도메인
│   │   └── category/           # 카테고리 도메인
│   ├── 📦 dto/                  # 데이터 전송 객체
│   ├── ⚙️ global/               # 전역 설정
│   │   ├── config/             # 설정 클래스
│   │   ├── security/           # 보안 설정
│   │   ├── util/               # 유틸리티
│   │   └── exception/          # 예외 처리
│   └── 🚀 MoamartApplication.java
├── 📂 src/main/resources/
│   ├── 📄 application.yml       # 기본 설정
│   ├── 📄 application-dev.yml   # 개발 환경 설정
│   ├── 📄 application-prod.yml  # 운영 환경 설정
│   └── 📂 templates/            # Thymeleaf 템플릿
└── 🔨 build.gradle
```

---

## 🚀 시작하기

### 📋 사전 요구사항

- ☕ Java 17 이상
- 🏗️ Gradle 9.2.1 이상
- 🐬 MySQL 8.0 이상 (운영 환경)

### ⚙️ 환경 설정

#### 1️⃣ 저장소 클론

```bash
git clone https://github.com/your-username/moamart.git
cd moamart
```

### 🏃 실행

#### 개발 환경

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

#### 운영 환경

```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```

또는 JAR 파일 빌드 후 실행:

```bash
./gradlew build
java -jar -Dspring.profiles.active=prod build/libs/moamart-0.0.1-SNAPSHOT.jar
```

### 🌐 접속

- 🏠 **메인 페이지**: http://localhost:8080
- 🔧 **관리자 페이지**: http://localhost:8080/admin/login
- 💾 **H2 콘솔** (개발 환경): http://localhost:8080/h2-console
- 😀 **운영 페이지:** https://www.moa-mart.com

---

## 🎯 주요 기능

### 👤 사용자 기능

#### 🛒 쇼핑
- ✅ 카테고리별 상품 조회
- ✅ 상품 상세 정보 확인
- ✅ 장바구니 추가/수정/삭제
- ✅ 실시간 재고 확인

#### 📦 주문
- ✅ 픽업 매장 선택
- ✅ 픽업 시간 지정
- ✅ 종량제 봉투 선택 (10L/20L)
- ✅ 주문 내역 조회
- ✅ 주문 상태 실시간 확인

#### 👥 회원
- ✅ 회원가입/로그인
- ✅ 비회원 주문 가능

### 🔧 관리자 기능

#### 📦 상품 관리
- ✅ 상품 등록/수정/삭제
- ✅ 카테고리 관리
- ✅ 재고 관리
- ✅ 이미지 업로드 (썸네일 자동 리사이징)
- ✅ Markdown 지원 상세 설명
- ✅ 상품 상태 관리 (판매중/품절/숨김)

#### 🚚 주문 관리
- ✅ 주문 목록 조회 및 필터링
- ✅ 주문 상태 변경
- ✅ 개별 상품 상태 관리
- ✅ 부분 반품/교환 처리
- ✅ 상품 분리 기능
- ✅ 차액/환불 금액 자동 계산

#### 🏪 매장 관리
- ✅ 매장 등록/수정
- ✅ 운영 상태 관리

---

## 🗂️ 주요 엔티티

### 🏷️ Product (상품)
```java
- id: 상품 ID
- name: 상품명
- price: 가격
- stockQuantity: 재고 수량
- status: 상태 (SELL/SOLD_OUT/STOP)
- isDisplayed: 노출 여부
- isNew: 최신 상품 여부
- thumbnailUrl: 썸네일 이미지
- category: 카테고리 (N:1)
- images: 상세 이미지 (1:N)
```

### 📋 Order (주문)
```java
- id: 주문 ID
- recipientName: 받는 사람
- phoneNumber: 연락처
- status: 주문 상태
- orderDate: 주문 일시
- pickupDateTime: 픽업 예정 일시
- bagType: 봉투 타입
- originalTotalPrice: 사전 결제 금액
- store: 픽업 매장 (N:1)
- orderItems: 주문 상품 (1:N)
```

### 📦 OrderItem (주문 상품)
```java
- id: 주문 상품 ID
- orderPrice: 주문 당시 가격
- count: 수량
- status: 상품 상태 (개별 관리)
- product: 상품 (N:1)
- order: 주문 (N:1)
```

---

## 🎨 화면 구성

### 🏠 사용자 화면
- `index.html` - 메인 페이지 (상품 목록)
- `product/detail.html` - 상품 상세
- `cart.html` - 장바구니
- `orders/review.html` - 주문 검토 (매장 선택)
- `order/checkout.html` - 주문/결제
- `order/complete.html` - 주문 완료
- `orders/list.html` - 주문 내역
- `orders/detail.html` - 주문 상세
- `member/join.html` - 회원가입
- `member/login.html` - 로그인

### 🔧 관리자 화면
- `admin/login.html` - 관리자 로그인
- `admin/product/list.html` - 상품 목록
- `admin/product/form.html` - 상품 등록/수정
- `admin/order/list.html` - 주문 목록
- `admin/order/detail.html` - 주문 상세
- `admin/store/list.html` - 매장 목록
- `admin/store/form.html` - 매장 등록/수정

---

## 🔐 보안

- ✅ Spring Security 기반 인증/인가
- ✅ 비밀번호 암호화 (BCrypt)
- ✅ AWS Secrets Manager 연동 (운영 환경)
- ✅ CSRF 보호
- ✅ 권한별 접근 제어 (ADMIN/USER)

---

## 📸 이미지 처리

### 🖼️ 로컬 파일 업로드 (개발 환경)
- 업로드 디렉토리: `./uploads/`
- 자동 리사이징: 썸네일(800x800), 원본(1920x1920)
- 압축: JPEG 품질 85%

### ☁️ AWS S3 업로드 (운영 환경)
- 버킷: `moamart-product-images-bucket`
- 경로: `images/` 디렉토리
- CloudFront 연동 지원

---

## 🧪 테스트

```bash
./gradlew test
```

---

## 📦 배포

### JAR 빌드

```bash
./gradlew clean build
```

### Docker (예정)

```bash
# 추후 추가 예정
```

---

## 🤝 기여

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'feat: Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### 📝 커밋 컨벤션

```
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷팅
refactor: 코드 리팩토링
test: 테스트 코드
chore: 빌드 업무 수정
```

---

## 📄 라이센스

This project is licensed under the MIT License.

---

## 👨‍💻 개발자

**Bing_9 (kobe)**

- GitHub: [@devKobe24](https://github.com/devKobe24)
- Email: dev.skyachieve91@gmail.com

---

## 🙏 Ref

이 프로젝트는 다음 기술들을 사용하여 만들어졌습니다:

- Spring Boot
- Thymeleaf
- AWS Services
- Thumbnailator
- marked.js / SimpleMDE
- 그 외 모든 오픈소스 기여자분들께 감사드립니다! 🎉

---

## 📞 문의

프로젝트에 대한 질문이나 제안사항이 있으시면 이슈를 등록해 주세요!
