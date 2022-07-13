package com.WriteBugsProfessionally.Java.Study;

/**
 Java 简介
 Java 是由 Sun Microsystems 公司于 1995 年 5 月推出的 Java 面向对象程序设计语言和 Java 平台的总称。由 James Gosling和同事们共同研发，并在 1995 年正式推出。

 后来 Sun 公司被 Oracle （甲骨文）公司收购，Java 也随之成为 Oracle 公司的产品。

 Java分为三个体系：

 JavaSE（J2SE）（Java2 Platform Standard Edition，java平台标准版）
 JavaEE(J2EE)(Java 2 Platform,Enterprise Edition，java平台企业版)
 JavaME(J2ME)(Java 2 Platform Micro Edition，java平台微型版)。
 2005 年 6 月，JavaOne 大会召开，SUN 公司公开 Java SE 6。此时，Java 的各种版本已经更名，以取消其中的数字 "2"：J2EE 更名为 Java EE，J2SE 更名为Java SE，J2ME 更名为 Java ME。

 主要特性
 Java 语言是简单的：

 Java 语言的语法与 C 语言和 C++ 语言很接近，使得大多数程序员很容易学习和使用。另一方面，Java 丢弃了 C++ 中很少使用的、很难理解的、令人迷惑的那些特性，如操作符重载、多继承、自动的强制类型转换。特别地，Java 语言不使用指针，而是引用。并提供了自动分配和回收内存空间，使得程序员不必为内存管理而担忧。

 Java 语言是面向对象的：

 Java 语言提供类、接口和继承等面向对象的特性，为了简单起见，只支持类之间的单继承，但支持接口之间的多继承，并支持类与接口之间的实现机制（关键字为 implements）。Java 语言全面支持动态绑定，而 C++语言只对虚函数使用动态绑定。总之，Java语言是一个纯的面向对象程序设计语言。

 Java语言是分布式的：

 Java 语言支持 Internet 应用的开发，在基本的 Java 应用编程接口中有一个网络应用编程接口（java net），它提供了用于网络应用编程的类库，包括 URL、URLConnection、Socket、ServerSocket 等。Java 的 RMI（远程方法激活）机制也是开发分布式应用的重要手段。

 Java 语言是健壮的：
 Java 的强类型机制、异常处理、垃圾的自动收集等是 Java 程序健壮性的重要保证。对指针的丢弃是 Java 的明智选择。Java 的安全检查机制使得 Java 更具健壮性。

 Java语言是安全的：

 Java通常被用在网络环境中，为此，Java 提供了一个安全机制以防恶意代码的攻击。除了Java 语言具有的许多安全特性以外，Java 对通过网络下载的类具有一个安全防范机制（类 ClassLoader），如分配不同的名字空间以防替代本地的同名类、字节代码检查，并提供安全管理机制（类 SecurityManager）让 Java 应用设置安全哨兵。

 Java 语言是体系结构中立的：

 Java 程序（后缀为 java 的文件）在 Java 平台上被编译为体系结构中立的字节码格式（后缀为 class 的文件），然后可以在实现这个 Java 平台的任何系统中运行。这种途径适合于异构的网络环境和软件的分发。

 Java 语言是可移植的：

 这种可移植性来源于体系结构中立性，另外，Java 还严格规定了各个基本数据类型的长度。Java 系统本身也具有很强的可移植性，Java 编译器是用 Java 实现的，Java 的运行环境是用 ANSI C 实现的。

 Java 语言是解释型的：

 如前所述，Java 程序在 Java 平台上被编译为字节码格式，然后可以在实现这个 Java 平台的任何系统中运行。在运行时，Java 平台中的 Java 解释器对这些字节码进行解释执行，执行过程中需要的类在联接阶段被载入到运行环境中。

 Java 是高性能的：

 与那些解释型的高级脚本语言相比，Java 的确是高性能的。事实上，Java 的运行速度随着 JIT(Just-In-Time）编译器技术的发展越来越接近于 C++。

 Java 语言是多线程的：

 在 Java 语言中，线程是一种特殊的对象，它必须由 Thread 类或其子（孙）类来创建。通常有两种方法来创建线程：其一，使用型构为 Thread(Runnable) 的构造子类将一个实现了 Runnable 接口的对象包装成一个线程，其二，从 Thread 类派生出子类并重写 run 方法，使用该子类创建的对象即为线程。值得注意的是 Thread 类已经实现了 Runnable 接口，因此，任何一个线程均有它的 run 方法，而 run 方法中包含了线程所要运行的代码。线程的活动由一组方法来控制。Java 语言支持多个线程的同时执行，并提供多线程之间的同步机制（关键字为 synchronized）。

 Java 语言是动态的：

 Java 语言的设计目标之一是适应于动态变化的环境。Java 程序需要的类能够动态地被载入到运行环境，也可以通过网络来载入所需要的类。这也有利于软件的升级。另外，Java 中的类有一个运行时刻的表示，能进行运行时刻的类型检查。

 发展历史
 1995 年 5 月 23 日，Java 语言诞生
 1996 年 1 月，第一个 JDK-JDK1.0 诞生
 1996 年 4 月，10 个最主要的操作系统供应商申明将在其产品中嵌入 JAVA 技术
 1996 年 9 月，约 8.3 万个网页应用了 JAVA 技术来制作
 1997 年 2 月 18 日，JDK1.1 发布
 1997 年 4 月 2 日，JavaOne 会议召开，参与者逾一万人，创当时全球同类会议规模之纪录
 1997 年 9 月，JavaDeveloperConnection 社区成员超过十万
 1998 年 2 月，JDK1.1 被下载超过 2,000,000次
 1998 年 12 月 8 日，JAVA2 企业平台 J2EE 发布
 1999 年 6月，SUN 公司发布 Java 的三个版本：标准版（JavaSE, 以前是 J2SE）、企业版（JavaEE 以前是 J2EE）和微型版（JavaME，以前是 J2ME）
 2000 年 5 月 8 日，JDK1.3 发布
 2000 年 5 月 29 日，JDK1.4 发布
 2001 年 6 月 5 日，NOKIA 宣布，到 2003 年将出售 1 亿部支持 Java 的手机
 2001 年 9 月 24 日，J2EE1.3 发布
 2002 年 2 月 26 日，J2SE1.4 发布，自此 Java 的计算能力有了大幅提升
 2004 年 9 月 30 日 18:00PM，J2SE1.5 发布，成为 Java 语言发展史上的又一里程碑。为了表示该版本的重要性，J2SE1.5 更名为 Java SE 5.0
 2005 年 6 月，JavaOne 大会召开，SUN 公司公开 Java SE 6。此时，Java 的各种版本已经更名，以取消其中的数字 "2"：J2EE 更名为 Java EE，J2SE 更名为 Java SE，J2ME 更名为 Java ME
 2006 年 12 月，SUN 公司发布 JRE6.0
 2009 年 04 月 20 日，甲骨文 74 亿美元收购 Sun，取得 Java 的版权。
 2010 年 11 月，由于甲骨文对于 Java 社区的不友善，因此 Apache 扬言将退出 JCP。
 2011 年 7 月 28 日，甲骨文发布 Java7.0 的正式版。
 2014 年 3 月 18 日，Oracle 公司发表 Java SE 8。
 2017 年 9 月 21 日，Oracle 公司发表 Java SE 9
 2018 年 3 月 21 日，Oracle 公司发表 Java SE 10
 2018 年 9 月 25 日，Java SE 11 发布
 2019 年 3 月 20 日，Java SE 12 发布
 Java 开发工具
 Java 语言尽量保证系统内存在 1G 以上，其他工具如下所示：

 Linux 系统、Mac OS 系统、Windows 95/98/2000/XP，WIN 7/8系统。
 Java JDK 7、8……
 vscode 编辑器或者其他编辑器。
 IDE：Eclipse、 IntelliJ IDEA、NetBeans 等。*/
//import org.omg.CORBA.Object;

import javax.swing.plaf.BorderUIResource;
import javax.swing.text.rtf.RTFEditorKit;
import java.util.*;
import java.lang.*;
import java.text.*;
import java.lang.String;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.*;


//包机制 package定义包 import 导入包，必须放在最上面，不要重名，有多个文件时可以用通配符*
/*
 包机制
 为了更好地组织类，Java提供了包机制，用于区别类名的命名空间。
 包语句的语法格式为：
 package pkg1[. pkg2[. pkg3...]];
 一般利用公司域名倒置作为包名；com.qing.index
 为了能够使用某一个包的成员，我们需要在Java程序中明确导入该包。使用“import"语句可完成此功能
 import package1[.package...].(classname|*);
*/

/*
 参数信息
@author 作者名
@version 版本号
@since指明需要最早使用的jdk版本@param 参数名
@return 返回值情况
@throws 异常抛出情况
*/



public class MyFirstJava {
	private static int age;//静态变量
	private String name;//非静态变量(详情跳转到1104行)
	public static void main(String[] args) {
		System.out.println("Hello,world!");
		String me = "me";
		world world = new world();
		world.execute(me);


		// 单行注释

		/*
		 * 多
		 * 行
		 * 注
		 * 释
		 */

		// Javadoc，文档注释
		/**
		 * @Description HelloWorld!
		 */

		// —————————————————————————————————————————————————————————————————————————————————————————————————————

		/*
		 * 标识符和关键字
		 *
		 * int a=123;a为标识符
		 *
		 * 标识符定义规范
		 * 1.有一定意义
		 * 2.不建议使用中文
		 * 3.第一个首字母小写，之后的每一个单词首字母大写;例如:peopleNnemberOne
		 * 4.不能使用java关键字
		 *
		 * Java关键字
		 * 48个关键字：abstract、assert、
		 * boolean、break、byte、
		 * case、catch、char、class、continue、
		 * default、do、double、
		 * else、enum、extends、
		 * final、finally、float、for、if、
		 * implements、import、int、interface、instanceof、
		 * long、
		 * native、new、
		 * package、private、protected、public、
		 * return、
		 * short、static、strictfp、super、switch、synchronized、
		 * this、throw、throws、transient、try、
		 * void、volatile、
		 * while。
		 * 2个保留字（现在没用以后可能用到作为关键字）：goto、const。
		 * 3个特殊直接量：true、false、null
		 *
		 * 标识符注意点
		 * 所有的标识符都应该以字母(A-Z或者a-z)，美元符($)、或者下划线(_)开始
		 * 首字符之后可以是字母（A-Z或者a-z)，美元符($)、下划线(_)或数字的任何字符组合
		 * 不能使用关键字作为变量名或方法名。
		 * 标识符是大小写敏感的
		 * 合法标识符举例：age、$salary、_value、__1_value
		 * 非法标识符举例：123abc、-salary、#abc
		 * 例：
		 * public static void main(String[] args)
		 * String 王者荣耀=“最强王者”；
		 * System.out.println(王者荣耀)；
		 * 可以使用中文命名，但是一般不建议这样去使用，也不建议使用拼音，很Low
		 *
		 */

		// —————————————————————————————————————————————————————————————————————————————————————————————————————

		/*
		 * 字节
		 * 位(bit)：是计算机 内部数据 储存的最小单位，11001100是一个八位二进制数。字节(byte)：是计算机中 数据处理
		 * 的基本单位，习惯上用大写B来表示，
		 * 1B(byte字节)=8bit(位)
		 * 字符：是指计算机中使用的字母、数字、字和符号
		 * 1bit表示1位，
		 * 1Byte表示一个字节1B=8b。
		 * 1024B=1KB
		 * 1024KB=1M
		 * 1024M=1G.
		 */

		// —————————————————————————————————————————————————————————————————————————————————————————————————————


		//基本数据类型

		// byte
		System.out.println("基本类型：byte 二进制位数：" + Byte.SIZE);
		System.out.println("包装类：java.lang.Byte");
		System.out.println("最小值：Byte.MIN_VALUE=" + Byte.MIN_VALUE);
		System.out.println("最大值：Byte.MAX_VALUE=" + Byte.MAX_VALUE);
		System.out.println();

		// short
		System.out.println("基本类型：short 二进制位数：" + Short.SIZE);
		System.out.println("包装类：java.lang.Short");
		System.out.println("最小值：Short.MIN_VALUE=" + Short.MIN_VALUE);
		System.out.println("最大值：Short.MAX_VALUE=" + Short.MAX_VALUE);
		System.out.println();

		// int
		System.out.println("基本类型：int 二进制位数：" + Integer.SIZE);
		System.out.println("包装类：java.lang.Integer");
		System.out.println("最小值：Integer.MIN_VALUE=" + Integer.MIN_VALUE);
		System.out.println("最大值：Integer.MAX_VALUE=" + Integer.MAX_VALUE);
		System.out.println();

		// long
		System.out.println("基本类型：long 二进制位数：" + Long.SIZE);
		System.out.println("包装类：java.lang.Long");
		System.out.println("最小值：Long.MIN_VALUE=" + Long.MIN_VALUE);
		System.out.println("最大值：Long.MAX_VALUE=" + Long.MAX_VALUE);
		System.out.println();

		// float
		System.out.println("基本类型：float 二进制位数：" + Float.SIZE);
		System.out.println("包装类：java.lang.Float");
		System.out.println("最小值：Float.MIN_VALUE=" + Float.MIN_VALUE);
		System.out.println("最大值：Float.MAX_VALUE=" + Float.MAX_VALUE);
		System.out.println();

		// double
		System.out.println("基本类型：double 二进制位数：" + Double.SIZE);
		System.out.println("包装类：java.lang.Double");
		System.out.println("最小值：Double.MIN_VALUE=" + Double.MIN_VALUE);
		System.out.println("最大值：Double.MAX_VALUE=" + Double.MAX_VALUE);
		System.out.println();

		// char
		System.out.println("基本类型：char 二进制位数：" + Character.SIZE);
		System.out.println("包装类：java.lang.Character");
		// 以数值形式而不是字符形式将Character.MIN_VALUE输出到控制台
		System.out.println("最小值：Character.MIN_VALUE=" + (int) Character.MIN_VALUE);
		// 以数值形式而不是字符形式将Character.MAX_VALUE输出到控制台
		System.out.println("最大值：Character.MAX_VALUE=" + (int) Character.MAX_VALUE);

		System.out.println("—————————————————————————————————————————————————————————————————————————————");

		// 整数拓展 进制 二进制0b 八进制0 十进制 十六进制0x
		System.out.println("整数类型拓展（进制 二进制 八进制 十进制 十六进制）：");
		int i = 0b10101010010010;
		int i1 = 012345671231;
		int i2 = 12_5484_6375;
		int i3 = 0x137584;
		System.out.println(i);
		System.out.println(i1);
		System.out.println(i2);
		System.out.println(i3);

		System.out.println("—————————————————————————————————————————————————————————————————————————————");

		// 浮点数，离散，有误差，四舍五入，大约，接近但不等于(最好完全避免使用浮点数进行比较)
		System.out.println("浮点数：");
		float f = 0.1f;
		double d = 1.0 / 10;
		System.out.println(f);
		System.out.println(d);
		System.out.println(f == d);// false

		float f1 = 58085382568828585857f;
		float f2 = f1 + 1;
		System.out.println(f1 == f2);// true

		System.out.println("—————————————————————————————————————————————————————————————————————————————");

		/***
		 *            .,,       .,:;;iiiiiiiii;;:,,.     .,,
		 *          rGB##HS,.;iirrrrriiiiiiiiiirrrrri;,s&##MAS,
		 *         r5s;:r3AH5iiiii;;;;;;;;;;;;;;;;iiirXHGSsiih1,
		 *            .;i;;s91;;;;;;::::::::::::;;;;iS5;;;ii:
		 *          :rsriii;;r::::::::::::::::::::::;;,;;iiirsi,
		 *       .,iri;;::::;;;;;;::,,,,,,,,,,,,,..,,;;;;;;;;iiri,,.
		 *    ,9BM&,            .,:;;:,,,,,,,,,,,hXA8:            ..,,,.
		 *   ,;&@@#r:;;;;;::::,,.   ,r,,,,,,,,,,iA@@@s,,:::;;;::,,.   .;.
		 *    :ih1iii;;;;;::::;;;;;;;:,,,,,,,,,,;i55r;;;;;;;;;iiirrrr,..
		 *   .ir;;iiiiiiiiii;;;;::::::,,,,,,,:::::,,:;;;iiiiiiiiiiiiri
		 *   iriiiiiiiiiiiiiiii;;;::::::::::::::::;;;iiiiiiiiiiiiiiiir;
		 *  ,riii;;;;;;;;;;;;;:::::::::::::::::::::::;;;;;;;;;;;;;;iiir.
		 *  iri;;;::::,,,,,,,,,,:::::::::::::::::::::::::,::,,::::;;iir:
		 * .rii;;::::,,,,,,,,,,,,:::::::::::::::::,,,,,,,,,,,,,::::;;iri
		 * ,rii;;;::,,,,,,,,,,,,,:::::::::::,:::::,,,,,,,,,,,,,:::;;;iir.
		 * ,rii;;i::,,,,,,,,,,,,,:::::::::::::::::,,,,,,,,,,,,,,::i;;iir.
		 * ,rii;;r::,,,,,,,,,,,,,:,:::::,:,:::::::,,,,,,,,,,,,,::;r;;iir.
		 * .rii;;rr,:,,,,,,,,,,,,,,:::::::::::::::,,,,,,,,,,,,,:,si;;iri
		 *  ;rii;:1i,,,,,,,,,,,,,,,,,,:::::::::,,,,,,,,,,,,,,,:,ss:;iir:
		 *  .rii;;;5r,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,sh:;;iri
		 *   ;rii;:;51,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,.:hh:;;iir,
		 *    irii;::hSr,.,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,.,sSs:;;iir:
		 *     irii;;:iSSs:.,,,,,,,,,,,,,,,,,,,,,,,,,,,..:135;:;;iir:
		 *      ;rii;;:,r535r:...,,,,,,,,,,,,,,,,,,..,;sS35i,;;iirr:
		 *       :rrii;;:,;1S3Shs;:,............,:is533Ss:,;;;iiri,
		 *        .;rrii;;;:,;rhS393S55hh11hh5S3393Shr:,:;;;iirr:
		 *          .;rriii;;;::,:;is1h555555h1si;:,::;;;iirri:.
		 *            .:irrrii;;;;;:::,,,,,,,,:::;;;;iiirrr;,
		 *               .:irrrriiiiii;;;;;;;;iiiiiirrrr;,.
		 *                  .,:;iirrrrrrrrrrrrrrrrri;:.
		 *                        ..,:::;;;;:::,,.
		 */


		// 字符拓展：所有字符本质上都是数字
		// 编码 Unicode 2字节 0～65536=2¹⁶
		/*
		 * Unicode编码
		 * 代码 显示 描述
		 *
		 * U+0020 空格
		 *
		 * U+0021 ! 叹号
		 *
		 * U+0022 " 双引号
		 *
		 * U+0023 # 井号
		 *
		 * U+0024 $ 价钱/货币符号
		 *
		 * U+0025 % 百分比符号
		 *
		 * U+0026 & 英文“and”的简写符号
		 *
		 * U+0027 ' 引号
		 *
		 * U+0028 ( 开 左圆括号
		 *
		 * U+0029 ) 关 右圆括号
		 *
		 * U+002A * 星号
		 *
		 * U+002B + 加号
		 *
		 * U+002C , 逗号
		 *
		 * U+002D - 连字号/减号
		 *
		 * U+002E . 句号
		 *
		 * U+002F / 左斜杠
		 *
		 * U+0030 0 数字 0
		 *
		 * U+0031 1 数字 1
		 *
		 * U+0032 2 数字 2
		 *
		 * U+0033 3 数字 3
		 *
		 * U+0034 4 数字 4
		 *
		 * U+0035 5 数字 5
		 *
		 * U+0036 6 数字 6
		 *
		 * U+0037 7 数字 7
		 *
		 * U+0038 8 数字 8
		 *
		 * U+0039 9 数字 9
		 *
		 * U+003A : 冒号
		 *
		 * U+003B ; 分号
		 *
		 * U+003C < 小于符号
		 *
		 * U+003D = 等于号
		 *
		 * U+003E > 大于符号
		 *
		 * U+003F ? 问号
		 *
		 * U+0040 @ 英文“at”的简写符号
		 *
		 * U+0041 A 拉丁字母 A
		 *
		 * U+0042 B 拉丁字母 B
		 *
		 * U+0043 C 拉丁字母 C
		 *
		 * U+0044 D 拉丁字母 D
		 *
		 * U+0045 E 拉丁字母 E
		 *
		 * U+0046 F 拉丁字母 F
		 *
		 * U+0047 G 拉丁字母 G
		 *
		 * U+0048 H 拉丁字母 H
		 *
		 * U+0049 I 拉丁字母 I
		 *
		 * U+004A J 拉丁字母 J
		 *
		 * U+004B K 拉丁字母 K
		 *
		 * U+004C L 拉丁字母 L
		 *
		 * U+004D M 拉丁字母 M
		 *
		 * U+004E N 拉丁字母 N
		 *
		 * U+004F O 拉丁字母 O
		 *
		 * U+0050 P 拉丁字母 P
		 *
		 * U+0051 Q 拉丁字母 Q
		 *
		 * U+0052 R 拉丁字母 R
		 *
		 * U+0053 S 拉丁字母 S
		 *
		 * U+0054 T 拉丁字母 T
		 *
		 * U+0055 U 拉丁字母 U
		 *
		 * U+0056 V 拉丁字母 V
		 *
		 * U+0057 W 拉丁字母 W
		 *
		 * U+0058 X 拉丁字母 X
		 *
		 * U+0059 Y 拉丁字母 Y
		 *
		 * U+005A Z 拉丁字母 Z
		 *
		 * U+005B [ 开 方括号
		 *
		 * U+005C \ 右斜杠
		 *
		 * U+005D ] 关 方括号
		 *
		 * U+005E ^ 抑扬（重音）符号
		 *
		 * U+005F _ 底线
		 *
		 * U+0060 ` 重音符
		 *
		 * U+0061 a 拉丁字母 a
		 *
		 * U+0062 b 拉丁字母 b
		 *
		 * U+0063 c 拉丁字母 c
		 *
		 * U+0064 d 拉丁字母 d
		 *
		 * U+0065 e 拉丁字母 e
		 *
		 * U+0066 f 拉丁字母 f
		 *
		 * U+0067 g 拉丁字母 g
		 *
		 * U+0068 h 拉丁字母 h
		 *
		 * U+0069 i 拉丁字母 i
		 *
		 * U+006A j 拉丁字母 j
		 *
		 * U+006B k 拉丁字母 k
		 *
		 * U+006C l 拉丁字母 l（L的小写）
		 *
		 * U+006D m 拉丁字母 m
		 *
		 * U+006E n 拉丁字母 n
		 *
		 * U+006F o 拉丁字母 o
		 *
		 * U+0070 p 拉丁字母 p
		 *
		 * U+0071 q 拉丁字母 q
		 *
		 * U+0072 r 拉丁字母 r
		 *
		 * U+0073 s 拉丁字母 s
		 *
		 * U+0074 t 拉丁字母 t
		 *
		 * U+0075 u 拉丁字母 u
		 *
		 * U+0076 v 拉丁字母 v
		 *
		 * U+0077 w 拉丁字母 w
		 *
		 * U+0078 x 拉丁字母 x
		 *
		 * U+0079 y 拉丁字母 y
		 *
		 * U+007A z 拉丁字母 z
		 *
		 * U+007B { 开 左花括号
		 *
		 * U+007C | 直线
		 *
		 * U+007D } 关 右花括号
		 *
		 * U+007E ~ 波浪纹
		 *
		 * 拉丁字母
		 *
		 * 拉丁字母-1
		 *
		 * 代码 显示 描述
		 *
		 * U+00A1 ¡ 倒转的叹号
		 *
		 * U+00A2 ¢ （货币单位）分钱、毫子
		 *
		 * U+00A3 £ （货币）英镑
		 *
		 * U+00A4 ¤ （货币）当货币未有符号时以此替代
		 *
		 * U+00A5 ¥ （货币）日元
		 *
		 * U+00A6 ¦ 两条断开的直线
		 *
		 * U+00A7 § 文件分不同部分
		 *
		 * U+00A8 ¨ （语言）分音
		 *
		 * U+00A9 © 版权符
		 *
		 * U+00AA ª （意大利文、葡萄牙文、西班牙文）阴性序数
		 *
		 * U+00AB « 双重角形引号
		 *
		 * U+00AC ¬ 逻辑非
		 *
		 * U+00AE ® 商标
		 *
		 * U+00AF ¯ 长音
		 *
		 * U+00B0 ° 角度
		 *
		 * U+00B1 ± 正负号
		 *
		 * U+00B2 2 二次方
		 *
		 * U+00B3 3 三次方
		 *
		 * U+00B4 ´ 锐音符
		 *
		 * U+00B5 µ 百万分之一，10?6
		 *
		 * U+00B6 ¶ 文章分段
		 *
		 * U+00B7 · 间隔号
		 *
		 * U+00B8 ¸ 软音符
		 *
		 * U+00B9 1 一次方
		 *
		 * U+00BA º （意大利文、葡萄牙文、西班牙文）阳性序数
		 *
		 * U+00BB » 指向右的双箭头
		 *
		 * U+00BC ¼ 四分之一
		 *
		 * U+00BD ½ 二分之一
		 *
		 * U+00BE ¾ 四分之三
		 *
		 * U+00BF ¿ 倒转的问号
		 *
		 * U+00C1 Á 在拉丁字母 A 上加锐音符
		 *
		 * U+00C2 Â 在拉丁字母 A 上加抑扬符“^”
		 *
		 * U+00C3 Ã 在拉丁字母 A 上加“~”
		 *
		 * U+00C4 Ä 在拉丁字母 A 上加分音符“..”
		 *
		 * U+00C5 Å 在拉丁字母 A 上加角度符“°”
		 *
		 * U+00C6 Æ 拉丁字母 A、E 的混合
		 *
		 * U+00C7 Ç 在拉丁字母 C 下加软音符
		 *
		 * U+00C8 È 在拉丁字母 E 上加重音符
		 *
		 * U+00C9 É 在拉丁字母 E 上加锐音符
		 *
		 * U+00CA Ê 在拉丁字母 E 上加抑扬符
		 *
		 * U+00CB Ë 在拉丁字母 E 上加分音符
		 *
		 * U+00CC Ì 在拉丁字母 I 上加重音符
		 *
		 * U+00CD Í 在拉丁字母 I 上加锐音符
		 *
		 * U+00CE Î 在拉丁字母 I 上加抑扬符
		 *
		 * U+00CF Ï 在拉丁字母 I 上加分音符
		 *
		 * U+00D0 Ð 古拉丁字母，现只有法罗文和冰岛文使用
		 *
		 * U+00D1 Ñ 在拉丁字母 N 上加波浪纹“~”
		 *
		 * U+00D2 Ò 在拉丁字母 O 上加重音符
		 *
		 * U+00D3 Ó 在拉丁字母 O 上加锐音符
		 *
		 * U+00D4 Ô 在拉丁字母 O 上加抑扬符
		 *
		 * U+00D5 Õ 在拉丁字母 O 上加波浪纹“~”
		 *
		 * U+00D6 Ö 在拉丁字母 O 上加分音符
		 *
		 * U+00D7 × 乘号，亦可拖按“Alt”键，同时按“41425”五键
		 *
		 * U+00D8 Ø 在拉丁字母 O 由右上至左下加对角斜线“/”
		 *
		 * U+00D9 Ù 在拉丁字母 U 上加重音符
		 *
		 * U+00DA Ú 在拉丁字母 U 上加锐音符
		 *
		 * U+00DB Û 在拉丁字母 U 上加抑扬符
		 *
		 * U+00DC Ü 在拉丁字母 U 上加分音符
		 *
		 * U+00DD Ý 在拉丁字母 Y 上加锐音符
		 *
		 * U+00DE Þ 古拉丁字母，现已被“Th”取替
		 *
		 * U+00DF ß 德文字母
		 *
		 * U+00E0 à 在拉丁字母 a 上加重音符
		 *
		 * U+00E1 á 在拉丁字母 a 上加锐音符
		 *
		 * U+00E2 â 在拉丁字母 a 上加抑扬符
		 *
		 * U+00E3 ã 在拉丁字母 a 上加波浪纹“~”
		 *
		 * U+00E4 ä 在拉丁字母 a 上加分音符
		 *
		 * U+00E5 å 在拉丁字母 a 上加角度符“°”
		 *
		 * U+00E6 æ 拉丁字母 a、e 的混合
		 *
		 * U+00E7 ç 在拉丁字母 c 下加软音符
		 *
		 * U+00E8 è 在拉丁字母 e 上加锐音符
		 *
		 * U+00E9 é 在拉丁字母 e 上加重音符
		 *
		 * U+00EA ê 在拉丁字母 e 上加抑扬符
		 *
		 * U+00EB ë 在拉丁字母 e 上加分音符
		 *
		 * U+00EC ì 在拉丁字母 i 上加重音符
		 *
		 * U+00ED í 在拉丁字母 i 上加锐音符
		 *
		 * U+00EE î 在拉丁字母 i 上加抑扬符
		 *
		 * U+00EF ï 在拉丁字母 i 上加分音符
		 *
		 * U+00F0 ð 古拉丁字母
		 *
		 * U+00F1 ñ 在拉丁字母 n 上加波浪纹“~”
		 *
		 * U+00F2 ò 在拉丁字母 o 上加重音符
		 *
		 * U+00F3 ó 在拉丁字母 o 上加锐音符
		 *
		 * U+00F4 ô 在拉丁字母 o 上加抑扬符
		 *
		 * U+00F5 õ 在拉丁字母 o 上加波浪纹“~”
		 *
		 * U+00F6 ö 在拉丁字母 o 上加分音符
		 *
		 * U+00F7 ÷ 除号，亦可拖按“Alt”键，同时按“41426”五键
		 *
		 * U+00F8 ø 在拉丁字母 o 由右上至左下加对角斜线“/”
		 *
		 * U+00F9 ù 在拉丁字母 u 上加重音符
		 *
		 * U+00FA ú 在拉丁字母 u 上加锐音符
		 *
		 * U+00FB ? 在拉丁字母 u 上加抑扬符
		 *
		 * U+00FC ü 在拉丁字母 u 上加分音符
		 *
		 * U+00FD ý 在拉丁字母 y 上加锐音符
		 *
		 * U+00FE þ 古拉丁字母，现已被“th”取替
		 *
		 * U+00FF ü 在拉丁字母 u 上加分音符
		 *
		 * 拉丁字母（扩展 A）
		 *
		 * 代码 显示 描述
		 *
		 * U+0100 Ā 在拉丁字母 A 上加长音符
		 *
		 * U+0101 ā 在拉丁字母 a 上加长音符
		 *
		 * U+0102 Ă 在拉丁字母 A 上加短音符
		 *
		 * U+0103 ă 在拉丁字母 a 上加短音符
		 *
		 * U+0104 Ą 在拉丁字母 A 上加反尾形符
		 *
		 * U+0105 ą 在拉丁字母 a 上加反尾形符
		 *
		 * 拉丁字母（扩展 C）
		 *
		 * 代码 显示 描述
		 *
		 * U+2C60 Ⱡ 在拉丁字母“L”中间加两条横线“=”
		 *
		 * U+2C61 ⱡ 在拉丁字母“l”（L 的小写）中间加两条横线“=”
		 *
		 * U+2C62 Ɫ 在拉丁字母“L”（大写）中间加一条波浪线“~”
		 *
		 * U+2C63 Ᵽ 在拉丁字母“P”中间加一条横线“-”
		 *
		 * U+2C64 Ɽ 在拉丁字母“R”下加一条尾巴
		 *
		 * U+2C65 ⱥ 在拉丁字母“a”上加一条对角斜线“/”
		 *
		 * U+2C66 ⱦ 在拉丁字母“t”上加一条对角斜线“/”
		 *
		 * U+2C67 Ⱨ 在拉丁字母“H”下加一条尾巴
		 *
		 * U+2C68 ⱨ 在拉丁字母“h”下加一条尾巴
		 *
		 * U+2C69 Ⱪ 在拉丁字母“K”下加一条尾巴
		 *
		 * U+2C6A ⱪ 在拉丁字母“k”下加一条尾巴
		 *
		 * U+2C6B Ⱬ 在拉丁字母“Z”下加一条尾巴
		 *
		 * U+2C6C ⱬ 在拉丁字母“z”下加一条尾巴
		 *
		 * U+2C74 ⱴ 在拉丁字母“v”的起笔加一个弯勾
		 *
		 * U+2C75 Ⱶ 拉丁字母“H”的左半部
		 *
		 * U+2C76 ⱶ 拉丁字母“h”的左半部
		 *
		 * U+2C77 ⱷ 希腊字母“φ”的上半部
		 *
		 * 彝文字母
		 *
		 * 拉丁字母（扩展 D）
		 *
		 * 代码 显示 描述
		 *
		 * U+A720 ꜠ 强调音调音昂的改造字母
		 *
		 * U+A721 ꜡ 强调音调低沉的改造字母
		 *
		 * 特殊
		 *
		 * 代码 显示 描述
		 *
		 * U+FFFC ￼ 取代无法显示字符的“OBJ”
		 *
		 * U+FFFD � 无法显示的字符以它取代
		 *
		 * 编码表
		 *
		 * Unicode 编码表
		 *
		 * BMP SMP SIP SSP
		 *
		 * 0000—0FFF 8000—8FFF 10000—10FFF 20000—20FFF 28000—28FFF E0000—E0FFF
		 *
		 * 1000—1FFF 9000—9FFF 21000—21FFF 29000—29FFF
		 *
		 * 2000—2FFF A000—AFFF 12000—12FFF 22000—22FFF 2A000—2AFFF
		 *
		 * 3000—3FFF B000—BFFF 23000—23FFF
		 *
		 * 4000—4FFF C000—CFFF 24000—24FFF 2F000—2FFFF
		 *
		 * 5000—5FFF D000—DFFF 1D000—1DFFF 25000—25FFF
		 *
		 * 盲文图案
		 *
		 * 6000—6FFF E000—EFFF 26000—26FFF
		 *
		 * 7000—7FFF F000—FFFF 1F000—1FFFF 27000—27FFF
		 *
		 * 注：除非有特别指明，否则以下符号皆属“半角”而非“全角”。
		 */

		System.out.println("字符拓展：所有字符本质上都是数字");

		char c = 'w';
		char c1 = '好';
		System.out.println(c);
		System.out.println((int) c);// 强制装换
		System.out.println(c1);
		System.out.println((int) c1);

		System.out.println("—————————————————————————————————————————————————————————————————————————————");

		/*
		 * 转义字符
		 * \a 响铃(BEL) 007
		 * \b 退格(BS) 008
		 * \f 换页(FF) 012
		 * \n 换行(LF) 010
		 * \r 回车（CR) 013
		 * \t 水平制表(HT) 009
		 * \v 垂直制表(VT) 011
		 * \\ 反斜杠 092
		 * \? 问号字符 063
		 * \' 单引号字符 039
		 * \" 双引号字符 034
		 * \0 空字符(NULL) 000
		 * \ddd 任意字符 三位八进制
		 * \xhh 任意字符 二位十六进制
		 *
		 * JAVA中转义字符：
		 * 1.八进制转义序列：\+1到3位5数字；
		 * 范围'\000'~"377'
		 * \0：空字符
		 *
		 * 2.Unicode转义字符：
		 * \\u+四位十六进制数字
		 * 0~65535
		 * \u0000：空字符
		 *
		 * 3.特殊字符：
		 * \"：双引号
		 * \'：单引号
		 * \\：反斜线
		 *
		 * 4.控制字符：5个
		 * \' 单引号字符
		 * \\ 反斜杠字符
		 * \r 回车
		 * \n 换行
		 * \f 走纸换页
		 * \t 横向跳格
		 * \b 退格
		 *
		 * 点的转义：.==> u002E
		 * 美元符号的转义：$==> u0024
		 * 乘方符号的转义：A==> u005E
		 * 左大括号的转义：{==> u007B
		 * 左方括号的转义：[==> u005B
		 * 左圆括号的转义：(==> u0028
		 * 竖线的转义：|==> u007C
		 * 右圆括号的转义：)==> u0029
		 * 星号的转义：*==> u002A
		 * 加号的转义：+==> u002B
		 * 问号的转义：?==>u003F
		 * 反斜杠的转义：==>u005C
		 */

		/***
		 * ┌───┐   ┌───┬───┬───┬───┐ ┌───┬───┬───┬───┐ ┌───┬───┬───┬───┐ ┌───┬───┬───┐
		 * │Esc│   │ F1│ F2│ F3│ F4│ │ F5│ F6│ F7│ F8│ │ F9│F10│F11│F12│ │P/S│S L│P/B│  ┌┐    ┌┐    ┌┐
		 * └───┘   └───┴───┴───┴───┘ └───┴───┴───┴───┘ └───┴───┴───┴───┘ └───┴───┴───┘  └┘    └┘    └┘
		 * ┌───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───────┐ ┌───┬───┬───┐ ┌───┬───┬───┬───┐
		 * │~ `│! 1│@ 2│# 3│$ 4│% 5│^ 6│& 7│* 8│( 9│) 0│_ -│+ =│ BacSp │ │Ins│Hom│PUp│ │N L│ / │ * │ - │
		 * ├───┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─────┤ ├───┼───┼───┤ ├───┼───┼───┼───┤
		 * │ Tab │ Q │ W │ E │ R │ T │ Y │ U │ I │ O │ P │{ [│} ]│ | \ │ │Del│End│PDn│ │ 7 │ 8 │ 9 │   │
		 * ├─────┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴─────┤ └───┴───┴───┘ ├───┼───┼───┤ + │
		 * │ Caps │ A │ S │ D │ F │ G │ H │ J │ K │ L │: ;│" '│ Enter  │               │ 4 │ 5 │ 6 │   │
		 * ├──────┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴────────┤     ┌───┐     ├───┼───┼───┼───┤
		 * │ Shift  │ Z │ X │ C │ V │ B │ N │ M │< ,│> .│? /│  Shift   │     │ ↑ │     │ 1 │ 2 │ 3 │   │
		 * ├─────┬──┴─┬─┴──┬┴───┴───┴───┴───┴───┴──┬┴───┼───┴┬────┬────┤ ┌───┼───┼───┐ ├───┴───┼───┤ E││
		 * │ Ctrl│    │Alt │         Space         │ Alt│    │    │Ctrl│ │ ← │ ↓ │ → │ │   0   │ . │←─┘│
		 * └─────┴────┴────┴───────────────────────┴────┴────┴────┴────┘ └───┴───┴───┘ └───────┴───┴───┘
		 */

		System.out.println("转义字符：");

		System.out.println("\"HelloWorld\"");
		System.out.println("\'HelloWorld\'");
		System.out.println("\\HelloWorld\\");
		System.out.println("Hello\rWorld");
		System.out.println("Hello\nWorld");
		System.out.println("Hello\fWorld");
		System.out.println("Hello\tWorld");
		System.out.println("Hello\bWorld");

		System.out.println("—————————————————————————————————————————————————————————————————————————————");

		System.out.println("字符串比较（new）：");
		String s = new String("Hello，World");
		String s1 = new String("Hello，World");
		System.out.println(s == s1);// false
		String s2 = "HelloWorld";
		String s3 = "HelloWorld";
		System.out.println(s2 == s3);// ture

		// —————————————————————————————————————————————————————————————————————————————————————————————————————

		System.out.println("if语句写法：");
		boolean b = true;
		// 他们是一样的
		if (b == true) {
		} // 新手
		if (b) {
		} // 老手
		// Less is more！

		// —————————————————————————————————————————————————————————————————————————————————————————————————————

		// 类型转换
		// 低————————————————————————————————————→高
		// byte、short、char→int→long→float→double
		// 运算中，不同类型的数据先转化为同一类型，再进行运算(默认为int)
		// 强制转换(高转低) 变量类型 变量名=(变量类型)变量名; [会有精度和溢出等问题]
		System.out.println("类型转换（强制转换(高转低)[会有精度和溢出等问题]）：");
		double d1 = 589.025;
		float f3 = (float) d1;
		long l = (long) d1;
		int i4 = (int) d1;
		byte b2 = (byte) d1;
		short s4 = (short) d1;
		char c2 = (char) d1;
		System.out.println(d1);
		System.out.println(f3);
		System.out.println(l);
		System.out.println(i4);
		System.out.println(b2);
		System.out.println(s4);
		System.out.println(c2);

		System.out.println("———————————————————————————————————————————");

		// 自动转换(低转高) char是字符，必须强制转换
		System.out.println("类型转换（自动转换(低转高)）：");
		byte b3 = 69;
		short s5 = b3;
		char c3 = (char) b3;
		int i5 = b3;
		long l1 = b3;
		float f4 = b3;
		double d2 = b3;
		System.out.println(b3);
		System.out.println(s5);
		System.out.println(c3);
		System.out.println(i5);
		System.out.println(l1);
		System.out.println(f4);
		System.out.println(d2);

		/***
		 * 佛曰:
		 * 写字楼里写字间，写字间里程序员；
		 * 程序人员写程序，又拿程序换酒钱。
		 * 酒醒只在网上坐，酒醉还来网下眠；
		 * 酒醉酒醒日复日，网上网下年复年。
		 * 但愿老死电脑间，不愿鞠躬老板前；
		 * 奔驰宝马贵者趣，公交自行程序员。
		 * 别人笑我忒疯癫，我笑自己命太贱；
		 * 不见满街漂亮妹，哪个归得程序员？
		 */

		/*
		 * 注意点：
		 * 1. 不能对布尔值进行转换
		 * 2. 不能把对象类型转换为不相干的类型
		 * 3. 在把高容量转换到低容量的时候，强制转换
		 * 4.转换的时候可能存在内存溢出，或者精度问题！
		 */

		System.out.println("—————————————————————————————————————————————————————————————————————————————");

		// 变量：可以变化的量
		/*
		 * 注意事项：
		 * 每个变量都有类型，类型可以是基本类型，也可以是引用类型。
		 * 变量名必须是合法的标识符。
		 * 变量声明是一条完整的语句，因此每一个声明都必须以分号结束
		 */
		/*
		 * 常量
		 * 常量(Constant)：初始化(initialize)后不能再改变值！不会变动的值。
		 * 所谓常量可以理解成一种特殊的变量，它的值被设定后，在程序运行过程中不允许被改变。变final 常量名=值；
		 * final double PI=3.14; 4;
		 * 常量名一般使用大写字符。
		 */
		/*
		 * 变量的命名规范
		 * 所有变量、方法、类名：见名知意
		 * 类成员变量：首字母小写和驼峰原则：montbSalayy
		 * 局部变量：首字母小写和驼峰原则
		 * 常量：大写字母和下划线：MAX_VALUE
		 * 类名：首字母大写和驼峰原则：Man、GoodMan
		 * 方法名：首字母小写和驼峰原则：run0,runRun0
		 */

		/***
		 *                    _ooOoo_
		 *                   o8888888o
		 *                   88" . "88
		 *                   (| -_- |)
		 *                    O\ = /O
		 *                ____/`---'\____
		 *              .   ' \\| |// `.
		 *               / \\||| : |||// \
		 *             / _||||| -:- |||||- \
		 *               | | \\\ - /// | |
		 *             | \_| ''\---/'' | |
		 *              \ .-\__ `-` ___/-. /
		 *           ___`. .' /--.--\ `. . __
		 *        ."" '< `.___\_<|>_/___.' >'"".
		 *       | | : `- \`.;`\ _ /`;.`/ - ` : | |
		 *         \ \ `-. \_ __\ /__ _/ .-` / /
		 * ======`-.____`-.___\_____/___.-`____.-'======
		 *                    `=---='
		 *
		 * .............................................
		 *          佛祖保佑             永无BUG
		 */


		/*
		 * 运算符
		 * Java语言支持如下运算符：
		 * 算术运算符：+，-，*，/，%，++，--
		 * 赋值运算符：=
		 * 关系运算符：>，<，>=，<=，==，!= instanceof
		 * 逻辑运算符：&&，||，！(与，或，非)
		 * 位运算符：&，|，A，~，>>，<<，>>>
		 * 条件运算符？：
		 * 扩展赋值运算符:+=，-=，*=，/=
		 */

		/*Java运算符优先级
		当多个运算符出现在一个表达式中，谁先谁后呢？这就涉及到运算符的优先级别的问题。在一个多运算符的表达式中，运算符优先级不同会导致最后得出的结果差别甚大。
		例如，（1+3）＋（3+2）*2，这个表达式如果按加号最优先计算，答案就是 18，如果按照乘号最优先，答案则是 14。
		再如，x = 7 + 3 * 2;这里x得到13，而不是20，因为乘法运算符比加法运算符有较高的优先级，所以先计算3 * 2得到6，然后再加7。
		后缀	() [] . (点操作符)	左到右
		一元	expr++ expr--	从左到右
		一元	++expr --expr + - ～ ！	从右到左
		乘性 	* /％	左到右
		加性 	+ -	左到右
		移位 	>> >>>  << 	左到右
		关系 	> >= < <= 	左到右
		相等 	==  !=	左到右
		按位与	＆	左到右
		按位异或	^	左到右
		按位或	|	左到右
		逻辑与	&&	左到右
		逻辑或	| |	左到右
		条件	？：	从右到左
				赋值	= + = - = * = / =％= >> = << =＆= ^ = | =	从右到左
		逗号	，	左到右*/

		// 算术运算符
		System.out.println("算术运算符：");
		int i6 = 68;
		int i7 = 27;
		System.out.println(i6 + i7);
		System.out.println(i6 - i7);
		System.out.println(i6 * i7);
		System.out.println(i6 / i7);
		System.out.println(i6 % i7);

		System.out.println("———————————————————————————————————————————");

		// 关系运算符
		System.out.println("关系运算符：");
		System.out.println(i6 > i7);
		System.out.println(i6 < i7);
		System.out.println(i6 >= i7);
		System.out.println(i6 <= i7);
		System.out.println(i6 == i7);
		System.out.println(i6 != i7);

		System.out.println("———————————————————————————————————————————");

		System.out.println("自增自减运算符：");
		// 自增自减运算符++ --
		int i8 = 76;
		System.out.println(i8);
		int i9 = i8++;
		// a++ a=a+1 先赋值，后自增
		System.out.println(i8);
		// ++a a=a+1 先自增，后赋值
		int i10 = ++i8;
		System.out.println(i8);
		System.out.println(i9);
		System.out.println(i10);

		System.out.println("————————————————————————————————————————————");

		// 幂运算 用数学方法Math
		/*Number & Math 类方法
		xxxValue()   将 Number 对象转换为xxx数据类型的值并返回。
		compareTo()  将number对象与参数比较。
		equals()     判断number对象是否与参数相等。
		valueOf()    返回一个 Number 对象指定的内置数据类型
		toString()   以字符串形式返回值。
		parseInt()   将字符串解析为int类型。
		abs()        返回参数的绝对值。
        ceil()       返回大于等于( >= )给定参数的的最小整数，类型为双精度浮点型。
        floor()      返回小于等于（<=）给定参数的最大整数 。
        rint()       返回与参数最接近的整数。返回类型为double。
        round()      它表示四舍五入，算法为 Math.floor(x+0.5)，即将原来的数字加上 0.5 后再向下取整，所以，Math.round(11.5) 的结果为12，Math.round(-11.5) 的结果为-11。
        min()        返回两个参数中的最小值。
        max()        返回两个参数中的最大值。
        exp()        返回自然数底数e的参数次方。
        log()        返回参数的自然数底数的对数值。
        pow()        返回第一个参数的第二个参数次方。
        sqrt()       求参数的算术平方根。
        sin()        求指定double类型参数的正弦值。
        cos()        求指定double类型参数的余弦值。
        tan()        求指定double类型参数的正切值。
        asin()       求指定double类型参数的反正弦值。
        acos()       求指定double类型参数的反余弦值。
        atan()       求指定double类型参数的反正切值。
        atan2()      将笛卡尔坐标转换为极坐标，并返回极坐标的角度值。
        toDegrees()  将参数转化为角度。
        toRadians()  将角度转换为弧度。
        random()     返回一个随机数。


        Math 的 floor,round 和 ceil 方法实例比较
        参数  Math.floor	Math.round	Math.ceil
        1.4	   1	        1	        2
        1.5	   1	        2	        2
        1.6	   1	        2	        2
        -1.4   -2	        -1	        -1
        -1.5   -2	        -1	        -1
        -1.6   -2	        -2	        -1
        */


		System.out.println("幂运算：");
		double pow = Math.pow(15, 4);
		System.out.println(pow);

		System.out.println("————————————————————————————————————————————");



		// —————————————————————————————————————————————————————————————————————————————————————————————————————

		/***
		 * _ooOoo_
		 * o8888888o
		 * 88" . "88
		 * (| -_- |)
		 *  O\ = /O
		 * ___/`---'\____
		 * .   ' \\| |// `.
		 * / \\||| : |||// \
		 * / _||||| -:- |||||- \
		 * | | \\\ - /// | |
		 * | \_| ''\---/'' | |
		 * \ .-\__ `-` ___/-. /
		 * ___`. .' /--.--\ `. . __
		 * ."" '< `.___\_<|>_/___.' >'"".
		 * | | : `- \`.;`\ _ /`;.`/ - ` : | |
		 * \ \ `-. \_ __\ /__ _/ .-` / /
		 * ======`-.____`-.___\_____/___.-`____.-'======
		 * `=---='
		 *          .............................................
		 *           佛曰：bug泛滥，我已瘫痪！
		 */


		// —————————————————————————————————————————————————————————————————————————————————————————————————————

		// 逻辑运算符
		// 与(and) 或(or) 非（取反）
		System.out.println("逻辑运算符：");
		boolean b1 = true;
		boolean b4 = false;
		System.out.println("b1 && b4:" + (b1 && b4));// 逻辑与运算：两个变量都为真，结果才为true
		System.out.println("b1||b4:" + (b1 || b4)); // 逻辑或运算：两个变量有一个为真，则结果才为true
		System.out.println("! （b1 && b4)：" + !(b1 && b4));// 如果是真，则变为假，如果是假则变为真

		// 短路运算
		// “与”在运算的时候如果a&&b中a为false，则不会进行b的运算
		System.out.println("短路运算：");
		int i11 = 75;
		boolean b5 = (i11 < 32) && (i11++ < 43);
		System.out.println(b5);
		System.out.println(i11);

		System.out.println("———————————————————————————————————————————");

		// 位运算符
		/*
		 * A=0001 1100
		 * B=0000 1101
		 * A&B=0000 1100(与)[都是0为0，都是1为1，有0有1取1]
		 * A|B=0001 1101(或)[有1取1，没有取0]
		 * A^B=0001 0001(非)[一样取0，不一样取1]
		 * ~A=1110 0011(取反)
		 * ~B=1111 0010
		 *
		 * <<左移 每移一位*2
		 * >>右移 每移一位/2
		 * 0000 0000 0
		 * 0000 0001 1
		 * 0000 0010 2
		 * 0000 0011 3
		 * 0000 0100 4
		 * 0000 0101 5
		 * …………………………
		 */
		System.out.println("位运算符：");
		System.out.println(2 << 3);
		System.out.println(3 >> 1);

		System.out.println("———————————————————————————————————————————");

		// 扩展赋值运算符
		System.out.println("扩展赋值运算符：");
		int i12 = 34;
		int i13 = 86;
		System.out.println(i12);
		i12 += i13;// a=a+b
		System.out.println(i12);
		i13 -= i12;// a=a-b
		System.out.println(i13);
		i12 *= i13;// a=a*b
		System.out.println(i12);
		i13 /= i12;// a=a/b
		System.out.println(i13);

		System.out.println("———————————————————————————————————————————");

		// 字符串连接符 +

		int i14 = 57;
		int i15 = 79;
		System.out.println(i14 + i15);
		System.out.println("" + i14 + i15);// ""相当于字符串String，当""在前面时，后面的会转化为字符串
		System.out.println(i14 + i15 + "");// 如果""在后面，则会进行正常的运算

		System.out.println("———————————————————————————————————————————");

		// 条件运算符(三元运算符)
		// x?y:z
		// 如果 x==ture 则输出y 反之输出z
		int i16 = 56;
		String s6 = i16 < 100 ? "i16<100" : "i16>100";
		System.out.println(s6);

		System.out.println("—————————————————————————————————————————————————————————————————————————————");

		/*
		 * 顺序结构
		 * JAVA的基本结构就是顺序结构，除非特别指明，否则就按照顺序一句一句执行。
		 * 顺序结构是最简单的算法结构。
		 * 语句与语句之间，框与框之间是按从上到下的顺序进行的，它是由若干个依次执行的处理步骤组成的，它是任何一个算法都离不开的一种基本算法结构。
		 */
		System.out.println("Hello1");
		System.out.println("Hello2");
		System.out.println("Hello3");
		System.out.println("Hello4");
		System.out.println("Hello5");

		System.out.println("—————————————————————————————————————————————————————————————————————————————");

		// 命令行传参
		// args.length 数组长度
		for (int j = 0; j < args.length; j++) {
			System.out.println("args[" + j + "]:" + args[1]);
		}

		VariableTypesAndInitialization.variableTypesAndInitialization();

		AboutScanner.aboutScanner();

		TestScanner.testScanner();

		ChooseStructure.chooseStructure();

		LoopStructure.loopStructure();

		AboutMethod.aboutMethod();

		AboutArrays.aboutArrays();

		ObjectOriented.ObjectOriented();

		AboutClass.ninth();

		Human.human();

		One.one();

		Zero.zero();

		Person.person();

		Animals.animals();

		System.out.println("static关键字");
		MyFirstJava testStatic = new MyFirstJava();
		System.out.println(MyFirstJava.age);
		//System.out.println(MyFirstJava.name);
		System.out.println(testStatic.name);
		System.out.println(testStatic.age);

		MyFirstJava testStaticObject = new MyFirstJava();
		new MyFirstJava().run();
		new MyFirstJava().go();
		testStaticObject.run();
		testStaticObject.go();
		//run();
		go();

		AboutStatic.aboutStatic();

		AbstractClassTest abstractClassTest = new AbstractClassTest();
		abstractClassTest.abstractClassTest();

		InterfaceImpl.interfaceImpl();

		Outer.outer();

		TestException.testException();

		TestMyException.testMyException();

		NumberAndMath.numberAndMath();

		DateTest.date();
		//SimpleDateFormat 类有一些附加的方法，特别是parse()，它试图按照给定的SimpleDateFormat 对象的格式化存储来解析字符串。
		//解析字符串为时间
		SimpleDateFormat sft = new SimpleDateFormat ("yyyy-MM-dd");
		String input = args.length == 0 ? "1818-11-11" : args[0];
		System.out.print(input + " Parses as ");
		Date t;
		try {
			t = sft.parse(input);
			System.out.println(t);
		} catch (ParseException e) {
			System.out.println("Unparseable using " + sft);
		}

		TestCalendar.testCalendar();

		TestGregorianCalendar.testGregorianCalendar();

		TestRegularExpression.testRegularExpression();

		AboutObject.aboutObject();

		JavaGenerics.javaGenerics();

		JavaGenericsClass.javaGenericsClass();

		AboutJavaWrapperClass.aboutJavaWrapperClass();

		JavaEnum.javaEnum();

		JavaOverrideAndOverload.javaOverrideAndOverload();
		//TestObjectMethod.
	}
	public void run(){
		System.out.println("mainRun");
	}

