## Utility/Helpers methods which used in java projects

In a big projects it require common utility/helper functions on top of existing frameworks.

These are few examples which commonly appear in most of the java projects.

**1. Date utilities**

     In order to convert current time to ISO8601Format
     
     ```
     String iso8601DateStr = DateUtil.getIso8601Format(System.currentTimeMillis());
     ```
     
     To get start time of the given day and convert to ISO8601 string
     
     ```
     long dayStarTime = DateUtil.getDayStartTime();
     String iso8601DateStr = DateUtil.getIso8601Format(dayStartTime);
     ```
     
**2. Thread utilities**

**3. Crypto utilities**

**4. Handling http calls**

**5. Json handling**

**6. Common helper functions** 
