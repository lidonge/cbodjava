# Cobol -> Java 语义审查报告

## 本轮范围

- 主 Cobol：`/home/lidong/cb2j/xbod/cbodapp/onbb.cbl/GTD01641.cbl`
- 对应 Java：`/home/lidong/cb2j/cbodjava/generated-sources/cbod/src/main/java/cbod/java/onbb/cbl/Gtd01641.java`
- 本轮仅审查 `GTD01641`，未继续分析其他 cbl。
- 本报告已按当前工作区内容重新复审一次。

## 结论

重新 review 当前工作区后，结论需要更新：`Gtd01641.java` 的主流程、初始化和 `9802-GNP-TDAC1TXN-RTN` 的两段 DBI 检索条件，现已与 `GTD01641.cbl` 对齐。本轮关注的 `WK-TXN-KEY -> DBI-KEY-VALUE1(2)` 复合键语义问题已经修正。

## 发现

### 1. `9802-GNP-TDAC1TXN-RTN` 的第二段检索键问题已修正

- Cobol 在 `9802-GNP-TDAC1TXN-RTN` 中明确设置了两段条件：
  - `DBI-OP1(2)` 取 `'>='` 或 `'> '`
  - `DBI-SEGMENT-NAME(1) = 'TDAC1AC1'`
  - `DBI-SEGMENT-NAME(2) = 'TDAC1TXN'`
  - `DBI-KEY-VALUE1(1) = WK-ACCT-KEY`
  - `DBI-KEY-VALUE1(2) = WK-TXN-KEY`
- 证据：
  - Cobol：`GTD01641.cbl:646-655`
  - Java：`Gtd01641.java:748-763`
- 这次复核后可确认：
  - Java 已正确使用两段条件槽位：`dbiConditionArea[1]` 放 `TDAC1AC1`，`dbiConditionArea[2]` 放 `TDAC1TXN`
  - `DBI-OP1` 也已落在第 2 段，与 Cobol 一致
- 当前 Java 已改为：
  - `dbiConditionArea[2].dbiSaa.dbiKeyValue1=Util.copyCastToString(wkArea.wkKey.wkTxnKey);`
- 这与 Cobol 的 `MOVE WK-TXN-KEY TO DBI-KEY-VALUE1(2)` 语义一致，`WK-PSBK-KEY` 与 `WK-SQ-KEY` 均由复合键整体承载。
- 判定：`已修正`

## 其余观察

- 主流程顺序、输入检查、`CTDCHK0` 调用、`WK-IRQ-TYP` 分类、`5300/5400/7000/9000` 的主干控制流整体上与 Cobol 基本对应。
- 这次复核确认，旧报告中“`INITIALIZE` 只剩注释”的结论已经不再成立；当前工作区里的 `PDBIMAIN`、`PCCMPAR2`、`STD16410` 等位置都已补成实际初始化代码。
- 本轮关注的 `9802` 起始检索键问题已修正；基于当前审查范围，未再看到这一段的直接语义偏差。

## 复审说明

- 本次是基于当前工作区重新逐段复核后的结论。
- 重新核对当前 `Gtd01641.java` 后，确认 `9802` 的“条件槽位覆盖”问题已经修正，`INITIALIZE` 缺失问题也已修正，`DBI-KEY-VALUE1(2)` 也已改为完整承载 `WK-TXN-KEY`。

## 本轮结论

本轮只完成 `GTD01641.cbl` 与 `Gtd01641.java` 的语义对比，并已更新本报告。当前已确认本轮关注的 `GTD01641` 关键语义问题修正完成，可进入下一条语义审查或提交当前结果。

---

## 第二轮范围

- 主 Cobol：`/home/lidong/cb2j/xbod/cbodapp/ccbmain.cbl/GSYSTIME.cbl`
- 对应 Java：`/home/lidong/cb2j/cbodjava/generated-sources/cbod/src/main/java/cbod/java/ccbmain/cbl/Gsystime.java`
- 本轮按你的要求从 `ccbmain.cbl` 开始，只审查 `GSYSTIME`，未继续分析其他 `ccbmain` 主程序。

## 第二轮结论

