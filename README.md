# TQSPROJECT
2020/2021 \
P3_G302 
## Autores:
Alexandre Rodrigues [92951] : Product Owner\
Diogo Bento [93391] : DevOps \
Pedro Laranjinha [93179] : Team Lead\
Pedro Silva [93011] : QA Engineer

## Index
- [1. Pivotal Tracker](#1-pivotal-tracker)
- [2. Sonarqube](#2-sonarqube)
  - [2.1. MainApp](#21-mainApp)
  - [2.2. ClientApp](#22-clientApp)
- [3. Quality Assurance Manual](#3-quality-assurance-manual)
- [4. Product Specification Report](#4-product-specification-report)
- [5. Final Presentation](#5-final-presentation)
- [6. Continuous Deployment](#5-continuous-deployment)
- [7. Accounts](#7-accounts)


 ## 1. Pivotal Tracker
[Pivotal Tracker](https://www.pivotaltracker.com/n/projects/2499427)

## 2. Sonarqube
### 2.1. mainApp
[mainApp Sonarqube](https://sonarcloud.io/dashboard?branch=dev&id=buckaroo69_TQSPROJECT)
### 2.2. clientApp
[clientApp Sonarqube](https://sonarcloud.io/dashboard?branch=dev&id=tqs-side-client)
## 3. Quality Assurance Manual
[Quality Assurance Manual](https://drive.google.com/file/d/12jFk8szVuGfCYiRAILaeVrHQ-5xhbpTN/view?usp=sharing)

## 4. Product Specification Report
[Product Specification Report](https://drive.google.com/file/d/1mCa_Vn9Cjj906eXiIGCmfjluL3PKtuIP/view?usp=sharing)

## 5. Final Presentation
[Final Presentation](https://drive.google.com/file/d/15P4AhhRrBMvCzAPtatxokMu5ji7zNPNn/view?usp=sharing)

## 6. Continuous Deployment
Updates are continuously deployed to our virtual machine via Watchtower at deti-tqs-11.ua.pt\
[Logistics](http://deti-tqs-11.ua.pt:8080)\
[Restaurant](http://deti-tqs-11.ua.pt:8000)

If you fail to connect make sure you are using the correct VPN

## 7. Accounts

There are premade accounts on both services, they are listed below:
#### Logistics

| Name         | Password     | Role    | Info                      |
|--------------|--------------|---------|---------------------------|
| admin        | admin        | ADMIN   | admin                     |
| marchingfood | marchingfood | COMPANY | used in other app         |
| rider        | rider        | DRIVER  | normal rider              |
| stoppedFood  | stoppedFood  | COMPANY | does not have API key     |
| keylessRider | keylessRider | DRIVER  | does not have API key     |
| badRider     | badRider     | DRIVER  | has bad rating (bannable) |

#### Restaurant

| Name  | Password | admin |
|-------|----------|-------|
| admin | admin    | YES   |
| user1 | 12345    | NO    |
