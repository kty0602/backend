# Trello 프로젝트

## 📖 목차
1. [프로젝트 소개](#프로젝트-소개)
2. [팀소개](#팀소개)
3. [프로젝트 계기](#프로젝트-계기)
4. [주요기능](#주요기능)
5. [개발기간](#개발기간)
6. [기술스택](#기술스택)
7. [와이어프레임](#와이어프레임)
8. [API 명세서](#API-명세서)
9. [ERD](#ERD)
10. [프로젝트 파일 구조](#프로젝트-파일-구조)
11. [Trouble Shooting](#trouble-shooting)
    
## 👨‍🏫 프로젝트 소개
Trello는 프로젝트 관리를 위한 협업 툴로, 직관적인 칸반 보드(Kanban Board) 방식으로 작업을 관리할 수 있어, 많은 기업에서 애용하는 도구입니다. 
이번 프로젝트를 통해 Trello의 백엔드를 개발하며 실무에서 사용하는 여러 기술을 접하고 적용해보는 기회를 가질 것입니다.

## 팀소개
| 팀장 | 실세 | 실세 | 실세 |
| :------------: | :------------: |:------------:|:------------:|
|[@강태영](https://github.com/kty0602)|[@정은교](https://github.com/ekj1003)|[@김아름](https://github.com/areum0116)|[@황인서](https://github.com/inseooo0)|

### [팀 노션](https://teamsparta.notion.site/14-fcb0547f0e6b41dab956f85ac8ee21d1)

## 💜 주요기능

- LOCK을 활용한 동시성 제어

- Redis를 활용한 캐시 전략

- 인덱스를 활용한 최적화

- 알림 기능

## ⏲️ 개발기간
- 2024.10.14(월) ~ 2024.10.17(목)

## 📚️ 기술스택

### ✔️ Tool
![Figma](https://img.shields.io/badge/figma-%23F24E1E.svg?style=for-the-badge&logo=figma&logoColor=white)
![Slack](https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white)
<img src="https://img.shields.io/badge/Amazon%20S3-569A31?style=for-the-badge&logo=Amazon%20S3&logoColor=white">

### ✔️ Language
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)

### ✔️ Version Control
![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)

### ✔️ IDE
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJIDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)

### ✔️ Framework
<img src="https://img.shields.io/badge/spring boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=Spring Security&logoColor=white"> <img src="https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white">

### ✔️  DBMS
![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)

### ✔️ Library
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)

## ✔️ 와이어프레임
![](https://github.com/SemiFinalPJ/backend/blob/dev/src/img/%EC%99%80%EC%9D%B4%EC%96%B4%20%ED%94%84%EB%A0%88%EC%9E%84.png)

## ✔️ API 명세서
| API 명세 | API 명세  | API 명세 | API 명세 |
| :------------: | :------------: |:------------:|:------------:|
|<img src="https://github.com/SemiFinalPJ/backend/blob/dev/src/img/api%20%EB%AA%85%EC%84%B81.png" width="300" height="200"/>|<img src="https://github.com/SemiFinalPJ/backend/blob/dev/src/img/api%20%EB%AA%85%EC%84%B82.png" width="300" height="200"/>|<img src="https://github.com/SemiFinalPJ/backend/blob/dev/src/img/api%20%EB%AA%85%EC%84%B83.png" width="300" height="200"/>|<img src="https://github.com/SemiFinalPJ/backend/blob/dev/src/img/api%20%EB%AA%85%EC%84%B84.png" width="300" height="200"/>|

## ✔️ ERD
![](https://github.com/SemiFinalPJ/backend/blob/dev/src/img/ERD.png)

## ✔️ Trouble Shooting
강태영
- 인덱스를 조회 속도가 가장 느린 조건에 설정했으나, 성능 개선이 크게 이뤄지지 않음, 성능 개선을 위해 상대적으로 덜 중복되는 title과 info필드를 인덱스로 지정함.
김아름
- 하나의 카드에 여러 스레드가 접근해 변경을 시도하는 테스트에서 낙관적 락을 사용하니 충돌이 너무 많아 계속 롤백하고 다시 시도하며 테스트가 끝나지 않는 이슈 발생. 최대 시도 횟수 정의한 후 초과시 종료하도록 함.
정은교
- Jmeter 테스트 진행 시, 여러개의 유저와 요청을 어떻게 설정해야되는지 문제가 있었으나, csv 파일로 여러 유저의 토큰과 여러 uri이 담긴 2개의 파일을 생성하여, Jmeter 상에서 환경변수를 설정하여 테스트를 진행함.
황인서
- 