`Gsystime.java` 当前未正确表达 `GSYSTIME.cbl` 的原始语义，至少存在 2 处明确问题，其中 1 处会直接让 `GO TO 0000-EXIT` 失效，另 1 处会让外部时间转换调用完全不起作用。

## 第二轮发现

### 1. `GO TO 0000-EXIT` 被翻译成普通方法调用，后续逻辑没有终止

- Cobol 在两个分支中都明确 `GO TO 0000-EXIT`：
  - `I-INQ-TYPE = SPACES` 时直接返回 `WK-ABS-TIME`
  - `I-INQ-TYPE = 'CYYDDDHHMISS999'` 时格式化后直接退出
- 证据：
  - Cobol：`GSYSTIME.cbl:74-92`
  - Java：`Gsystime.java:115-145`
- Java 当前只是调用了 `m_0000Exit()`：
  - `Gsystime.java:118-119`
  - `Gsystime.java:134-135`
- 但 `m_0000Exit()` 本身是空方法，没有 `return`，因此流程会继续往下执行：
  - `Gsystime.java:150-152`
  - `Gsystime.java:137-148`
- 语义影响：
  - 当 `I-INQ-TYPE = SPACES` 时，Cobol 应立即返回 `WK-ABS-TIME`；Java 却还会继续执行 `1000-COUNT-LEN-RTN` 和后面的通用 `CEEDATM` 分支，结果可能被再次覆盖。
  - 当 `I-INQ-TYPE = 'CYYDDDHHMISS999'` 时，也会在设置完 `O-TIME-RESULT` 后继续落入后续通用格式化逻辑。
  - 这会直接改变输出值，不再满足 Cobol 的“命中特定分支后立即退出”语义。
- 判定：`严重语义偏差`

### 2. `CEEGMT/CEEGMTO/CEEDATM` 外部调用当前是空实现，核心时间值不会被生成

- Cobol 依赖三个外部调用产生关键结果：
  - `CEEGMT` 生成 GMT 日期/时间：`GSYSTIME.cbl:61-63`
  - `CEEGMTO` 生成时区偏移：`GSYSTIME.cbl:66-69`
  - `CEEDATM` 按图片串生成输出时间串：`GSYSTIME.cbl:85-88`、`GSYSTIME.cbl:99-102`
- Java 也调用了对应服务：
  - `Gsystime.java:105`
  - `Gsystime.java:109`
  - `Gsystime.java:128`
  - `Gsystime.java:145`
- 但本地外部服务实现目前全是空方法：
  - `../sppframe/cbod/src/main/java/free/cobol2java/java/external/Ceegmt.java:5-7`
  - `../sppframe/cbod/src/main/java/free/cobol2java/java/external/Ceegmto.java:5-7`
  - `../sppframe/cbod/src/main/java/free/cobol2java/java/external/Ceedatm.java:5-7`
- 语义影响：
  - `WK-ABS-TIME`、`WK-TIME-ZONE`、`WK-OUTPUT-TIMESTAMP` 都不会按 Cobol 语义被填充。
  - 后续 `ADD WK-TIME-ZONE-B TO WK-ABS-TIME-B`、`MOVE WK-ABS-TIME TO O-TIME-RESULT`、`MOVE WK-OUTPUT-TIMESTAMP TO O-TIME-RESULT` 都建立在空值或初始值之上。
  - 也就是说，这个程序的核心功能“返回特殊格式的系统时间”在当前 Java 运行时并未真正实现。
- 判定：`严重语义偏差`

## 第二轮其余观察

- `WK-ABS-TIME/WK-ABS-TIME-B`、`WK-TIME-ZONE/WK-TIME-ZONE-B`、`WK-OUTPUT-TIMESTAMP/WK-CENTURY-R` 的 `REDEFINES` 关系在 Java 中已经建出来了，结构映射本身没有明显缺口。
- `1000-COUNT-LEN-RTN` 的尾部去空格循环与 Cobol 基本对应，当前没有看到比上面两项更严重的控制流问题。
- 但由于“分支提前退出失效”和“外部时间服务空实现”这两处已经足以改变主功能结果，因此本文件不能判定为语义转换正确。

## 第二轮结论

本轮只完成 `GSYSTIME.cbl` 与 `Gsystime.java` 的语义对比，并已更新本报告。这里停止，等待你确认是否继续下一轮。