	public static void go(){
		System.out.println("mainGo");
	}

	//重写finalize()方法
	/**
	 * @throws Throwable
	 */
	@Override
	protected void finalize() throws Throwable {
		System.out.println(this.name+"垃圾对象已被回收！");
	}

}

class world{
	public static void execute(String me){
		System.out.println("Hello,world!");
	}
}

class VariableTypesAndInitialization {
	// 不声明和初始化，则输出默认值，boolean默认false，其他7大基本类型为0(浮点数为0.0)，其余的都是null
	static int i = 37; // 类变量，要有关键词(static)，可以直接调用
	static int i3;
	// 修饰符(static、final)不分前后顺序
	static final int FIRST = 48;
	final static int FIRST1 = 489;
	int i1 = 458;
	int i2;
	String s = "Hello！"; // 实例变量，整个类都有效，不用声明和初始化
	String s1;

	public static VariableTypesAndInitialization variableTypesAndInitialization() {// 这里的“void”就是方法
		int i4 = 0;// 局部变量);，在方法里，必须声明和初始化值，只在这个方法里有效
		// 变量类型(自定义类型) 变量名=new 类名();
		VariableTypesAndInitialization variableTypesAndInitialization = new VariableTypesAndInitialization();
		// 调用：变量名.实例变量名
		System.out.println(variableTypesAndInitialization.s);
		System.out.println(variableTypesAndInitialization.s1);
		System.out.println(variableTypesAndInitialization.i1);
		System.out.println(variableTypesAndInitialization.i2);
		System.out.println(i);
		System.out.println(i3);
		return variableTypesAndInitialization;
	}
}

class AboutScanner {
	public static AboutScanner aboutScanner() {
		/*
		 * 通过 Scanner类的 next()[下一个] 与nextLine()[下一行] 方法获取输入的字符串，在读取前我们一般需要使
		 * 用 hasNext()与hasNextLine(0判断是否还有输入的数据
		 */
		Scanner s = new Scanner(System.in);// out是输出，in是输入
		System.out.print("请输入内容(next)：");
		// 有空格的话，hasNext只会输出空格前面的
		if (s.hasNext() == true) {
			String sa = s.next();
			System.out.println("输入的内容为：" + sa);
		}
		s.close();// IO类型的类，不关闭会占用资源

		/***
		 *
		 *   █████▒█    ██  ▄████▄   ██ ▄█▀       ██████╗ ██╗   ██╗ ██████╗
		 * ▓██   ▒ ██  ▓██▒▒██▀ ▀█   ██▄█▒        ██╔══██╗██║   ██║██╔════╝
		 * ▒████ ░▓██  ▒██░▒▓█    ▄ ▓███▄░        ██████╔╝██║   ██║██║  ███╗
		 * ░▓█▒  ░▓▓█  ░██░▒▓▓▄ ▄██▒▓██ █▄        ██╔══██╗██║   ██║██║   ██║
		 * ░▒█░   ▒▒█████▓ ▒ ▓███▀ ░▒██▒ █▄       ██████╔╝╚██████╔╝╚██████╔╝
		 *  ▒ ░   ░▒▓▒ ▒ ▒ ░ ░▒ ▒  ░▒ ▒▒ ▓▒       ╚═════╝  ╚═════╝  ╚═════╝
		 *  ░     ░░▒░ ░ ░   ░  ▒   ░ ░▒ ▒░
		 *  ░ ░    ░░░ ░ ░ ░        ░ ░░ ░
		 *           ░     ░ ░      ░  ░
		 */


		System.out.println("———————————————————————————————————————————");

		/*
		 * Scanner对象
		 * next():
		 * 1、一定要读取到有效字符后才可以结束输入。
		 * 2、对输入有效字符之前遇到的空白，next()方法会自动将其去掉。
		 * 3、只有输入有效字符后才将其后面输入的空白作为分隔符或者结束符。
		 * 4、next()不能得到带有空格的字符串。
		 *
		 * nextLine():
		 * 1、以Enter为结束符，也就是说nextLine()方法返回的是输入回车之前的所有字符。
		 * 2、可以获得空白。
		 */
		Scanner s1 = new Scanner(System.in);
		System.out.print("请输入内容(nextLine):");
		// 即使有空格，他也会输出全部
		if (s1.hasNextLine() == true) {
			String sb = s1.nextLine();
			System.out.println("输入的内容为:" + sb);
		}
		s1.close();

		System.out.println("\n"+"———————————————————————————————————————————");

		Scanner s2 = new Scanner(System.in);
		System.out.println("请输入内容(nextLine):");
		if (s2.hasNextLine()) {
			String sc = s2.nextLine();
			System.out.println("输入的内容为:" + sc);
		}

		s2.close();

		System.out.println("———————————————————————————————————————————");

		Scanner s3 = new Scanner(System.in);
		byte b = 0;
		System.out.print("请输入byte类型的整数(-128~127)：");
		if (s3.hasNextByte()) {
			b = s3.nextByte();
			System.out.println("整数数据:" + b);
		} else {
			System.out.println("请输入合理的字符(整数)!");
		}
		s3.close();

		System.out.println("———————————————————————————————————————————");

		Scanner s4 = new Scanner(System.in);
		short ss1 = 0;
		System.out.print("请输入short类型的整数(-32768~32767)：");
		if (s4.hasNextShort()) {
			ss1 = s4.nextShort();
			System.out.println("整数数据:" + ss1);
		} else {
			System.out.println("请输入合理的字符(整数)：");
		}
		s4.close();

		System.out.println("———————————————————————————————————————————");

		Scanner s5 = new Scanner(System.in);
		System.out.print("请输入int类型的整数(-2147483648~2147483647)：");
		int i = 0;
		if (s5.hasNextInt()) {
			i = s5.nextInt();
			System.out.println("整数数据:" + i);
		} else {
			System.out.println("请输入合理的字符(整数)！");
		}
		s5.close();

		System.out.println("———————————————————————————————————————————");

		Scanner s6 = new Scanner(System.in);
		System.out.print("请输入long类型的整数(-9223372036854775808~9223372036854775807)：");
		long l = 0;
		if (s6.hasNextLong()) {
			l = s6.nextLong();
			System.out.println("整数数据" + l);
		} else {
			System.out.println("请输入合理的字符(整数)！");
		}
		s6.close();

		System.out.println("———————————————————————————————————————————");

		Scanner s7 = new Scanner(System.in);
		float f = 0.0f;
		System.out.print("请输入浮点数(小数，float类型)：");
		if (s7.hasNextFloat()) {
			f = s7.nextFloat();
			System.out.println("浮点数数据:" + f);
		} else {
			System.out.println("请输入合理的字符(小数)！");
		}
		s7.close();

		System.out.println("———————————————————————————————————————————");

		Scanner s8 = new Scanner(System.in);
		double d = 0.0;
		System.out.print("请输入浮点数(小数,double类型)：");
		if (s8.hasNextDouble()) {
			d = s8.nextDouble();
			System.out.println("浮点数数据:" + d);
		} else {
			System.out.println("请输入合理的字符(小数)！");
		}
		s8.close();

		System.out.println("———————————————————————————————————————————");

		return new AboutScanner();
	}
}

/***
 *                    .::::.
 *                  .::::::::.
 *                 :::::::::::  FUCK YOU
 *             ..:::::::::::'
 *           '::::::::::::'
 *             .::::::::::
 *        '::::::::::::::..
 *             ..::::::::::::.
 *           ``::::::::::::::::
 *            ::::``:::::::::'        .:::.
 *           ::::'   ':::::'       .::::::::.
 *         .::::'      ::::     .:::::::'::::.
 *        .:::'       :::::  .:::::::::' ':::::.
 *       .::'        :::::.:::::::::'      ':::::.
 *      .::'         ::::::::::::::'         ``::::.
 *  ...:::           ::::::::::::'              ``::.
 * ```` ':.          ':::::::::'                  ::::..
 *                    '.:::::'                    ':'````..
 */


class TestScanner {
	public static TestScanner testScanner() {
		// 输入多个数据求平均值，求出它们所相加的和，非数字结束
		Scanner s = new Scanner(System.in);
		System.out.print("请输入第1个数：");
		// 它们所相加的和
		double d = 0;
		// 输入了多少个数字
		int i = 0;
		// 通过循环进行判断是否还有输入，并且对没每一次都进行求和与统计
		while (s.hasNextDouble()) {
			double d1 = s.nextDouble();
			i = i + 1;// 或者i++
			d = d + d1;// 当前的数(前面的d)是原来的数(等号后的d)加上输入的数(d1)
			System.out.print("请输入第" + (1 + i) + "个数：");
		}
		System.out.println("共输入了" + i + "个数");
		System.out.println("当前输入数字的和为：" + d);
		System.out.println("平均数为：" + (d / i));
		s.close();

		System.out.println("———————————————————————————————————————————");

		return new TestScanner();
	}
}

/***
 * 【ギラついた眼の光と 蕩け切った顔で待ち受けるケダモノがそこにいた
 * ?
 * ハ.{
 * ≦≧s、{ ソ-v s≦……………≧s、
 * 回 0/ /...... \、)'} .…
 * /}八 さ、早くシマしょいっばいしましょ
 * マレビト様が御出でにならな間ハ}^\…}\<ソ
 * 八……/) ソ…………………\……、)=) そのぶっとい魔羅様ぶちこんで
 * 手近で浮気出産しまくったサセコで-す{筏気) {イ弋斥、{{…} …K
 * 代夕///////<} >"}……!///////`巧//…人八:.. 姉妹まんこにずっぶし種付けえ
 * 八…/////,=7 /-!…i. =、//// …/、1}
 * 最初にさいきょ一雄様の味を覚えたせいで …/\ {i……. 弋心 ノ/-- 貴方様の優等血統 残しましよ
 * .イ…/_}γ卜 <…/!
 * -
 * 結局 欲求不満募るばっかり·!~ Y (一){ {/}i
 * ちや~んと責任とってくださ♪/> i: !:::( !!/y i}>-< }，>_ィ¡ ノト…_}きもちよ-く ドッビュンして
 * '_-{::
 * 金玉空に、お腹ばっこりさせましょ
 * マ7-_, V
 * 厂>一<i
 * /1
 * }`ト- r^iiY..…. ……… -" r_'イ- ///
 * li
 * { i i}…、///
 * /:!/ }!.:|/(`ㄨ
 * i/(X /..]/ 八
 * /....." //:: 〉-ゥ/ :/ ij.. v… i
 * V <-< }:i ]
 * {!八 i :{ \ ≦\} ..
 * :、/ }>--ソ 人//
 * : :/ ’-v≦ニ辶7 V.Y
 * デ-<フ|八!
 * / /+ V
 * i
 * 一七 /
 * (()
 * 八 7^7! :j
 * r!
 *
 */

