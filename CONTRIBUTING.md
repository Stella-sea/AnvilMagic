# 🤝 贡献指南

感谢您对AnvilMagic项目的关注！我们欢迎各种形式的贡献。

![贡献者](https://image.pollinations.ai/prompt/diverse%20team%20of%20minecraft%20modders%20collaborating%20on%20code%20with%20magical%20enchanting%20effects?width=600&height=300&nologo=true)

## 🌟 如何参与贡献

### 💡 提出想法
- [💬 讨论区](https://github.com/AnvilMagic/AnvilMagic/discussions) - 分享想法和建议
- [🐛 Bug报告](https://github.com/AnvilMagic/AnvilMagic/issues/new?template=bug_report.md)
- [✨ 功能请求](https://github.com/AnvilMagic/AnvilMagic/issues/new?template=feature_request.md)

### 🔧 代码贡献
1. **Fork项目** 到你的GitHub账户
2. **创建功能分支** (`git checkout -b feature/amazing-feature`)
3. **提交更改** (`git commit -m 'feat: 添加惊人功能'`)
4. **推送分支** (`git push origin feature/amazing-feature`)
5. **创建Pull Request**

## 📝 开发规范

### 🎯 代码风格
```java
// ✅ 好的例子
@Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
private void onUpdateResult(CallbackInfo ci) {
    // 清晰的注释说明
    if (shouldSplitEnchantedBook(leftStack, rightStack)) {
        handleEnchantmentSplitting(handler, leftStack);
        ci.cancel();
    }
}

// ❌ 避免的例子
@Inject(method="updateResult",at=@At("HEAD"),cancellable=true)
private void a(CallbackInfo ci){if(b(c,d)){e(f,c);ci.cancel();}}
```

### 📋 提交信息规范
使用[Conventional Commits](https://conventionalcommits.org/)格式：

```
<类型>[可选范围]: <描述>

[可选正文]

[可选脚注]
```

**类型说明**：
- `feat`: 新功能
- `fix`: Bug修复  
- `docs`: 文档更新
- `style`: 代码格式（不影响逻辑）
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建过程或辅助工具的变动

**示例**：
```
feat(mixin): 添加附魔书拆分功能

- 实现AnvilScreenHandlerMixin注入逻辑
- 支持多附魔书拆分为单个附魔
- 添加1级经验消耗机制

Closes #123
```

### 🧪 测试要求
- **功能测试**: 确保新功能正常工作
- **回归测试**: 验证现有功能未被破坏
- **兼容性测试**: 测试与其他模组的兼容性
- **性能测试**: 确保无明显性能影响

### 📚 文档要求
- **JavaDoc**: 所有public方法必须有文档
- **README更新**: 重要功能需要更新使用说明
- **变更日志**: 在CHANGELOG.md中记录变更

## 🐛 Bug报告指南

### 🔍 报告前检查
1. 搜索[现有Issues](https://github.com/AnvilMagic/AnvilMagic/issues)
2. 使用最新版本测试
3. 检查[常见问题](https://github.com/AnvilMagic/AnvilMagic/wiki/FAQ)

### 📋 Bug报告模板
请按照[Bug报告模板](https://github.com/AnvilMagic/AnvilMagic/issues/new?template=bug_report.md)提供：

- **环境信息**: MC版本、模组版本、其他相关模组
- **复现步骤**: 详细的操作步骤
- **预期行为**: 期望发生什么
- **实际行为**: 实际发生了什么
- **日志文件**: 相关的错误日志

## ✨ 功能建议指南

### 💭 建议格式
使用[功能请求模板](https://github.com/AnvilMagic/AnvilMagic/issues/new?template=feature_request.md)：

- **功能描述**: 清晰描述建议的功能
- **使用场景**: 什么情况下会用到这个功能
- **实现思路**: 如果有技术想法请分享
- **替代方案**: 考虑过的其他解决方案

### 🎯 优先级考虑
我们会根据以下因素确定优先级：
- **用户需求**: 社区投票和反馈
- **技术可行性**: 实现难度和风险
- **项目目标**: 与项目愿景的契合度
- **维护成本**: 长期维护的复杂度

## 🛠️ 开发环境设置

### 📋 前置要求
- Java 21+
- Git
- IntelliJ IDEA (推荐)

### 🚀 快速开始
```bash
# 克隆仓库
git clone https://github.com/YOUR_USERNAME/AnvilMagic.git
cd AnvilMagic

# 构建项目
./gradlew build

# 运行开发客户端
./gradlew runClient
```

详细说明请参考[开发指南](DEVELOPMENT.md)。

## 🔄 Pull Request指南

### ✅ PR检查清单
在提交PR前，请确认：

- [ ] 代码遵循项目风格规范
- [ ] 通过所有现有测试
- [ ] 添加了必要的新测试
- [ ] 更新了相关文档
- [ ] 提交信息符合规范
- [ ] PR描述清晰明确

### 📝 PR模板
请使用[PR模板](https://github.com/AnvilMagic/AnvilMagic/blob/main/.github/pull_request_template.md)：

- **变更描述**: 详细说明做了什么改动
- **相关Issue**: 链接相关的Issue编号
- **测试情况**: 描述如何测试了这些改动
- **截图/GIF**: 如果涉及UI变化，提供视觉证据

### 🔍 代码审查流程
1. **自动检查**: CI/CD流水线验证
2. **初步审查**: 维护者进行代码审查
3. **讨论反馈**: 根据反馈进行调整
4. **最终批准**: 合并到主分支

## 🏆 贡献者认可

### 🎖️ 贡献类型
我们认可各种形式的贡献：
- 🔧 **代码贡献**: 功能开发、Bug修复
- 📚 **文档贡献**: 文档编写、翻译工作
- 🐛 **测试贡献**: Bug报告、测试案例
- 💡 **创意贡献**: 设计建议、功能想法
- 🌍 **社区贡献**: 社区支持、答疑解惑

### 📊 贡献统计
优秀贡献者将在以下地方得到认可：
- README贡献者列表
- 发布说明中的特别感谢
- 项目Wiki的贡献者页面

## 📞 联系我们

遇到问题或需要帮助？

- 💬 [讨论区](https://github.com/AnvilMagic/AnvilMagic/discussions)
- 📧 [Issues](https://github.com/AnvilMagic/AnvilMagic/issues)
- 📱 社区QQ群：[待创建]

## 📄 行为准则

参与项目时，请遵守我们的[行为准则](CODE_OF_CONDUCT.md)：

- 🤝 **尊重包容**: 欢迎不同背景的贡献者
- 💬 **友善交流**: 保持建设性的讨论氛围
- 🎯 **专注目标**: 围绕项目目标进行协作
- 📚 **乐于学习**: 开放心态，互相学习成长

---

**感谢您的贡献，让我们一起打造更好的AnvilMagic！** 🎉

![感谢](https://image.pollinations.ai/prompt/thank%20you%20minecraft%20community%20magical%20sparkles%20and%20hearts?width=400&height=200&nologo=true)