# Boot-Practice

csdn地址:  [https://blog.csdn.net/qq_41773026/article/details/117250511?spm=1001.2014.3001.5501](https://blog.csdn.net/qq_41773026/article/details/117250511?spm=1001.2014.3001.5501)

	2021-05-25日  早11点
> 本文主要意思是：从github上创建一个项目，然后拉到本地，创建Springboot多模块项目，同时能够及时同步到github。为java初学者提供便利。

> 环境，pc一台，IDEA2020 git maven3.6.5 Java环境。

# 1 github新建项目
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210525104324381.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxNzczMDI2,size_16,color_FFFFFF,t_70)

# 2 调试项目初始环境
## 2-1 下拉项目
> 点击clone ssh 然后
> 在本地，   git clone git@github.com:1367379258/Boot-Practice.git
> 进入Boot-Practice 目录，git init,    git config --list 
> 如果没有name 和email 此时

> git config --local user.name "1367379258"
> git config --local user.email "1367379258@qq.com"
> git config --global  http.postbuffer 1048576000  缓冲区放大

ok，此时git下拉完毕，准备IDEA打开项目，构建多模块项目。

> 注意.idea 下的文件, 不要提交
> https://blog.csdn.net/Maxiao1204/article/details/116995886?spm=1001.2101.3001.6650.1&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-1-116995886-blog-89487264.pc_relevant_antiscanv4&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-1-116995886-blog-89487264.pc_relevant_antiscanv4&utm_relevant_index=2

