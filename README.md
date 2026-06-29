# eSIM Manager · eSIM 信息管理汇总

一款专为 Android 设计的 eSIM 卡片管理工具，支持桌面小组件实时查看服务商、到期日期、余额等关键信息。

![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)
![Language](https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white)
![Material](https://img.shields.io/badge/UI-Material%203-757575?logo=materialdesign&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

---

## ✨ 功能特性

- 📇 **eSIM 信息集中管理** — 一处录入、统一查看，告别多平台来回切换
- 🏠 **桌面小组件汇总** — 主屏幕实时显示每张 eSIM 的服务商、到期日期、余额、流量等信息
- 🌐 **内置 31 家主流服务商** — Airalo、Holafly、Nomad、Saily、Ubigi、Vodafone、AT&T、SoftBank 等，并支持自定义添加
- 🌍 **常用国家/地区列表** — 内置选择，无需手动输入
- 💰 **余额与多币种支持** — 填写金额并从常用币种中选择
- 📊 **流量管理** — 支持记录总流量与剩余流量
- ⏰ **到期提醒** — 可设置提前 1 / 3 / 7 / 15 天通过系统通知或闹钟提醒
- 🎨 **10 种主题配色** — 极光蓝、雅致白、深邃黑、新绿、活力橙、浪漫紫等，组件背景随心切换
- 📞 **附加信息** — 支持记录电话号码与自定义备注
- 🪶 **优雅的 Material 3 界面** — 卡片式分区布局，简洁美观

---

## 📱 应用截图

> 应用截图：

| 主界面 添加 eSIM 桌面小组件 | 
|:---:|
|<img width="420" height="938" alt="image" src="https://github.com/user-attachments/assets/14543798-ee88-418a-b8cf-307e0315a9dd" />
|<img width="422" height="926" alt="image" src="https://github.com/user-attachments/assets/72bfbc67-4cd8-4e8a-8580-423fdad25ff9" />
  | <img width="441" height="869" alt="image" src="https://github.com/user-attachments/assets/7ec1fa2f-765c-479f-be85-d32ef8b70126" />
 |

---

## 🛠️ 技术栈

- **语言**：Kotlin
- **UI 框架**：Android View + Material Components 3
- **数据持久化**：SharedPreferences (JSON 序列化)
- **桌面组件**：App Widget + RemoteViewsService
- **提醒机制**：AlarmManager + NotificationManager
- **最低支持**：Android 8.0 (API 26) 及以上

---

## 🚀 快速开始

### 环境要求

- Android Studio (推荐最新稳定版)
- JDK 17
- Android SDK API 26+

### 克隆并运行

```bash
# 克隆仓库
git clone https://github.com/你的用户名/eSIMManager.git

# 用 Android Studio 打开项目，等待 Gradle Sync 完成后即可运行
