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

计划下次会添加入自动转换的类：  
Date, LocalDate, LocalTime  

以下为目前支持的 yaml 格式示例：  
```yaml
objectName: !qualified name of class
  property1: this is first property (String example)
  property2: 1 (int example)
  property3: 
    - collection1 (String example)
    - collection2
  property4:
    - !qualified name of class2
        property1 of subObj: 1000.00 (BigDecimal example)
        property2 of subObj: 100.0L (long example)
    - !qualified name of class2
        property1 of subObj: 2000.00
        property2 of subObj: 200.0L (Long example)
```
### 0.0.1 基础功能
#### 1.根据 yaml 文件读取并创建类实例对象，手动获取或自动注入至类中  
对测试类进行注解，添加 yaml 路径和文件名：  
```java
@Yaml4test(Path = "xxx.yml")
```
选择是否跨包，CrossPack 表示在默认路径不为当前包，通过 Yaml 中的全路径名来寻找 Class：
```java
@Yaml4test(Path = "xxx.yml", Pack = Yaml4test.Pack.CrossPack)
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
全局获取实例：
```java
YamlFactory.getBean("");

//Example
Person jack = (Person) YamlFactory.getBean("jack");
```
在调用前调用代码块进行加载：
```java
{
    YamlFactory.refreshFactory(this);
}
```

#### 2.使用 BeanOperator 对实例进行操作  
Utils 中的 BeanOperator 类中的 deepCopy() 方法可以实现类实例的深度拷贝。  
原理是将类实例序列化为 EventList 后，再通过 Producer 进行生成。  
因此最底层部分的属性同样受上面定义的基础类型和常用类型限制。
```java
/**
 * 用作示例的实例类 Person
 */
Person {
  public String name;
  public int age;
  public String gender;
  
  Person(String name, int age, String gender) {
    this.name = name;
    this.age = age;
    this.gender = gender;
  }
}
```
```java
Person jack = new Person("Jack", 23, "Male");
// 拷贝出一个与 jack 实例值相同的独立的实例。
Person copy = BeanOperator.deepCopy(jack);
```
   
深拷贝的同时可以按需求修改新实例的值，通过 modifyCopy() 方法实现:  
```java
Person jack = new Person("Jack", 23, "Male");
//修改单条属性
Person copy = BeanOperator.modifyCopy(jack, "age", "24");
```
也可以对一个实例的多个属性值进行修改，需要创建 ModifyCollector:  
```java
Person jack = new Person("Jack", 23, "Male");
ModifyCollector collector = new ModifyCollector();
collector.add("name", "Rose");
collector.add("age", "24");
collector.add("gender", "Female");
// 同时修改多条属性
Person copy = BeanOperator.modifyCopy(jack, collector);
```
可以通过 ModifyCollector 中的修改值创建一组示例:  
```java
Person jack = new Person("Jack", 23, "Male");
ModifyCollector collector = new ModifyCollector();
collector.add("name", "Rose");
collector.add("age", "24");
collector.add("gender", "Female");
// 创建修改组合
Person[] groups = BeanOperator.createModifiedGroup(jack, collector);
```
对于较为复杂的类，填入的属性名示例：
```java
// 当根部的属性为集合时，以下标 [index]. 开头
// 修改 Person 集合的第 1 个人的 name。
collector.add("[0].name", "Jack");

// 当根部的属性非集合，子属性为集合时，直接在属性后添加 [index] 即可
collector.add("Group.Person[0].name", "Jack");
```