class ChooseStructure {
	public static ChooseStructure chooseStructure() {
		/*
		 * 选择结构
		 * 一.if选择结构
		 * 1.if单选择结构
		 * 2.if双选择结构
		 * 3.if多选择结构
		 * 4.嵌套的if结构
		 * 二.switch多选择结构
		 */

		/*
		 * 1.if单选结构
		 * 基本语法
		 * if(布尔表达式){
		 * 如果为真执行的代码
		 * }
		 * 如果为假则执行下一句(可参考顺序结构)
		 */
		Scanner s = new Scanner(System.in);
		System.out.print("请输入内容：");

		// equals：判断字符串是否想相等
		if (s.hasNextLine()) {
			String s1 = s.nextLine();
			if (s1.equals("你好啊！")) {
				System.out.println("Hello！");
			}
		}
		System.out.println("End");
		// s.close();

		System.out.println("———————————————————————————————————————————");

		/*
		 * 2.if双选择结构
		 * 基本语法
		 * if(布尔表达式){
		 * 如果为真执行的代码
		 * }else{
		 * 如果为假执行的代码
		 * }
		 */
		Scanner s2 = new Scanner(System.in);
		System.out.print("请输入年龄：");

		if (s2.hasNextLine()) {
			int age = s2.nextInt();
			if (age >= 18) {
				System.out.println("您可以观看");
			} else {
				System.out.println("未成年人禁止观看");
			}
		}
		// s2.close();

		System.out.println("\n"+"———————————————————————————————————————————");

		/*
		 * 3.if多选择结构
		 * 基本语法
		 * if(布尔表达式1){
		 * 如果为布尔表达式1真执行的代码
		 * }else if(布尔表达式2){
		 * 如果布尔表达式2为真执行的代码
		 * }else if(布尔表达式3){
		 * 如果布尔表达式3为真执行的代码
		 * }else if(布尔表达式4){
		 * 如果布尔表达式4为真执行的代码
		 * }
		 * ……………………
		 */
		Scanner s3 = new Scanner(System.in);
		System.out.print("请输入学历：");
		if (s3.hasNextLine()) {
			String s4 = s3.nextLine();
			if (s4 == "没上过学") {
				System.out.println("学历不够");
			} else if (s4 == "小学学历") {
				System.out.println("em，学历不够");
			} else if (s4 == "初中学历") {
				System.out.println("呃，学历不足");
			} else if (s4 == "高中学历") {
				System.out.println("还可以");
			} else if (s4 == "大学学历") {
				System.out.println("学历合格");
			} else {
				System.out.println("请输入合理的内容");
			}
		}
		// s3.close();

		System.out.println("\n"+"———————————————————————————————————————————");

		/*
		 * 4.if嵌套结构
		 * 基本语法
		 * if(布尔表达式){
		 * if(布尔表达式){
		 * if(布尔表达式){
		 *
		 * }
		 * }
		 * }
		 */
		Scanner s5 = new Scanner(System.in);
		System.out.print("请输入年龄：");
		if (s5.hasNextLine()) {
			int age1 = s5.nextInt();
			System.out.print("请输入体重：");
			double wight = s5.nextDouble();
			if (age1 >= 18) {
				if (wight >= 50) {
					System.out.println("您可以献血");
				} else {
					System.out.println("体重过轻，您不能献血!");
				}
			} else {
				System.out.println("年龄太小，不能献血哦！");
			}
		}
		System.out.println("\n"+"—————————————————————————————————————————————————————————————————————————————");

		// switch多选择结构
		/*
		 * 多选择结构还有一种实现方式就是switch case 语句
		 * switch case 语句判断一个变量与一系列之中某个值是否相等，每个值简称为一个分支
		 *
		 * 基本语法：
		 * switch(expression){
		 * case value:
		 * //语句
		 * break;//可选
		 * case value:
		 * //语句
		 * break;//可选
		 * 你可以有任意数量的case语句
		 * default://可选
		 * //语句
		 * }
		 *
		 * 注意：
		 * switch语句中的变量类型可以是：
		 * 1.byte、short、int或char
		 * 2.从Java SE7开始 switch 支持String 类型了
		 * 3.同时case标签必须为字符串常量或字面量
		 */
		// case穿透，没有break; 只要满足就会执行满足的代码以及他下面的所有代码,直到进程结束或者有break;为止
		// switch语句 匹配一个具体的值
		char c = 'c';
		switch (c) {
			case 'a':
				System.out.println("好");
				break;
			case 'b':
				System.out.println("还可以");
				break;
			case 'c':
				System.out.println("一般");
			case 'd':
				System.out.println("较差");
			case 'e':
				System.out.println("差");
			default:
				System.out.println("对不起，您失败了！");
		}
		System.out.println("———————————————————————————————————————————");

		switch (c) {
			case 'a':
				System.out.println("好");
				break;
			case 'b':
				System.out.println("还可以");
				break;
			case 'c':
				System.out.println("一般");
				break;
			case 'd':
				System.out.println("较差");
				break;
			case 'e':
				System.out.println("差");
				break;
			default:
				System.out.println("对不起，您失败了！");
		}
		System.out.println("———————————————————————————————————————————");

		// JDK7的新特性，表达式可以是字符串！！！
		// 字符的本质还是数字

		// 反编译 Java文件---->(编译)class文件（字节码文件）-----(反编译)

		String name = "清";
		switch (name) {
			case "杨":
				System.out.println("这不是作者！");
				break;
			case "清":
				System.out.println("这是作者！");
				break;
			default:
				System.out.println("emmmm,您好像不认识作者！");
		}

		System.out.println("———————————————————————————————————————————");
		return new ChooseStructure();
	}
}

/***
 **************************************************************
 *                                                            *
 *   .=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-.       *
 *    |                     ______                     |      *
 *    |                  .-"      "-.                  |      *
 *    |                 /            \                 |      *
 *    |     _          |              |          _     |      *
 *    |    ( \         |,  .-.  .-.  ,|         / )    |      *
 *    |     > "=._     | )(__/  \__)( |     _.=" <     |      *
 *    |    (_/"=._"=._ |/     /\     \| _.="_.="\_)    |      *
 *    |           "=._"(_     ^^     _)"_.="           |      *
 *    |               "=\__|IIIIII|__/="               |      *
 *    |              _.="| \IIIIII/ |"=._              |      *
 *    |    _     _.="_.="\          /"=._"=._     _    |      *
 *    |   ( \_.="_.="     `--------`     "=._"=._/ )   |      *
 *    |    > _.="                            "=._ <    |      *
 *    |   (_/                                    \_)   |      *
 *    |                                                |      *
 *    '-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-='      *
 *                                                            *
 *           LASCIATE OGNI SPERANZA, VOI CH'ENTRATE           *
 **************************************************************
 */


class LoopStructure {
	public static LoopStructure loopStructure(){

		/*
		 * 循环结构
		 * 1.while循环
		 * 2，do...while循环
		 * 3.for循环
		 */

		// while循环
		/*
		 * 这是最基本的循环
		 *
		 * 基本语法：
		 * while(布尔表达式){
		 * //循环内容
		 * }
		 * 注意：
		 * 1.只要布尔表达式为true，就会一直循环下去
		 * 2.我们大多数情况是会让循环停下来，我们需要一个让表达式失效的方式来结束循环
		 * 3.少部分情况下需要循环一直执行比如服务器的请求响应监听等
		 * 4.循环条件一直为true就会造成无限循环【死循环】，我们正常的业务编程中应该尽量避免死循环。会影响程序性能或者造成程序卡死崩溃！！！！！
		 */

		// 输出1~100
		int num = 0;// 初始化条件
		while (num < 100) {// 条件判断
			num++;// 迭代
			System.out.println(num);// 循环体
		}

		System.out.println("———————————————————————————————————————————");

		// 计算1+2+3+...+99+100=？
		int i = 0;
		int i1 = 0;
		while (i <= 100) {
			i1 = i1 + i;
			i++;
		}
		System.out.println(i1);

		System.out.println("—————————————————————————————————————————————————————————————————————————————");

		/*
		 * do...while循环
		 * 对于while语句而言，如果不满足条件，则不能进入循环。但是有时候我们需要即使不满足条件也要至少执行一次
		 * do...while循环和while循环相似，不同的是，do...while循环至少会执行一次
		 *
		 * 基本语法：
		 * do{
		 * //代码语句
		 * }while(布尔表达式);
		 *
		 * 区别：
		 * 1.while先判断后执行，do...while是先执行一次后判断
		 * 2.do...while总是会保证循环体会被执行一次！！！
		 */

		int i2 = 0;
		int i3 = 0;
		do {
			i2++;
			i3 = i3 + i2;
		} while (i2 < 100);
		System.out.println(i3);

		System.out.println("———————————————————————————————————————————");

		// 区别实例
		int a = 0;
		while (a < 0) {// 先判断，判断为假，不执行代码
			System.out.println(a);
			a++;
		}

		System.out.println("————————————————————————");

		do {// 先执行一次
			System.out.println(a);
			a++;
		} while (a < 0);

		System.out.println("—————————————————————————————————————————————");

		/***
		 *
		 *           _____                    _____                    _____                    _____
		 *          /\    \                  /\    \                  /\    \                  /\    \
		 *         /::\____\                /::\    \                /::\    \                /::\    \
		 *        /:::/    /                \:::\    \              /::::\    \              /::::\    \
		 *       /:::/    /                  \:::\    \            /::::::\    \            /::::::\    \
		 *      /:::/    /                    \:::\    \          /:::/\:::\    \          /:::/\:::\    \
		 *     /:::/____/                      \:::\    \        /:::/__\:::\    \        /:::/__\:::\    \
		 *    /::::\    \                      /::::\    \      /::::\   \:::\    \      /::::\   \:::\    \
		 *   /::::::\    \   _____    ____    /::::::\    \    /::::::\   \:::\    \    /::::::\   \:::\    \
		 *  /:::/\:::\    \ /\    \  /\   \  /:::/\:::\    \  /:::/\:::\   \:::\____\  /:::/\:::\   \:::\    \
		 * /:::/  \:::\    /::\____\/::\   \/:::/  \:::\____\/:::/  \:::\   \:::|    |/:::/__\:::\   \:::\____\
		 * \::/    \:::\  /:::/    /\:::\  /:::/    \::/    /\::/   |::::\  /:::|____|\:::\   \:::\   \::/    /
		 *  \/____/ \:::\/:::/    /  \:::\/:::/    / \/____/  \/____|:::::\/:::/    /  \:::\   \:::\   \/____/
		 *           \::::::/    /    \::::::/    /                 |:::::::::/    /    \:::\   \:::\    \
		 *            \::::/    /      \::::/____/                  |::|\::::/    /      \:::\   \:::\____\
		 *            /:::/    /        \:::\    \                  |::| \::/____/        \:::\   \::/    /
		 *           /:::/    /          \:::\    \                 |::|  ~|               \:::\   \/____/
		 *          /:::/    /            \:::\    \                |::|   |                \:::\    \
		 *         /:::/    /              \:::\____\               \::|   |                 \:::\____\
		 *         \::/    /                \::/    /                \:|   |                  \::/    /
		 *          \/____/                  \/____/                  \|___|                   \/____/
		 */


		// for循环
		/*
		 * for循环语句是支持迭代的一种通用结构，是最有效、最灵活的循环结构。
		 * for循环执行的次数是在执行前就决定的
		 *
		 * 基本语法：
		 * for(初始化;布尔表达式;更新){
		 * //代码语句
		 * }
		 *
		 * 说明：
		 * 最先执行器初始化步骤，可以声明一种类型，但可以初始化一个或多个需闭环控制变量，也可以是空语句
		 * 然后，检测布尔表达式的值。如果为true，循环体被执行如果为false，循环终止，开始执行循环体后面的语句
		 * 执行一次循环后，更新循环控制变量（迭代因子控制循环变量的增进）
		 * 再次检测布尔表达式，循环执行上面的过程
		 */

		///// 初始化|条件判断|迭代 输出1~100
		for (int b = 1; b < 100; b++) { // 里面三个参数
			System.out.println(b);
		}
		System.out.println("循环结束");

		System.out.println("———————————————————————————————————————————");

		// 计算0到100之间奇数和偶数和
		int oddSum = 0;
		int evenSum = 0;
		for (int i4 = 0; i4 < 100; i4++) {
			if (i4 % 2 != 0) {// 奇数
				oddSum += i4;
			} else {// 偶数
				evenSum += i4;
			}
		}
		System.out.println("奇数的和：" + oddSum);
		System.out.println("偶数的和：" + evenSum);

		System.out.println("———————————————————————————————————————————");

		// 用while循环或for循环输出1-1000之间能被5整除的数，并且每行输出3个
		for (int i5 = 0; i5 <= 1000; i5++) {
			if (i5 % 5 == 0) {
				System.out.print(i5 + "\t");// '\t'换行
			}
			if (i5 % (5 * 3) == 0) {
				System.out.println("\n");// 或 System.out.println();
			}
		}

		System.out.println("\n" + "———————————————————————————————————————————");

		/***
		 *              ,----------------,              ,---------,
		 *         ,-----------------------,          ,"        ,"|
		 *       ,"                      ,"|        ,"        ,"  |
		 *      +-----------------------+  |      ,"        ,"    |
		 *      |  .-----------------.  |  |     +---------+      |
		 *      |  |                 |  |  |     | -==----'|      |
		 *      |  |  I LOVE DOS!    |  |  |     |         |      |
		 *      |  |  Bad command or |  |  |/----|`---=    |      |
		 *      |  |  C:\>_          |  |  |   ,/|==== ooo |      ;
		 *      |  |                 |  |  |  // |(((( [33]|    ,"
		 *      |  `-----------------'  |," .;'| |((((     |  ,"
		 *      +-----------------------+  ;;  | |         |,"
		 *         /_)______________(_/  //'   | +---------+
		 *    ___________________________/___  `,
		 *   /  oooooooooooooooo  .o.  oooo /,   \,"-----------
		 *  / ==ooooooooooooooo==.o.  ooo= //   ,`\--{)B     ,"
		 * /_==__==========__==_ooo__ooo=_/'   /___________,"
		 *
		 */


		// 打印九九乘法表
		// 1.打印第一列
		// 2.我们把固定的1用另一个for循环包起来
		// 3.去掉重复项（即在第一个for循环的条件判断中把9改成另一个for循环）
		// 4.调整样式

		for (int i7 = 0; i7 <= 9; i7++) {
			for (int i6 = 1; i6 <= i7; i6++) {
				System.out.print(i7 + "*" + i6 + "=" + (i7 * i6) + "\t");
			}
			System.out.println();
		}

		System.out.println("———————————————————————————————————————————");

		// 增强for循环
		// 基本语法
		/*
		 * for(声明语句：表达式)
		 * {
		 * //代码句子
		 * }
		 */
		// 声明语句：声明新的局部变量，该变量的类型必须和数组元素的类型匹配。其作用域限定在循环语句块，其值与此时数组元素的值相等。
		// 表达式：表达式是要访问的数组名，或者是返回值为数组的方法

		int[] number = { 10, 20, 30, 40, 50 };// 定义了一个数组

		for (int i8 = 0; i8 < 5; i8++) {// 普通for循环
			System.out.println(number[i8]);
		}

		System.out.println("———————————————————————————————————————————");

		// 遍历数组的元素
		for (int x : number) {// 增强for循环
			System.out.println(x);
		}

		System.out.println("———————————————————————————————————————————");

		// break和continue
		/*
		 * 1.break在任何循环的主体部分，均可用break控制循环的流程。break用于强行退出循环，不执行循环中剩余的语句。（
		 * break语句也在switch语句中使用）
		 * 2.continue语句在循环语句体中，用于终止某次循环过程，即跳过循环体中尚未执行的语句，接着进行下一次是否循环的判定。
		 *
		 * 关于goto关键字
		 * goto关键字很早就在程序设计语言中出现。尽管goto仍是Java的一个保留字，但并未在语言中得到正确的使用；Java没有goto，然而，
		 * 在break和continue这两个关键字的身上，我们仍能看出一些goto的影子---带标签的break和continue
		 * “标签”是指后面跟一个冒号的标识符，例如：label:
		 * 对Java来说唯一用到标签的地方是在循环语句之前。而循环之前设置标签的唯一理由是：我们希望在其中一个嵌套另一个循环，
		 * 由于break和continue关键字通常只中断进行循环，但若随同标签使用，他们就会中断到存在标签的地方。
		 */

		int i9 = 0;
		while (i9 < 100) {
			i9++;
			System.out.println(i9);
			if (i9 == 47) {
				break;
			}
		}
		// break仅仅终止循环，不会结束进程（强制终止）
		System.out.println("测试");

		System.out.println("———————————————————————————————————————————");

		int i10 = 0;
		while (i10 < 100) {
			i10++;
			if (i10 % 10 == 0) {
				System.out.println();
				continue;
			}
			System.out.print(i10 + "\t");
		}
		// continue,执行到continue时就会跳过，重新进行循环

		System.out.println("———————————————————————————————————————————");

		// 标签
		// 打印101~150之间的所有质数（指数是指在大于1的自然数中，除了一和它本身之外不再有其它因数的自然数）

		int count = 0;
		outer: for (int _i = 101; _i < 150; _i++) {
			for (int _i1 = 2; _i1 < _i / 2; _i1++) {
				if (_i % _i1 == 0) {
					continue outer;
				}
			}
			System.out.print(_i + "\t");
		}

		System.out.println("\n" + "———————————————————————————————————————————");

		// 打印一个5行的三角形
		for (int _i2 = 1; _i2 <= 5; _i2++) {
			for (int j = 5; j >= _i2; j--) {
				System.out.print(" ");
			}
			for (int j = 1; j <= _i2; j++) {
				System.out.print("*");
			}
			for (int j = 1; j < _i2; j++) {
				System.out.print("*");
			}
			System.out.println();
		}
		System.out.println("———————————————————————————————————————————");
		//for (;;) {
		//} // 死循环
		return new LoopStructure();
	}
}

/***
 *                 .-~~~~~~~~~-._       _.-~~~~~~~~~-.
 *             __.'              ~.   .~              `.__
 *           .'//                  \./                  \\`.
 *         .'//                     |                     \\`.
 *       .'// .-~"""""""~~~~-._     |     _,-~~~~"""""""~-. \\`.
 *     .'//.-"                 `-.  |  .-'                 "-.\\`.
 *   .'//______.============-..   \ | /   ..-============.______\\`.
 * .'______________________________\|/______________________________`.
 *
 */


class AboutMethod {
	// main方法
	public static AboutMethod aboutMethod() {
		// java方法
		/*
		 * 1.何为方法
		 * 2.方法的定义及调用
		 * 3.方法重载
		 * 4.命令行传参
		 * 5.可变参数
		 * 6.递归
		 */

		// 1.什么是方法？
		/*
		 * 例如:System.out.println();
		 * 其中“System”是系统的一个类，“out”是对象，“println”就是方法
		 *
		 * Java方法是语句的集合，他们在一起执行一个功能
		 * （1）方法是解决一类问题的步骤的有序组合
		 * （2）方法包含于类或对象中
		 * （3）方法在程序中被创建，在其他地方被引用
		 *
		 * 设计方法的原则：方法的本意是功能块，就是实现某个功能的语句块集合。我们设计方法的时候最好保持方法的原子性。就是一个方法只完成一个功能，
		 * 这样有利于我们后期的扩展
		 */

		// 加法
		// 实际参数，实际调用传递给它的参数
		int addSum = add1(2, 9);
		System.out.println(addSum);

		System.out.println("———————————————————————————————————————————");

		text();

		System.out.println("———————————————————————————————————————————");

		// 方法的定义
		/*
		 * Java的方法类似于其它语言的函数，是一段用来完成特定功能的代码片段，一般情况下，定义一个方法包含以下语法：
		 * 方法包含一个方法头和方法体。下面是一个方法的所有部分
		 * 1.修饰符：修饰符，这是可选的，告诉编译器如何调用该方法。定义了该方法的访问类型
		 * 2.返回值类型：方法可能会返回值。returnValueType
		 * 是方法返回值的数据类型。有些方法执行所需的操作，但没有返回值。在这种情况下，returnValueType是关键字void
		 * 3.方法名：是方法的实际名称。方法名和参数表共同构成方法签名
		 * 4.参数类型：参数像是一个占位符。当方法在调用时，传递值给参数。这个值被称为实参或变量参数列表是指方法的参数类型、顺序和参数的个数。参数是可选的，
		 * 方法不包含任何参数
		 * （传入的实参类型要和形参相同）
		 * （1）形式参数：在方法被调用时用于接收外界输入的数据
		 * （2）实际参数：调用方法时实际传给方法的数据
		 * 5.方法体：方法体包含具体的语句，定义该方法的功能
		 * 6.break和return的区别：break，跳出循环；return，结束方法，返回一个结果
		 * 7.异常抛出
		 *
		 * 基本语法：
		 * 修饰符 返回值类型 方法名(参数类型 参数名){
		 * .......................
		 * 方法体
		 * .......................
		 * return 返回值;
		 * }
		 */

		/*
		 * 方法调用
		 * 1.调用方法：对象名.方法名(实参列表)
		 * 2.Java支持两种调用方法的方式，根据方法是否返回值来选择
		 * 3.当方法返回一个值的时候，方法调用通常被当作一个值。例如：
		 * int larger = max(30,40);
		 * 4.如果方法返回值是void，方法调用一定是一条语句
		 * System.out.println("Hello!")
		 *
		 * 静态方法  static
		 * 非静态方法  无static
		 * [如果a方法和b方法都(不)是静态方法，那么a（b）方法可以直接调用b（a）方法[不是递归]，因为static是和类一起加载的，非静态方法需要把类实例化之后才存在]
		 * 值传递和引用传递
		 */

		/*
		 * 值传递和引用传递
		 * 值传递（Java）：值传递是指在调用函数时将实际参数复制一份传递到函数中，这样在函数中如果对参数进行修改，将不会影响到实际参数。
		 * 引用传递：在调用函数时将实际参数的地址传递到函数中，那么在函数中对参数所进行的修改，将影响到实际参数
		 */

		int m = max(19, 89);
		int n = max(78, 78);
		System.out.println(m);
		System.out.println(n);

		System.out.println("———————————————————————————————————————————");

		// 方法的重载
		/*
		 * 重载就是在一个类中，有相同的函数名称但形参不同的参数
		 *
		 * 方法重载的规则：
		 * 1.方法名称必须相同
		 * 2.参数列表必须不同（个数不同、或类型不同，参数排列顺序不同等）
		 * 3.方法返回的类型可以相同也可以不相同
		 * 4.仅仅返回类型不同步已成为方法的重载
		 *
		 * 实现理论：
		 * 方法名相同时，编译器会根据调用方法的参数个数、参数类型等去逐个判断，宜选择对应的方法，如果匹配失败，则编译器报错。
		 */

		int e = max(19, 89);
		double f = max(78, 78);
		System.out.println(e);
		System.out.println(f);

		System.out.println("———————————————————————————————————————————");

		// 可变参数
		/*
		 * JDK1.5开始Java开始支持传递同类型的可变参数给一个方法
		 * 在声明方法中，在指定参数类型后加一个省略号（...）
		 * 一个方法中只能指定一个可变参数，它必须是方法的最后一个参数，任何普通的参数必须在他之前声明
		 */

		AboutMethod sort = new AboutMethod();
		sort.sort(1, 990, 76, 54, 3, 78);

		// 调用可变参数的方法
		printMax(326, 38, 2, 83, 9, 89, 328, 89, 222, 98);
		printMax(new double[] { 128, 819, 278, 82, 73, 983, 298, });

		System.out.println("———————————————————————————————————————————");

		// 递归(尽量不用)
		/*
		 * 递归就是:A方法调用A方法！就是自己调用自己
		 *
		 * 递归可以用简单的程序来解决一些复杂的问题。它通常把一个大型复杂的问题层层转化为一个与原问题相似的规模较小的问题来求解，
		 * 递归策略只需要少量的程序就可以描述出解题过程所需要的多次重复计算，大大的减少了程序的代码量。递归的能力在于用有限的语句来定义对象的无限集合
		 *
		 * 递归结构包括两个部分：
		 * （1）递归头：什么时候不调用自身方法。如果没有头，将陷入死循环。
		 * （2）递归体：什么时候需要调用自身方法。
		 */

		AboutMethod text1 = new AboutMethod();
		// 阶乘
		System.out.println(text1(1));
		System.out.println(text1(5));
		System.out.println(text1(999));

		System.out.println("———————————————————————————————————————————");

		/*
		 * 一、JAVA的六种存储地址
		 *
		 * 1.寄存器 register
		 * 位于处理器内部，是最快的储存器，但是数量极其有限。由编译器根据需求进行分配，不能由代码控制，对于开发者来说是 无感知 的。
		 *
		 * 2.堆栈 stack
		 * 位于RAM中，堆栈指针下移分配新的内存，上移释放内存。创建程序的时候，编译器必须知道存储在堆栈中所有数据的确切大小和生命周期。某些JAVA数据存储在堆栈中
		 * ——特别是对象引用，但是JAVA对象不存储其中。
		 *
		 * 3.堆 heap
		 * 位于RAM中，相比堆栈的优势是不需要知道从堆里分配多少存储空间以及存活时间。JAVA对象存储在这里。
		 *
		 * 4.静态存储 static
		 * 这里的“静态”是指“在固定的位置”。静态存储里存放程序运行时一直存在的数据。你可用关键字static来标识一个对象的特定元素是静态的
		 *
		 * 5.常量存储 constant
		 * 常量值通常直接存放在程序代码内部，这样做是安全的，因为它们永远不会被改变。有时，在嵌入式系统中，常量本身会和其他部分分割离开，所以在这种情况下，
		 * 可以选择将其放在ROM中。
		 *
		 * 6.非RAM存储
		 * 如果数据完全存活于程序之外，那么它可以不受程序的任何控制，在程序没有运行时也可以存在。
		 *
		 *
		 *
		 *
		 * 二、栈、堆、方法区存储的内容
		 * 1.堆区:
		 * 存储的全部是对象，每个对象都包含一个与之对应的class的信息。(class的目的是得到操作指令)
		 * jvm只有一个堆区(heap)被所有线程共享，堆中不存放基本类型和对象引用，只存放对象本身 。
		 * 存放new的对象的和数组
		 * 可以被所有的线程共享，不会存放别的对象引用
		 *
		 * 2.栈区:
		 * 每个线程包含一个栈区，栈中只保存基础数据类型的值和对象以及基础数据的引用
		 * 每个栈中的数据(基础数据类型和对象引用)都是私有的，其他栈不能访问。
		 * 栈分为3个部分：基本类型变量区、执行环境上下文、操作指令区(存放操作指令)。
		 * 存放基本变量类型（会包含这个基本类型的具体数值）
		 * 引用对象的变量（会存放这个引用在堆里面的具体地址）
		 *
		 * 3.方法区:
		 * 又叫静态区，跟堆一样，被所有的线程共享。方法区包含所有的class和static变量。
		 * 方法区中包含的都是在整个程序中永远唯一的元素，如class，static变量。
		 * 可以比而所有的线程共享
		 * 包含了所有的class和static变量
		 *
		 *
		 *
		 *
		 * 三、JAVA 基本数据类型的存储
		 * 基本类型一共有8种，即int, short, long, byte, float, double, boolean, char。这种类型的定义是通过int
		 * a = 3 的形式来定义的，称为自动变量。值得注意的是自动变量存的是字面值，不是类的实例。
		 * 例如 int a =
		 * 3，这里的a是一个指向int类型的引用，指向3这个字面值，整个过程种没有类的存在。字面值的数据大小可知，生存期可知(定义在程序块中，程序块退出后，
		 * 字段值就消失了)，存在栈中。
		 * 栈有一个很重要的特殊性，就是存在栈中的数据可以共享
		 * 假设我们同时定义 int a = 3; int b = 3；
		 * 编译器先处理int a =
		 * 3；首先它会在栈中创建一个变量为a的引用，然后查找有没有字面值为3的地址，没找到，就开辟一个存放3这个字面值的地址，然后将a指向3的地址。接着处理int
		 * b = 3；在创建完b的引用变量后，由于在栈中已经有3这个字面值，便将b直接指向3的地址。这样，就出现了a与b同时均指向3的情况。
		 * 特别注意的是，这种字面值的引用与类对象的引用不同。假定两个类对象的引用同时指向一个对象，如果一个对象引用变量修改了这个对象的内部状态，
		 * 那么另一个对象引用变量也即刻反映出这个变化。相反，通过字面值的引用来修改其值，不会导致另一个指向此字面值的引用的值也跟着改变的情况。
		 * 如上例，我们定义完a与
		 * b的值后，再令a=4；那么，b不会等于4，还是等于3。在编译器内部，遇到a=4；时，它就会重新搜索栈中是否有4的字面值，如果没有，重新开辟地址存放4的值；
		 * 如果已经有了，则直接将a指向这个地址。因此a值的改变不会影响到b的值。
		 */

		//非静态方法的调用
		//实例化这个类
		//对象类型 对象名 = 对象值;
		new AboutMethod().play();

		AboutMethod play = new AboutMethod();
		play.play();

		System.out.println("———————————————————————————————————————————");

		//值传递
		int i = 1;
		System.out.println(i);

		AboutMethod.change(i);
		System.out.println(i);

		System.out.println("———————————————————————————————————————————");

		//引用传递（本质还是值传递）
		SixthReferencePassing sixthReferencePassing = new SixthReferencePassing();
		System.out.println(sixthReferencePassing.i);//0

		AboutMethod.changeSixthReferencePassing(sixthReferencePassing);
		System.out.println(sixthReferencePassing.i);//9
		return new AboutMethod();
	}

	// 加法
	// 形式参数，用来定义的
	public static int add1(int a, int b) {
		return a + b;
	}

	public static void text() {
		for (int _i2 = 1; _i2 <= 5; _i2++) {
			for (int j = 5; j >= _i2; j--) {
				System.out.print(" ");
			}
			for (int j = 1; j <= _i2; j++) {
				System.out.print("*");
			}
			for (int j = 1; j < _i2; j++) {
				System.out.print("*");
			}
			System.out.println();
		}
	}

	// 比较大小
	// 修饰符(可有可无)|返回值类型|方法名()
	public static int max(int num1, int num2) {

		if (num1 == num2) {
			System.out.println(num1 + "=" + num2);
			return 0;// 还可以终止方法
		}

		int result;
		if (num1 > num2) {
			result = num1;
		} else {
			result = num2;
		}

		return result;
	}

	// 方法重载
	public static double max(double num1, double num2) {

		if (num1 == num2) {
			System.out.println(num1 + "=" + num2);
			return 0;// 还可以终止方法
		}

		double result;
		if (num1 > num2) {
			result = num1;
		} else {
			result = num2;
		}
		return result;
	}

	// 可变参数（不定项参数）
	public void end() {
	}

	public void end(int i) {
	}

	public void end(char c) {
	}

	public void end(short s) {
	}

	public void end(byte b) {
	}

	public void end(float f) {
	}

	public void end(long l) {
	}

	public void end(double d) {
	}

	public void end(boolean y) {
	}

	public void sort(int... i) {
		System.out.println(i[0]);
		System.out.println(i[1]);
		System.out.println(i[2]);
		System.out.println(i[3]);
		System.out.println(i[4]);
		System.out.println(i[5]);
	}

	public static void printMax(double... num) {
		if (num.length == 0) {
			System.out.println("没有输入参数");
			return;
		}

		double result = num[0];

		// 排序
		for (int i = 1; i < num.length; i++) {
			if (num[i] > result) {
				result = num[i];
			}
		}
		System.out.println("最大的数为：" + result);
	}

	// 阶乘
	/*
	 * 2！ 2*1
	 * 3！ 3*2*1
	 * 4！ 4*3*2*1
	 */
	public static double text1(int n) {
		if (n == 1) {
			return 1;
		} else {
			return n * text1(n - 1);
		}
	}

	public int maxsum(int a,int b){
		return a > b ? a : b;//如果a>b就返回a,反之返回b（三元运算符）
	}

	//非静态方法
	public void play(){
		System.out.println("Play!");
	}

	//值传递
	public static void change(int a){
		a = 10;
	}

	//引用传递
	public static void changeSixthReferencePassing(SixthReferencePassing sixthReferencePassing){
		sixthReferencePassing.i = 9;
	}
}

//定义了一个类
class SixthReferencePassing{
	//属性
	int i;
}

/***
 *          .,:,,,                                        .::,,,::.
 *        .::::,,;;,                                  .,;;:,,....:i:
 *        :i,.::::,;i:.      ....,,:::::::::,....   .;i:,.  ......;i.
 *        :;..:::;::::i;,,:::;:,,,,,,,,,,..,.,,:::iri:. .,:irsr:,.;i.
 *        ;;..,::::;;;;ri,,,.                    ..,,:;s1s1ssrr;,.;r,
 *        :;. ,::;ii;:,     . ...................     .;iirri;;;,,;i,
 *        ,i. .;ri:.   ... ............................  .,,:;:,,,;i:
 *        :s,.;r:... ....................................... .::;::s;
 *        ,1r::. .............,,,.,,:,,........................,;iir;
 *        ,s;...........     ..::.,;:,,.          ...............,;1s
 *       :i,..,.              .,:,,::,.          .......... .......;1,
 *      ir,....:rrssr;:,       ,,.,::.     .r5S9989398G95hr;. ....,.:s,
 *     ;r,..,s9855513XHAG3i   .,,,,,,,.  ,S931,.,,.;s;s&BHHA8s.,..,..:r:
 *    :r;..rGGh,  :SAG;;G@BS:.,,,,,,,,,.r83:      hHH1sXMBHHHM3..,,,,.ir.
 *   ,si,.1GS,   sBMAAX&MBMB5,,,,,,:,,.:&8       3@HXHBMBHBBH#X,.,,,,,,rr
 *   ;1:,,SH:   .A@&&B#&8H#BS,,,,,,,,,.,5XS,     3@MHABM&59M#As..,,,,:,is,
 *  .rr,,,;9&1   hBHHBB&8AMGr,,,,,,,,,,,:h&&9s;   r9&BMHBHMB9:  . .,,,,;ri.
 *  :1:....:5&XSi;r8BMBHHA9r:,......,,,,:ii19GG88899XHHH&GSr.      ...,:rs.
 *  ;s.     .:sS8G8GG889hi.        ....,,:;:,.:irssrriii:,.        ...,,i1,
 *  ;1,         ..,....,,isssi;,        .,,.                      ....,.i1,
 *  ;h:               i9HHBMBBHAX9:         .                     ...,,,rs,
 *  ,1i..            :A#MBBBBMHB##s                             ....,,,;si.
 *  .r1,..        ,..;3BMBBBHBB#Bh.     ..                    ....,,,,,i1;
 *   :h;..       .,..;,1XBMMMMBXs,.,, .. :: ,.               ....,,,,,,ss.
 *    ih: ..    .;;;, ;;:s58A3i,..    ,. ,.:,,.             ...,,,,,:,s1,
 *    .s1,....   .,;sh,  ,iSAXs;.    ,.  ,,.i85            ...,,,,,,:i1;
 *     .rh: ...     rXG9XBBM#M#MHAX3hss13&&HHXr         .....,,,,,,,ih;
 *      .s5: .....    i598X&&A&AAAAAA&XG851r:       ........,,,,:,,sh;
 *      . ihr, ...  .         ..                    ........,,,,,;11:.
 *         ,s1i. ...  ..,,,..,,,.,,.,,.,..       ........,,.,,.;s5i.
 *          .:s1r,......................       ..............;shs,
 *          . .:shr:.  ....                 ..............,ishs.
 *              .,issr;,... ...........................,is1s;.
 *                 .,is1si;:,....................,:;ir1sr;,
 *                    ..:isssssrrii;::::::;;iirsssssr;:..
 *                         .,::iiirsssssssssrri;;:.
 */


