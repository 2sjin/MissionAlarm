# 기상미션 알람 애플리케이션 개발<br>(Developing a wake-up mission alarm application)
![image](https://user-images.githubusercontent.com/91407433/151686367-5dccde1e-a91d-42e6-b576-a8a34f60e5b0.png)

- 기여자: 이승진, 조준호

## 목표
- 기존 알람 앱에 미션 시스템과 벌칙 시스템을 결합한 알람 앱을 개발한다.
- 늦잠으로 인하여 좋지 않은 상황이 발생하는 것을 방지한다.
- 잠을 깨는 데 도움 주는 미션을 통해 재 취침을 방지한다.
- 벌칙을 통해 정시 기상에 대한 경각심을 높여준다.
- 최신 OS에서도 정상적으로 사용할 수 있도록 최적화한 앱을 개발한다.

## 유사사례 분석
- SNS 및 SMS를 활용한 벌칙 알람 애플리케이션인 '*알람런(Alarm Run)*'의 한계
  - 터치 한 번만으로 알람을 끌 수 있어 다시 취침할 가능성이 크다.
  - 개발이 중단되어 최신 OS에서는 알람 기능을 사용할 수 없다. (오류 발생 및 강제종료)
  - 2022년 1월 기준, ESD 점유율 1위인 Google Play에 등록되어 있지 않다.
- '*알라미(Alarmy)*'와 같은 미션 알람 애플리케이션은 지속적으로 다운로드 되고 있음(Google Play 기준)
- 벌칙 기능과 미션 기능을 결합한 알람 애플리케이션은 존재하지 않음
- 미션 시스템과 벌칙 시스템을 적절하게 결합한 알람 애플리케이션을 사용함으로써 시너지 효과가 기대됨


## 운영체제(OS)
Android 8.0(Oreo) 이상 버전에 최적화됨.

## 소프트웨어 설계

### 애플리케이션 구성
![image](https://user-images.githubusercontent.com/91407433/151685995-a09ed702-a0a1-44aa-a184-fdf07de69a16.png)
- 알람 객체
  - *알람 설정 화면*을 통해 알람 추가 시, 입력한 값에 따라 '**알람 객체**'가 생성된다.
  - *알람 설정 화면*을 통해 알람 수정 시, 변경한 값에 따라 '**알람 객체**'의 필드가 변경된다.
- 알람 실행 과정
  - *메인 화면*의 리스트에 저장된 '**알람 객체**'는 '**백그라운드 서비스**' 내에 상주하는 알람 매니저에 의하여 관리된다.
  - 알람 시간이 되면 '**알람 객체**' 정보를 '**알람 리시버**'에 전달한다.
  - '**알람 리시버**'는 전달받은 '**알람 객체**'를 *알람 ON 화면*의 Activity에 전달하고, *알람 ON 화면*을 호출한다.
- 미션 및 벌칙 수행
  - 미션 활성화 시, 시작 버튼을 통해 *미션 화면*을 호출한다.
  - 벌칙 활성화 시, '**타이머**'를 불러와 작동시킨다.
  - '**타이머**' 시간 초과 시, *벌칙 화면*을 불러오고 '**벌칙**'을 수행한다.

### 알람 클래스(객체)의 필드
|제목|내용|설명|
|------|---|---|
|hour|int|알람 시간(시)|
|minute|int|알람 시간(분)|
|week|boolean[]|요일별 알람 활성화 여부(일~토)|
|name|String|알람 이름|
|ringtoneUri|Uri|알람음(Audio 파일 식별자)|
|ringtoneVolume|int|알람 음량(0~100)|
|vibration|boolean|진동 출력 여부|
|mission|boolean[]|종류별 미션 활성화 여부|
|penalty|boolean[]|종류별 벌칙 활성화 여부|
|phone|String|벌칙 시 문자메시지 발신번호|

## 소프트웨어 구현

### 메인 화면

<details markdown="1">
<summary>메인 화면(알람 리스트)</summary>

![image](https://user-images.githubusercontent.com/91407433/151685632-ff16d48d-fc10-4959-a100-b8d95c77d7bd.png)
![image](https://user-images.githubusercontent.com/91407433/151685634-5f948aaa-a113-4db9-bc39-51ee49300121.png)
- 좌: 권한 활성화 후 앱을 처음 실행할 때 또는 리스트에서 모든 알람을 삭제하였을 때
- 우: 알람을 추가 또는 수정할 경우 알람 리스트에 반영
> - 알람 추가: [추가] 버튼 누르기
> - 알람 수정: 리스트에서 수정을 원하는 알람 항목 누르기

</details>

<details markdown="1">
<summary>애플리케이션 권한 활성화</summary>

![image](https://user-images.githubusercontent.com/91407433/151685641-e8c7cf9b-076b-4dba-80c5-2031043349ca.png)
![image](https://user-images.githubusercontent.com/91407433/151685670-a6278d4e-fefa-4f9f-a386-de889157275f.png)
- 좌: 문자메시지(SMS) 권한 활성화
- 우: 다른 앱 위에 표시 권한 활성화

</details>

<details markdown="1">
<summary>알람 삭제 다이얼로그</summary>

![image](https://user-images.githubusercontent.com/91407433/151685675-d97a402a-3ef8-4f23-a377-d0540b31fe6c.png)
- 알람 리스트에서 삭제를 원하는 항목을 길게 누르면 위와 같이 삭제를 묻는 다이얼로그가 출력된다.
- [삭제]를 누르면 리스트에서 해당 알람이 삭제된다.

</details>

### 알람 설정 화면

<details markdown="1">
<summary>알람 추가 및 수정</summary>

![image](https://user-images.githubusercontent.com/91407433/151685681-a79ddcf7-2bd1-45b2-8d85-21e3a37ac5f3.png)
![image](https://user-images.githubusercontent.com/91407433/151685683-7e0cfab2-1469-4ddb-9c71-78d96a6ed6a9.png)
- 좌: 알람 설정 화면(시간, 요일, 알람 이름, 알람음, 미션, 벌칙)
- 우: [♬] 버튼(알람음 선택 버튼)을 누르면 위와 같이 알람음 선택기 호출
>  모든 요일 토글 버튼(일~토)을 비활성화하면 알람이 OFF됨(상단의 [알람 끄기] 버튼이 이를 한번에 수행함)

</details>

### 알람 화면

<details markdown="1">
<summary>알람 ON 화면</summary>

![image](https://user-images.githubusercontent.com/91407433/151685697-c257a47e-242a-47f3-bf5f-0c41f0719967.png)
![image](https://user-images.githubusercontent.com/91407433/151685699-94a001fa-69cb-4a75-833c-1a757cfeb24c.png)
- 좌: 기본적인 알람 ON 화면(미션과 벌칙을 비활성화 하였을 경우)
- 우: 미션을 활성화하면[미션 시작하기] 버튼이 추가된다. 벌칙을 활성화하면 상단에 타이머 남은 시간이 출력된다.
> 타이머 시간은 150~300초 사이가 적합할 것으로 생각됨
</details>

<details markdown="1">
<summary>미션 화면</summary>

![image](https://user-images.githubusercontent.com/91407433/151685703-5853065f-6706-4276-86ca-2c0c25a2def2.png)
![image](https://user-images.githubusercontent.com/91407433/151685704-8e7d3a08-9494-43a7-ba54-b94ab2b4aefb.png)
- 좌: 사자성어 퀴즈 미션 예시
- 우: 수학(사칙연산) 퀴즈 미션 예시
> [미리보기 종료] 버튼은 알람 설정 화면에서 미리보기를 하였을 경우에만 활성화된다. 실제 알람 미션 수행 시에는 활성화되지 않는다. 

</details>

<details markdown="1">
<summary>벌칙 화면</summary>

![image](https://user-images.githubusercontent.com/91407433/151685706-daa9fc52-62eb-4327-81bf-76b98fc46a6f.png)
![image](https://user-images.githubusercontent.com/91407433/151685709-09c51c2e-19be-422c-b8e2-0cbe9bb364c8.png)
- 좌: 타이머 시간 초과 시, 알람 설정 시 입력한 연락처로 문자메시지 전송
- 우: 실제로 문자메시지가 전송되었음을 확인할 수 있음
> 미션 수행 중 타이머 시간을 초과하기 전에 버튼 입력이 감지되면 타이머가 처음부터 재시작된다.

</details>

## 개선사항 및 향후 연구 방향
- 정확한 알람 시스템을 위해 필요한 개선사항
  - 애플리케이션 사용자 임의 종료 방지(전원 버튼, 홈 버튼, 백 버튼 등의 입력 차단)
  - 기기를 재시작한 후 애플리케이션을 실행하지 않아도 자동으로 백그라운드 서비스 시작
  - 알람 리스트를 저장하기 위한 파일 관리 시스템 구현

- 사용자의 편의성 측면에서 필요한 개선사항
  - 다양한 미션 시스템 구현(QR/바코드, 모션 인식, 다양한 주제의 퀴즈/퍼즐 등)
  - 다양한 벌칙 시스템 구현(SNS 게시글 업로드, 갤러리 이미지 전송 등)
  - 벌칙 설정 시, 사용자 자유도 향상(최대 연락처 수 증가, 전송할 내용 입력하기 등)
  - 벌칙 설정 시, 기기에 저장된 연락처 연동하기
  - 요일 뿐만 아니라 특정한 알람 날짜를 선택할 수 있도록 캘린더(달력)와 연동하기
