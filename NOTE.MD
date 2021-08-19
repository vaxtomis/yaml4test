           BE
       ↙   ↑
     ↙     |
   ↙       |
K←--------→S
↑          ↑| ↖↘         
|          ||    ↖↘       
|          |↓       ↖↘     
|          V--->BSS--->E   
|         ↙|        ↙  |
|       ↙  |     ↙     |
|     ↙    ↓  ↙        |
|   ↙      C           |
| ↙     ↙              |
|    ↙                 | 
BMS←-------------------  

CONSTRAINTS:
KS  -> V         :k-v need value
VS  -> BE        :block end
    -> K         :next key
ES  -> E         :next entry
    -> BE        :block sequence close
BE  -> BE        :to BE
    -> K         :next k-v
    -> E
    -> SE
V   -> S         :k-v complete
    -> C         :get sub class name
    -> BMS       :sub mapping start
    -> BSS       :sub sequencing start
C   -> BMS       :sub class start
BMS -> K         :first k-v
BSS -> E         :first entry
E   -> S         :value of entry
    -> C         :get entry class name
    -> BMS       :start sub block mapping
    
Parser EXAMPLE:
<BMS> (push "Map" in)
    <GET_NAME> -> class                 {Map1: class -> !Class}
    <GET_CLASSNAME>
    <BMS> (push "class" in)             {Stack: class}
        <GET_NAME> -> B                 {Map2: B -> !TestPojo.classB, C -> CCC, D -> DDD}
        <GET_CLASSNAME> -> TestPojo.classB
            <BMS> (push "B" in)         {Stack: class, B}
                <GET_NAME> <GET_VALUE>  {Map3: suba -> "!aaa", subb -> bbb}
                <GET_NAME> <GET_VALUE>
            <BME> (pop "B" out) (Inject the map3 into B)
        <GET_NAME> <GET_VALUE>
        <GET_NAME> <GET_VALUE>
    <BMS> (pop "class" out) (Inject the map2 into class)
<BMS>

Parser EXAMPLE:
<BMS>
    <GET_NAME>
    <BSS>
        <GET_ENTRY (class)>
        <BMS>
            <GET_NAME> <GET_VALUE>
            <GET_NAME> <GET_VALUE>
        <BME>
        <GET_ENTRY (class)>
        <BMS>
            <GET_NAME> <GET_VALUE>
            <GET_NAME> <GET_VALUE>
        <BME>       
    <BSE>
<BME>    

Parser EXAMPLE:
<BMS>
    <GET_NAME>
    <BSS>
        <GET_ENTRY (value)>
        <GET_ENTRY (value)>
    <BSE>
<BMS>