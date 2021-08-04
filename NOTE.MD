           NEXT
           ↑   ↖
           |     ↖
           |       ↖      
K--------→S---------→BE
↑         ↑| ↖↘         
|         ||    ↖↘       
|         |↓       ↖↘     
|   C←-----V-->BSS-->E   
|    ↘     |         |
|      ↘   |         |
|        ↘ ↓         | 
 ----------BMS←------  

KS -> V         :k-v need value
VS -> BE        :block end
ES -> E         :next entry
   -> BE        :block sequence close
BE -> NEXT      :to NEXT
S  -> NEXT      :to NEXT
V  -> S         :k-v complete
   -> C         :get sub class name
   -> BMS       :sub mapping start
   -> BSS       :sub sequence start
C  -> BMS       :sub class start
BMS-> K         :first k-v
BSS-> E         :first entry
E  -> S         :value of entry
   -> BMS       :start sub block mapping
