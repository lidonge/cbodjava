任务：做 Cobol -> Java 语义对比审查。

目标：
检查生成的 Java 是否正确表达原始 Cobol 语义。

上下文：
- 主 Cobol 文件：/home/lidong/cb2j/xbod/cbodapp/onbb.cbl/GTD01641.cbls
- Cobol 根目录：/home/lidong/cb2j/xbod/cbodapp
- Java 生成目录：generated-sources/cbod/src/main/java
- Java 生成COPYBOOK目录：generated-sources/cbod/src/main/java/cbod/java/models
- 报告文件：cobol_java_semantic_review.md

要求：
1. 先只分析主 cbl。
2. 找到它对应的 Java 文件。
3. 仅关注语义转换是否正确，不关注代码风格。
4. 分析完成后，更新报告文件。
5. 更新完报告后立即停止，等我看。
6. 不要继续分析第二个 cbl。
7. 不要只说计划，必须真的更新报告后再停。
8. 我说继续后，再从Java 生成目录下随机挑选一个非COPYBOOK，从2-8开始闭环

done when:
- 已完成主 cbl 的语义对比
- 已更新 cobol_java_semantic_review.md
- 已停止等待我确认