# yaml4test
开发中。（In dev）
## 内容列表
- [背景](#背景)
- [使用说明](#使用说明)
## 背景
在复杂业务流情况下，对数据结构繁杂的 POJO 对象使用 Mock 测试，需要大量进行赋值操作。  
yaml4test 通过预先在 yaml 文件中定义属性值和类之间的包含关系，在测试中直接对所需对象进行注入（反序列化）。  
基于 ASM 对方法进行插桩自定义修改。（预开发中）  
通过注释和 Tag 对 POJO 对象值进行修改，用较少工作量帮助测试进行代码分支的覆盖。（预开发中）  

## 使用说明
### Yaml格式
以下基础类型和常用类型会由类中的属性类型自动转换：  
char, Character, String, Byte,  
short, Short, long, Long,   
double, Double, float, Float,  
int, Integer, BigInteger, BigDecimal  

以下为目前支持的 yaml 格式示例：  
```yaml
objectName: !qualified name of class
  property1: String
  property2: int
  property3: 
    - collection1
    - collection2
  property4:
    - !qualified name of class2
        property1 of subObj: BigDecimal
        property2 of subObj: long
    - !qualified name of class2
        property1 of subObj: BigDecimal
        property2 of subObj: Long
```
### 0.0.1 基础功能
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
可以指定映射对象返回是单例还是原型：
```java
// Singleton
@YamlInject(Scope = YamlInject.Scope.Singleton)
// Prototype
@YamlInject(Scope = YamlInject.Scope.Prototype)
```
在调用前调用静态方法进行加载：
```java
{
    YamlFactory.refreshFactory(this);
}
```
Utils 中的 BeanCopy 类，deepCopy(T source) 方法可以实现类实例的深度拷贝。  
原理是将类实例序列化为 EventList 后，再通过 Producer 进行生成。  
因此最底层部分的属性同样受上面定义的基础类型和常用类型限制。  