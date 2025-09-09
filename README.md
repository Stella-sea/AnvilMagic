# 🔨 AnvilMagic - 铁砧拆分魔法

![AnvilMagic封面](https://image.pollinations.ai/prompt/minecraft%20anvil%20with%20magical%20glowing%20enchanted%20books%20floating%20around%20it%20pixelart%20style%20vibrant%20purple%20and%20golden%20effects?width=800&height=400&nologo=true)

[![模组版本](https://img.shields.io/badge/版本-v1.0.1-brightgreen)](https://github.com/AnvilMagic/AnvilMagic/releases)
[![MC版本](https://img.shields.io/badge/MC-1.21.1-blue)](https://minecraft.net/)
[![Fabric](https://img.shields.io/badge/Fabric-✓-yellow)](https://fabricmc.net/)
[![许可证](https://img.shields.io/badge/许可证-MIT-green)](./LICENSE)

> **客户端模组：拆分附魔书，净消耗1级经验，零UI侵入** 🎯

## ✨ 功能特性

- 🔍 **零UI侵入** - 无需额外界面，直接在原版铁砧操作
- 📚 **智能拆分** - 将多附魔书拆分为单个附魔书
- ⚡ **经验消耗** - 每次拆分净消耗1级经验（约10经验点）
- 🎮 **客户端模组** - 仅需客户端安装，服务器无需同步
- 🔧 **Mixin技术** - 深度集成原版机制，稳定可靠

## 🎯 使用方法

1. **准备材料**：
   - 左槽：多附魔的附魔书
   - 右槽：普通书籍（或留空）

2. **执行拆分**：
   - 铁砧会显示拆分后的单个附魔书
   - 消耗1级经验
   - 取出结果后，原附魔书变为剩余附魔

3. **结果说明**：
   - 获得：包含第一个附魔的新书
   - 剩余：原书变为去除第一个附魔后的书
   - 消耗：净消耗1级经验

## 🚀 安装指南

### 前置要求
- ✅ Minecraft 1.21.1
- ✅ Fabric Loader 0.16.14+
- ✅ Fabric API 0.116.6+1.21.1

### 安装步骤
1. 下载最新版本的 [AnvilMagic-1.0.1-1.21.1-client.jar](https://github.com/AnvilMagic/AnvilMagic/releases)
2. 将jar文件放入 `.minecraft/mods/` 文件夹
3. 启动游戏，开始使用！

## 🔧 开发环境

### 环境配置
- **IDE**: 任意Java IDE (推荐IntelliJ IDEA)
- **JDK**: Java 21+
- **构建工具**: Gradle 8.0+
- **映射**: Yarn 1.21.1+build.3

### 本地构建
```bash
git clone https://github.com/AnvilMagic/AnvilMagic.git
cd AnvilMagic
./gradlew build
```

### 开发测试
```bash
# 启动开发客户端
./gradlew runClient

# 启动Mixin审计模式
./gradlew runClient --args="--mixin audit"
```

## 🎨 技术实现

### 核心技术栈
- **Mixin框架** - 运行时字节码注入
- **AnvilScreenHandler** - 铁砧界面逻辑处理
- **ItemEnchantmentsComponent** - 新版附魔组件系统
- **Fabric Client API** - 客户端模组接口

### 关键组件
```
src/client/java/com/anvilmagic/
├── AnvilMagicClient.java          # 客户端入口点
└── mixin/
    ├── AnvilScreenHandlerMixin.java      # 结果计算逻辑
    └── AnvilScreenHandlerTakeMixin.java  # 物品取出处理
```

## 📊 版本历史

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| v1.0.1 | 2024-12 | 修正版本兼容性，优化经验逻辑 |
| v1.0.0 | 2024-12 | 初始版本，实现基础拆分功能 |

## 🐛 问题反馈

遇到问题？欢迎提交Issue！

- 🐞 [报告Bug](https://github.com/AnvilMagic/AnvilMagic/issues/new?template=bug_report.md)
- 💡 [功能建议](https://github.com/AnvilMagic/AnvilMagic/issues/new?template=feature_request.md)
- 💬 [讨论交流](https://github.com/AnvilMagic/AnvilMagic/discussions)

## 📄 许可证

本项目基于 [MIT许可证](./LICENSE) 开源发布。

---

<div align="center">

**如果这个模组对你有帮助，请给我们一个 ⭐ Star！**

![制作团队](https://image.pollinations.ai/prompt/minecraft%20modding%20team%20coding%20scene%20with%20enchanted%20books%20and%20magical%20coding%20effects%20pixelart?width=600&height=200&nologo=true)

*AnvilMagic - 让附魔管理更简单* ✨

</div>