// 数组
/**
 * 数组是相同类型数据的有序结合
 * 数组描述的是相同类型的若干数据，按照一定的先后次序排列组合而成
 * 其中，每一个数据称作一个数组元素，每个数组元素可以通过一个下标来访问他们
 */
class AboutArrays {
	public static AboutArrays aboutArrays() {
		// 变量的类型 变量的名字 = 变量的值
		/*
		 * dataType[] arrayRefVar; 首选
		 * 或
		 * dataType arrayRefVar[]; 也可以，但不是首选
		 *
		 * Java要用new操作符来创建数组
		 * dataType[] arrayRefVar = new dataType[arraySize];
		 *
		 * 数组的元素是通过索引访问的，数组索引从0开始
		 *
		 * 不赋值就会输出默认类型
		 *
		 * 数组的四个基本特点
		 * 其长度是确定的。数组一旦被创建，它的大小就是不可以改变的。
		 * 其元素必须是相同类型，不允许出现混合类型。
		 * 数组中的元素可以是任何数据类型，包括基本类型和引用类型。
		 * 数组变量属引用类型，数组也可以看成是对象，数组中的每个元素相当于该对象的成员变量。数组本身就是对象，Java中对象是在堆中的，因此数组无论保存原始类型还是其他对象类型，数组对象本身是在堆中的。
		 */

		// 定义一个数组
		int[] nums;
		int nums1[];

		nums = new int[15];// 创建了一个数组

		// 给数组赋值
		nums[0] = 1;
		nums[1] = 672;
		nums[2] = 29;
		nums[3] = 120;
		nums[4] = 908;
		nums[5] = 128;
		nums[6] = 99;
		nums[7] = 176;

		System.out.println(nums[0]);
		System.out.println(nums[1]);
		System.out.println(nums[2]);
		System.out.println(nums[3]);
		System.out.println(nums[4]);
		System.out.println(nums[5]);

		// 计算所有元素的和
		int sum = 0;
		for (int i = 0; i < nums.length; i++) {
			sum = sum + nums[i];
		}
		System.out.println("总和为：" + sum);

		System.out.println("———————————————————————————————————————————");

		int[] num2 = new int[20];

		/*
		 * 数组的三种初始化
		 * 一、静态初始化
		 * 例：
		 *   int[] a = {1,2,3,4};
		 *   Man[] mans = {new Man(1,1),new Man(2,2)};
		 *
		 * 二、动态初始化
		 * 例：
		 *   int[] a = new int[9];
		 *   a[0] = 1;
		 *   a[1] = 9;
		 *
		 * 三、默认初始化
		 * 数组是引用类型，他的元素相当于类的实际变量，因此一些数组已经空间分配，其中的每个元素也被按照实例变量同样的方式被隐式初始化
		 * */

		//静态初始化:创建 + 赋值
		int[] a = {1,2,4,43,3,32,34,43,4,3};
		System.out.println(a[0]);
		System.out.println(a[1]);
		System.out.println(a[2]);
		System.out.println(a[3]);
		System.out.println(a[5]);
		System.out.println(a[6]);
		System.out.println(a[7]);
		System.out.println(a[8]);
		System.out.println(a[9]);


		System.out.println("—————————————————————");

		//动态初始化:包含默认初始化
		int[] b =new int[10];
		b[0] = 9;
		b[1] = 16;
		b[2] = 287;
		b[3] = 239;
		b[4] = 929;

		System.out.println(b[0]);
		System.out.println(b[1]);
		System.out.println(b[2]);
		System.out.println(b[3]);
		System.out.println(b[4]);
		System.out.println(b[5]);

		System.out.println("———————————————————————————————————————————");

		//数组的四个基本点
		/*
		 * 1.其长度都是确定的。数组一旦被创建，它的大小就是不可以改变的
		 * 2.其元素必须是相同类型不允许出现混合类型
		 * 3.数组中的元素可以是任何数据类型，包括基本类型和引用类型
		 * 4.数组变量属于引用类型，数组也可以看成是对象，数组中的每个元素相当于该对象的成员变量。数组本身就是对象，Java中对象是在堆中的，因此数组无论保存原始类型还是其他对象类型，数组对象本身是在堆中的
		 * */

		//数组边界
		/*
		 * 下标的合法区间：[0,length-1],越界就会报错
		 * 例：
		 *   public static void main(String[] args) {
		 *       int[] a = new int[2];
		 *       System.out.println(a[2]);
		 *   }
		 *
		 * ArrayIndexOutOfBoundsException:数组下标越界异常
		 *
		 * 数组是相同的数据类型（数据类型可以为任意类型）的有序结合
		 * 数组也是对象，数组元素相当于对象的成员变量
		 * 数组的长度是确定的，不可变的。如果越界就会报错
		 * */

//		int[] c = {1,2,3,4,5,6,7,8};
//		System.out.println(c[0]);
//		for (int f = 0; f <= c.length; f++) {
//			System.out.println(c[f]);
//		}

		int[] arrays = {1,2,3,4,5,6,7};

		//打印数组的每一个元素
		for (int e = 0; e < arrays.length; e++) {
			System.out.println(arrays[e]);
		}

		System.out.println("—————————————————————");

		//计算所有元素的和
		int summer = 0;
		for (int i = 0; i < arrays.length; i++) {
			summer +=arrays[i];
		}
		System.out.println(summer);

		System.out.println("—————————————————————");

		//查找最大元素
		int max = arrays[0];
		for (int i = 0; i < arrays.length; i++) {
			if (arrays[i] > max){
				max = arrays[i];
			}
		}
		System.out.println(max);

		System.out.println("———————————————————————————————————————————");

		//for-each循环
		for (int array : arrays) {
			System.out.println(array);
		}

		System.out.println("———————————————————————————————————————————");

		//数组作方法入参
		printArray(arrays);

		System.out.println();
		System.out.println("———————————————————————————————————————————");

		//数组作为返回值
		int[] reverse = reverse(arrays);
		printArray(reverse);
		System.out.println();
		System.out.println(reverse);

		System.out.println("———————————————————————————————————————————");

		//多维数组
		/*
		 * 多维数组可以看成是数组的数组，比如二维数组就是一个特殊的一维数组，其中每一个元素都是一个一维数组。
		 * 二维数组
		 * int[][] = new int[x][y];
		 * 该二维数组可以看成是一个两行五列的数组
		 * */
		System.out.println("多维数组：");
		int[][] array = new int [5][10];
		array[0][0] = 1;
		array[1][1] = 9;
		int[][] arrayOne = {{1,2,3,4,5,6},{27,383,4,483,493,29},{23,43,3,454,3498,4,34}};

		System.out.println(arrayOne[0]);//这是数组的对象

		printArray(arrayOne[0]);//arrayOne下的{1,2,3,4,5,6}这个数组
		System.out.println();
		System.out.println(arrayOne[0][3]);//arrayOne下的{1,2,3,4,5,6}这个数组中的4

		System.out.println("—————————————————————");

		//打印数组arrayOne的每一个元素
		for (int i = 0; i < arrayOne.length; i++) {
			for (int j = 0; j < arrayOne[i].length; j++) {
				System.out.println(arrayOne[i][j]);
			}
		}

		System.out.println("———————————————————————————————————————————");

		/*
		 * Arrays 类
		 * 数组的工具类java.util.Arrays
		 * 由于数组对象本身并没有什么方法可以供我们调用，但API中提供了一个工具类Arrays供我们使用，从而可以对数据对象进行一些基本的操作。
		 * 查看JDK帮助文档
		 * Arrays类中的方法都是static修饰的静态方法，在使用的时候可以直接使用类名进行调用，而"不用“使用对象来调用(注意：是"不用”而不是“不能")
		 * 具有以下常用功能：
		 * 给数组赋值：通过fill方法。
		 * 对数组排序：通过sort方法，按升序。
		 * 比较数组：通过equals方法比较数组中元素值是否相等。
		 * 查找数组元素：通过binarySearch方法能对排序好的数组进行二分查找法操作。
		 * */

		//打印数组  Arrays.toString   可以自己写
		System.out.println("打印数组：");
		int[] arraysTwo = {2,3,4,23,324,432,324,43,5667,87};
		System.out.println(Arrays.toString(arraysTwo));
		//自己写的方法
		printArrays(arraysTwo);

		System.out.println();
		System.out.println("—————————————————————");

		//数组排序
		System.out.println("数组排序：");
		Arrays.sort(arraysTwo);
		System.out.println(Arrays.toString(arraysTwo));//升序

		System.out.println("———————————————————————————————————————————");

		Arrays.fill(arraysTwo,3,9,78);//数组中下标为3~9的数字被78填充
		System.out.println(Arrays.toString(arraysTwo));

		System.out.println("—————————————————————");

		//填充
		Arrays.fill(arraysTwo,78);
		System.out.println(Arrays.toString(arraysTwo));

		System.out.println("———————————————————————————————————————————");

		//冒泡排序
		/*
		 * 冒泡排序是最为出名的排序算法之一，共有八大排序
		 * 冒泡的代码两层循环，外层冒泡论述，里层以此比较
		 * 嵌套循环，这个算法的时间复杂度为O(n^2)
		 * */
		System.out.println("冒泡排序：");
		int[] i = {12,3,82,9283,0,3};
		int[] sort = sort(i);
		System.out.println(Arrays.toString(sort));

		System.out.println("———————————————————————————————————————————");

		//稀疏数组
		/*
		 * 稀疏数组介绍
		 * 当一个数组中大部分元素是0，或者为同一值得数组时，可以用稀疏数组来保存该数组
		 * 稀疏数组的处理方式：
		 * （1）记录数组有几行几列，有多少个不同的值
		 * （2）把具有不同值的元素和行列以及值记录在一个小规模的数组中
		 * */

		/*
		 * 1.创建一个二维数组11*11，当作棋盘  0：没有棋子  1：黑子  2：白子
		 * 2.创建一个稀疏数组的数组
		 * 3.遍历二维数组，将非零的值存放在稀疏数组中
		 * 4.输出
		 * */
		System.out.println("稀疏数组：");
		int[][] arrayThree = new int[11][11];
		arrayThree[1][2] = 1;
		arrayThree[2][3] = 2;
		getSparseArray(arrayThree);

		System.out.println("———————————————————————————————————————————");

		//数组的基本查找
		//1.遍历数组挨个查找
		//2.二分查找

		//定义一个数组
		System.out.println("遍历数组挨个查找：");
		int[] arrayLookup = {12,3,234,35,4,34,343,66,89};
		//根据该元素去查找第一次该元素出现的索引
		int indexOfArrayLookUP = getIndexArrayLookUp(arrayLookup,234);
		System.out.println(indexOfArrayLookUP);

		//二分查找
		/*
		* 注意：该数组元素必须有序
		* 思想：每一次查找中间的元素，比较大小就能减少一半的元素
		*
		*    最小索引                             中间索引                                                 最大索引
		* minIndex = 0              centerIndex = (minIndex + maxIndex)/2                      maxIndex = array.length - 1
		*    10          20           30           40            50            60            70           80
		*
		* 查找时，我们拿这个元素和中间索引对应的元素去比较
		* 比较之后会有三种情况：
		* 1.寻找的元素正好等于中间索引所对应的元素，则返回中间索引
		* 2.寻找的元素小于中间索引所对应的元素，则改变最大索引maxIndex，改为maxIndex = centerIndex - 1;
		* 3.寻找的元素大于中间索引所对应的元素，则改变最小索引minIndex，改为minIndex = centerIndex + 1;
		*
		* 改变之后要重新计算中间索引
		* 循环去找，直到最小索引等于最大索引(minIndex = maxIndex;)
		* */

		//二分查找
		System.out.println("二分查找：");
		int[] arrayBinarySearch = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,89,222,9999,88988};
		int indexOfArrayBinarySearch = getIndexArrayBinarySearch(arrayBinarySearch,4);
		System.out.println(indexOfArrayBinarySearch);
		indexOfArrayBinarySearch = getIndexArrayBinarySearch(arrayBinarySearch,88988);
		System.out.println(indexOfArrayBinarySearch);

		System.out.println("———————————————————————————————————————————");

		//选择排序
		/*
		* 原理：
		* 从0索引开始，依次和后面的元素相比较，小的元素放前，经过一轮比较后，最小的元素在最小索引处
		* 第n轮比较就从n-1个索引处开始进行比较
		* */
		System.out.println("选择排序：");
		int[] arraySelectionSort = {23,45,13,99,3,44,55};

		//低级的选择排序，当数组中有6个元素时才能使用
		getLowLevelArraySelectionSort(arraySelectionSort);

		System.out.println("——————————————————————————");

		//简便的选择排序
		getAdvancedArraySelectionSort(arraySelectionSort);


		System.out.println("———————————————————————————————————————————");

		//直接插入排序（相当于是增值为1的希尔排序）
		/*
		* 它是将一个记录插入到一个长度为m的有序列表中，使之仍保持有序，他是最简单的排序方法
		* 例：
		* 49,27,37，27,88,388  原始数据
		* [49],27，37,27,88,388  从索引1开始插入
		* [27,49],37,88,388
		* [27,37,49],88,388
		* [27,37,49,88],388
		* [27,37,49,88,388]
		* */

		System.out.println("直接插入排序：");
		int[] arrayInsertionSort = {12,3,4,1,99,455,2,33,44,9,49,00,95,-1,-33,-273};
		getArrayInsertionSort(arrayInsertionSort);


		System.out.println("———————————————————————————————————————————");
		//希尔排序
		/*
		* 希尔排序又称缩小增量排序
		* 思想：先将原表按增量incremental分组，每个子文件按照直接插入法排序。同样，用下一个增量incremental/2将文件再分为子文件，再直接插入法排序。直到incremental = 1时将整个文件排好序
		* 关键：选择合适的增量
		* */
		System.out.println("希尔排序：");
		int[] arrayHillSort = {47,28,398,-29,38,498,43,37};
		int[] arrayHillSortCopy = {47,28,398,-29,38,498,43,37,22,27,848,364,263,3663,33,266,28,393,764,474};
		//只能当数组中只有8个元素时才可使用
		getArrayHillSortComplicated(arrayHillSort);
		getArrayHillSortComplicated(arrayHillSortCopy);
		//随意(普通)
		getArrayHillSortCommon(arrayHillSort);
		getArrayHillSortCommon(arrayHillSortCopy);
		//高效排序
		getArrayHillSortEfficient(arrayHillSort);
		getArrayHillSortEfficient(arrayHillSortCopy);

		System.out.println("———————————————————————————————————————————");

		//快速排序
		/*
		* 分治法：先比较大小，再进行排序
		* 首先从数组中选取一个数作为基准数
		* 分区：比他大或者相等的数放在右边，比它小的数放在左边
		* 再对左右区间重复第二步，直到每个区间只有一个数
		*
		* 实现思路：挖坑填数
		* 1.将基准数挖出形成第一个坑；
		* 2.由后向前找比他小的数，找到后挖出此数填到前一个坑中；
		* 3.由前向后找比它大的数或等于的数，找到后也挖出此数填到前一个坑中
		* 4.将基准数填到最后一个坑里
		* （重复执行2、3）
		* */

		System.out.println("快速排序：");
		//定义一个数组
		int[] arrayQuicksort = {22,3343,646,-4,90,-38,309,-99,-39,-393,229,3847};
		//调用工具类
		getArrayQuickSort(arrayQuicksort,0,arrayQuicksort.length-1);
		System.out.println(Arrays.toString(arrayQuicksort));

		System.out.println("———————————————————————————————————————————");

		//归并排序
		/*归并排序(Merge Sort)就是利用归并的思想实现排序的方法。
		它的原理是假设初始序列有N个记录，则可以看成是N个有序的子序列，每个子序列的长度为1，然后两两归并，
		得到N/2个长度为2或1的有序子序列，再两两归并...如此重复，直至得到一个长度为N的有序序列为止，这种排序方法称为2路归并排序。
		*/

		System.out.println("归并排序：");
		//原始待排序数组
		int[] arrayMergeSort = {12,33,6,44,35,-1,55,45,67,456,567,21,65};

		//拆分
		getArraySplit(arrayMergeSort,0,arrayMergeSort.length-1);

		//归并
		getArrayMerge(arrayMergeSort,0,arrayMergeSort.length/2-1,arrayMergeSort.length-1);

		//输出原数组：
		System.out.println(Arrays.toString(arrayMergeSort));

		System.out.println("———————————————————————————————————————————");

		//基数排序
		/*
		* 基数排序不同于之前的排序基数排序不需要进行对关键字的比较，只需要对关键字进行“分配”与收集两种操作来完成
		* 先比较个位数的大小，再比较十位数的大小，直到最高位
		*
		* */
		System.out.println("基数排序：");
		int[] arrayCardinalitySorting = {-33,-4,-23,236,477,46,58,887,64,645,4838,68,69,647,6768,99999,7777,100000};
		int[] otherArrayCardinalitySorting = {56,766,8676,98,236,477,46,58,887,64,645,4838,68,69,647,6768,99999,7777};
		int[] A=new int[]{73,22, 93, 43, 55, 14, 28, 65, 39, 81};
		getArrayCardinalitySorting(A);
		System.out.println(Arrays.toString(A));
		radixSort(A, 100);
		System.out.println(Arrays.toString(A));
		for (int num:A) {
			System.out.print(num+"\t");
		}
		System.out.println();



		//捕捉异常
		try {
			getArrayCardinalitySorting(arrayCardinalitySorting);
			getArrayCardinalitySorting(otherArrayCardinalitySorting);
			otherRadixSort(arrayCardinalitySorting);
		}catch (Throwable throwable){
			System.out.println("错误！");
			//System.out.println(Arrays.toString(arrayCardinalitySorting));
			//System.out.println(Arrays.toString(arrayCardinalitySorting));
		}


		System.out.println("———————————————————————————————————————————");
		//堆排序
		/*
		* 堆排序
		* 堆排序是利用堆这种数据结构而设计的一种排序算法，堆排序是一种选择排序。
		* 堆排序的基本思想是：
		* 1.将待排序序列构造成一个大顶堆，此时，整个序列的最大值就是堆顶的根节点
		* 2.将其与末尾元素进行交换，此时末尾就为最大值。
		* 3.然后将剩余n-1个元素重新构造成一个堆，这样会得到n个元素的次小值。
		* 4.如此反复执行，便能得到一个有序序列了.
		* 大顶堆：根节点大于所有子节点
		* 小顶堆：根节点小于所有子节点
		* 升序排列用大顶堆
		* 降序排列用小顶堆
		* （顶堆是由完全二叉树转化来的，转成大顶堆，从最后一个非叶子节点开始）
		* */
		System.out.println("堆排序：");
		int[] heapSortingArray = {0,32,776,34,88,232,7878,55,45};
		//定义开始调整的位置
		int startIndex = (heapSortingArray.length-1)/2;
		//循环开始调用
		for (int j = startIndex; j >= 0; j--) {
			toMaxHeap(heapSortingArray,heapSortingArray.length,j);
		}
		System.out.println(Arrays.toString(heapSortingArray));
		//此时数组是大顶堆，把根元素和最后一个元素进行交换
		for (int j = heapSortingArray.length-1; j < 0; j--) {
			//进行调换
			int var = heapSortingArray[0];
			heapSortingArray[0] = heapSortingArray[j];
			heapSortingArray[j] = var;
			//还完之后再把数组调成大顶堆
			toMaxHeap(heapSortingArray,j,0);
		}
		System.out.println(Arrays.toString(heapSortingArray));

		System.out.println("——————————————————————————————————————————————————————————————————————————————————————");
		return new AboutArrays();
	}




	//打印数组元素
	public static void printArray(int[] array){
		for (int i = 0; i < array.length; i++) {
			System.out.print(array[i]+" ");
		}
	}

	//反转数组
	public static int[] reverse(int[]arrays){
		int[] result = new int[arrays.length];
		for (int i = 0,j=result.length-1; i < arrays.length; i++,j--) {
			result[j] = arrays[i];
		}
		return result;
	}

	//打印数组
	public static void printArrays(int[] a){
		for (int i = 0; i < a.length; i++) {
			if (i == 0){
				System.out.print("[");
			}
			if (i == a.length-1){
				System.out.print(a[i]+"]");
			}else {
				System.out.print(a[i]+", ");
			}
		}
	}

	//获取数组的最大元素
	public static int getsTheLargestElementOfTheArray(int[] array){
		int max = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] > max){
				max = array[i];
			}
		}
		return max;
	}

	//稀疏数组
	public static void getSparseArray(int [][] arrayThree){
		//输出原始的数组
		System.out.println("原始数组：");
		for (int[] ints : arrayThree) {
			for (int anInt : ints) {
				System.out.print(anInt+"\t");
			}
			System.out.println();
		}

		System.out.println("—————————————————————");

		//转换为稀疏数组
		//获取有效值的个数
		int sumOne = 0;
		for (int j = 0; j < 11; j++) {
			for (int k = 0; k < 11; k++) {
				if (arrayThree[j][k] != 0){
					sumOne++;
				}
			}
		}
		System.out.println("有效值共有："+sumOne+"个");

		//2.创建一个稀疏数组的数组
		int[][] arrayFour = new int[sumOne+1][3];//sum+1行，3列
		arrayFour[0][0] = 11;//行
		arrayFour[0][1] = 11;//列
		arrayFour[0][2] = sumOne;//有效值

		//3.遍历二维数组，将非零的值存放在稀疏数组中
		int count = 0;
		for (int j = 0; j < arrayThree.length; j++) {
			for (int k = 0; k < arrayThree[j].length; k++) {
				if (arrayThree[j][k] != 0){
					count++;
					arrayFour[count][0] = j;
					arrayFour[count][1] = k;
					arrayFour[count][2] = arrayThree[j][k];
				}
			}
		}
		//4.输出
		for (int j = 0; j < arrayFour.length; j++) {
			System.out.println(arrayFour[j][0]+"\t"+arrayFour[j][1]+"\t"+arrayFour[j][2]+"\t");
		}

		System.out.println("—————————————————————");

		//5.还原
		//（1）读取稀疏数组
		int[][] arrayFive = new int[arrayFour[0][0]][arrayFour[0][1]];
		//（2）给元素还原值
		for (int j = 1; j < arrayFour.length; j++) {
			arrayFive[arrayFour[j][0]][arrayFour[j][1]] = arrayFour[j][2];
		}
		//（3）打印还原
		System.out.println("还原数组：");
		for (int[] ints : arrayFive) {
			for (int anInt : ints) {
				System.out.print(anInt+"\t");
			}
			System.out.println();
		}

	}

	//冒泡排序
	/*
	 * 1.比较数组中两个相邻的元素，如果第一个数比第二个数大，我们就交换他们的位置
	 * 2.每一次比较都会产生出一个最大或最小的数字
	 * 3.下一轮则可以少一次排序
	 * 4.依次循环，直到结束
	 * */
	public static int[] sort(int[] array){
		//临时变量
		int temp = 0;

		//外层循环，判断要循环多少次
		for (int i = 0; i < array.length; i++) {

			boolean flag = false;//通过flag标识位来减少没有意义的比较

			//内层循环，比较两个是，如果第一个数比第二个数大，则交换位置
			for (int j = 0; j < array.length-1-i; j++) {//可以通过更改j < array.length的<>改变顺序
				if (array[j+1] > array[j]){
					temp = array[j];
					array[j] = array[j+1];
					array[j+1] = temp;
					flag = true;
				}
			}
			if (flag == false){
				break;
			}
		}
		return array;
	}

	//遍历数组查找元素
	public static int getIndexArrayLookUp(int[] array,int i) {
		for (int j = 0; j < array.length; j++) {
			if (i == array[j]) {
				return j;
			}
		}
		//没找到元素就返回-1
		return -1;
	}


	//二分查找
	 public static int getIndexArrayBinarySearch(int[] arrayBinarySearch, int i) {
		//定义最小索引，中间索引，最大索引
		int arrayBinarySearchMinIndex = 0;
		int arrayBinarySearchMaxIndex = 0;
		arrayBinarySearchMaxIndex = arrayBinarySearch.length-1;
		int arrayBinarySearchCenterIndex = (arrayBinarySearchMinIndex + arrayBinarySearchMaxIndex)/2;

		while (arrayBinarySearchMinIndex <= arrayBinarySearchMaxIndex){
			//寻找的元素正好等于中间索引所对应的元素
			if (i == arrayBinarySearch[arrayBinarySearchCenterIndex]){
				return arrayBinarySearchCenterIndex;
			} else if (i >= arrayBinarySearchCenterIndex) {
				//寻找的元素大于中间索引所对应的元素，则改变最小索引minIndex，改为minIndex = centerIndex + 1;
				arrayBinarySearchMinIndex = arrayBinarySearchCenterIndex+1;
			} else if (i <= arrayBinarySearchCenterIndex) {
				//寻找的元素小于中间索引所对应的元素，则改变最大索引maxIndex，改为maxIndex = centerIndex - 1;
				arrayBinarySearchMaxIndex = arrayBinarySearchCenterIndex-1;
			}
			//重新计算中间索引
			arrayBinarySearchCenterIndex = (arrayBinarySearchMinIndex + arrayBinarySearchMaxIndex)/2;
		}

		return -1;
	}
	//低级选择排序
	public static void getLowLevelArraySelectionSort(int[] arraySelectionSort){
		//第一轮比较（从0索引处比较）
		int arraySelectionSortIndex = 0;
		for (int j = 1; j < arraySelectionSort.length; j++) {
			if (arraySelectionSort[arraySelectionSortIndex] >arraySelectionSort[j]){
				int arraySelectionSortIndexVariation = arraySelectionSort[arraySelectionSortIndex];
				arraySelectionSort[arraySelectionSortIndex] = arraySelectionSort[j];
				arraySelectionSort[j] = arraySelectionSortIndexVariation;
			}
		}
		System.out.println(Arrays.toString(arraySelectionSort));

		//第二轮比较
		arraySelectionSortIndex = 1;
		for (int j = 1+arraySelectionSortIndex; j < arraySelectionSort.length; j++) {
			if (arraySelectionSort[arraySelectionSortIndex] >arraySelectionSort[j]){
				int arraySelectionSortIndexVariation = arraySelectionSort[arraySelectionSortIndex];
				arraySelectionSort[arraySelectionSortIndex] = arraySelectionSort[j];
				arraySelectionSort[j] = arraySelectionSortIndexVariation;
			}
		}
		System.out.println(Arrays.toString(arraySelectionSort));

		//第三轮比较
		arraySelectionSortIndex = 2;
		for (int j = 1+arraySelectionSortIndex; j < arraySelectionSort.length; j++) {
			if (arraySelectionSort[arraySelectionSortIndex] >arraySelectionSort[j]){
				int arraySelectionSortIndexVariation = arraySelectionSort[arraySelectionSortIndex];
				arraySelectionSort[arraySelectionSortIndex] = arraySelectionSort[j];
				arraySelectionSort[j] = arraySelectionSortIndexVariation;
			}
		}

		System.out.println(Arrays.toString(arraySelectionSort));

		//第四轮比较
		arraySelectionSortIndex = 3;
		for (int j = 1+arraySelectionSortIndex; j < arraySelectionSort.length; j++) {
			if (arraySelectionSort[arraySelectionSortIndex] >arraySelectionSort[j]){
				int arraySelectionSortIndexVariation = arraySelectionSort[arraySelectionSortIndex];
				arraySelectionSort[arraySelectionSortIndex] = arraySelectionSort[j];
				arraySelectionSort[j] = arraySelectionSortIndexVariation;
			}
		}

		System.out.println(Arrays.toString(arraySelectionSort));

		//第五轮比较
		arraySelectionSortIndex = 4;
		for (int j = 1+arraySelectionSortIndex; j < arraySelectionSort.length; j++) {
			if (arraySelectionSort[arraySelectionSortIndex] >arraySelectionSort[j]){
				int arraySelectionSortIndexVariation = arraySelectionSort[arraySelectionSortIndex];
				arraySelectionSort[arraySelectionSortIndex] = arraySelectionSort[j];
				arraySelectionSort[j] = arraySelectionSortIndexVariation;
			}
		}

		System.out.println(Arrays.toString(arraySelectionSort));

		//第六轮比较
		arraySelectionSortIndex = 5;
		for (int j = 1+arraySelectionSortIndex; j < arraySelectionSort.length; j++) {
			if (arraySelectionSort[arraySelectionSortIndex] >arraySelectionSort[j]){
				int arraySelectionSortIndexVariation = arraySelectionSort[arraySelectionSortIndex];
				arraySelectionSort[arraySelectionSortIndex] = arraySelectionSort[j];
				arraySelectionSort[j] = arraySelectionSortIndexVariation;
			}
		}

		System.out.println(Arrays.toString(arraySelectionSort));
	}

	//高级选择排序
	public static void getAdvancedArraySelectionSort(int[] arraySelectionSort){
		int arraySelectionSortIndex = 0;
		for (int simpleArraySelectionSortIndex = 1; simpleArraySelectionSortIndex < arraySelectionSort.length-1; simpleArraySelectionSortIndex++) {
			for (int j = 1+simpleArraySelectionSortIndex; j < arraySelectionSort.length; j++) {
				if (arraySelectionSort[arraySelectionSortIndex] >arraySelectionSort[j]){
					int arraySelectionSortIndexVariation = arraySelectionSort[arraySelectionSortIndex];
					arraySelectionSort[arraySelectionSortIndex] = arraySelectionSort[j];
					arraySelectionSort[j] = arraySelectionSortIndexVariation;
				}
			}
		}
		
		System.out.println(Arrays.toString(arraySelectionSort));
	}


	//插入排序
	//相当于是增值为一的希尔排序
	public static void getArrayInsertionSort(int[] arrayInsertionSort){
		//外层循环定义伦次
		for (int j = 0; j < arrayInsertionSort.length; j++) {
			//里层循环进行比较插入
			int k = j;
			while(k > 0 && arrayInsertionSort[k] < arrayInsertionSort[k-1]){
				int arrayInsertionSortOrderSwap = arrayInsertionSort[k];
				arrayInsertionSort[k] = arrayInsertionSort[k-1];
				arrayInsertionSort[k-1] = arrayInsertionSortOrderSwap;
				k--;
			}
		}

		System.out.println(Arrays.toString(arrayInsertionSort));

		//另一种写法
		for (int i = 1; i < arrayInsertionSort.length; i++) {
			for (int j = i; j > 0; j--) {
				if (arrayInsertionSort[j] < arrayInsertionSort[j-1]){
					int arrayInsertionSortOrderSwap = arrayInsertionSort[j];
					arrayInsertionSort[j] = arrayInsertionSort[j-1];
					arrayInsertionSort[j-1] = arrayInsertionSortOrderSwap;
				}
			}
		}
		System.out.println(Arrays.toString(arrayInsertionSort));

		//调用数组调换方法
		for (int i = 1; i < arrayInsertionSort.length; i++) {
			for (int j = i; j > 0; j--) {
				if (arrayInsertionSort[j] < arrayInsertionSort[j-1]){
					getInsertionSortArraySwap(arrayInsertionSort,j,j-1);
				}
			}
		}
		System.out.println(Arrays.toString(arrayInsertionSort));
	}

	//插入排序的数组调换方法
	public static void getInsertionSortArraySwap(int[] arrayInsertionSort,int i,int j){
		int arrayInsertionSortOrderSwap = arrayInsertionSort[i];
		arrayInsertionSort[i] = arrayInsertionSort[j];
		arrayInsertionSort[j] = arrayInsertionSortOrderSwap;
	}


	//希尔排序
	public static void getArrayHillSortComplicated(int[] arrayHillSort){
		//定义增量
		int incremental = 4;

		//第一轮
		for (int i = incremental; i < arrayHillSort.length; i++) {
			for (int j = i; j > incremental-1; j-=incremental) {
				if (arrayHillSort[j] < arrayHillSort[j-incremental]){
					getInsertionSortArraySwap(arrayHillSort,j,j-incremental);
				}
			}
		}
		System.out.println(Arrays.toString(arrayHillSort));

		//第二轮
		incremental = 2;
		for (int i = incremental; i < arrayHillSort.length; i++) {
			for (int j = i; j > incremental - 1; j -= incremental) {
				if (arrayHillSort[j] < arrayHillSort[j-incremental]){
					getInsertionSortArraySwap(arrayHillSort,j,j-incremental);
				}
			}
		}
		System.out.println(Arrays.toString(arrayHillSort));

		//第三轮
		incremental = 1;
		for (int i = incremental; i < arrayHillSort.length; i++) {
			for (int j = i; j > incremental - 1; j -= incremental) {
				if (arrayHillSort[j] < arrayHillSort[j-incremental]){
					getInsertionSortArraySwap(arrayHillSort,j,j-incremental);
				}
			}
		}
		System.out.println(Arrays.toString(arrayHillSort));
	}

	//希尔排序
	//第一次这个增量选取数组的一半，然后不断的减半
	public static void getArrayHillSortCommon(int[] arrayHillSort){
		for (int incremental = arrayHillSort.length/2; incremental > 0; incremental /= 2) {
			for (int i = incremental; i < arrayHillSort.length; i++) {
				for (int j = i; j > incremental - 1; j -= incremental) {
					if (arrayHillSort[j] < arrayHillSort[j-incremental]){
						getInsertionSortArraySwap(arrayHillSort,j,j-incremental);
					}
				}
			}
		}
		System.out.println(Arrays.toString(arrayHillSort));
	}


	//高效希尔排序
	public static void getArrayHillSortEfficient(int[] arrayHillSort){
		//我们第一次选取数组长度的一半也不是很好，我们可以使用一种克努特（Knuth）排序
		int incrementalCopy = 1;
		incrementalCopy = 3*incrementalCopy+1;
		//1,4,13,40,121,364..............

		//根据克努特列序选取我们第一的增量
		int knuthSequenceArrayInterval = 1;
		while(knuthSequenceArrayInterval <= arrayHillSort.length/3){
			knuthSequenceArrayInterval = knuthSequenceArrayInterval * 3 + 1;
		}
		for (int incremental = knuthSequenceArrayInterval; incremental > 0; incremental  = (incremental - 1) / 3) {
			for (int i = incremental; i < arrayHillSort.length; i++) {
				for (int j = i; j > incremental - 1; j -= incremental) {
					if (arrayHillSort[j] < arrayHillSort[j-incremental]){
						getInsertionSortArraySwap(arrayHillSort,j,j-incremental);
					}
				}
			}
		}
		System.out.println(Arrays.toString(arrayHillSort));
	}

	//快速排序
	public static void getArrayQuickSort(int[]arrayQuickSort,int arrayQuickSortStartNumber,int arrayQuickSortEndNUmber){
		//找出分左右两区的索引位置，然后对左右两区进行递归调用
		if (arrayQuickSortStartNumber < arrayQuickSortEndNUmber){
			int arrayQuickSortIndex = getArrayQuickSortIndex(arrayQuickSort,arrayQuickSortStartNumber,arrayQuickSortEndNUmber);
			//左区递归
			getArrayQuickSort(arrayQuickSort,arrayQuickSortStartNumber,arrayQuickSortEndNUmber-1);
			//右区递归
			getArrayQuickSort(arrayQuickSort,arrayQuickSortStartNumber+1,arrayQuickSortEndNUmber);
		}
	}

	/*实现思路：挖坑填数
	 * 1.将基准数挖出形成第一个坑；
	 * 2.由后向前找比他小的数，找到后挖出此数填到前一个坑中；
	 * 3.由前向后找比它大的数或等于的数，找到后也挖出此数填到前一个坑中
	 * 4.将基准数填到最后一个坑里
	 * （重复执行2、3）
	 * */
	public static int getArrayQuickSortIndex(int[] arrayQuickSort, int arrayQuickSortStartNumber, int arrayQuickSortEndNUmber) {
		int takeOverArrayQuickSortStartNumber = arrayQuickSortStartNumber;
		int takeOverArrayQuickSortEndNUmber = arrayQuickSortEndNUmber;
		int takeOverArrayQuickSortBenchmarkNumber = arrayQuickSort[takeOverArrayQuickSortStartNumber];
		//挖坑填数
		while(takeOverArrayQuickSortStartNumber < takeOverArrayQuickSortEndNUmber){
			//由后向前找比他小的数，找到后挖出此数填到前一个坑中；
			while(takeOverArrayQuickSortStartNumber < takeOverArrayQuickSortEndNUmber && arrayQuickSort[takeOverArrayQuickSortEndNUmber] >= takeOverArrayQuickSortBenchmarkNumber){
				takeOverArrayQuickSortEndNUmber--;
			}
			if (takeOverArrayQuickSortStartNumber < takeOverArrayQuickSortEndNUmber){
				arrayQuickSort[takeOverArrayQuickSortStartNumber] = arrayQuickSort[takeOverArrayQuickSortEndNUmber];
				takeOverArrayQuickSortStartNumber++;
			}

			//由前向后找比它大的数或等于的数，找到后也挖出此数填到前一个坑中
			while(takeOverArrayQuickSortStartNumber < takeOverArrayQuickSortEndNUmber && arrayQuickSort[takeOverArrayQuickSortEndNUmber] < takeOverArrayQuickSortBenchmarkNumber){
				takeOverArrayQuickSortStartNumber++;
			}
			if (takeOverArrayQuickSortStartNumber < takeOverArrayQuickSortEndNUmber){
				arrayQuickSort[takeOverArrayQuickSortEndNUmber] = arrayQuickSort[takeOverArrayQuickSortStartNumber];
				takeOverArrayQuickSortEndNUmber--;
			}
		}

		arrayQuickSort[takeOverArrayQuickSortStartNumber] = takeOverArrayQuickSortBenchmarkNumber;

		//将基准数填到最后一个坑里
		return takeOverArrayQuickSortStartNumber;
		//return takeOverArrayQuickSortEndNUmber;
	}


	//归并排序
	//拆分
	public static void getArraySplit(int[] arrayMergeSort,int arrayMergeSortStartIndex,int arrayMergeSortEndIndex){
		//计算中间索引
		int arrayMergeSortCenterIndex = (arrayMergeSortStartIndex + arrayMergeSortEndIndex)/2;
		if (arrayMergeSortStartIndex < arrayMergeSortEndIndex){
			getArraySplit(arrayMergeSort,arrayMergeSortStartIndex,arrayMergeSortCenterIndex);
			getArraySplit(arrayMergeSort,arrayMergeSortCenterIndex+1,arrayMergeSortEndIndex);
			//归并
			getArrayMerge(arrayMergeSort,arrayMergeSortStartIndex,arrayMergeSortCenterIndex,arrayMergeSortEndIndex);
		}
	}

	//归并
	public static void getArrayMerge(int[] arrayMergeSort,int arrayMergeSortStartIndex,int arrayMergeSortCenterIndex,int arrayMergeSortEndIndex){
		//定义一个临时数组
		int[] temporaryArrayMergeSort = new int[arrayMergeSortEndIndex-arrayMergeSortStartIndex+1];
		//定义左边数组的起始索引
		int leftStartArrayMergeSortIndex = arrayMergeSortStartIndex;
		//定义右边数组的起始索引
		int rightStartArrayMergeSortIndex = arrayMergeSortCenterIndex+1;
		//定义临时数组的起始索引
		int temporaryArrayMergeSortStartIndex = 0;
		//比较左右两个数组的元素大小，往临时数组里放
		while (leftStartArrayMergeSortIndex <= arrayMergeSortCenterIndex && rightStartArrayMergeSortIndex <= arrayMergeSortEndIndex){
			if (arrayMergeSort[leftStartArrayMergeSortIndex] <= arrayMergeSort[rightStartArrayMergeSortIndex]) {
				temporaryArrayMergeSort[temporaryArrayMergeSortStartIndex] = arrayMergeSort[leftStartArrayMergeSortIndex];
				leftStartArrayMergeSortIndex++;
			}else {
				temporaryArrayMergeSort[temporaryArrayMergeSortStartIndex] = arrayMergeSort[rightStartArrayMergeSortIndex];
				rightStartArrayMergeSortIndex++;
			}
			temporaryArrayMergeSortStartIndex++;
		}
		//处理剩余元素
		//左边
		while (leftStartArrayMergeSortIndex <= arrayMergeSortCenterIndex){
			temporaryArrayMergeSort[temporaryArrayMergeSortStartIndex] = arrayMergeSort[leftStartArrayMergeSortIndex];
			leftStartArrayMergeSortIndex++;
			temporaryArrayMergeSortStartIndex++;
		}
		//右边
		while (rightStartArrayMergeSortIndex<=arrayMergeSortEndIndex){
			temporaryArrayMergeSort[temporaryArrayMergeSortStartIndex] = arrayMergeSort[rightStartArrayMergeSortIndex];
			rightStartArrayMergeSortIndex++;
			temporaryArrayMergeSortStartIndex++;
		}

		//将临时数组中的元素取回原数组中
		for (int i = 0; i < temporaryArrayMergeSort.length; i++) {
			arrayMergeSort[i + arrayMergeSortStartIndex] = temporaryArrayMergeSort[i];
		}
		//System.out.println("将原始数组排成有序的：");
		//System.out.println(Arrays.toString(temporaryArrayMergeSort));
	}


	//基数排序(桶排序)
	public static void getArrayCardinalitySorting(int[] arrayCardinalitySorting){
		if (arrayCardinalitySorting == null || arrayCardinalitySorting.length <= 1){
			return;
		}
		int max = arrayCardinalitySorting[0];
		for (int i = 0; i < arrayCardinalitySorting.length; i++) {
			if (arrayCardinalitySorting[i] > max){
				max = arrayCardinalitySorting[i];
			}
		}

		//定义一个二维数组，获取10个容器（桶）
		int[][] temporaryArrayCardinalitySorting = new int[10][arrayCardinalitySorting.length];
		//定义统计数组
		int[] temporaryStatisticsArray = new int[10];
		//获取数组的最大值
		int maxLength=(max+"").length();//得到数组最大值的位数，max+""将整型max转为字符串再利用字符串求长度方法length求max的位数
		//int arrayCardinalitySortingMax = getsTheLargestElementOfTheArray(arrayCardinalitySorting);
		//确定排序轮次
		//int sortTheRounds = String.valueOf(arrayCardinalitySortingMax).length();

		for (int j = 0,variation = 1; j < maxLength; j++,variation*=10) {
			for (int l = 0; l < arrayCardinalitySorting.length; l++) {
				//获取每个位上的数字
				int getArrayBit = arrayCardinalitySorting[l]/variation%10;
				//放到临时数组里
				temporaryArrayCardinalitySorting[getArrayBit][temporaryStatisticsArray[getArrayBit]] = arrayCardinalitySorting[l];
				temporaryStatisticsArray[getArrayBit]++;
			}
			//取出容器里的元素
			int index = 0;
			for (int k = 0; k < temporaryStatisticsArray.length; k++) {
				//判断是否为空元素
				if (temporaryStatisticsArray[k] != 0){
					//遍历数组
					for (int h = 0; h < temporaryStatisticsArray[k]; h++) {
						//从容器中取出元素放回原数组
						arrayCardinalitySorting[index++] = temporaryArrayCardinalitySorting[k][h];
					}
					temporaryStatisticsArray[k] = 0;//清除上一次统计的个数
				}
			}
		}
	}



	public static void radixSort(int[] array,int d) {
		int n=1;//代表位数对应的数：1,10,100...
		int k=0;//保存每一位排序后的结果用于下一位的排序输入
		int length=array.length;
		int[][] bucket=new int[10][length];//排序桶用于保存每次排序后的结果，这一位上排序结果相同的数字放在同一个桶里
		int[] order=new int[length];//用于保存每个桶里有多少个数字
		while(n<d)
		{
			for(int num:array) //将数组array里的每个数字放在相应的桶里
			{
				int digit=(num/n)%10;
				bucket[digit][order[digit]]=num;
				order[digit]++;
			}
			for(int i=0;i<length;i++)//将前一个循环生成的桶里的数据覆盖到原数组中用于保存这一位的排序结果
			{
				if(order[i]!=0)//这个桶里有数据，从上到下遍历这个桶并将数据保存到原数组中
				{
					for(int j=0;j<order[i];j++)
					{
						array[k]=bucket[i][j];
						k++;
					}
				}
				order[i]=0;//将桶里计数器置0，用于下一次位排序
			}
			n*=10;
			k=0;//将k置0，用于下一轮保存位排序结果
		}
	}

	public static void otherRadixSort(int[] arr) {
		int max=arr[0];
		for(int i=1;i<arr.length;i++) {
			if(arr[i]>max) {
				max=arr[i];
			}
		}
		int maxLength=(max+"").length();//得到数组最大值的位数，max+""将整型max转为字符串再利用字符串求长度方法length求max的位数
		int[][] bucket=new int[10][arr.length];//此处10表示10个桶分别为0~9
		int[] bucketElemCount=new int [10];//为了记录每个桶中，实际存放了多少个数据,我们定义一个一维数组来记录各个桶的每次放入的数据个数,比如：bucketElemCount[0],记录的就是bucket[0] 桶的放入数据个数
		for(int i=0,n=1;i<maxLength;i++,n*=10){// n=1 表示处理个位，n=10表示处理十位，n=100表示处理百位 ......
			for(int j=0;j<arr.length;j++) {
				int digit=arr[j]/n%10;//取出每个元素的对应位的值
				bucket[digit][bucketElemCount[digit]]=arr[j];//放入到对应的桶中
				bucketElemCount[digit]++;
			}
			int index=0;
			for(int k=0;k<bucketElemCount.length;k++) {//遍历每一桶，并将桶中的数据，放入到原数组
				for(int l=0;l<bucketElemCount[k];l++) {
					arr[index++]=bucket[k][l];
				}
				bucketElemCount[k]=0;//第i+1轮处理后，需要将每个 bucketElementCounts[k] = 0 即桶中数据置空方便下次使用
			}
		}
	}

	/**
	 *
	 * @param heapSortingArray 要排序的数组
	 * @param size 调整的元素个数
	 * @param index 从哪里开始调整
	 */
	//堆排序
	public static void toMaxHeap(int[] heapSortingArray,int size,int index){
		//获取左右子节点的索引
		int leftNodeIndex = index*2+1;//左节点
		int rightNodeIndex = index*2+2;//右节点
		//查找最大节点所对应的节点
		int maxIndex = index;
		if (leftNodeIndex < size && heapSortingArray[leftNodeIndex] > heapSortingArray[maxIndex]) {
			maxIndex = leftNodeIndex;
		}
		if (rightNodeIndex < size && heapSortingArray[rightNodeIndex] < heapSortingArray[maxIndex]){
			maxIndex = rightNodeIndex;
		}
		//调换位置
		if (maxIndex != index){
			int var = heapSortingArray[maxIndex];
			heapSortingArray[maxIndex] = heapSortingArray[index];
			heapSortingArray[index] = var;
			//调换完之后，可能会影响到下面的子树不是大顶堆，还需要再次调换
			toMaxHeap(heapSortingArray,size,maxIndex);
		}
	}
}


