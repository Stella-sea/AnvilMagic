# AnvilMagic 开发指南

![开发环境](https://image.pollinations.ai/prompt/minecraft%20modding%20development%20environment%20with%20code%20editor%20and%20anvil%20magical%20effects?width=600&height=300&nologo=true)

## 🛠️ 开发环境设置

### 必需软件
- **JDK 21+** (推荐 OpenJDK 或 Oracle JDK)
- **IntelliJ IDEA** (推荐) 或其他Java IDE
- **Git** 用于版本控制

### 克隆项目
```bash
git clone https://github.com/AnvilMagic/AnvilMagic.git
cd AnvilMagic
```

### 导入IDE
1. 用IntelliJ IDEA打开项目根目录
2. 等待Gradle同步完成
3. 确保项目SDK设置为Java 21

## 🧪 开发与测试

### 构建项目
```bash
# Windows
.\gradlew.bat build

# Linux/Mac
./gradlew build
```

### 运行开发客户端
```bash
# 普通运行
.\gradlew.bat runClient

# 带Mixin调试信息
.\gradlew.bat runClient --args="--mixin audit"
```

### 代码热重载
开发过程中，可以使用以下命令进行增量构建：
```bash
.\gradlew.bat build --continuous
```

## 📁 项目结构

```
AnvilMagic/
├── src/
│   ├── main/
│   │   └── resources/
│   │       ├── fabric.mod.json          # 模组元数据
│   │       └── assets/anvilmagic/        # 资源文件
│   └── client/
│       ├── java/com/anvilmagic/
│       │   ├── AnvilMagicClient.java     # 客户端入口
│       │   └── mixin/                    # Mixin注入类
│       │       ├── AnvilScreenHandlerMixin.java
│       │       └── AnvilScreenHandlerTakeMixin.java
│       └── resources/
│           └── anvilmagic.client.mixins.json  # Mixin配置
├── build.gradle                         # 构建配置
├── gradle.properties                    # 项目属性
└── settings.gradle                      # Gradle设置
```

## 🔧 关键技术组件

### Mixin系统
AnvilMagic使用Mixin框架在运行时修改Minecraft代码：

- **`AnvilScreenHandlerMixin`**: 拦截铁砧结果计算逻辑
- **`AnvilScreenHandlerTakeMixin`**: 处理物品取出和经验扣除

### 注入点说明
```java
@Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
```
- `method`: 目标方法名（混淆后）
- `at = @At("HEAD")`: 在方法开始时注入
- `cancellable = true`: 允许取消原版逻辑

## 🐛 调试技巧

### 启用详细日志
在`log4j2.xml`中添加：
```xml
<Logger level="DEBUG" name="com.anvilmagic"/>
```

### Mixin调试
使用Mixin audit模式检查注入状态：
```bash
.\gradlew.bat runClient --args="--mixin audit"
```

### 常见问题
1. **ClassCastException**: 检查Mixin目标类型
2. **MixinApplyError**: 验证方法签名匹配
3. **NoSuchMethodError**: 确认混淆映射正确

## 📝 代码规范

### 命名约定
- 类名：PascalCase (`AnvilScreenHandlerMixin`)
- 方法名：camelCase (`onTakeOutput`)
- 常量：UPPER_SNAKE_CASE (`MAX_ENCHANTMENTS`)

### 注释要求
- 所有public方法必须有JavaDoc
- 复杂逻辑需要行内注释
- Mixin注入点需要说明目的

### 提交规范
遵循[Conventional Commits](https://conventionalcommits.org/)：
```
feat: 添加附魔书拆分功能
fix: 修复经验计算错误
docs: 更新README文档
```

## 🧪 测试流程

### 功能测试
1. 创建测试世界
2. 获取多附魔书籍
3. 验证拆分逻辑
4. 检查经验消耗
5. 确认UI无侵入

### 兼容性测试
- 测试与主流模组的兼容性
- 验证多版本MC支持
- 检查服务端兼容性

## 🚀 发布流程

### 版本号规范
采用语义化版本控制：`MAJOR.MINOR.PATCH`
- MAJOR: 不兼容的API更改
- MINOR: 向后兼容的功能新增
- PATCH: 向后兼容的错误修复

### 发布步骤
1. 更新版本号 (`gradle.properties`)
2. 更新变更日志 (`CHANGELOG.md`)
3. 构建最终jar包
4. 创建Git标签
5. 发布到GitHub Releases

```bash
# 构建发布版本
.\gradlew.bat build

# 创建标签
git tag -a v1.0.1 -m "Release v1.0.1"
git push origin v1.0.1
```

## 📚 扩展资源

- [Fabric官方文档](https://docs.fabricmc.net/)
- [Mixin官方Wiki](https://github.com/SpongePowered/Mixin/wiki)
- [Minecraft开发工具](https://linkie.shedaniel.me/)
- [Yarn映射查询](https://maven.fabricmc.net/net/fabricmc/yarn/)

---

*持续改进，欢迎贡献！* 🎯