## 2-2 构建项目
> 首先，我们把此项目变为 maven项目， 
>![在这里插入图片描述](https://img-blog.csdnimg.cn/20210525110713822.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxNzczMDI2,size_16,color_FFFFFF,t_70)
> 右击，选中Add Framework support 然后选中maven，此时项目下就会出现一个pom.xml 文件

> 我们进入这个文件，根据自己项目，然后修改pom.xml

```javascript
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.2.RELEASE</version>
        <relativePath/>
    </parent>

    <groupId>com.xlg</groupId>
    <artifactId>Boot-Practice</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <name>Boot-Practice</name>
    <description>练习</description>

    <scm>
        <connection>scm:git:git@github.com:1367379258/Boot-Practice.git</connection>
        <developerConnection>scm:git:git@github.com:1367379258/Boot-Practice.git
        </developerConnection>
        <tag>HEAD</tag>
    </scm>

    <properties>
        <java.version>1.8</java.version>
        <revision>1.0.0-SNAPSHOT</revision>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <junit.version>4.12</junit.version>
        <lombok.version>1.18.10</lombok.version>
        <log4j.version>1.2.17</log4j.version>
        <mysql.version>5.1.40</mysql.version>
        <druid.version>1.1.20</druid.version>
        <boot.version>2.2.2.RELEASE</boot.version>
    </properties>

    <!-- 开发人员说明 -->
    <developers>
        <developer>
            <name>wangqingwei</name> <!-- 改对 -->
        </developer>
    </developers>

    <dependencyManagement>
        <dependencies>
        <!--  暂时不需要这两个，后续会需要 -->
<!--            <dependency>-->
<!--                <groupId>com.xlg</groupId>-->
<!--                <artifactId>xili-task-component</artifactId>-->
<!--                <version>${project.version}</version>-->
<!--            </dependency>-->

<!--            <dependency>-->
<!--                <groupId>com.xlg</groupId>-->
<!--                <artifactId>xili-task-sdk</artifactId>-->
<!--                <version>${project.version}</version>-->
<!--            </dependency>-->

            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-test</artifactId>
                <scope>test</scope>
            </dependency>

            <!--mysql-->
            <dependency>
                <groupId>mysql</groupId>
                <artifactId>mysql-connector-java</artifactId>
                <version>${mysql.version}</version>
            </dependency>
            <!-- druid-->
            <dependency>
                <groupId>com.alibaba</groupId>
                <artifactId>druid-spring-boot-starter</artifactId>
                <version>${druid.version}</version>
            </dependency>
            <!--junit-->
            <dependency>
                <groupId>junit</groupId>
                <artifactId>junit</artifactId>
                <version>${junit.version}</version>
            </dependency>
            <!--log4j-->
            <dependency>
                <groupId>log4j</groupId>
                <artifactId>log4j</artifactId>
                <version>${log4j.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <!-- 发布时必须依赖的插件 -->
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>flatten-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

此时，一个初步的项目构建完毕，接下来，我们构建cms后台， component公共模块，api(h5 小程序), sdk(外部引用)，runner(consumer/rpc) 模块。


## 2-3 构建多模块
### 2-3-1 首先是sdk
点击Boot-Practice 项目，右键，new module ,选择maven 

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210525112849505.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxNzczMDI2,size_16,color_FFFFFF,t_70)

直接next,  ![在这里插入图片描述](https://img-blog.csdnimg.cn/2021052511135812.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxNzczMDI2,size_16,color_FFFFFF,t_70)
注意，选择父Parent。然后next。 修改pom

```javascript
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>xili-task</artifactId>
        <groupId>com.xlg</groupId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>boot-practice-sdk</artifactId>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
    </dependencies>

</project>
```
OK。此时sdk模块构建完毕。

### 2-3-2 component模块
还是，add maven   boot-practice-component

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210525115227609.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxNzczMDI2,size_16,color_FFFFFF,t_70)
pom.xml 
```javascript
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>Boot-Practice</artifactId>
        <groupId>com.xlg</groupId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>boot-practice-component</artifactId>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <!-- sdk -->
        <dependency>
            <groupId>com.xlg</groupId>
            <artifactId>boot-practice-sdk</artifactId>
        </dependency>

        <!-- https://mvnrepository.com/artifact/com.github.phantomthief/more-lambdas -->
        <dependency>
            <groupId>com.github.phantomthief</groupId>
            <artifactId>more-lambdas</artifactId>
            <version>0.1.54</version>
        </dependency>

        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi</artifactId>
            <version>3.17</version>
        </dependency>
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi-ooxml</artifactId>
            <version>3.17</version>
        </dependency>
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi-ooxml-schemas</artifactId>
            <version>3.17</version>
        </dependency>

        <!-- test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <version>${boot.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>antlr</groupId>
            <artifactId>antlr</artifactId>
            <version>2.7.2</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.47</version>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-collections4</artifactId>
            <version>4.0</version>
        </dependency>
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>27.0.1-jre</version>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.9</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>

        <dependency>
            <groupId>org.apache.rocketmq</groupId>
            <artifactId>rocketmq-spring-boot-starter</artifactId>
            <version>2.0.3</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/joda-time/joda-time -->
        <dependency>
            <groupId>joda-time</groupId>
            <artifactId>joda-time</artifactId>
            <version>2.10</version>
        </dependency>
        <!--redis-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>
        <!--监控-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- 下面这段 sdk 不用增加，只有 component/api/runner 需要增加，避免 deploy 出去 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-deploy-plugin</artifactId>
                <configuration>
                    <skip>true</skip>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
```

此时，放开 Boot-Practice 下面的  pom.xml 中 
````javascript
       <dependency>
                <groupId>com.xlg</groupId>
                <artifactId>boot-practice-component</artifactId>
                <version>${project.version}</version>
            </dependency>

            <dependency>
                <groupId>com.xlg</groupId>
                <artifactId>boot-practice-sdk</artifactId>
                <version>${project.version}</version>
            </dependency>
````

然后再terminal  中  进入 Boot-Practice 目录下: 
maven clean package  打包，成功即可进入下一步。

### 2-3-2 runner模块
还是老样子， add maven  填写name boot-practice-runner

然后给pom.xml 中加入
```javascript
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>Boot-Practice</artifactId>
        <groupId>com.xlg</groupId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>boot-practice-runner</artifactId>
    <packaging>jar</packaging>


    <dependencies>

        <!-- component -->
        <dependency>
            <groupId>com.xlg</groupId>
            <artifactId>boot-practice-component</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <version>${boot.version}</version>
            <scope>test</scope>
        </dependency>

    </dependencies>


    <build>
        <!-- 更改打包之后的 jar name，去掉 version 信息，便于部署 -->
        <finalName>${project.artifactId}</finalName>
        <plugins>
            <!-- 下面这段 sdk 不用增加，只有 component/api/runner 需要增加，避免 deploy 出去 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-deploy-plugin</artifactId>
                <configuration>
                    <skip>true</skip>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>copy-dependencies</goal>
                        </goals>
                        <configuration>
                            <includeScope>runtime</includeScope>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```
同样， maven clean package 

### 2-3-4 cms模块
此时，我们要注意，这个模块是对外暴露的模块，所以是一个web模块。
还是老样子，直接去add maven   boot-practice-cms-api

然后，因为是一个web项目，直接去add framework support 加入一个web.xml 
然后把web目录 迁移到，   并改名为webapp. 。![在这里插入图片描述](https://img-blog.csdnimg.cn/20210525122557742.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxNzczMDI2,size_16,color_FFFFFF,t_70)

然后点击， 最后那个，进入![在这里插入图片描述](https://img-blog.csdnimg.cn/20210525122627642.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210525122650952.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxNzczMDI2,size_16,color_FFFFFF,t_70)
修改resource 指定webapp目录。 
然后在resource目录下新建，这几个目录![在这里插入图片描述](https://img-blog.csdnimg.cn/2021052512272873.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxNzczMDI2,size_16,color_FFFFFF,t_70)
application.yml
```javascript
server:
  port: 8081   # web\u5E94\u7528\u670D\u52A1\u7AEF\u53E3



spring:
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8

  #热加载生效
  devtools:
    restart:
      enabled: true
      #额外新增的热加载目录
      additional-paths: src/main/java
    #热加载排除目录
    #spring.devtools.restart.exclude=

  # 默认是以  开发环境
  profiles:
    active: prod
```

application-prod.yml
```javascript
server:
    port: 8082   # web\u5E94\u7528\u670D\u52A1\u7AEF\u53E3


spring:
    thymeleaf:
        cache: false # 启用缓存:建议生产开启

        check-template-location: true # 检查模版是否存在
        enabled: true # 是否启用
        encoding: UTF-8 # 模版编码
        excluded-view-names: # 应该从解析中排除的视图名称列表（用逗号分隔）
        mode: HTML5 # 模版模式
        #        mode: LEGACYHTML5
        classic_compile: true
        suffix: .html # 模版后缀
        prefix: classpath:/templates/ # 模版存放路径

    servlet:
        multipart:
            max-file-size:  100MB
            max-request-size: 1000MB

    datasource:
        url: jdbc:mysql://127.0.0.1:3306/xlgTask?useUnicode=true&characterEncoding=utf-8&useSSL=false
        username: root
        password: 123456
        driver-class-name: com.mysql.jdbc.Driver
    http:
        encoding:
            # 编码集
            charset: utf-8
            enabled: true

web:
    upload-path: /Users/easy/others/gitPro/xili-task/boot-practice-cms-api/src/main/resources/static/upload/

debug: true

constant:
    cookies:
        prefix:
            user:
                "cookie_prefix_user"
            role:
                "cookie_prefix_role"

logging:
    level:
        com.qw.bootlearn: debug
```


然后在main 目录下新建，  com.xlg.cms.api  目录
然后新建一个启动类Application.java

```javascript
package com.xlg.cms.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 */
@SpringBootApplication(scanBasePackages = "com.xlg")
@ServletComponentScan
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}

```
右键启动，如果启动不起来，把所有pom下依赖的 redis 和mysql等等都删除即可。

下面是启动之后的图: 
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210525123130443.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQxNzczMDI2,size_16,color_FFFFFF,t_70)

ok。 多模块项目构建完毕。