class ObjectOriented {
	//面向对象&面向过程
	/*面向过程思想：（线性思维）
	 * 步骤清晰简单，第一步做什么，第二步做什么······
	 * 面向过程适合处理一些简单的问题
	 * */

	/*面向对象思维：
	 * 物以类聚，分类的思维模式，思考问题首先会解决问题需要哪些分类，然后对这些分类进行单独思考。最后，才对某个分类下的细节进行面向过程的思索
	 * 面向对象适合处理复杂的问题，适合处理需要多人协作的问题
	 *
	 * 对于描述复杂的事物，为了从宏观上把握，从整体上合理分析，我们需要使用面向对象的思路来分析整个系统。但是，具体到微观操作，仍需要面向过程的思路去处理
	 * */

	//属性+方法=类

	/*面向对象
	 * 面向对象编程（Object-Oriented Programming，OOP）
	 * 面向对象的编程的本质就是：以类的方式组织代码，以对象的组织（封装）数据
	 *
	 * 抽象
	 *
	 * 三大特性：
	 * （1）封装
	 * （2）继承
	 * （3）多态
	 *
	 * 从认识的角度考虑是先有对象后又有类。对象，是具体的事物。类是抽象的，是对对象的抽象。
	 * 从代码运行的角度考虑是先有类后有对象。类是对象的模板。
	 *
	 * 创建与初始化对象
	 * 使用new关键字创建对象
	 * 使用new关键字创建的时候，除了分配内存空间之外，还会给创建好的对象进行默认的初始化以及类中构造器的调用
	 * 类中的构造器也称构造方法，是在创建对象的时候必须要调用的，并且构造器有以下两个特点：
	 * （1）必须与类的名字相同
	 * （2）必须没有返回类型，也不能写void
	 * */

	/*
	 * 1.类与对象
	 *   类是一个模板（抽象）；对象是一个具体的实例
	 * 2.方法
	 *   定义以及调用
	 * 3.对应的引用
	 *   引用类型和8大基本类型
	 *   对象是通过引用来操作的：栈————>堆
	 * 4.属性 Field 成员变量
	 *   默认初始化：
	 *     数字：0；0.0
	 *     char：u0000
	 *     boolean：false
	 *     引用：null
	 *   修饰符 属性类型 属性名 = 属性值;
	 * 5.对象的创建和使用
	 *   - 必须使用new关键字创造对象 类名 属性名 = new 类名();
	 *   - 对象的属性  属性名.变量名;
	 *   - 对象的方法  属性名.方法名();
	 * 6.类
	 *   静态的属性  属性
	 *   动态的行为  方法
	 * */

	//属性：字段
	String name;
	int age;

	public String petName;
	public int petAge;

	//属性私有
	private String studentName;//名字
	private char studentGender;//性别
	private int id;//学号
	private int studentAge;//年龄
	//即使new了一个对象也不能直接调用，这时就需要提供一些可以操作这个属性的方法
	//提供一些 public的get、set方法
	//get 获得这个数据
	//set 给数获得的据赋值

	public String getStudentName(){
		return this.studentName;
	}
	public char getStudentGender(){
		return this.studentGender;
	}
	public int getId(){
		return this.id;
	}
	public int getStudentAge(){
		return this.studentAge;
	}
	public void setStudentName(String studentName){
		this.studentName = studentName;
	}
	public void setStudentGender(char studentGender){
		this.studentGender = studentGender;
	}
	public void setId(int id){
		this.id = id;
	}
	public void setStudentAge(int studentAge){
		if (studentAge < 0 || studentAge > 120){
			this.studentAge = 0;
		}else {
			this.studentAge = studentAge;
		}
	}



	public static ObjectOriented ObjectOriented() {
		System.out.println("面向对象：");
		//类，抽象的，实例化

		//类实例化后回返回一个自己的对象
		//study对象就是AboutObject类的具体实例
		ObjectOriented study = new ObjectOriented();
		ObjectOriented studyOne = new ObjectOriented();

		study.name = "Amy";
		study.age = 12;

		studyOne.name = "Nick";
		studyOne.age = 13;

		System.out.println(study.name);
		System.out.println(study.age);

		System.out.println(studyOne.name);
		System.out.println(studyOne.age);

		System.out.println("———————————————————————————————————————————");

		ObjectOriented pet = new ObjectOriented();
		pet.petName = "明";
		pet.petAge = 6;
		System.out.println("宠物名："+pet.petName);
		System.out.println("宠物年龄："+pet.petAge);
		pet.petShout();

		System.out.println("———————————————————————————————————————————");

		//封装（大部分为实行）
		/*
		 * 该露的露，该藏的藏
		 * 我们程序设计要追求”高内聚，低耦合“。高内聚是指类的内部数据操作细节自己完成，不允许外部干涉；低耦合：仅暴露少量的方法给外部调用
		 *
		 * 封装（数据的隐藏）
		 * 通常，应禁止直接访问一个对象中数据的实际表示，而应通过操作接口来访问，这称为信息隐藏
		 *
		 * 属性私有 get/set
		 *
		 * 意义
		 * 1.提高程序的安全性，保护数据
		 * 2.隐藏代码的实现细节
		 * 3.统一接口
		 * 4.增加系统可维护性
		 * */

		ObjectOriented studentOne = new ObjectOriented();
		studentOne.getStudentName();
		studentOne.setStudentName("凌");
		studentOne.setStudentGender('男');
		studentOne.setId(1);
		studentOne.setStudentAge(15);
		System.out.println("姓名："+studentOne.getStudentName());
		System.out.println("性别："+studentOne.getStudentGender());
		System.out.println("学号："+studentOne.getId());
		System.out.println("年龄："+studentOne.getStudentAge());

		System.out.println("—————————————————————");

		ObjectOriented studentTwo = new ObjectOriented();
		studentTwo.getStudentName();
		studentTwo.setStudentName("暎");
		studentTwo.setStudentGender('女');
		studentTwo.setId(2);
		studentTwo.setStudentAge(7887);
		System.out.println("姓名："+studentTwo.getStudentName());
		System.out.println("性别："+studentTwo.getStudentGender());
		System.out.println("学号："+studentTwo.getId());
		System.out.println("年龄："+studentTwo.getStudentAge());

		System.out.println("———————————————————————————————————————————");

		return new ObjectOriented();

	}

	//方法
	public void study(){
		System.out.println(this.name+"：Hello!");
		System.out.println("———————————————————————————————————————————");
	}

	public void petShout(){
		System.out.println("啊！");
	}
}

class AboutClass {
	//一个类即使什么都不写，他也会存在一个方法
	//显示的定义构造器

	//可以初始化一些信息
	String name;
	int age;
	//实例化初始值
	//1.使用new关键字，必须要有构造器
	//2.一般用来初始化值

	//无参构造
	public AboutClass(){
		this.name = "qing";
		this.age = 14;
	}

	//有参构造:一旦定义了有参构造，无参就必须显示定义
	public AboutClass(String name, int age){
		//this.name是上面类本身的，后面的name是方法的参数
		this.name = name;
		this.age = age;
	}
	public static AboutClass ninth(){
		//new 实例化了一个对象
		AboutClass introduce = new AboutClass();
		AboutClass introduceOne = new AboutClass("qing",25);
		System.out.println("姓名："+introduce.name);
		System.out.println("年龄："+introduce.age);
		System.out.println("姓名："+introduceOne.name);
		System.out.println("年龄："+introduceOne.age);

		return new AboutClass();
	}
}
//父类（基类）
//继承
/*
 * 继承的本质是对某一批类的抽象，从而实现对现实世界更好的建模
 * extends的意思是”扩展“，子类是父类的扩展
 * Java只有单继承，没有多继承
 * 继承是类和类之间的关系。除此之外，类和类之间还有依赖、组合、聚合等
 * 继承关系的两个类，一个为子类（派生类），一个为父类（基类），子类继承父类，使用关键字extends
 * 子类和父类之间，从意义上讲应该具有”is a“的关系
 *
 * 子类可以继承父类的所有方法，但是，私有的不能继承（属性和方法都不行）
 * public  公共的
 * default  默认的
 * private  私有的
 * protected  受保护的
 *
 * 在Java中，所有的类都默认继承Object类
 * */

/*
super注意点：
    1. super调用父类的构造方法，必须在构造方法的第一个
    2. super 必须只能出现在子类的方法或者构造方法中！
    3. super和this 不能同时调用构造方法！
this:
    代表的对象不同：
    this:本身调用者这个对象
    super:代表父类对象的应用
 前提
    this：没哟继承也可以使用
    super:只能在继承条件才可以使用
 构造方法
    this()；本类的构造
    super()：父类的构造！
* */
class Human {

	public Human(){
		System.out.println("基类无参构造");
	}

	protected String name = "苏安";
	private int money = 10_0000_0000;

	public static Human human(){

		HumanMen humanMen = new HumanMen();
		humanMen.eat("面包");

		System.out.println("———————————————————————————————————————————");

		HumanWomen name = new HumanWomen();
		name.name("箐");

		System.out.println("———————————————————————————————————————————");

		humanMen.sayThings();

		System.out.println("———————————————————————————————————————————");

		HumanMen parameter = new HumanMen();

		System.out.println("———————————————————————————————————————————");

		Human human = new Human();

		System.out.println("——————————————————————————————————————————————————————————————————————————————————————");

		return human;
	}
	public void eat(String food){
		System.out.println("吃了一个"+food);
	}

	public int getMoney(){
		return this.money;
	}

	public int setMoney(int money){
		return this.money = money;
	}
	public void say(){
		System.out.println("Human");
	}
}
//子类（派生类）
class HumanMen extends Human {
	public HumanMen(){
		//隐藏代码：默认地调用了父类的无参构造
		//调用父类构造器的代码必须放在子类构造器的第一行
		//
		super();
		System.out.println("派生类无参构造");
	}
	public void say(){
		System.out.println("HumanMen");
	}
	//私有的东西无法被继承
	public void sayThings(){
		say();
		this.say();//当前HumanMen类中的public void say()方法
		super.say();//父级Human类中的public void say()方法
	}
}

//子类（派生类）
class HumanWomen extends Human {

	private String name = "焜";

	public void name(String name){
		System.out.println(name);//形参中的name
		System.out.println(this.name);//HumanMen类（本类）中的private name
		System.out.println(super.name);//Human类（父类）中的protected name
	}
}

class Country {
	String area;
	public Country(String area){
	}
}

class China extends Country {
	//父类没有无参构造子类也没有，子类如果要有无参构造，必须调用父类的有参构造,否则会报错：没有可用的默认构造函数
	public China(){
		//super();
		super("北京");
	}
}


/***
 *           _.._        ,------------.
 *        ,'      `.    ( We want you! )
 *       /  __) __` \    `-,----------'
 *      (  (`-`(-')  ) _.-'
 *      /)  \  = /  (
 *     /'    |--' .  \
 *    (  ,---|  `-.)__`
 *     )(  `-.,--'   _`-.
 *    '/,'          (  Uu",
 *     (_       ,    `/,-' )
 *     `.__,  : `-'/  /`--'
 *       |     `--'  |
 *       `   `-._   /
 *        \        (
 *        /\ .      \.  freebuf
 *       / |` \     ,-\
 *      /  \| .)   /   \
 *     ( ,'|\    ,'     :
 *     | \,`.`--"/      }
 *     `,'    \  |,'    /
 *    / "-._   `-/      |
 *    "-.   "-.,'|     ;
 *   /        _/["---'""]
 *  :        /  |"-     '
 *  '           |      /
 *              `      |
 */

//方法重写，和属性无关
/*
重写：需要有继承关系，子类重写父类的方法！
1. 方法名必须相同
2.参数列表列表必须相同
3.修饰符：范围可以扩大但不能缩小： public>Protected>Default>private
4.抛出的异常：范围，可以被缩小，但不能扩大； ClassNotFoundException --> Exception(大)

重写：子类的方法和父类的方法一致，方法体不同

重写方法的作用：
  1.父类的功能子类不一定需要，或者不一定满足
*/
class One extends Two {
	public static One one() {

		System.out.println("方法重写：");

		//静态方法：方法的调用只和定义的数据类型有关（左边）
		//非静态方法：重写
		One oneTest = new One();
		oneTest.test();

		//父类的引用指向了子类
		Two twoTest = new One();//子类重写了父类的方法
		twoTest.test();


		return new One();
	}

	public void test(){
		System.out.println("OneClass");
	}
}

class Zero extends Two{

	public static Zero zero(){

		Zero zeroTest = new Zero();
		zeroTest.test();

		System.out.println("——————————————————————————————————————————————————————————————————————————————————————");

		return new Zero();
	}
	@Override//注解
	public void test(){
		super.test();
		System.out.println("zeroClass");
	}
}

class Two {
	public void test(){
		System.out.println("TwoClass");
	}
}

//多态
/*
* 即同一方法可以根据发送对象的不同而采用多种不同的行为方式
* 一个对象的实际类型是确定的，但是可以指向对象的引用类型有很多
*
* 存在条件：
*   1.有继承关系
*   2.子类重写父类的方法
*   3.父类引用指向子类对象
*
* 多态是方法的多态，属性没有多态
*
1. 多态是方法的多态，属性没有多态
2. 父类和子类，有联系类型转换异常！CLassCastException!
3.存在条件：继承关系，方法需要重写，父类引用指向子类对象！ Father f1 = new Son();
   1. static 方法，属于类，它不属于实例
   2. final 常量；
   3. private方法；
*
* instanceof（类型转换）引用类型，判断一个对象是什么类型（两个类之间是否有继承关系）
* * */

class Person{
	public static Person person(){

		System.out.println("多态：");

		//一个对象的实际类型是确定的
		//可以指向的引用类型不确定
		//new PersonMen();

		//子类能调用的方法都是自己的或者从父类继承的
		PersonMen men = new PersonMen();
		//父类可以指向子类，但是不能直接调用子类的方法
		Person men1 = new PersonMen();//父类的引用指向子类的类型
		Object men2 = new PersonMen();
		Object men3 = new Person();

		men1.say();//没重写方法，子类继承父类的方法；重写方法后，调用子类重写的方法
		men.say();//类型是“PersonMen”的才能调用
		//Object是所有类的父类，所以称其为爷类
		((PersonMen) men1).look();//强制转换为子类的类型(高转低)
		((PersonMen) men2).say();//调用子类重写的say方法
		((Person) men2).say();//new的是子类，调用子类重写的say方法，尽管强制转换为父类
		((PersonMen) men2).look();//调用子类独有的方法look
		((Person) men3).say();//new的是父类，所以调用父类的say方法
		((PersonMen) men2).eat();//子类没重写父类的eat方法，继承了父类的eat方法，所以转换为子类的类型也能调用
		((Person) men3).eat();//直接调用父类的eat方法


		System.out.println("———————————————————————————————————————————");

		return new Person();
	}

	public void say(){
		System.out.println("Person");
	}

	public  void eat(){
		System.out.println("Eating!");
	}
}
class PersonMen extends Person{

	@Override
	public void say() {
		System.out.println("PersonMen");
	}

	public void look() {
		System.out.println("Look!");
	}
}

class Animals{
	public static Animals animals(){

		//System.out.println(X instanceof Y);(能否编译通过)

		Object object = new Cat();

		System.out.println(object instanceof Cat);//ture
		System.out.println(object instanceof Animals);//ture
		System.out.println(object instanceof Object);//ture
		System.out.println(object instanceof Dog);//false
		System.out.println(object instanceof String);//false


		System.out.println("————————————————————————");

		Animals animalsCat = new Cat();

		System.out.println(animalsCat instanceof Cat);//ture
		System.out.println(animalsCat instanceof Animals);//ture
		System.out.println(animalsCat instanceof Object);//ture
		System.out.println(animalsCat instanceof Dog);//false
		//System.out.println(animalsCat instanceof String);//错误

		System.out.println("————————————————————————");

		Cat cat = new Cat();
		System.out.println(cat instanceof Cat);//ture
		System.out.println(cat instanceof Animals);//ture
		System.out.println(cat instanceof Object);//ture
		//System.out.println(Cat instanceof Dog);//错误
		//System.out.println(Cat instanceof String);//错误

		System.out.println("——————————————————————————");

		//类型之间的转换：父  子
		Animals animalsCatGo = new Cat();
		//将父类型的对象转换为子类型的（高转低）[子类转换为父类，可能会丢失自己本来的一些方法]
		//低级的类型【子类】 低级对象名 = (子类)  高级对象名;
		//低级对象名.低级方法名();
		//或
		//((子类) 高级对象名).低级方法名;
		Cat animalsCatGo1 = (Cat) animalsCatGo;
		animalsCatGo1.go();
		//或者
		((Cat) animalsCatGo).go();
		((Cat) animalsCatGo).sleep();

		Dog dog = new Dog();
		dog.woof();
		Animals animalsDog = dog;

		System.out.println("————————————————————————————————————————————————");

		return new Animals();
	}

	public void sleep(){
		System.out.println("sleep");
	}

}

class Cat extends Animals{
	public void go(){
		System.out.println("go");
	}
}

class Dog extends Animals{
	public void woof(){
		System.out.println("woof!woof!");
	}

}
/*
1. 父类引用指向子类的对象
2. 把子类转换为父类，向上转型；
3. 把父类转换为子类，向下转型； 强制转换
4.方便方法的调用，减少重复的代码！
* */


class AboutStatic {

	{//代码块（匿名代码块）
		System.out.println("匿名代码块");
	}

	static {//静态代码块（和类一起加载，最早输出，只执行一次
		// ）
		System.out.println("静态代码块");
	}

	public AboutStatic() {
		System.out.println("构造器");
	}

	public static AboutStatic aboutStatic() {
		AboutStatic teatCodeBlocks = new AboutStatic();
		AboutStatic aboutStatic = new AboutStatic();

		System.out.println(Math.random());
		System.out.println(random());
		System.out.println(Math.PI);
		System.out.println(PI);

		System.out.println("———————————————————————————————————————————————————————————————————————————————————————————");

		return aboutStatic;
	}
}
/*
抽象类
abstract修饰符可以用来修饰方法也可以修饰类，如果修饰方法，那么该方法就是抽象方法；如果修饰类，那么该类就是抽象类。
抽象类中可以没有抽象方法，但是有抽象方法的类一定要声明为抽象类。
抽象类，不能使用new关键字来创建对象，它是用来让子类继承的。
抽象方法，只有方法的声明，没有方法的实现，它是用来让子类实现的。
子类继承抽象类，那么就必须要实现抽象类没有实现的抽象方法，否则该子类也要声明为抽象类。
* */

abstract class AbstractClass {

	//约束，让别人来帮我们实现
	//abstract，抽象方法，只有方法名字，没有方法实现
	public abstract void play();
	//抽象类有构造器
	public AbstractClass(){}

}

//抽象类的所有方法，继承了它的子类，都要去实现它的方法，除非子类也是抽象类
class AbstractClassTest extends AbstractClass {
	public AbstractClassTest abstractClassTest(){
		play();

		System.out.println("————————————————————————————————————————————————");

		AbstractClassTest abstractClassTest = new AbstractClassTest();
		return abstractClassTest;
	}

	@Override
	public void play() {
		System.out.println("抽象类");
	}
}


//接口
/*
接口
普通类：只有具体实现
抽象类：具体实现和规范(抽象方法)都有！
接口：只有规范！
接口就是规范，定义的是一组规则，体现了现实世界中“如果你是...则必须能..…”的思想。如果你是天使，则必须能飞。如果你是汽车，则必须能跑。如果你好人，则必须干掉坏人；如果你是坏人，则必须欺负好人。接口的本质是契约，就像我们人间的法律一样。制定好后大家都遵守。
O0的精髓，是对对象的抽象，最能体现这一点的就是接口。为什么我们讨论设计 模式都只针对具备了抽象能力的语言(比如c++、java、c#等)，就是因为设计模式所研究的，实际上就是如何合理的去抽象。
声明类的关键字是class，声明接口的关键字是interface
*/

interface Interface{
	//接口中的所有定义其实都是抽象的，要有实现类
	public abstract void run();
	void go();
	void add(String name);//增
	void delete(String name);//删
	void update(String name);//改
	void query(String name);//查

}

interface Timer{
	void timer();
	//接口中定义的都是常量
	public static final int age = 18;
}

