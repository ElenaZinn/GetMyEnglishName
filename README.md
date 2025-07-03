# GetMyEnglishName

一个帮助中文用户获取英文名的 Android 应用。支持多种命名方式，包括拼音转换、威妥玛拼音转换、字义翻译和在线翻译等功能。

## 功能特点

1. **多种命名方式**
   - 基于拼音的英文名转换
   - 威妥玛拼音（Wade-Giles）转换
   - 基于汉字字义的英文名翻译
   - 百度翻译 API 在线翻译支持

2. **智能命名系统**
   - 姓氏和名字分开处理
   - 丰富的英文名词库
   - 押韵和谐音组合
   - 避免重复和不协调的组合

3. **多样化结果展示**
   - 显示英文名
   - 显示威妥玛拼音
   - 显示原中文名
   - 支持多个备选名称

## 使用方法

1. **本地转换**
   - 输入中文姓名
   - 点击"获取名字"按钮
   - 如果名字不满意，可以再次点击"获取名字"按钮，刷新名字
   - 查看转换结果，包括：
     * 拼音
     * 威妥玛拼音
     * 推荐英文名

2. **在线翻译**
   - 在 `MainActivity.kt` 中配置百度翻译 API 密钥：
     ```kotlin
     private const val DEFAULT_APP_ID = "YOUR_APP_ID"
     private const val DEFAULT_APP_SECRET = "YOUR_APP_SECRET"
     ```
   - 输入中文姓名
   - 点击"在线翻译"按钮
   - 获取在线翻译结果

## 示例

输入中文名后，应用会提供多种形式的转换结果：

```
输入：张伟
输出：Jasper Great (Chang Wei) - 张伟

输入：李明
输出：Light Bright (Li Ming) - 李明

输入：王华
输出：Wonder Splendid (Wang Hua) - 王华
```

<img width="402" alt="image" src="https://github.com/user-attachments/assets/4c688aab-691d-45fe-8048-8e30c1ed43eb" />


## 技术特点

1. **拼音转换**
   - 使用 Pinyin4j 库进行准确的拼音转换
   - 支持多音字处理
   - 完整的声母韵母映射

2. **威妥玛拼音转换**
   - 完整的转换规则实现
   - 特殊情况处理
   - 声调位置优化

3. **智能选择算法**
   - 基于词长选择
   - 避免重复组合
   - 优先选择积极含义的词

## 依赖

- Pinyin4j: 中文转拼音
- Retrofit2: 网络请求
- OkHttp3: HTTP 客户端
- Gson: JSON 解析
- Kotlin Coroutines: 异步处理

## 注意事项

1. 使用在线翻译功能需要配置百度翻译 API 密钥
2. 建议使用 Android Studio 2022.3.1 或更高版本
3. 最低支持 Android API 级别 21 (Android 5.0)

## 未来计划

- [ ] 添加更多汉字的直接翻译
- [ ] 增加更多英文名选项
- [ ] 支持自定义转换规则
- [ ] 添加名字含义解释
- [ ] 支持更多在线翻译服务
