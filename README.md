#yaml4test
开发中。（In dev）  
##版本功能
###0.0.1 基础功能
根据 yaml 文件读取并创建类实例对象，自动注入至类中。  
对测试类进行注解，添加 yaml 路径和文件名：  
```java
@Yaml4test(Path = "xxx.yml")
```
对需要自动注入的对象添加注解，默认映射相同的对象名：
```java
@YamlInject
```
或者可以指定映射的对象名：
```java
@YamlInject(Name = "")
```
在调用前调用静态方法进行加载：
```java
{
    YamlFactory.refreshFactory(this);
}
```