//类通过implements关键字实现接口
//实现了接口的类，就要实现接口的方法
class InterfaceImpl implements Interface,Timer{
	//接口实现多继承
	public static InterfaceImpl interfaceImpl(){

		System.out.println("接口：");

		InterfaceImpl interfaceImpl = new InterfaceImpl();
		interfaceImpl.add("add");
		interfaceImpl.run();
		interfaceImpl.go();
		interfaceImpl.delete("delete");
		interfaceImpl.query("query");
		interfaceImpl.timer();
		interfaceImpl.update("update");

		System.out.println("————————————————————————————————————————————————————————————————————————————————————————————————");
		return interfaceImpl;
	}

	/**
	 *
	 */
	@Override
	public void run() {
		System.out.println("run");
	}

	/**
	 *
	 */
	@Override
	public void go() {
		System.out.println("go");
	}

	/**
	 * @param name
	 */
	@Override
	public void add(String name) {
		System.out.println(name);
	}

	/**
	 * @param name
	 */
	@Override
	public void delete(String name) {
		System.out.println(name);
	}

	/**
	 * @param name
	 */
	@Override
	public void update(String name) {
		System.out.println(name);
	}

	/**
	 * @param name
	 */
	@Override
	public void query(String name) {
		System.out.println(name);
	}

	/**
	 *
	 */
	@Override
	public void timer() {
		System.out.println("timer");
	}
}



class Outer{


//外部类
/*
内部类
内部类就是在一个类的内部在定义一个类，比如，A类中定义一个B类，那么B类相对A类来说就称为内部类，而A类相对B类来说就是外部类了。
1.成员内部类
2.静态内部类
3.局部内部类
4.匿名内部类
*/

	public static Outer outer(){
		System.out.println("内部类：");
		//内部类
		/*
		* 在一个类的内部再定义一个类
		* 编译之后有独立的字节码文件
		* 内部类可以直接访问外部类的私有成员，而不破坏封装
		* 可以为外部类提供一些内部的功能组件
		* */
		Outer outer = new Outer();
		//通过外部类实例化内部类
		Outer.MemberInnerClass memberInnerClass = outer.new MemberInnerClass();

		MemberInnerClass memberInnerClass1 = new Outer().new MemberInnerClass();
		memberInnerClass1.in();
		memberInnerClass.in();
		System.out.println(outer.id);
		memberInnerClass.getID();

		outer.method();


		System.out.println("————————————————————————————————————————————————");

		//直接创建静态内部类对象
		Outer.StaticInnerClass staticInnerClass = new Outer.StaticInnerClass();
		Outer.StaticInnerClass staticInnerClass1 = new StaticInnerClass();
		//调用静态内部类的方法
		staticInnerClass.in();
		staticInnerClass1.getID();

		System.out.println("————————————————————————————————————————————————");

		//局部内部类的调用，先实例化外部类对象
		Outer partInnerOfOuter = new Outer();
		partInnerOfOuter.method();

		System.out.println("————————————————————————————————————————————————");

		//匿名内部类
		/*没有类名的局部内部类（一切特征与内部类相同）
		* 必须继承一个父类或者实现一个接口
		* 定义类、实现类、创建对象和语法合并，只能创建一个该类的对象
		* 优点：减少代码量
		* 缺点：可读性较差
		* */



		//创建接口类型的属性
		//有名字初始化类，不用将实例保存到变量中(匿名类)
		OuterInterface outerInterfaceTest = new OuterClassTest();
		outerInterfaceTest.test();
		//outerInterfaceTest.run();

		new OuterClassTest().run();


		//实现接口的局部内部类
		class InnerClass implements OuterInterface{

			/**
			 *
			 */
			@Override
			public void test() {
				System.out.println("AnonymousInner");
				System.out.println("实现接口的局部内部类");
			}
		}
		OuterInterface outerInterfaceAnonymousPart = new OuterClassTest();
		outerInterfaceAnonymousPart.test();

		//用匿名内部类优化（相当于创建了一个局部内部类）
		OuterInterface outerInterface = new OuterInterface(){

			//实现接口
			/**
			 *
			 */
			@Override
			public void test() {
				System.out.println("OuterInterfaceTest");
				System.out.println("匿名内部类实现接口");
			}

		};
		outerInterface.test();

		OuterSonClass outerSon = new OuterSonClass(){
			@Override
			public void outerSonClass() {
				super.outerSonClass();
				System.out.println("匿名内部类继承父类");
				OuterSonClass outerSonClass = new OuterSonClass();
				outerSonClass.outerSonClass();
			}
		};
		outerSon.outerSonClass();

		System.out.println("————————————————————————————————————————————————");
		return outer;
	}

	public void out(){
		System.out.println("外部类的方法!");
		System.out.println(this.id);
	}

	private int id;
	private static int staticId;

	private String name;
	//成员内部类
	public class MemberInnerClass {
		/* 成员内部类
		 * 在类的内部定义，与实例变量，实例方法同级别的类
		 * 外部类的一个实例部分，创建内部类对象时，必须依赖外部类对象
		 * 当外部类和内部类存在同名属性时，会优先访问内部类属性
		 * 成员内部类不能定义静态属性
		 * */
		int id = 278127;
		String name = "name";

		//不能定义静态成员
		//private static int age;
		//可以定义静态常量(Java16+,Java8不行)
		//private static final int num;

		public void in(){
			//内部类的属性名和外部类相同,如果要调用外部类的属性需要在前面加上一个    外部类名.this.
			System.out.println(Outer.this.name);//调用外部类的属性
			System.out.println("内部类的方法!");
		}

		//获得外部类的私有属性
		public void getID(){
			System.out.println(id);
		}

	}



	private String staticInnerClassName = "静态内部类name";
	private int staticInnerClassAge = 19;
	//静态内部类
	public static class StaticInnerClass{
		/*静态内部类（相当于一个外部类）
		* 不依赖外部类对象，可以直接创建或通过类名访问，可以声明静态成员
		* 与外部类同级别
		* 只有内部类才能是静态的，一般的类不是静态的
		* */

		private String address = "地区";
		private int age = 18;
		//静态成员
		private static char gender = '男';
		public void in(){
			System.out.println("静态内部类!");
			//创建内部类对象
			Outer outer = new Outer();
			//调用外部类对象的属性
			System.out.println(outer.staticInnerClassName);
			System.out.println(outer.staticInnerClassAge);
			//调用静态内部类的属性和方法
			System.out.println(address);
			System.out.println(age);
			//调用静态内部类的静态属性和方法
			System.out.println(StaticInnerClass.gender);
		}

		public void getID(){
			//System.out.println(id);
			System.out.println(staticId);
		}

	}

	private String partInnerClassName = "泙";
	private int partInnerClassAge = 15;

	//局部内部类
	public void method(){
		/*局部内部类
		* 定义在外部类的方法中，作用范围和创建对象仅限于当前方法
		* 局部内部类访问外部类当前方法中的局部属性时，因无法保证属性的生命周期与自身相同，属性必须修饰为final
		* */

		//定义局部属性
		String address = "关嘉";
		final int height = 168;

		//局部内部类，同局部变量一样，不能有任何访问修饰符
		class PartInnerClass{
			//局部内部类的局部属性
			private int age = 16;
			private char gender = '女';
			public void partIn(){
				System.out.println("局部内部类!");
				//访问外部类的属性（可以直接访问，因为它和局部变量同级别）
				System.out.println(Outer.this.partInnerClassName);
				System.out.println(partInnerClassAge);
				System.out.println("—————————————————————————");
				//调用方法
				encliticMethod();
				System.out.println("—————————————————————————");
				//访问内部类的属性
				System.out.println(this.age);
				System.out.println(gender);
				//访问方法里的局部属性；jdk1.7要求局部属性必须是常量,1.8之后不用，自动就是常量（不能修改）
				System.out.println(address);
				System.out.println(height);

			}
		}
		PartInnerClass partInnerClass = new PartInnerClass();
		partInnerClass.partIn();
	}

	public static void encliticMethod(){
		//定义局部变量
		String address = "关嘉";
		//局部内部类，同局部变量一样，不能有任何访问修饰符
		class PartInnerClass{
			//局部内部类的局部属性
			private int age = 16;
			private char gender = '女';
			//不能定义静态属性
			//private static int weight = 48;

			//可以定义静态常量
			private final static int weight = 48;
			public void partIn(){
				System.out.println("局部内部类!");
				//访问外部类的属性（可以直接访问，因为它和局部变量同级别;静态方法需要实例化外部类）因为局部是行执行完会消失，但是对象不会在堆里消失
				Outer outer = new Outer();
				System.out.println(outer.partInnerClassName);
				System.out.println(outer.partInnerClassAge);
			}
		}
		//创建局部内部类对象
		PartInnerClass partInnerClass = new PartInnerClass();
		partInnerClass.partIn();
	}

}

class OuterSonClass extends Outer{
	public OuterSonClass() {
	}

	public void outerSonClass(){
		System.out.println("继承父类的匿名内部类");
	}
}


class OuterClassTest implements OuterInterface{
	public void run(){
		System.out.println("run");
	}

	/**
	 *
	 */
	@Override
	public void test() {
		System.out.println("Test");
	}
}

interface OuterInterface{
	void test();
}

/*
实际工作中，遇到的情况不可能是非常完美的。比如：你写的某个模块，用户输入不一定符合你的要求、你的程序要打开某个文件，这个文件可能不存在或者文件格式不对，你要读取数据库的数据，数据可能是空的等。我们的程序再跑着，内存或硬盘可能满了。等等。
软件程序在运行过程中，非常可能遇到刚刚提到的这些异常问题，我们叫异常，英文是：Exception，意思是例外。这些，例外情况，或者叫异常，怎么让我们写的程序做出合理的处理。而不至于程序崩溃。
异常指程序运行中出现的不期而至的各种状况，如：文件找不到、网络连接失败、非法参数等。异常发生在程序运行期间，它影响了正常的程序执行流程。


简单分类
要理解Java异常处理是如何工作的，你需要掌握以下三种类型的异常：
检查性异常：最具代表的检查性异常是用户错误或问题引起的异常，这是程序员无法预见的。例如要打开一个不存在文件时，一个异常就发生了，这些异常在编译时不能被简单地忽略。运行时异常：运行时异常是可能被程序员避免的异常。与检查性异常相反，运行时异常可以在编译时被忽略。
错误：错误不是异常，而是脱离程序员控制的问题。错误在代码中通常被忽略。例如，当栈溢出时，一个错误就发生了，它们在编译也检查不到的。


Error
Error类对象由Java虚拟机生成并抛出，大多数错误与代码编写者所执行的操作无关。
Java虚拟机运行错误(Virtual MachineError)，当JVM不再有继续执行操作所需的内存资源时，将出现OutOfMemoryError。这些异常发生时，Java虚拟机(JVM)一般会选择线程终止；
还有发生在虚拟机试图执行应用时，如类定义错误(NoClassDefFoundError)、链接错误(LinkageError)。这些错误是不可查的，因为它们在应用程序的控制和处理能力之外，而且绝大多数是程序运行时不允许出现的状况。


Exception
在Exception分支中有一个重要的子类RuntimeException(运行时异常)
ArrayIndexOutOfBoundsException(数组下标越界)
NullPointerException(空指针异常)
ArithmeticException(算术异常)
MissingResourceException(丢失资源)
ClassNotFoundException(找不到类)等异常，这些异常是不检查异常，程序中可以选择捕获处理，也可以不处理。
这些异常一般是由程序逻辑错误引起的，程序应该从逻辑角度尽可能避免这类异常的发生；
Error和Exception的区别：Error通常是灾难性的致命的错误，是程序无法控制和处理的，当出现这些异常时，Java虚拟机(JVM)一般会选择终止线程；Exception通常情况下是可以被程序处理的，并且在程序中应该尽可能的去处理这些异常。





算术异常类：ArithmeticException

空指针异常类：NullPointerException

类型强制转换异常：ClassCastException

数组负下标异常：NegativeArrayException

数组下标越界异常：ArrayIndexOutOfBoundsException

违背安全原则异常：SecurityException

文件已结束异常：EOFException

文件未找到异常：FileNotFoundException

字符串转换为数字异常：NumberFormatException

操作数据库异常：SQLException

输入输出异常：IOException

方法未找到异常：NoSuchMethodException

java.lang.AbstractMethodError
抽象方法错误。当应用试图调用抽象方法时抛出。

java.lang.AssertionError
断言错。用来指示一个断言失败的情况。

java.lang.ClassCircularityError
类循环依赖错误。在初始化一个类时，若检测到类之间循环依赖则抛出该异常。

java.lang.ClassFormatError
类格式错误。当Java虚拟机试图从一个文件中读取Java类，而检测到该文件的内容不符合类的有效格式时抛出。

java.lang.Error
错误。是所有错误的基类，用于标识严重的程序运行问题。这些问题通常描述一些不应被应用程序捕获的反常情况。

java.lang.ExceptionInInitializerError
初始化程序错误。当执行一个类的静态初始化程序的过程中，发生了异常时抛出。静态初始化程序是指直接包含于类中的static语句段。

java.lang.IllegalAccessError
违法访问错误。当一个应用试图访问、修改某个类的域（Field）或者调用其方法，但是又违反域或方法的可见性声明，则抛出该异常。

java.lang.IncompatibleClassChangeError
不兼容的类变化错误。当正在执行的方法所依赖的类定义发生了不兼容的改变时，抛出该异常。一般在修改了应用中的某些类的声明定义而没有对整个应用重新编译而直接运行的情况下，容易引发该错误。

java.lang.InstantiationError
实例化错误。当一个应用试图通过Java的new操作符构造一个抽象类或者接口时抛出该异常.

java.lang.InternalError
内部错误。用于指示Java虚拟机发生了内部错误。

java.lang.LinkageError
链接错误。该错误及其所有子类指示某个类依赖于另外一些类，在该类编译之后，被依赖的类改变了其类定义而没有重新编译所有的类，进而引发错误的情况。

java.lang.NoClassDefFoundError
未找到类定义错误。当Java虚拟机或者类装载器试图实例化某个类，而找不到该类的定义时抛出该错误。

java.lang.NoSuchFieldError
域不存在错误。当应用试图访问或者修改某类的某个域，而该类的定义中没有该域的定义时抛出该错误。

java.lang.NoSuchMethodError
方法不存在错误。当应用试图调用某类的某个方法，而该类的定义中没有该方法的定义时抛出该错误。

java.lang.OutOfMemoryError
内存不足错误。当可用内存不足以让Java虚拟机分配给一个对象时抛出该错误。

java.lang.StackOverflowError
堆栈溢出错误。当一个应用递归调用的层次太深而导致堆栈溢出时抛出该错误。

java.lang.ThreadDeath
线程结束。当调用Thread类的stop方法时抛出该错误，用于指示线程结束。

java.lang.UnknownError
未知错误。用于指示Java虚拟机发生了未知严重错误的情况。

java.lang.UnsatisfiedLinkError
未满足的链接错误。当Java虚拟机未找到某个类的声明为native方法的本机语言定义时抛出。

java.lang.UnsupportedClassVersionError
不支持的类版本错误。当Java虚拟机试图从读取某个类文件，但是发现该文件的主、次版本号不被当前Java虚拟机支持的时候，抛出该错误。

java.lang.VerifyError
验证错误。当验证器检测到某个类文件中存在内部不兼容或者安全问题时抛出该错误。

java.lang.VirtualMachineError
虚拟机错误。用于指示虚拟机被破坏或者继续执行操作所需的资源不足的情况。

java.lang.ArithmeticException
算术条件异常。譬如：整数除零等。

java.lang.ArrayIndexOutOfBoundsException
数组索引越界异常。当对数组的索引值为负数或大于等于数组大小时抛出。

java.lang.ArrayStoreException
数组存储异常。当向数组中存放非数组声明类型对象时抛出。

java.lang.ClassCastException
类造型异常。假设有类A和B（A不是B的父类或子类），O是A的实例，那么当强制将O构造为类B的实例时抛出该异常。该异常经常被称为强制类型转换异常。

java.lang.ClassNotFoundException
找不到类异常。当应用试图根据字符串形式的类名构造类，而在遍历CLASSPAH之后找不到对应名称的class文件时，抛出该异常。

java.lang.CloneNotSupportedException
不支持克隆异常。当没有实现Cloneable接口或者不支持克隆方法时,调用其clone()方法则抛出该异常。

java.lang.EnumConstantNotPresentException
枚举常量不存在异常。当应用试图通过名称和枚举类型访问一个枚举对象，但该枚举对象并不包含常量时，抛出该异常。

java.lang.Exception
根异常。用以描述应用程序希望捕获的情况。

java.lang.IllegalAccessException
违法的访问异常。当应用试图通过反射方式创建某个类的实例、访问该类属性、调用该类方法，而当时又无法访问类的、属性的、方法的或构造方法的定义时抛出该异常。

java.lang.IllegalMonitorStateException
违法的监控状态异常。当某个线程试图等待一个自己并不拥有的对象（O）的监控器或者通知其他线程等待该对象（O）的监控器时，抛出该异常。

java.lang.IllegalStateException
违法的状态异常。当在Java环境和应用尚未处于某个方法的合法调用状态，而调用了该方法时，抛出该异常。

java.lang.IllegalThreadStateException
违法的线程状态异常。当县城尚未处于某个方法的合法调用状态，而调用了该方法时，抛出异常。

java.lang.IndexOutOfBoundsException
索引越界异常。当访问某个序列的索引值小于0或大于等于序列大小时，抛出该异常。

java.lang.InstantiationException
实例化异常。当试图通过newInstance()方法创建某个类的实例，而该类是一个抽象类或接口时，抛出该异常。

java.lang.InterruptedException
被中止异常。当某个线程处于长时间的等待、休眠或其他暂停状态，而此时其他的线程通过Thread的interrupt方法终止该线程时抛出该异常。

java.lang.NegativeArraySizeException
数组大小为负值异常。当使用负数大小值创建数组时抛出该异常。

java.lang.NoSuchFieldException
属性不存在异常。当访问某个类的不存在的属性时抛出该异常。

java.lang.NoSuchMethodException
方法不存在异常。当访问某个类的不存在的方法时抛出该异常。

java.lang.NullPointerException
空指针异常。当应用试图在要求使用对象的地方使用了null时，抛出该异常。譬如：调用null对象的实例方法、访问null对象的属性、计算null对象的长度、使用throw语句抛出null等等。

java.lang.NumberFormatException
数字格式异常。当试图将一个String转换为指定的数字类型，而该字符串确不满足数字类型要求的格式时，抛出该异常。

java.lang.RuntimeException
运行时异常。是所有Java虚拟机正常操作期间可以被抛出的异常的父类。

java.lang.SecurityException
安全异常。由安全管理器抛出，用于指示违反安全情况的异常。

java.lang.StringIndexOutOfBoundsException
字符串索引越界异常。当使用索引值访问某个字符串中的字符，而该索引值小于0或大于等于序列大小时，抛出该异常。

java.lang.TypeNotPresentException
类型不存在异常。当应用试图以某个类型名称的字符串表达方式访问该类型，但是根据给定的名称又找不到该类型是抛出该异常。该异常与ClassNotFoundException的区别在于该异常是unchecked（不被检查）异常，而ClassNotFoundException是checked（被检查）异常。

java.lang.UnsupportedOperationException
不支持的方法异常。指明请求的方法不被支持情况的异常。

异常
javax.servlet.jsp.JspException: Cannot retrieve mapping for action /Login（/Login是你的action名字）
可能原因
action没有再struts-config.xml中定义，或没有找到匹配的action，例如在JSP文件中使用



*/

//异常处理机制
/*
 * 抛出异常
 * 捕获异常
 *
 * 五个关键字：
 * try，catch，finally，throw，throws
 *
 * */

/*
自定义异常
使用Java内置的异常类可以描述在编程时出现的大部分异常情况。除此之外，用户还可以自定义异常。用户自定义异常类，只需继承Exception类即可。
在程序中使用自定义异常类，大体可分为以下几个步骤：
1.创建自定义异常类。
2.在方法中通过throw关键字抛出异常对象。
3.如果在当前抛出异常的方法中处理异常，可以使用try-catch语句捕获并处理；否则在方法的声明处通过throws关键字指明要抛出给方法调用者的异常，继续进行下一步操作。
4.在出现异常方法的调用者中捕获并处理异常。
*/
class TestException {
	public static TestException testException(){

		int a = 1;
		int b = 0;

		try {//try监控区域
			System.out.println(a/b);
		}catch (ArithmeticException arithmeticException){//catch(想要捕获的异常类型)捕获异常,可以写多个，但只会执行一个，一般把最大的写在最下面
			System.out.println("除数不能为0");
		} catch(Error error){
			System.out.println("除数不能为0");
		}catch(Exception exception){
			System.out.println("除数不能为0");
		}catch (Throwable throwable){
			System.out.println("除数不能为0");
		} finally {//无论出不出异常都会执行，善后工作
			System.out.println("finally");
		}

		//System.out.println(a/b);  ArithmeticException

		try {
			new TestException().a();
		}catch (Throwable throwable){
			System.out.println("错误");
		}finally {
			System.out.println("finally");
		}

		try {
			new TestException().test(1,0);
		} catch (ArithmeticException e) {
			System.out.println("除数不能为0");
		}

		//快捷键Ctrl + Alt + T
		try {
			System.out.println(a/b);
		} catch (Exception e) {
			//System.exit(0);//结束运行
			//throw new RuntimeException(e); 抛出一个新的异常
			//e.printStackTrace();//打印错误的栈信息
		} finally {
			System.out.println("finally");
		}

		System.out.println("————————————————————————————————————————————————");

		return new TestException();
	}


	public void a(){
		b();
	}

	public void b(){
		a();
	}

	//方法解决不了这个异常，就要向上抛出
	public void test(int a,int b) throws ArithmeticException{
		if (b == 0){//主动抛出异常   throw   throws
			System.out.println("除数不能为0");
		}
		System.out.println(a/b);
	}

}

//自定义异常要继承Exception类
class MyException extends Exception{
	//传递数字 < 0
	private int detail;

	public MyException(int a){

		this.detail = a;
	}

	//toString 异常的打印信息
	@Override
	public String toString() {
		return "MyException{" +
				"detail=" + detail +
				'}';
	}
}
class TestMyException{

	public static TestMyException testMyException(){

		try {
			test(-4);
		} catch (MyException e) {
			System.out.println("MyException->"+e);
		}

		System.out.println("————————————————————————————————————————————————");

		return new TestMyException();
	}

	public static void test(int a) throws MyException {
		System.out.println("传递的参数为："+a);

		if (a < 0){
			throw new MyException(a);
		}

		System.out.println("OK!");
	}
	public static void test1(int a){
		if (a < 0){
			try {
				throw new MyException(a);
			} catch (MyException e) {
				throw new RuntimeException(e);
			}
		}
	}

}

class AboutObject{
/*
Object类
超类、基类，所有的类都直接或者间接继承Object类，为于继承树的最顶层
任何类，如果没有写extends显示继承某个类，则都默认直接继承Object类，否则为间接继承
Object类中所定义的方法，是所有对象都具备的方法
Object类型可以存储任何对象
   1.作为参数，可以接受任何对象
   2.作为返回值，可以返回任何对象

类的构造函数
Object()
构造一个新对象。

类的方法

1	protected Object clone()
创建并返回一个对象的拷贝

2	boolean equals(Object obj)
比较两个对象是否相等

3	protected void finalize()
当 GC (垃圾回收器)确定不存在对该对象的有更多引用时，由对象的垃圾回收器调用此方法。

4	Class<?> getClass()
获取对象的运行时对象的类

5	int hashCode()
获取对象的 hash 值

6	void notify()
唤醒在该对象上等待的某个线程

7	void notifyAll()
唤醒在该对象上等待的所有线程

8	String toString()
返回对象的字符串表示形式

9	void wait()
让当前线程进入等待状态。直到其他线程调用此对象的 notify() 方法或 notifyAll() 方法。

10	void wait(long timeout)
让当前线程处于等待(阻塞)状态，直到其他线程调用此对象的 notify() 方法或 notifyAll() 方法，或者超过参数设置的timeout超时时间。

11	void wait(long timeout, int nanos)
与 wait(long timeout) 方法类似，多了一个 nanos 参数，这个参数表示额外时间（以纳秒为单位，范围是 0-999999）。 所以超时的时间还需要加上 nanos 纳秒。。
* */
	public static AboutObject aboutObject(){
		//getClass()方法
		/*
		* public final Class<?> getClass(){}
		* 返回引用中存储的实际对象类型
		* 应用：通常用于判断两个引用中的实际存储对象类型是否一致
		* */
		TestObjectMethod testObjectMethod = new TestObjectMethod("椿",18);
		TestObjectMethod testObjectMethod1 = new TestObjectMethod("槎",19);
		//判断testGetClass和testGetClass1是不是同一个类型
		Class<? extends TestObjectMethod> class1 = testObjectMethod.getClass();
		Class<? extends TestObjectMethod> class2 = testObjectMethod1.getClass();

		if (class1 == class2){
			System.out.println("属于同一类型");
		}else {
			System.out.println("不属于同一个类型");
		}

		System.out.println("————————————————————————————————————————————————");

		//hashCode()方法
		/*
		* public int hashCode(){}
		* 返回该对象的哈希码值
		* 哈希值根据对象生成的地址、字符串或数字用hash算法计算出来的int类型的数值
		* 一般情况下相同对象返回相同的哈希码值
		* */
		System.out.println(testObjectMethod.hashCode());
		System.out.println(testObjectMethod1.hashCode());

		TestObjectMethod testObjectMethod2 = testObjectMethod;
		System.out.println(testObjectMethod2.hashCode());

		System.out.println("————————————————————————————————————————————————");

		//toString()方法
		/*
		* public String toString(){}
		* 返回该对象的字符串表示（表现形式）
		* 可以根据程序需求覆盖该方法，如：展示对象各个属性值
		* */

		//原始的toString方法
		//com.WriteBugsProfessionally.Java.Study.TestObjectMethod@2b193f2d
		//com.WriteBugsProfessionally.Java.Study.TestObjectMethod@355da254

		//重写的toString方法
		/*
		public String toString(){
		    return name+":"+age;
	    }
	    */
		//椿:18
		//槎:19


		//系统生成的toString方法
		System.out.println(testObjectMethod.toString());
		System.out.println(testObjectMethod1.toString());


		System.out.println("————————————————————————————————————————————————");

		//equals()方法:判断两个对象是否相等
		/*
		* public boolean equals(Object obj){}
		* 默认实现为(this == obj),比较两个对象地址是否相同
		* 可进行覆盖，比较两个对象的内容是否相同
		* */

		System.out.println(testObjectMethod.equals(testObjectMethod1));//false
		System.out.println(testObjectMethod.equals(testObjectMethod2));//ture

		TestObjectMethod testObjectMethod3 = new TestObjectMethod("莞",21);
		TestObjectMethod testObjectMethod4 = new TestObjectMethod("莞",21);
		System.out.println(testObjectMethod3.equals(testObjectMethod4));

		System.out.println("————————————————————————————————————————————————");

		//finalize()方法
		/*
		* 当对象被判定为垃圾对象时，由JVM自动调用此方法，用以标记垃圾对象，进入回收队列
		* 垃圾对象：没有有效引用指向此对象，为垃圾对象
		* 垃圾回收：由GC销毁垃圾对象，释放数据存储空间
		* 自动回收机制：JVM的内存耗尽，一次性回收所有垃圾对象
		* 手动回收机制：使用System.gc();通知JVM执行垃圾回收
		* */

		TestObjectMethod testObjectMethodAboutFinalize1 = new TestObjectMethod("菏",15);
		TestObjectMethod testObjectMethodAboutFinalize2 = new TestObjectMethod("洺",18);
		TestObjectMethod testObjectMethodAboutFinalize3 = new TestObjectMethod("苧",16);
		TestObjectMethod testObjectMethodAboutFinalize4 = new TestObjectMethod("柏",23);
		TestObjectMethod testObjectMethodAboutFinalize5 = new TestObjectMethod("晶",17);

		new TestObjectMethod("菏",15);
		new TestObjectMethod("洺",18);
		new TestObjectMethod("苧",16);
		new TestObjectMethod("柏",23);
		new TestObjectMethod("晶",17);
		//回收垃圾对象
		System.gc();
		System.out.println("垃圾回收");

		System.out.println("————————————————————————————————————————————————");
		return new AboutObject();
	}

}
class TestObjectMethod {
	private String name;
	public int age;

	public TestObjectMethod(){

	}
	public TestObjectMethod(String name, int age){
		super();
		this.name = name;
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "TestObjectMethod{" + "name='" + name + '\'' + ", age=" + age + '}';
	}

//	public String toString(){
//		return name+":"+age;
//	}

	/*
	* equals()方法覆盖步骤
	* 比较两个引用是否指向同一个对象
	* 判断obj是否为null
	* 判断两个引用指向的实际对象类型是否一致
	* 强制转换类型
	* 依次比较各个属性值是否相同
	* */

	@Override
	public boolean equals(Object obj) {
		//判断两个对象是否是同一个引用
		if (this == obj){
			return true;
		}

		//判断obj是否为null
		if (obj == null) {
			return false;
		}
		//判断是否为同一类型
		if (this.getClass() == obj.getClass()) {
			//强制类型转换
			TestObjectMethod testObjectMethod = (TestObjectMethod) obj;
			//比较属性值
			if (this.name.equals(testObjectMethod.getName()) && this.age == testObjectMethod.getAge()){
				return true;
			}
		}
		//或
		if (obj instanceof TestObjectMethod){
			//强制类型转换
			TestObjectMethod testObjectMethod = (TestObjectMethod) obj;
			//比较属性值
			if (this.name.equals(testObjectMethod.getName()) && this.age == testObjectMethod.getAge()){
				return true;
			}
		}
		return false;
	}

	//重写finalize()方法
	/**
	 * @throws Throwable
	 */
	@Override
	protected void finalize() throws Throwable {
		System.out.println(this.name+"垃圾对象已被回收！");
	}
}

class AboutJavaWrapperClass {
	//包装类
	/*
	基本数据类型所对应的引用数据类型
	Object类可统一所有数据，包装类的默认值是null
	基本数据类型    包装类型
	byte          Byte
	short         Short
	int           Integer
	long          Long
	float         Float
	double        Double
	boolean       Boolean
	char          Character
	这种由编译器特别支持的包装称为装箱，所以当内置数据类型被当作对象使用的时候，编译器会把内置类型装箱为包装类。相似的，编译器也可以把一个对象拆箱为内置类型。Number 类属于 java.lang 包。
	* */
	public static AboutJavaWrapperClass aboutJavaWrapperClass(){
		//类型转换与装箱、拆箱
		/*
		* 基本数据类型都存储在栈空间里
		* 将基本数据类型从栈里放到堆里，相当于把基本类型转成一个对象，这个过程就叫做装箱
		* 拆箱即相反，将基本数据类型从堆里放到栈里，相当于把一个对象转成基本数据类型
		* 拆箱
		* 修饰符和类型                          方法和说明
		* byte                                byteValue() 返回指定号码作为值 byte ，这可能涉及舍入或截断。
		* abstract double                     doubleValue() 返回指定数字的值为 double ，可能涉及四舍五入。
		* abstract float                      floatValue() 返回指定数字的值为 float ，可能涉及四舍五入。
		* abstract int                        intValue() 返回指定号码作为值 int ，这可能涉及舍入或截断。
		* abstract long                       longValue() 返回指定数字的值为 long ，可能涉及四舍五入或截断。
		* short                               shortValue() 返回指定号码作为值 short ，这可能涉及舍入或截断。
		* */

		/*
		雷诺个性转换也装箱拆箱
		·8种包装类提供不同类型间的转换方式：
		·Number父类中提供的6个共性方法。
		·parseXXX()静态方法。
		·valueOf()静态方法。
		·注意：需保证类型兼容，否则抛出NumberFormatException异常。
		*/
		System.out.println("装箱与拆箱：");
		int num = 10;
		//jdk1.5之前的装箱、拆箱方法
		//装箱
		Integer integer = new Integer(num);
		Integer integer1 = Integer.valueOf(num);
		System.out.println(integer);
		System.out.println(integer1);
		//拆箱
		Integer integer2 = new Integer(100);
		int i = integer2.intValue();
		System.out.println(i);


		//jdk1.5之后支持自动装箱、拆箱
		//自动装箱
		Integer integer3 = num;
		System.out.println(integer3);
		//自动拆箱
		int num1 = integer3;
		System.out.println(num1);

		Integer x = 5;
		x =  x + 10;
		System.out.println(x);
		//当 x 被赋为整型值时，由于x是一个对象，所以编译器要对x进行装箱。然后，为了使x能进行加运算，所以要对x进行拆箱。

		System.out.println("————————————————————————————————————————————————");

		//基本类型与字符串之间的转换
		//1.基本类型转换成字符串
		int s = 100;
		//(1)使用“+”
		String s1 = s+"";
		System.out.println(s1);
		//(2)Integer中的toString()方法
		String s2 = Integer.toString(s);
		String s3 = Integer.toString(s,16);//后面的radix相当于进制
		System.out.println(Integer.toBinaryString(s));//二进制
		System.out.println(Integer.toOctalString(s));//八进制
		System.out.println(Integer.toHexString(s));//十六进制

		System.out.println(s2);
		System.out.println(s3);

		//2，字符串转换为基本类型
		String s4 = "你好啊！";//该字符串不可用
		String s5 = "167";
		//使用Integer中的parseXXX()方法
		try {
			System.out.println(Integer.parseInt(s4));//NumberFormatException数字格式异常
		}catch (Throwable throwable){
			System.out.println("数字格式异常!");
		}
		System.out.println(Integer.parseInt(s5));
		//boolean字符串形式转成基本类型"ture"——>ture,其他为false
		String str = "true";
		boolean b = Boolean.parseBoolean(str);
		System.out.println(b);
		System.out.println(Boolean.parseBoolean(s4));//拿上面的字符串举例

		System.out.println("————————————————————————————————————————————————");

		//Integer缓冲区
		Integer integer4 = new Integer(100);
		Integer integer5 = new Integer(100);
		System.out.println(integer4 == integer5);//false

		Integer integer6 = 100;//自动装箱
		Integer integer7 = 100;
		System.out.println(integer6 == integer7);//true

		Integer integer8 = 200;
		Integer integer9 = 200;//自动装箱
		System.out.println(integer8 == integer9);//false

		System.out.println("————————————————————————————————————————————————");
		return new AboutJavaWrapperClass();

	}

}

