# HighV - E-Commerce

## 프로젝트 설명

이 프로젝트는 Kotlin과 Spring Boot를 사용하여 인터넷 쇼핑몰을 구현한 프로젝트입니다.

## 주요 기능

- **상품 확인 및 검색**: 사용자가 홈페이지에서 상품을 확인하고 검색할 수 있습니다.
- **블랙리스트 기능**: 블랙리스트에 등록된 사용자는 특정 기능을 사용할 수 없습니다.
- **쿠폰 기능**: 다양한 쿠폰을 제공하여 할인 혜택을 받을 수 있습니다.
- **판매자 상태 관리**: 판매자 상태에 따라 상품 등록이 제한되며, 판매자가 탈퇴하면 등록된 상품이 자동으로 삭제됩니다.
- **상품 상세 페이지**: 장바구니 추가 및 리뷰 작성 기능을 제공합니다.
- **백오피스 기능**: 관리자용 백오피스를 통해 상품, 주문, 사용자 관리가 가능합니다.

## 팀원 소개

<table align="center">
    <thead>
        <tr>
            <th style="text-align:center;">김도균</th>
            <th style="text-align:center;">류원형</th>
            <th style="text-align:center;">김형섭</th>
            <th style="text-align:center;">최민수</th>
            <th style="text-align:center;">정혜린</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td><a href="https://ibb.co/XytT1N0"><img width="160" src="https://github.com/user-attachments/assets/6f90a5f2-4368-45e5-b9a7-2c64ce5aa3ae" alt="김도균"></a></td>
            <td><a href="https://ibb.co/XytT1N0"><img width="160" src="https://i.ibb.co/SQBpbWY/DALL-E-2024-06-18-22-05-45-An-illustration-of-a-young-boy-who-loves-boxing-and-is-also-a-developer-i.png" alt="류원형"></a></td>
            <td><a href="https://imgbb.com/"><img width="160" src="https://i.ibb.co/z4t29cZ/162294237.png" alt="김형섭"></a></td>
            <td><a href="https://imgbb.com/"><img width="160" src="https://ca.slack-edge.com/T07BXLG6UF8-U07BPNS8CTZ-26ee1b54322e-512" alt="최민수"></a></td>
            <td><a href="https://imgbb.com/"><img width="160" src="https://ca.slack-edge.com/T07BXLG6UF8-U07BPNS64PR-26ce5573acdd-512" alt="정혜린"></a></td>
        </tr>
        <tr>
            <td><a href="https://github.com/Ppajingae">@Ppajingae</a></td>
            <td><a href="https://github.com/1hyung">@1hyung</a></td>
            <td><a href="https://github.com/hysup">@hysup</a></td>
            <td><a href="https://github.com/HifumiAlice">@HifumiAlice</a></td>
            <td><a href="https://github.com/DEVxMOON">@DEVxMOON</a></td>
        </tr>
    </tbody>
</table>

## 협업 도구

- ![Git](https://img.shields.io/badge/Git-F05032?style=flat-square&logo=git&logoColor=white)
- ![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white)
- ![Slack](https://img.shields.io/badge/Slack-4A154B?style=flat-square&logo=slack&logoColor=white)

## [Wireframe](https://www.figma.com/design/Pcn6VeErcGenAJxDOor9SG/Spa-2%EC%A1%B0-%EC%B5%9C%EC%A2%85?node-id=0-1&t=YT5Oho8LHWcjdKjn-0)

![Wireframe Image](https://github.com/user-attachments/assets/cd24b2ca-ed0f-4b69-987c-9f70445bbf58)

## [ERD](https://www.figma.com/board/9tp3ICiW8Z5K6XbrL9iiQ9/%EC%8B%A4%EC%A0%84%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-5%EC%A1%B0?node-id=128-646&t=zWtge9vkIq5qf5xp-0)

![ERD Image](https://github.com/user-attachments/assets/e6da319d-a03c-4490-945c-daffd647a00d)

## 기능 구현 세부사항

1. **판매자 상태에 따른 상품 등록 제한**:
    - **판매자 상태 확인**: 상품을 등록할 때, 판매자의 상태가 PENDING(대기) 또는 RESIGNED(탈퇴)일 경우, 상품을 등록할 수 없도록 예외를 발생시킵니다.
    - **API 구현**: 판매자의 상태를 확인하여 상품 등록을 제한하는 로직이 포함된 API를 구현했습니다. 이로써 부적격 판매자가 상품을 등록하는 것을 방지합니다.

2. **판매자 탈퇴 시 상품 삭제**:
    - **탈퇴 승인 처리**: 판매자가 탈퇴할 경우, 해당 판매자의 상태를 RESIGNED(탈퇴 승인)으로 변경합니다.
    - **상품 삭제**: 탈퇴한 판매자와 관련된 모든 상품을 소프트 삭제 처리하여 데이터베이스에서 제거된 것처럼 표시합니다.
    - **API 구현**: 탈퇴 승인 시 해당 판매자의 상품을 자동으로 삭제하는 로직을 포함한 API를 구현했습니다.


## 환경설정

- **Language**: Kotlin
- **Framework**: Spring Boot
- **IDE**: IntelliJ Community Edition
- **JDK**: Temurin-17
- **Database**: MySQL
