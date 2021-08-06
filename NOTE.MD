K←--------→S---------→BE----->NEXT
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
BE  -> NEXT      :to NEXT
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