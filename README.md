## <div align="center">LuckWhirl platform</div>

The project took more than half a year from demand analysis, architecture learning and design to coding implementation, and went through three versions of iterations.

<div align="center">[Version 1.0]</div>
<img src="docs\readme\v1.jpg" style="zoom: 33%;" />	
<div align="center">[Version 2.0]</div>
<img src="docs\readme\v2.jpg" style="zoom: 33%;" />
<div align="center">[Version 3.0]</div>
<img src="docs\readme\v3.jpg" style="zoom: 33%;" />



So is a robust and comprehensive marketing platform featuring functionalities such as **point accounts, daily sign-in rewards, virtual product exchanges, marketing lotteries, lottery unlocking, weighting systems, blacklists, and tiered lottery mechanisms.**

It is an end-to-end project covering product management, requirement analysis, frontend and backend development, and DevOps practices. From architecture design to coding implementation and deployment, the system handles real-world scenarios, providing a seamless experience across both consumer-facing and operational workflows.

The following is the key technologies and components of LuckWhirl platform:

1. **Frontend:**

   - React

   - TypeScript

2. **Backend:**

   - Spring & SpringBoot

   - MyBatis

   - RabbitMQ

   - Sharding-JDBC

   - Redis

   - MySQL

   - Mock

3. **DevOps:**

   - Git

   - Docker

4. **Design Patterns and Architecture:**

   - DDD (Domain-Driven Design)

   - Factory Pattern, Strategy Pattern, Template Pattern, Composite Pattern



The system is divided into three main modules:

1. **Activity and Strategy Initialization Assembly**: This module manages the setup and configuration of marketing activities and strategies.
2. **Lottery Module**: Handles the entire process of lottery participation, from entry to prize distribution.
3. **Check-in Rebates and Points Redemption Module**: Manages user interactions for daily sign-in, rebate rewards, and virtual product redemptions.

Finally is a diagram of the **Inventory Control Mechanism**, ensures smooth management and tracking of product stock throughout the marketing activities.

<img src="docs\readme\luckWhirl workflow.png" style="zoom: 33%;" />