class JavaEnum{
	/*Java 枚举(enum)
	Java 枚举是一个特殊的类，一般表示一组常量，比如一年的 4 个季节，一个年的 12 个月份，一个星期的 7 天，方向有东南西北等。
	Java 枚举类使用 enum 关键字来定义，各个常量使用逗号 , 来分割。

例如定义一个颜色的枚举类。*/
	 enum ColorEnum {
		Red,Green,Blue,Black;

		// 构造函数
		ColorEnum()
		{
			System.out.println("Constructor called for : " + this.toString());
		}

		public void colorInfo()
		{
			System.out.println("Universal Color");
		}

	}


	//枚举类中的抽象方法实现，需要枚举类中的每个对象都对其进行实现。
	enum Color{
		RED{
			public String getColor(){//枚举对象实现抽象方法
				return "红色";
			}
		},
		GREEN{
			public String getColor(){//枚举对象实现抽象方法
				return "绿色";
			}
		},
		BLUE{
			public String getColor(){//枚举对象实现抽象方法
				return "蓝色";
			}
		};
		public abstract String getColor();//定义抽象方法
	}


	public static JavaEnum javaEnum(){
		System.out.println("Java枚举：");
		 ColorEnum c1 = ColorEnum.Red;
		System.out.println(c1);
		//每个枚举都是通过 Class 在内部实现的，且所有的枚举值都是 public static final 的。

		//迭代枚举元素
		//可以使用 for 语句来迭代枚举元素：
		for (ColorEnum var:ColorEnum.values()) {
			System.out.println(var);
		}

		//在 switch 中使用枚举类
		//枚举类常应用于 switch 语句中：

		ColorEnum Blue = ColorEnum.Blue;
		switch (Blue){
			case Red:
				System.out.println("Red");
				break;
			case Blue:
				System.out.println("Blue");
				break;
			case Black:
				System.out.println("Black");
				break;
			case Green:
				System.out.println("Green");
				break;
		}


		/*values(), ordinal() 和 valueOf() 方法
		enum 定义的枚举类默认继承了 java.lang.Enum 类，并实现了 java.lang.Serializable 和 java.lang.Comparable 两个接口。
		values(), ordinal() 和 valueOf() 方法位于 java.lang.Enum 类中：

		values() 返回枚举类中所有的值.
		ordinal()方法可以找到每个枚举常量的索引，就像数组索引一样。
		valueOf()方法返回指定字符串值的枚举常量。
		*/

		// 调用 values()
		ColorEnum[] arr = ColorEnum.values();

		// 迭代枚举
		for (ColorEnum col : arr)
		{
			// 查看索引
			System.out.println(col + " at index " + col.ordinal());
		}

		// 使用 valueOf() 返回枚举常量，不存在的会报错 IllegalArgumentException
		System.out.println(ColorEnum.valueOf("Red"));
		// System.out.println(Color.valueOf("WHITE"));

		/*枚举类成员
		枚举跟普通类一样可以用自己的变量、方法和构造函数，构造函数只能使用 private 访问修饰符，所以外部无法调用。
		枚举既可以包含具体方法，也可以包含抽象方法。 如果枚举类具有抽象方法，则枚举类的每个实例都必须实现它。*/

		ColorEnum c2 = ColorEnum.Red;
		System.out.println(c2);
		c2.colorInfo();

		for (Color c:Color.values()){
			System.out.print(c.getColor() + "、");
		}
		System.out.println("\n"+"————————————————————————————————————————————————");
		return new JavaEnum();
	}
	static class ColorClass{
		public static final ColorClass RED = new ColorClass();
		public static final ColorClass BLUE = new ColorClass();
		public static final ColorClass GREEN = new ColorClass();
	}
}
class JavaGenerics{
/*Java 泛型
Java 泛型（generics）是 JDK 5 中引入的一个新特性, 泛型提供了编译时类型安全检测机制，该机制允许程序员在编译时检测到非法的类型。
泛型的本质是参数化类型，也就是说所操作的数据类型被指定为一个参数。
假定我们有这样一个需求：写一个排序方法，能够对整型数组、字符串数组甚至其他任何类型的数组进行排序，该如何实现？
答案是可以使用 Java 泛型。
使用 Java 泛型的概念，我们可以写一个泛型方法来对一个对象数组排序。然后，调用该泛型方法来对整型数组、浮点数数组、字符串数组等进行排序。




泛型方法
你可以写一个泛型方法，该方法在调用时可以接收不同类型的参数。根据传递给泛型方法的参数类型，编译器适当地处理每一个方法调用。

下面是定义泛型方法的规则：

所有泛型方法声明都有一个类型参数声明部分（由尖括号分隔），该类型参数声明部分在方法返回类型之前（在下面例子中的 <E>）。
每一个类型参数声明部分包含一个或多个类型参数，参数间用逗号隔开。一个泛型参数，也被称为一个类型变量，是用于指定一个泛型类型名称的标识符。
类型参数能被用来声明返回值类型，并且能作为泛型方法得到的实际参数类型的占位符。
泛型方法体的声明和其他方法一样。注意类型参数只能代表引用型类型，不能是原始类型（像 int、double、char 等）。
java 中泛型标记符：

E - Element (在集合中使用，因为集合中存放的是元素)
T - Type（Java 类）
K - Key（键）
V - Value（值）
N - Number（数值类型）
？ - 表示不确定的 java 类型



有界的类型参数:
可能有时候，你会想限制那些被允许传递到一个类型参数的类型种类范围。例如，一个操作数字的方法可能只希望接受Number或者Number子类的实例。这就是有界类型参数的目的。
要声明一个有界的类型参数，首先列出类型参数的名称，后跟extends关键字，最后紧跟它的上界。
*/

/*泛型类
泛型类的声明和非泛型类的声明类似，除了在类名后面添加了类型参数声明部分。
和泛型方法一样，泛型类的类型参数声明部分也包含一个或多个类型参数，参数间用逗号隔开。一个泛型参数，也被称为一个类型变量，是用于指定一个泛型类型名称的标识符。因为他们接受一个或多个参数，这些类被称为参数化的类或参数化的类型。


*/


	public static JavaGenerics javaGenerics(){
		// 创建不同类型数组： Integer, Double 和 Character
		Integer[] intArray = { 1, 2, 3, 4, 5 };
		Double[] doubleArray = { 1.1, 2.2, 3.3, 4.4 };
		Character[] charArray = { 'H', 'E', 'L', 'L', 'O' };

		System.out.println( "整型数组元素为:" );
		printArray( intArray  ); // 传递一个整型数组

		System.out.println( "\n双精度型数组元素为:" );
		printArray( doubleArray ); // 传递一个双精度型数组

		System.out.println( "\n字符型数组元素为:" );
		printArray( charArray ); // 传递一个字符型数组

		System.out.println("————————————————————————————————————————————————");

		//有界的参数类型
		System.out.printf( "%d, %d 和 %d 中最大的数为 %d\n\n",
				3, 4, 5, maximum( 3, 4, 5 ) );

		System.out.printf( "%.1f, %.1f 和 %.1f 中最大的数为 %.1f\n\n",
				6.6, 8.8, 7.7, maximum( 6.6, 8.8, 7.7 ) );

		System.out.printf( "%s, %s 和 %s 中最大的数为 %s\n","pear", "apple", "orange", maximum( "pear", "apple", "orange" ) );

		System.out.println("————————————————————————————————————————————————");

		/*类型通配符
		1、类型通配符一般是使用 ? 代替具体的类型参数。例如 List<?> 在逻辑上是 List<String>,List<Integer> 等所有 List<具体类型实参> 的父类。
		2、类型通配符上限通过形如List来定义，如此定义就是通配符泛型值接受Number及其下层子类类型。
		3、类型通配符下限通过形如 List<? super Number> 来定义，表示类型只能接受 Number 及其上层父类类型，如 Object 类型的实例。
		*/
		List<String> name = new ArrayList<String>();
		List<Integer> age = new ArrayList<Integer>();
		List<Number> number = new ArrayList<Number>();

		name.add("icon");
		age.add(18);
		number.add(314);

		getData(name);
		getData(age);
		getData(number);
		//解析： 因为 getData() 方法的参数是 List<?> 类型的，所以 name，age，number1 都可以作为这个方法的实参，这就是通配符的作用。

		List<String> name1 = new ArrayList<String>();
		List<Integer> age1 = new ArrayList<Integer>();
		List<Number> number1 = new ArrayList<Number>();

		name.add("icon");
		age.add(18);
		number1.add(314);

		//getUperNumber(name1);//1
		//getUperNumber(age1);//2
		//getUperNumber(number1);//3
		//解析： 在 //1 处会出现错误，因为 getUperNumber() 方法中的参数已经限定了参数泛型上限为 Number，所以泛型为 String 是不在这个范围之内，所以会报错

		System.out.println("————————————————————————————————————————————————");
		return new JavaGenerics();
	}

	// 泛型方法 printArray
	public static < E > void printArray( E[] inputArray )
	{
		// 输出数组元素
		for ( E element : inputArray ){
			System.out.printf( "%s ", element );
		}
		System.out.println();
	}

	//有界的参数类型
	public static <T extends Comparable<T>> T maximum(T x, T y, T z)
	{
		T max = x; // 假设x是初始最大值
		if ( y.compareTo( max ) > 0 ){
			max = y; //y 更大
		}
		if ( z.compareTo( max ) > 0 ){
			max = z; // 现在 z 更大
		}
		return max; // 返回最大对象
	}


	//类型通配符
	public static void getData(List<?> data) {
		try{
			System.out.println("data :" + data.get(0));
		}catch (Throwable throwable){
			System.out.println("IndexOutOfBoundsException");
		}

	}

	public static void getUperNumber(List<? extends Number> data) {
		try{
			System.out.println("data :" + data.get(0));
		}catch (Throwable throwable){
			System.out.println("IndexOutOfBoundsException");
		}
	}
}


//泛型类
class JavaGenericsClass<T> {

	private T t;

	public void add(T t) {
		this.t = t;
	}

	public T get() {
		return t;
	}

	public static JavaGenericsClass javaGenericsClass()  {
		JavaGenericsClass<Integer> integerJavaGenericsClass = new JavaGenericsClass<Integer>();
		JavaGenericsClass<String> stringJavaGenericsClass = new JavaGenericsClass<String>();

		integerJavaGenericsClass.add(new Integer(10));
		stringJavaGenericsClass.add(new String("文本文本文本文本文本文本文本文本文本文本文本文本"));

		System.out.printf("整型值为 :%d\n\n", integerJavaGenericsClass.get());
		System.out.printf("字符串为 :%s\n", stringJavaGenericsClass.get());

		System.out.println("————————————————————————————————————————————————");
		return new JavaGenericsClass();
	}
}


class NumberAndMath {
/*Java Math 类
Java 的 Math 包含了用于执行基本数学运算的属性和方法，如初等指数、对数、平方根和三角函数。
Math 的方法都被定义为 static 形式，通过 Math 类可以在主函数中直接调用。*/


/*
Math 的 floor,round 和 ceil 方法实例比较
参数	Math.floor	Math.round	Math.ceil
1.4	     1	        1	        2
1.5	     1       	2       	2
1.6	     1        	2        	2
-1.4	-2	       -1          -1
-1.5	-2	       -1	       -1
-1.6	-2	       -2	       -1
*/

/*Number & Math 类方法
Number & Math 类常用的一些方法：
1	xxxValue()
将 Number 对象转换为xxx数据类型的值并返回。
2	compareTo()
将number对象与参数比较。
3	equals()
判断number对象是否与参数相等。
4	valueOf()
返回一个 Number 对象指定的内置数据类型
5	toString()
以字符串形式返回值。
6	parseInt()
将字符串解析为int类型。
7	abs()
返回参数的绝对值。
8	ceil()
返回大于等于( >= )给定参数的的最小整数，类型为双精度浮点型。
9	floor()
返回小于等于（<=）给定参数的最大整数 。
10	rint()
返回与参数最接近的整数。返回类型为double。
11	round()
它表示四舍五入，算法为 Math.floor(x+0.5)，即将原来的数字加上 0.5 后再向下取整，所以，Math.round(11.5) 的结果为12，Math.round(-11.5) 的结果为-11。
12	min()
返回两个参数中的最小值。
13	max()
返回两个参数中的最大值。
14	exp()
返回自然数底数e的参数次方。
15	log()
返回参数的自然数底数的对数值。
16	pow()
返回第一个参数的第二个参数次方。
17	sqrt()
求参数的算术平方根。
18	sin()
求指定double类型参数的正弦值。
19	cos()
求指定double类型参数的余弦值。
20	tan()
求指定double类型参数的正切值。
21	asin()
求指定double类型参数的反正弦值。
22	acos()
求指定double类型参数的反余弦值。
23	atan()
求指定double类型参数的反正切值。
24	atan2()
将笛卡尔坐标转换为极坐标，并返回极坐标的角度值。
25	toDegrees()
将参数转化为角度。
26	toRadians()
将角度转换为弧度。
27	random()
返回一个随机数。*/

	public static NumberAndMath numberAndMath(){

		double[] nums = {1.4, 1.5, 1.6, -1.4, -1.5, -1.6};
		for (double num : nums) {
			test(num);
		}

		System.out.println("————————————————————————————————————————————————");

		System.out.println("90 度的正弦值：" + Math.sin(Math.PI/2));
		System.out.println("0度的余弦值：" + Math.cos(0));
		System.out.println("60度的正切值：" + Math.tan(Math.PI/3));
		System.out.println("1的反正切值： " + Math.atan(1));
		System.out.println("π/2的角度值：" + Math.toDegrees(Math.PI/2));
		System.out.println(Math.PI);


		System.out.println("————————————————————————————————————————————————");
		return new NumberAndMath();
	}
	private static void test(double num) {
		System.out.println("Math.floor(" + num + ")=" + Math.floor(num));
		System.out.println("Math.round(" + num + ")=" + Math.round(num));
		System.out.println("Math.ceil(" + num + ")=" + Math.ceil(num));
	}
}

class JavaOverrideAndOverload {
/*重写(Override)
重写是子类对父类的允许访问的方法的实现过程进行重新编写, 返回值和形参都不能改变。即外壳不变，核心重写！
重写的好处在于子类可以根据需要，定义特定于自己的行为。 也就是说子类能够根据需要实现父类的方法。
重写方法不能抛出新的检查异常或者比被重写方法申明更加宽泛的异常。例如： 父类的一个方法申明了一个检查异常 IOException，但是在重写这个方法的时候不能抛出 Exception 异常，因为 Exception 是 IOException 的父类，抛出 IOException 异常或者 IOException 的子类异常。
在面向对象原则里，重写意味着可以重写任何现有方法。*/

	public static JavaOverrideAndOverload javaOverrideAndOverload() {
		System.out.println("重写与重载：");
		Animal a = new Animal(); // Animal 对象
		Animal b = new Dog(); // Dog 对象

		a.move();// 执行 Animal 类的方法

		b.move();//执行 Dog 类的方法;
		/*在上面的例子中可以看到，尽管 b 属于 Animal 类型，但是它运行的是 Dog 类的 move方法。
		这是由于在编译阶段，只是检查参数的引用类型。
		然而在运行时，Java 虚拟机(JVM)指定对象的类型并且运行该对象的方法。
		因此在上面的例子中，之所以能编译成功，是因为 Animal 类中存在 move 方法，然而运行时，运行的是特定对象的方法。
		*/


		/*方法的重写规则
		参数列表与被重写方法的参数列表必须完全相同。
		返回类型与被重写方法的返回类型可以不相同，但是必须是父类返回值的派生类（java5 及更早版本返回类型要一样，java7 及更高版本可以不同）。
		访问权限不能比父类中被重写的方法的访问权限更低。例如：如果父类的一个方法被声明为 public，那么在子类中重写该方法就不能声明为 protected。
		父类的成员方法只能被它的子类重写。
		声明为 final 的方法不能被重写。
		声明为 static 的方法不能被重写，但是能够被再次声明。
		子类和父类在同一个包中，那么子类可以重写父类所有方法，除了声明为 private 和 final 的方法。
		子类和父类不在同一个包中，那么子类只能够重写父类的声明为 public 和 protected 的非 final 方法。
		重写的方法能够抛出任何非强制异常，无论被重写的方法是否抛出异常。但是，重写的方法不能抛出新的强制性异常，或者比被重写方法声明的更广泛的强制性异常，反之则可以。
		构造方法不能被重写。
		如果不能继承一个类，则不能重写该类的方法。
		*/

		System.out.println("————————————————————————————————————————————————");

		/*重载(Overload)
		重载(overloading) 是在一个类里面，方法名字相同，而参数不同。返回类型可以相同也可以不同。
		每个重载的方法（或者构造函数）都必须有一个独一无二的参数类型列表。
		最常用的地方就是构造器的重载。

		重载规则:
		被重载的方法必须改变参数列表(参数个数或类型不一样)；
		被重载的方法可以改变返回类型；
		被重载的方法可以改变访问修饰符；
		被重载的方法可以声明新的或更广的检查异常；
		方法能够在同一个类中或者在一个子类中被重载。
		无法以返回值类型作为重载函数的区分标准。*/
		Overloading o = new Overloading();
		System.out.println(o.test());
		o.test(1);
		System.out.println(o.test(1,"test3"));
		System.out.println(o.test("test4",1));
		/*重写与重载之间的区别
		区别点	重载方法	重写方法
		参数列表	必须修改	一定不能修改
		返回类型	可以修改	一定不能修改
		异常	    可以修改	可以减少或删除，一定不能抛出新的或者更广的异常
		访问	    可以修改	一定不能做更严格的限制（可以降低限制）

		总结
		方法的重写(Overriding)和重载(Overloading)是java多态性的不同表现，重写是父类与子类之间多态性的一种表现，重载可以理解成多态的具体表现形式。
		(1)方法重载是一个类中定义了多个方法名相同,而他们的参数的数量不同或数量相同而类型和次序不同,则称为方法的重载(Overloading)。
		(2)方法重写是在子类存在方法与父类的方法的名字相同,而且参数的个数与类型一样,返回值也一样的方法,就称为重写(Overriding)。
		(3)方法重载是一个类的多态性表现,而方法重写是子类与父类的一种多态性表现。
		*/

		return new JavaOverrideAndOverload();

	}

	static class Animal {
		public void move() {
			System.out.println("动物可以移动");
		}
	}

	static class Dog extends JavaOverrideAndOverload.Animal {
		public void move() {
			System.out.println("狗可以跑和走");
		}
	}

	public static class Overloading {
		public int test() {
			System.out.println("test1");
			return 1;
		}

		public void test(int a) {
			System.out.println("test2");
		}

		//以下两个参数类型顺序不同
		public String test(int a, String s) {
			System.out.println("test3");
			return "returntest3";
		}

		public String test(String s, int a) {
			System.out.println("test4");
			return "returntest4";
		}
	}
}
class DateTest {
	/*
        DateTest 对象创建以后，可以调用下面的方法。
        boolean after(DateTest date)：若当调用此方法的Date对象在指定日期之后返回true,否则返回false。
        boolean before(DateTest date)：若当调用此方法的Date对象在指定日期之前返回true,否则返回false。
        Object clone()：返回此对象的副本。
        int compareTo(DateTest date)：比较当调用此方法的Date对象和指定日期。两者相等时候返回0。调用对象在指定日期之前则返回负数。调用对象在指定日期之后则返回正数。
        int compareTo(Object obj)：若obj是Date类型则操作等同于compareTo(DateTest) 。否则它抛出ClassCastException。
        boolean equals(Object date)：当调用此方法的Date对象和指定日期相等时候返回true,否则返回false。
        long getTime()：返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此 DateTest 对象表示的毫秒数。
        int hashCode()：返回此对象的哈希码值。
        void setTime(long time)：用自1970年1月1日00:00:00 GMT以后time毫秒数设置时间和日期。
        String toString()：把此 DateTest 对象转换为以下形式的 String： dow mon dd hh:mm:ss zzz yyyy 其中： dow 是一周中的某一天 (Sun, Mon, Tue, Wed, Thu, Fri, Sat)。
        */

	//获取当前日期时间
	public static DateTest date(){
		// 初始化 DateTest 对象
		Date dateTest = new Date();

		// 使用 toString() 函数显示日期时间
		System.out.println(dateTest.toString());


		//使用 SimpleDateFormat 格式化日期
		Date Now = new Date( );
		SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");

		System.out.println("当前时间为: " + ft.format(Now));

		System.out.println("————————————————————————");

		new DateTest().Printf();

		System.out.println("————————————————————————");

		// 初始化 Date 对象
		Date date = new Date();


		//c的使用
		System.out.printf("全部日期和时间信息：%tc%n",date);

		//f的使用
		System.out.printf("年-月-日格式：%tF%n",date);

		//d的使用
		System.out.printf("月/日/年格式：%tD%n",date);

		//r的使用
		System.out.printf("HH:MM:SS PM格式（12时制）：%tr%n",date);

		//t的使用
		System.out.printf("HH:MM:SS格式（24时制）：%tT%n",date);

		//R的使用
		System.out.printf("HH:MM格式（24时制）：%tR",date);

		System.out.println();
		System.out.println("————————————");

		//定义日期格式的转换符可以使日期通过指定的转换符生成新字符串。这些日期转换符如下所示：
		//b的使用，月份简称
		String str=String.format(Locale.US,"英文月份简称：%tb",date);
		System.out.println(str);
		System.out.printf("本地月份简称：%tb%n",date);

		//B的使用，月份全称
		str=String.format(Locale.US,"英文月份全称：%tB",date);
		System.out.println(str);
		System.out.printf("本地月份全称：%tB%n",date);

		//a的使用，星期简称
		str=String.format(Locale.US,"英文星期的简称：%ta",date);

		System.out.println(str);
		//A的使用，星期全称
		System.out.printf("本地星期的简称：%tA%n",date);

		//C的使用，年前两位
		System.out.printf("年的前两位数字（不足两位前面补0）：%tC%n",date);

		//y的使用，年后两位
		System.out.printf("年的后两位数字（不足两位前面补0）：%ty%n",date);

		//j的使用，一年的天数
		System.out.printf("一年中的天数（即年的第几天）：%tj%n",date);

		//m的使用，月份
		System.out.printf("两位数字的月份（不足两位前面补0）：%tm%n",date);

		//d的使用，日（二位，不够补零）
		System.out.printf("两位数字的日（不足两位前面补0）：%td%n",date);

		//e的使用，日（一位不补零）
		System.out.printf("月份的日（前面不补0）：%te",date);

		System.out.println();
		System.out.println("————————————");

		//如果你需要重复提供日期，那么利用这种方式来格式化它的每一部分就有点复杂了。因此，可以利用一个格式化字符串指出要被格式化的参数的索引。
		//索引必须紧跟在%后面，而且必须以$结束。
		// 使用toString()显示日期和时间
		System.out.printf("%1$s %2$tB %2$td, %2$tY", "Due date:", date);

		System.out.println();
		System.out.println("————————————");

		//或者，你可以使用 < 标志。它表明先前被格式化的参数要被再次使用。
		// 显示格式化时间
		System.out.printf("%s %tB %<te, %<tY", "Due date:", date);

		System.out.println();
		System.out.println("————————————");
		//Java 休眠(sleep)
		//sleep()使当前线程进入停滞状态（阻塞当前线程），让出CPU的使用、目的是不让当前线程独自霸占该进程所获的CPU资源，以留一定时间给其他线程执行的机会。
		//你可以让程序休眠一毫秒的时间或者到您的计算机的寿命长的任意段时间。
		//休眠3s
		try {
			System.out.println(new Date( ) + "\n");
			Thread.sleep(1000*3);   // 休眠3秒
			System.out.println(new Date( ) + "\n");
		} catch (Exception e) {
			System.out.println("Got an exception!");
		}

		System.out.println("————————————");

		//测量时间
		//下面的一个例子表明如何测量时间间隔（以毫秒为单位）：
		try {
			long start = System.currentTimeMillis( );
			System.out.println(new Date( ) + "\n");
			Thread.sleep(5*60*10);
			System.out.println(new Date( ) + "\n");
			long end = System.currentTimeMillis( );
			long diff = end - start;
			System.out.println("Difference is : " + diff);
		} catch (Exception e) {
			System.out.println("Got an exception!");
		}

		System.out.println("————————————————————————————————————————————————");
		return new DateTest();
	}

/*日期和时间的格式化编码
时间模式字符串用来指定时间格式。在此模式中，所有的 ASCII 字母被保留为模式字母，定义如下：
字母	描述	示例
G	纪元标记	AD
y	四位年份	2001
M	月份	July or 07
d	一个月的日期	10
h	 A.M./P.M. (1~12)格式小时	12
H	一天中的小时 (0~23)	22
m	分钟数	30
s	秒数	55
S	毫秒数	234
E	星期几	Tuesday
D	一年中的日子	360
F	一个月中第几周的周几	2 (aboutScanner Wed. in July)
w	一年中第几周	40
W	一个月中第几周	1
a	A.M./P.M. 标记	PM
k	一天中的小时(1~24)	24
K	 A.M./P.M. (0~11)格式小时	10
z	时区	Eastern Standard Time
'	文字定界符	Delimiter
"	单引号	`
*/

	public void Printf(){
		/**
		 * 使用printf输出
		 */
		/**关键技术点
		 * 使用java.io.PrintStream的printf方法实现C风格的输出
		 * printf 方法的第一个参数为输出的格式,第二个参数是可变长的,表示待输出的数据对象
		 */
		/*** 输出字符串 ***/
		// %s表示输出字符串，也就是将后面的字符串替换模式中的%s
		System.out.printf("%s", new Integer(1212));
		// %n表示换行
		System.out.printf("%s%n", "end line");
		// 还可以支持多个参数
		System.out.printf("%s = %s%n", "Name", "Zhangsan");
		// %S将字符串以大写形式输出
		System.out.printf("%S = %s%n", "Name", "Zhangsan");
		// 支持多个参数时，可以在%s之间插入变量编号，1$表示第一个字符串，3$表示第3个字符串
		System.out.printf("%1$s = %3$s %2$s%n", "Name", "san", "Zhang");

		/*** 输出boolean类型 ***/
		System.out.printf("true = %b; false = ", true);
		System.out.printf("%b%n", false);

		/*** 输出整数类型***/
		Integer iObj = 342;
		// %d表示将整数格式化为10进制整数
		System.out.printf("%d; %d; %d%n", -500, 2343L, iObj);
		// %o表示将整数格式化为8进制整数
		System.out.printf("%o; %o; %o%n", -500, 2343L, iObj);
		// %x表示将整数格式化为16进制整数
		System.out.printf("%x; %x; %x%n", -500, 2343L, iObj);
		// %X表示将整数格式化为16进制整数，并且字母变成大写形式
		System.out.printf("%X; %X; %X%n", -500, 2343L, iObj);

		/*** 输出浮点类型***/
		Double dObj = 45.6d;
		// %e表示以科学技术法输出浮点数
		System.out.printf("%e; %e; %e%n", -756.403f, 7464.232641d, dObj);
		// %E表示以科学技术法输出浮点数，并且为大写形式
		System.out.printf("%E; %E; %E%n", -756.403f, 7464.232641d, dObj);
		// %f表示以十进制格式化输出浮点数
		System.out.printf("%f; %f; %f%n", -756.403f, 7464.232641d, dObj);
		// 还可以限制小数点后的位数
		System.out.printf("%.1f; %.3f; %f%n", -756.403f, 7464.232641d, dObj);

		/*** 输出日期类型***/
		// %t表示格式化日期时间类型，%T是时间日期的大写形式，在%t之后用特定的字母表示不同的输出格式
		Date dateTest = new Date();
		long dataL = dateTest.getTime();
		// 格式化年月日
		// %t之后用y表示输出日期的年份（2位数的年，如99）
		// %t之后用m表示输出日期的月份，%t之后用d表示输出日期的日号
		System.out.printf("%1$ty-%1$tm-%1$td; %2$ty-%2$tm-%2$td%n", dateTest, dataL);
		// %t之后用Y表示输出日期的年份（4位数的年），
		// %t之后用B表示输出日期的月份的完整名， %t之后用b表示输出日期的月份的简称
		System.out.printf("%1$tY-%1$tB-%1$td; %2$tY-%2$tb-%2$td%n", dateTest, dataL);

		// 以下是常见的日期组合
		// %t之后用D表示以 "%tm/%td/%ty"格式化日期
		System.out.printf("%1$tD%n", dateTest);
		//%t之后用F表示以"%tY-%tm-%td"格式化日期
		System.out.printf("%1$tF%n", dateTest);

		/*** 输出时间类型***/
		// 输出时分秒
		// %t之后用H表示输出时间的时（24进制），%t之后用I表示输出时间的时（12进制），
		// %t之后用M表示输出时间的分，%t之后用S表示输出时间的秒
		System.out.printf("%1$tH:%1$tM:%1$tS; %2$tI:%2$tM:%2$tS%n", dateTest, dataL);
		// %t之后用L表示输出时间的秒中的毫秒
		System.out.printf("%1$tH:%1$tM:%1$tS %1$tL%n", dateTest);
		// %t之后p表示输出时间的上午或下午信息
		System.out.printf("%1$tH:%1$tM:%1$tS %1$tL %1$tp%n", dateTest);

		// 以下是常见的时间组合
		// %t之后用R表示以"%tH:%tM"格式化时间
		System.out.printf("%1$tR%n", dateTest);
		// %t之后用T表示以"%tH:%tM:%tS"格式化时间
		System.out.printf("%1$tT%n", dateTest);
		// %t之后用r表示以"%tI:%tM:%tS %Tp"格式化时间
		System.out.printf("%1$tr%n", dateTest);

		/*** 输出星期***/
		// %t之后用A表示得到星期几的全称
		System.out.printf("%1$tF %1$tA%n", dateTest);
		// %t之后用a表示得到星期几的简称
		System.out.printf("%1$tF %1$ta%n", dateTest);

		// 输出时间日期的完整信息
		System.out.printf("%1$tc%n", dateTest);
		/**
		 *printf方法中,格式为"%s"表示以字符串的形式输出第二个可变长参数的第一个参数值;
		 *格式为"%n"表示换行;格式为"%S"表示将字符串以大写形式输出;在"%s"之间用"n$"表示
		 *输出可变长参数的第n个参数值.格式为"%b"表示以布尔值的形式输出第二个可变长参数
		 *的第一个参数值.
		 */
		/**
		 * 格式为"%d"表示以十进制整数形式输出;"%o"表示以八进制形式输出;"%x"表示以十六进制
		 * 输出;"%X"表示以十六进制输出,并且将字母(A、B、C、D、E、F)换成大写.格式为"%e"表
		 * 以科学计数法输出浮点数;格式为"%E"表示以科学计数法输出浮点数,而且将e大写;格式为
		 * "%f"表示以十进制浮点数输出,在"%f"之间加上".n"表示输出时保留小数点后面n位.
		 */
		/**
		 * 格式为"%t"表示输出时间日期类型."%t"之后用y表示输出日期的二位数的年份(如99)、用m
		 * 表示输出日期的月份,用d表示输出日期的日号;"%t"之后用Y表示输出日期的四位数的年份
		 * (如1999)、用B表示输出日期的月份的完整名,用b表示输出日期的月份的简称."%t"之后用D
		 * 表示以"%tm/%td/%ty"的格式输出日期、用F表示以"%tY-%tm-%td"的格式输出日期.
		 */
		/**
		 * "%t"之后用H表示输出时间的时(24进制),用I表示输出时间的时(12进制),用M表示输出时间
		 * 分,用S表示输出时间的秒,用L表示输出时间的秒中的毫秒数、用 p 表示输出时间的是上午还是
		 * 下午."%t"之后用R表示以"%tH:%tM"的格式输出时间、用T表示以"%tH:%tM:%tS"的格式输出
		 * 时间、用r表示以"%tI:%tM:%tS %Tp"的格式输出时间.
		 */
		/**
		 * "%t"之后用A表示输出日期的全称,用a表示输出日期的星期简称.
		 */
	}

}

class TestCalendar{
/*alendar类对象字段类型
Calendar类中用以下这些常量表示不同的意义，jdk内的很多类其实都是采用的这种思想

常量	描述
Calendar.YEAR	年份
Calendar.MONTH	月份
Calendar.DATE	日期
Calendar.DAY_OF_MONTH	日期，和上面的字段意义完全相同
Calendar.HOUR	12小时制的小时
Calendar.HOUR_OF_DAY	24小时制的小时
Calendar.MINUTE	分钟
Calendar.SECOND	秒
Calendar.DAY_OF_WEEK	星期几
*/

	public static TestCalendar testCalendar(){
//		Calendar类的功能要比Date类强大很多，而且在实现方式上也比Date类要复杂一些。
//   	Calendar类是一个抽象类，在实际使用时实现特定的子类的对象，创建对象的过程对程序员来说是透明的，只需要使用getInstance方法创建即可。
		//创建一个代表系统当前日期的Calendar对象
		Calendar c = Calendar.getInstance();//默认是当前日期
		//创建一个指定日期的Calendar对象
		//使用Calendar类代表特定的时间，需要首先创建一个Calendar的对象，然后再设定该对象中的年月日参数来完成。

		// 创建一个代表2009年6月12日的Calendar对象
		Calendar c1 = Calendar.getInstance();
		c1.set(2009, 6 - 1, 12);
		// 获得年份
		int year = c1.get(Calendar.YEAR);
		System.out.print(year+".");
		// 获得月份
		int month = c1.get(Calendar.MONTH) + 1;
		System.out.print(month+".");
		// 获得日期
		int date = c1.get(Calendar.DATE);
		System.out.print(date+".");
		// 获得小时
		int hour = c1.get(Calendar.HOUR_OF_DAY);
		System.out.print(hour+".");
		// 获得分钟
		int minute = c1.get(Calendar.MINUTE);
		System.out.print(minute+".");
		// 获得秒
		int second = c1.get(Calendar.SECOND);
		System.out.print(second);
		// 获得星期几（注意（这个与Date类是不同的）：1代表星期日、2代表星期1、3代表星期二，以此类推）
		int day = c1.get(Calendar.DAY_OF_WEEK);
		System.out.print(" 星期："+day);

		System.out.println();
		System.out.println("————————————————————————");

		//把c1对象的日期加上10，也就是c1也就表示为10天后的日期，其它所有的数值会被重新计算
		c1.add(Calendar.DATE, 10);

		//把c1对象的日期减去10，也就是c1也就表示为10天前的日期，其它所有的数值会被重新计算
		c1.add(Calendar.DATE, -10);

		// —————————————————————————————————————————————————————————————————————————————————————————————————————

		System.out.println("————————————————————————————————————————————————");
		return new TestCalendar();
	}
	public final void set(int year,int month,int date){
		Calendar c1 = Calendar.getInstance();
		c1.set(2009, 6, 12);//把Calendar对象c1的年月日分别设这为：2009、6、12
		System.out.println(c1);

	}

	//利用字段类型设置
	//如果只设定某个字段，例如日期的值，则可以使用如下set方法：
	public void set(int field,int value){
		Calendar c1 = Calendar.getInstance();

		//把 c1对象代表的日期设置为10号，其它所有的数值会被重新计算
		c1.set(Calendar.DATE,10);
		System.out.println(c1);

		//把c1对象代表的年份设置为2008年，其他的所有数值会被重新计算
		c1.set(Calendar.YEAR,2008);
		System.out.println(c1);
	}
}
class TestGregorianCalendar{
/*GregorianCalendar类
Calendar类实现了公历日历，GregorianCalendar是Calendar类的一个具体实现。
Calendar 的getInstance（）方法返回一个默认用当前的语言环境和时区初始化的GregorianCalendar对象。GregorianCalendar定义了两个字段：AD和BC。这是代表公历定义的两个时代。


构造函数和说明
GregorianCalendar()
在具有默认语言环境的默认时区内使用当前时间构造一个默认的 GregorianCalendar。

GregorianCalendar(int year, int month, int date)
在具有默认语言环境的默认时区内构造一个带有给定日期设置的 GregorianCalendar

GregorianCalendar(int year, int month, int date, int hour, int minute)
为具有默认语言环境的默认时区构造一个具有给定日期和时间设置的 GregorianCalendar。

GregorianCalendar(int year, int month, int date, int hour, int minute, int aboutScanner)
为具有默认语言环境的默认时区构造一个具有给定日期和时间设置的 GregorianCalendar。

GregorianCalendar(Locale aLocale)
在具有给定语言环境的默认时区内构造一个基于当前时间的 GregorianCalendar。

GregorianCalendar(TimeZone zone)
在具有默认语言环境的给定时区内构造一个基于当前时间的 GregorianCalendar。

GregorianCalendar(TimeZone zone, Locale aLocale)
在具有给定语言环境的给定时区内构造一个基于当前时间的 GregorianCalendar。





方法和说明
void add(int field, int amount)
根据日历规则，将指定的（有符号的）时间量添加到给定的日历字段中。

protected void computeFields()
转换UTC毫秒值为时间域值

protected void computeTime()
覆盖Calendar ，转换时间域值为UTC毫秒值

boolean equals(Object obj)
比较此 GregorianCalendar 与指定的 Object。

int get(int field)
获取指定字段的时间值

int getActualMaximum(int field)
返回当前日期，给定字段的最大值

int getActualMinimum(int field)
返回当前日期，给定字段的最小值

int getGreatestMinimum(int field)
 返回此 GregorianCalendar 实例给定日历字段的最高的最小值。

Date getGregorianChange()
获得格里高利历的更改日期。

int getLeastMaximum(int field)
返回此 GregorianCalendar 实例给定日历字段的最低的最大值

int getMaximum(int field)
返回此 GregorianCalendar 实例的给定日历字段的最大值。

Date getTime()
获取日历当前时间。

long getTimeInMillis()
获取用长整型表示的日历的当前时间

TimeZone getTimeZone()
获取时区。

int getMinimum(int field)
返回给定字段的最小值。

int hashCode()
重写hashCode.

boolean isLeapYear(int year)
确定给定的年份是否为闰年。

void roll(int field, boolean up)
在给定的时间字段上添加或减去（上/下）单个时间单元，不更改更大的字段。

void set(int field, int value)
用给定的值设置时间字段。

void set(int year, int month, int date)
设置年、月、日的值。

void set(int year, int month, int date, int hour, int minute)
设置年、月、日、小时、分钟的值。

void set(int year, int month, int date, int hour, int minute, int aboutScanner)
设置年、月、日、小时、分钟、秒的值。

void setGregorianChange(Date date)
设置 GregorianCalendar 的更改日期。

void setTime(Date date)
用给定的日期设置Calendar的当前时间。

void setTimeInMillis(long millis)
用给定的long型毫秒数设置Calendar的当前时间。

void setTimeZone(TimeZone value)
用给定时区值设置当前时区。

String toString()


*/
	public static TestGregorianCalendar testGregorianCalendar (){
		String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

		int year;
		// 初始化 Gregorian 日历
		// 使用当前时间和日期
		// 默认为本地时间和时区
		GregorianCalendar gcalendar = new GregorianCalendar();
		// 显示当前时间和日期的信息
		System.out.print("Date: ");
		System.out.print(months[gcalendar.get(Calendar.MONTH)]);
		System.out.print(" " + gcalendar.get(Calendar.DATE) + " ");
		System.out.println(year = gcalendar.get(Calendar.YEAR));
		System.out.print("Time: ");
		System.out.print(gcalendar.get(Calendar.HOUR) + ":");
		System.out.print(gcalendar.get(Calendar.MINUTE) + ":");
		System.out.println(gcalendar.get(Calendar.SECOND));

		// 测试当前年份是否为闰年
		if(gcalendar.isLeapYear(year)) {
			System.out.println("当前年份是闰年");
		}
		else {
			System.out.println("当前年份不是闰年");
		}
		System.out.println("————————————————————————————————————————————————");
		return  new TestGregorianCalendar();
	}
}

class TestRegularExpression{
//正则表达式
/*Java 正则表达式
正则表达式定义了字符串的模式。
正则表达式可以用来搜索、编辑或处理文本。
正则表达式并不仅限于某一种语言，但是在每种语言中有细微的差别。

正则表达式实例
一个字符串其实就是一个简单的正则表达式，例如 Hello World 正则表达式匹配 "Hello World" 字符串。
.（点号）也是一个正则表达式，它匹配任何一个字符如："a" 或 "1"。
下表列出了一些正则表达式的实例及描述：
正则表达式				描述
this is text        匹配字符串 "this is text"

this\s+is\s+text    注意字符串中的 \s+。
                    匹配单词 "this" 后面的 \s+ 可以匹配多个空格，之后匹配 is 字符串，再之后 \s+ 匹配多个空格然后再跟上 text 字符串。
                    可以匹配这个实例：this is text

^\d+(\.\d+)?        ^ 定义了以什么开始
                    \d+ 匹配一个或多个数字
                    ? 设置括号内的选项是可选的
                    \. 匹配 "."
                    可以匹配的实例："5", "1.5" 和 "2.21"。

Java 正则表达式和 Perl 的是最为相似的。
java.util.regex 包主要包括以下三个类：

Pattern 类：
pattern 对象是一个正则表达式的编译表示。Pattern 类没有公共构造方法。要创建一个 Pattern 对象，你必须首先调用其公共静态编译方法，它返回一个 Pattern 对象。该方法接受一个正则表达式作为它的第一个参数。

Matcher 类：
Matcher 对象是对输入字符串进行解释和匹配操作的引擎。与Pattern 类一样，Matcher 也没有公共构造方法。你需要调用 Pattern 对象的 matcher 方法来获得一个 Matcher 对象。

PatternSyntaxException：
PatternSyntaxException 是一个非强制异常类，它表示一个正则表达式模式中的语法错误。
*/







/*正则表达式语法
在其他语言中，\\ 表示：我想要在正则表达式中插入一个普通的（字面上的）反斜杠，请不要给它任何特殊的意义。

在 Java 中，\\ 表示：我要插入一个正则表达式的反斜线，所以其后的字符具有特殊的意义。

所以，在其他的语言中（如 Perl），一个反斜杠 \ 就足以具有转义的作用，而在 Java 中正则表达式中则需要有两个反斜杠才能被解析为其他语言中的转义作用。也可以简单的理解在 Java 的正则表达式中，两个 \\ 代表其他语言中的一个 \，这也就是为什么表示一位数字的正则表达式是 \\d，而表示一个普通的反斜杠是 \\。

System.out.print("\\");    // 输出为 \
System.out.print("\\\\");  // 输出为 \\

\
将下一字符标记为特殊字符、文本、反向引用或八进制转义符。例如， n匹配字符 n。\n 匹配换行符。序列 \\\\ 匹配 \\ ，\\( 匹配 (。

^
匹配输入字符串开始的位置。如果设置了 RegExp 对象的 Multiline 属性，^ 还会与"\n"或"\r"之后的位置匹配。

$
匹配输入字符串结尾的位置。如果设置了 RegExp 对象的 Multiline 属性，$ 还会与"\n"或"\r"之前的位置匹配。

*
零次或多次匹配前面的字符或子表达式。例如，zo* 匹配"z"和"zoo"。* 等效于 {0,}。

+
一次或多次匹配前面的字符或子表达式。例如，"zo+"与"zo"和"zoo"匹配，但与"z"不匹配。+ 等效于 {1,}。

?
零次或一次匹配前面的字符或子表达式。例如，"do(es)?"匹配"do"或"does"中的"do"。? 等效于 {0,1}。

{n}
n 是非负整数。正好匹配 n 次。例如，"o{2}"与"Bob"中的"o"不匹配，但与"food"中的两个"o"匹配。

{n,}
n 是非负整数。至少匹配 n 次。例如，"o{2,}"不匹配"Bob"中的"o"，而匹配"foooood"中的所有 o。"o{1,}"等效于"o+"。"o{0,}"等效于"o*"。

{n,m}
m 和 n 是非负整数，其中 n <= m。匹配至少 n 次，至多 m 次。例如，"o{1,3}"匹配"fooooood"中的头三个 o。'o{0,1}' 等效于 'o?'。注意：您不能将空格插入逗号和数字之间。

?
当此字符紧随任何其他限定符（*、+、?、{n}、{n,}、{n,m}）之后时，匹配模式是"非贪心的"。"非贪心的"模式匹配搜索到的、尽可能短的字符串，而默认的"贪心的"模式匹配搜索到的、尽可能长的字符串。例如，在字符串"oooo"中，"o+?"只匹配单个"o"，而"o+"匹配所有"o"。

.
匹配除"\r\n"之外的任何单个字符。若要匹配包括"\r\n"在内的任意字符，请使用诸如"[\s\S]"之类的模式。

(pattern)
匹配 pattern 并捕获该匹配的子表达式。可以使用 $0…$9 属性从结果"匹配"集合中检索捕获的匹配。若要匹配括号字符 ( )，请使用"\("或者"\)"。

(?:pattern)
匹配 pattern 但不捕获该匹配的子表达式，即它是一个非捕获匹配，不存储供以后使用的匹配。这对于用"or"字符 (|) 组合模式部件的情况很有用。例如，'industr(?:y|ies) 是比 'industry|industries' 更经济的表达式。

(?=pattern)

执行正向预测先行搜索的子表达式，该表达式匹配处于匹配 pattern 的字符串的起始点的字符串。它是一个非捕获匹配，即不能捕获供以后使用的匹配。例如，'Windows (?=95|98|NT|2000)' 匹配"Windows 2000"中的"Windows"，但不匹配"Windows 3.1"中的"Windows"。预测先行不占用字符，即发生匹配后，下一匹配的搜索紧随上一匹配之后，而不是在组成预测先行的字符后。

(?!pattern)
执行反向预测先行搜索的子表达式，该表达式匹配不处于匹配 pattern 的字符串的起始点的搜索字符串。它是一个非捕获匹配，即不能捕获供以后使用的匹配。例如，'Windows (?!95|98|NT|2000)' 匹配"Windows 3.1"中的 "Windows"，但不匹配"Windows 2000"中的"Windows"。预测先行不占用字符，即发生匹配后，下一匹配的搜索紧随上一匹配之后，而不是在组成预测先行的字符后。

x|y
匹配 x 或 y。例如，'z|food' 匹配"z"或"food"。'(z|f)ood' 匹配"zood"或"food"。

[xyz]
字符集。匹配包含的任一字符。例如，"[abc]"匹配"plain"中的"a"。

[^xyz]
反向字符集。匹配未包含的任何字符。例如，"[^abc]"匹配"plain"中"p"，"l"，"i"，"n"。

[a-z]
字符范围。匹配指定范围内的任何字符。例如，"[a-z]"匹配"a"到"z"范围内的任何小写字母。

[^a-z]
反向范围字符。匹配不在指定的范围内的任何字符。例如，"[^a-z]"匹配任何不在"a"到"z"范围内的任何字符。

\b
匹配一个字边界，即字与空格间的位置。例如，"er\b"匹配"never"中的"er"，但不匹配"verb"中的"er"。

\B
非字边界匹配。"er\B"匹配"verb"中的"er"，但不匹配"never"中的"er"。

\cx
匹配 x 指示的控制字符。例如，\cM 匹配 Control-M 或回车符。x 的值必须在 A-Z 或 a-z 之间。如果不是这样，则假定 c 就是"c"字符本身。

\d
数字字符匹配。等效于 [0-9]。

\D
非数字字符匹配。等效于 [^0-9]。

\f
换页符匹配。等效于 \x0c 和 \cL。

\n
换行符匹配。等效于 \x0a 和 \cJ。

\r
匹配一个回车符。等效于 \x0d 和 \cM。

\s
匹配任何空白字符，包括空格、制表符、换页符等。与 [ \f\n\r\t\v] 等效。

\S
匹配任何非空白字符。与 [^ \f\n\r\t\v] 等效。

\t
制表符匹配。与 \x09 和 \cI 等效。

\v
垂直制表符匹配。与 \x0b 和 \cK 等效。

\w
匹配任何字类字符，包括下划线。与"[A-Za-z0-9_]"等效。

\W
与任何非单词字符匹配。与"[^A-Za-z0-9_]"等效。

\xn
匹配 n，此处的 n 是一个十六进制转义码。十六进制转义码必须正好是两位数长。例如，"\x41"匹配"A"。"\x041"与"\x04"&"1"等效。允许在正则表达式中使用 ASCII 代码。

\num
匹配 num，此处的 num 是一个正整数。到捕获匹配的反向引用。例如，"(.)\1"匹配两个连续的相同字符。

\n
标识一个八进制转义码或反向引用。如果 \n 前面至少有 n 个捕获子表达式，那么 n 是反向引用。否则，如果 n 是八进制数 (0-7)，那么 n 是八进制转义码。

\nm
标识一个八进制转义码或反向引用。如果 \nm 前面至少有 nm 个捕获子表达式，那么 nm 是反向引用。如果 \nm 前面至少有 n 个捕获，则 n 是反向引用，后面跟有字符 m。如果两种前面的情况都不存在，则 \nm 匹配八进制值 nm，其中 n 和 m 是八进制数字 (0-7)。

\nml
当 n 是八进制数 (0-3)，m 和 l 是八进制数 (0-7) 时，匹配八进制转义码 nml。

\\un
匹配 n，其中 n 是以四位十六进制数表示的 Unicode 字符。例如，\u00A9 匹配版权符号 (©)。

根据 Java Language Specification 的要求，Java 源代码的字符串中的反斜线被解释为 Unicode 转义或其他字符转义。因此必须在字符串字面值中使用两个反斜线，表示正则表达式受到保护，不被 Java 字节码编译器解释。例如，当解释为正则表达式时，字符串字面值 "\b" 与单个退格字符匹配，而 "\\b" 与单词边界匹配。字符串字面值 "\(hello\)" 是非法的，将导致编译时错误；要与字符串 (hello) 匹配，必须使用字符串字面值 "\\(hello\\)"。
*/






/*Matcher 类的方法
索引方法
索引方法提供了有用的索引值，精确表明输入字符串中在哪能找到匹配：

方法及说明
public int start()
返回以前匹配的初始索引。

public int start(int group)
 返回在以前的匹配操作期间，由给定组所捕获的子序列的初始索引

public int end()
返回最后匹配字符之后的偏移量。

public int end(int group)
返回在以前的匹配操作期间，由给定组所捕获子序列的最后字符之后的偏移量。


查找方法
查找方法用来检查输入字符串并返回一个布尔值，表示是否找到该模式：

方法及说明
public boolean lookingAt()
 尝试将从区域开头开始的输入序列与该模式匹配。

public boolean find()
尝试查找与该模式匹配的输入序列的下一个子序列。

public boolean find(int start）
重置此匹配器，然后尝试查找匹配该模式、从指定索引开始的输入序列的下一个子序列。

public boolean matches()
尝试将整个区域与模式匹配。


替换方法
替换方法是替换输入字符串里文本的方法：

方法及说明
public Matcher appendReplacement(StringBuffer sb, String replacement)
实现非终端添加和替换步骤。

public StringBuffer appendTail(StringBuffer sb)
实现终端添加和替换步骤。

public String replaceAll(String replacement)
 替换模式与给定替换字符串相匹配的输入序列的每个子序列。

public String replaceFirst(String replacement)
 替换模式与给定替换字符串匹配的输入序列的第一个子序列。

public static String quoteReplacement(String s)
返回指定字符串的字面替换字符串。这个方法返回一个字符串，就像传递给Matcher类的appendReplacement 方法一个字面字符串一样工作。
*/



/*PatternSyntaxException 类的方法
PatternSyntaxException 是一个非强制异常类，它指示一个正则表达式模式中的语法错误。
PatternSyntaxException 类提供了下面的方法来帮助我们查看发生了什么错误。

方法及说明
public String getDescription()
获取错误的描述。

public int getIndex()
 获取错误的索引。

public String getPattern()
获取错误的正则表达式模式。

public String getMessage()
返回多行字符串，包含语法错误及其索引的描述、错误的正则表达式模式和模式中错误索引的可视化指示。
*/

	private static final String REGEX = "\\bcat\\b";
	private static final String INPUT = "cat cat cat cattie cat";


	private static final String REGEX1 = "foo";
	private static final String INPUT1 = "fooooooooooooooooo";
	private static final String INPUT2 = "ooooofoooooooooooo";
	private static Pattern pattern1;
	private static Matcher matcher;
	private static Matcher matcher2;


	private static String REGEX2 = "dog";
	private static String INPUT3 = "The dog says meow. " + "All dogs say meow.";
	private static String REPLACE = "cat";

	private static String REGEX3 = "a*b";
	private static String INPUT4 = "aabfooaabfooabfoobkkk";
	private static String REPLACE1 = "-";

	public static TestRegularExpression testRegularExpression (){
		//使用正则表达式 .*runoob.* 查找字符串中是否包了 runoob 子串
		String content = "I am noob " + "from runoob.com.";

		String pattern = ".*runoob.*";

		boolean isMatch = Pattern.matches(pattern, content);
		System.out.println("字符串中是否包含了 'runoob' 子字符串? " + isMatch);

		System.out.println("————————————————————————");

/*捕获组
捕获组是把多个字符当一个单独单元进行处理的方法，它通过对括号内的字符分组来创建。
例如，正则表达式 (dog) 创建了单一分组，组里包含"d"，"o"，和"g"。
捕获组是通过从左至右计算其开括号来编号。例如，在表达式（（A）（B（C））），有四个这样的组：
((A)(B(C)))
(A)
(B(C))
(C)
可以通过调用 matcher 对象的 groupCount 方法来查看表达式有多少个分组。groupCount 方法返回一个 int 值，表示matcher对象当前有多个捕获组。
还有一个特殊的组（group(0)），它总是代表整个表达式。该组不包括在 groupCount 的返回值中。
*/


		// 按指定模式在字符串查找
		String line = "This order was placed for QT3000! OK?";
		String pattern1 = "(\\D*)(\\d+)(.*)";

		// 创建 Pattern 对象
		Pattern r = Pattern.compile(pattern1);

		// 现在创建 matcher 对象
		Matcher m = r.matcher(line);
		if (m.find( )) {
			System.out.println("Found value: " + m.group(0) );
			System.out.println("Found value: " + m.group(1) );
			System.out.println("Found value: " + m.group(2) );
			System.out.println("Found value: " + m.group(3) );
		} else {
			System.out.println("NO MATCH");
		}

		System.out.println("————————————————————————");

		//start 和 end 方法
		//对单词 "cat" 出现在输入字符串中出现次数进行计数

		Pattern p = Pattern.compile(REGEX);
		Matcher m1 = p.matcher(INPUT); // 获取 matcher 对象
		int count = 0;

		while(m1.find()) {
			count++;
			System.out.println("Match number "+count);
			System.out.println("start(): "+m1.start());
			System.out.println("end(): "+m1.end());

		}
		//使用单词边界，以确保字母 "c" "a" "t" 并非仅是一个较长的词的子串。它也提供了一些关于输入字符串中匹配发生位置的有用信息。
		//Start 方法返回在以前的匹配操作期间，由给定组所捕获的子序列的初始索引，end 方法最后一个匹配字符的索引加 1。

		System.out.println("————————————————————————");

		/*matches 和 lookingAt 方法
		matches 和 lookingAt 方法都用来尝试匹配一个输入序列模式。它们的不同是 matches 要求整个序列都匹配，而lookingAt 不要求。
		lookingAt 方法虽然不需要整句都匹配，但是需要从第一个字符开始匹配。
		这两个方法经常在输入字符串的开始使用。
		*/
		p = Pattern.compile(REGEX1);
		matcher = p.matcher(INPUT1);
		matcher2 = p.matcher(INPUT2);

		System.out.println("Current REGEX is: "+REGEX1);
		System.out.println("Current INPUT is: "+INPUT1);
		System.out.println("Current INPUT2 is: "+INPUT2);


		System.out.println("lookingAt(): "+matcher.lookingAt());
		System.out.println("matches(): "+matcher.matches());
		System.out.println("lookingAt(): "+matcher2.lookingAt());

		System.out.println("————————————————————————");

		/*replaceFirst 和 replaceAll 方法
		replaceFirst 和 replaceAll 方法用来替换匹配正则表达式的文本。不同的是，replaceFirst 替换首次匹配，replaceAll 替换所有匹配。
		*/
		Pattern patten1 = Pattern.compile(REGEX2);
		// get a matcher object
		Matcher m2 = patten1.matcher(INPUT3);
		INPUT3 = m2.replaceAll(REPLACE);
		System.out.println(INPUT3);

		System.out.println("————————————————————————");

		/*appendReplacement 和 appendTail 方法
		Matcher 类也提供了appendReplacement 和 appendTail 方法用于文本替换：*/

		Pattern p2 = Pattern.compile(REGEX3);
		// 获取 matcher 对象
		Matcher m3 = p2.matcher(INPUT4);
		StringBuffer sb = new StringBuffer();
		while(m3.find()){
			m3.appendReplacement(sb,REPLACE1);
		}
		m3.appendTail(sb);
		System.out.println(sb.toString());
		System.out.println("————————————————————————————————————————————————");
		return new TestRegularExpression();
	}
}

//两数之和
/*
给定一个整数数组 nums和一个整数目标值 target，请你在该数组中找出 和为目标值 target 的那两个整数，并返回它们的数组下标。
你可以假设每种输入只会对应一个答案。但是，数组中同一个元素在答案里不能重复出现。
你可以按任意顺序返回答案。
 */
class LeetCodeOne {

	//暴力枚举
	/*
	 *思路及算法
	 * 最容易想到的方法是枚举数组中的每一个数 x，寻找数组中是否存在 target - x。
	 * 当我们使用遍历整个数组的方式寻找 target - x 时，需要注意到每一个位于 x 之前的元素都已经和 x 匹配过，因此不需要再进行匹配。而每一个元素不能被使用两次，所以我们只需要在 x 后面的元素中寻找 target - x。
	 *
	 * 复杂度分析
	 * 时间复杂度：O(N^2)，其中 NN 是数组中的元素数量。最坏情况下数组中任意两个数都要被匹配一次。
	 * 空间复杂度：O(1)O(1)。
	 * */
	// 时间复杂度：O(n^2)(n为数组的长度)
	// 空间复杂度：O(1)
	public int[] twoSum(int[] nums, int target) {
		if (nums == null || nums.length == 0) return new int[0];

		for (int i = 0; i < nums.length; i++) {
			int x = nums[i];
			// 线性查找 - O(n)
			for (int j = i + 1; j < nums.length; j++) {
				if (nums[j] == target - x) {
					return new int[]{i, j};
				}
			}
		}
		return new int[0];
	}

	//哈希表
	/*
	思路及算法
	注意到方法一的时间复杂度较高的原因是寻找 target - x 的时间复杂度过高。因此，我们需要一种更优秀的方法，能够快速寻找数组中是否存在目标元素。如果存在，我们需要找出它的索引。
	使用哈希表，可以将寻找 target - x 的时间复杂度降低到从 O(N)O(N) 降低到 O(1)O(1)。
	这样我们创建一个哈希表，对于每一个 x，我们首先查询哈希表中是否存在 target - x，然后将 x 插入到哈希表中，即可保证不会让 x 和自己匹配。
	 */
	public int[] twoSums(int[] nums, int target) {
		Map<Integer, Integer> hashtable = new HashMap<Integer, Integer>();
		for (int i = 0; i < nums.length; ++i) {
			if (hashtable.containsKey(target - nums[i])) {
				return new int[]{hashtable.get(target - nums[i]), i};
			}
			hashtable.put(nums[i], i);
		}
		return new int[0];
	}
}

//两数相加
/*给你两个非空 的链表，表示两个非负的整数。它们每位数字都是按照逆序的方式存储的，并且每个节点只能存储一位数字。
请你将两个数相加，并以相同形式返回一个表示和的链表。
你可以假设除了数字 0 之外，这两个数都不会以 0开头。

注意：
每个链表中的节点数在范围 [1, 100] 内
0 <= Node.val <= 9
题目数据保证列表表示的数字不含前导零
*/
class LeetCodeTow {
	/**
	 * Definition for singly-linked list.
	 * class ListNode {
	 * int val;
	 * ListNode next;
	 * ListNode() {}
	 * ListNode(int val) { this.val = val; }
	 * ListNode(int val, ListNode next) { this.val = val; this.next = next; }
	 * }
	 */
	public LeetCodeTowListNode addTwoNumbers(LeetCodeTowListNode l1, LeetCodeTowListNode l2) {
		/*
		 * 思路与算法
		 * 由于输入的两个链表都是逆序存储数字的位数的，因此两个链表中同一位置的数字可以直接相加。
		 * 我们同时遍历两个链表，逐位计算它们的和，并与当前位置的进位值相加。具体而言，如果当前两个链表处相应位置的数字为 n1,n2，进位值为 carry，则它们的和为 n1+n2+carry；其中，答案链表处相应位置的数字为 (n1+n2+carry)mod 10，而新的进位值为 (n1+n2+carry)/1010
		 * 如果两个链表的长度不同，则可以认为长度短的链表的后面有若干个 00 。
		 * 此外如果链表遍历结束后，有 carry>0，还需要在答案链表的后面附加一个节点，节点的值为 carry。
		 * */
		/*
		 * 复杂度分析
		 * 时间复杂度：O(\max(m,n))O(max(m,n))，其中 mm 和 nn 分别为两个链表的长度。我们要遍历两个链表的全部位置，而处理每个位置只需要 O(1)O(1) 的时间。
		 * 空间复杂度：O(1)O(1)。注意返回值不计入空间复杂度。
		 * */
		LeetCodeTowListNode head = null, tail = null;
		int carry = 0;
		while (l1 != null || l2 != null) {
			int n1 = l1 != null ? l1.val : 0;
			int n2 = l2 != null ? l2.val : 0;
			int sum = n1 + n2 + carry;
			if (head == null) {
				head = tail = new LeetCodeTowListNode(sum % 10);
			} else {
				tail.next = new LeetCodeTowListNode(sum % 10);
				tail = tail.next;
			}
			carry = sum / 10;
			if (l1 != null) {
				l1 = l1.next;
			}
			if (l2 != null) {
				l2 = l2.next;
			}
		}
		if (carry > 0) {
			tail.next = new LeetCodeTowListNode(carry);
		}
		return head;
	}
}
class LeetCodeTowListNode {
	public int val;
	public LeetCodeTowListNode next;

	public LeetCodeTowListNode(int i) {
	}
}

//3. 无重复字符的最长子串
/* 给定一个字符串 s ，请你找出其中不含有重复字符的 最长子串 的长度。
 * 0 <= s.length <= 5 * 104
 * s 由英文字母、数字、符号和空格组成
 * */
class LeetCodeThree {

/*
* 方法一：滑动窗口
思路和算法

我们先用一个例子考虑如何在较优的时间复杂度内通过本题。

我们不妨以示例一中的字符串 \texttt{abcabcbb}abcabcbb 为例，找出从每一个字符开始的，不包含重复字符的最长子串，那么其中最长的那个字符串即为答案。对于示例一中的字符串，我们列举出这些结果，其中括号中表示选中的字符以及最长的字符串：

以 \texttt{(a)bcabcbb}(a)bcabcbb 开始的最长字符串为 \texttt{(abc)abcbb}(abc)abcbb；
以 \texttt{a(b)cabcbb}a(b)cabcbb 开始的最长字符串为 \texttt{a(bca)bcbb}a(bca)bcbb；
以 \texttt{ab(c)abcbb}ab(c)abcbb 开始的最长字符串为 \texttt{ab(cab)cbb}ab(cab)cbb；
以 \texttt{abc(a)bcbb}abc(a)bcbb 开始的最长字符串为 \texttt{abc(abc)bb}abc(abc)bb；
以 \texttt{abca(b)cbb}abca(b)cbb 开始的最长字符串为 \texttt{abca(bc)bb}abca(bc)bb；
以 \texttt{abcab(c)bb}abcab(c)bb 开始的最长字符串为 \texttt{abcab(cb)b}abcab(cb)b；
以 \texttt{abcabc(b)b}abcabc(b)b 开始的最长字符串为 \texttt{abcabc(b)b}abcabc(b)b；
以 \texttt{abcabcb(b)}abcabcb(b) 开始的最长字符串为 \texttt{abcabcb(b)}abcabcb(b)。
发现了什么？如果我们依次递增地枚举子串的起始位置，那么子串的结束位置也是递增的！这里的原因在于，假设我们选择字符串中的第 kk 个字符作为起始位置，并且得到了不包含重复字符的最长子串的结束位置为 rk。那么当我们选择第 k+1k+1 个字符作为起始位置时，首先从 k+1k+1 到 rk的字符显然是不重复的，并且由于少了原本的第 kk 个字符，我们可以尝试继续增大 rk ，直到右侧出现了重复字符为止。

这样一来，我们就可以使用「滑动窗口」来解决这个问题了：
（1）我们使用两个指针表示字符串中的某个子串（或窗口）的左右边界，其中左指针代表着上文中「枚举子串的起始位置」，而右指针即为上文中的 r k ；
（2）在每一步的操作中，我们会将左指针向右移动一格，表示 我们开始枚举下一个字符作为起始位置，然后我们可以不断地向右移动右指针，但需要保证这两个指针对应的子串中没有重复的字符。在移动结束后，这个子串就对应着 以左指针开始的，不包含重复字符的最长子串。我们记录下这个子串的长度；
（3）在枚举结束后，我们找到的最长的子串的长度即为答案。

判断重复字符
在上面的流程中，我们还需要使用一种数据结构来判断 是否有重复的字符，常用的数据结构为哈希集合（即 C++ 中的 std::unordered_set，Java 中的 HashSet，Python 中的 set, JavaScript 中的 Set）。在左指针向右移动的时候，我们从哈希集合中移除一个字符，在右指针向右移动的时候，我们往哈希集合中添加一个字符。
至此，我们就完美解决了本题。*/
/*
* 复杂度分析
时间复杂度：O(N)O(N)，其中 NN 是字符串的长度。左指针和右指针分别会遍历整个字符串一次。
空间复杂度：O(∣Σ∣)，其中 Σ 表示字符集（即字符串中可以出现的字符），∣Σ∣ 表示字符集的大小。在本题中没有明确说明字符集，因此可以默认为所有 ASCII 码在 [0, 128)[0,128) 内的字符，即 ∣Σ∣=128。我们需要用到哈希集合来存储出现过的字符，而字符最多有 ∣Σ∣ 个，因此空间复杂度为 O(∣Σ∣)。
*/

	public int lengthOfLongestSubstring(String s) {
		// 哈希集合，记录每个字符是否出现过
		Set<Character> occ = new HashSet<Character>();
		int n = s.length();
		// 右指针，初始值为 -1，相当于我们在字符串的左边界的左侧，还没有开始移动
		int rk = -1, ans = 0;
		for (int i = 0; i < n; ++i) {
			if (i != 0) {
				// 左指针向右移动一格，移除一个字符
				occ.remove(s.charAt(i - 1));
			}
			while (rk + 1 < n && !occ.contains(s.charAt(rk + 1))) {
				// 不断地移动右指针
				occ.add(s.charAt(rk + 1));
				++rk;
			}
			// 第 i 到 rk 个字符是一个极长的无重复字符子串
			ans = Math.max(ans, rk - i + 1);
		}
		return ans;
	}
}































