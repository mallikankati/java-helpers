## Utility/Helpers methods which used in java projects

Big java project requires common utility/helper methods on top of existing frameworks. I compiled very useful helpers methods in different category. Please go through the source code for more details

These are the few usage examples which commonly appear in most of the projects. Look for more methods in respective `*Util and *Helper` classes.

**1. Date utilities**

  Convert current time to ISO8601Format
  
  ```java
   String iso8601DateStr = DateUtil.getIso8601Format(System.currentTimeMillis());
   ```
  
  Get start time of the given day and convert to ISO8601 string
   
   ```java
   long dayStarTime = DateUtil.getDayStartTime();
   String iso8601DateStr = DateUtil.getIso8601Format(dayStartTime);
   ```
   
  For more examples look at `DateUtil` class which has self explained methods
  
**2. Thread utilities**

   In order to span multiple tasks at the same time and wait for responses
      
     private Callable<String> create(final String message, final long sleepTime) {
	     Callable<String> c = new Callable<String>() {
	     
               @Override
               public String call() throws Exception {
                       logger.info("Executing task: " + Thread.currentThread().getId());
                       CommonUtil.sleep(sleepTime);
                       return message;
                }
            };
            return c;
     }
      
     List<Callable<String>> tasks = new ArrayList<>();
     tasks.add(create("Task1", 3000));
     tasks.add(create("Task2", 2000));
     tasks.add(create("Task3", 4000));
     List<String> results = ParallelTaskHelper.execute("Test", tasks);
     logger.info(results + "");
      
**3. Crypto utilities**
  
  `CryptUtil` contains all the cryptographic and encode/decode methods which commonly used in most of the projects. Ex: urlencode/decode and encrypt/decrypt strings
  
  To encrypt and decrpt the string
  
  ```java
   String xxx = "Hello World";
   String encrypt = CryptUtil.encrypt(xxx);
   logger.info(encrypt);
   logger.info(CryptUtil.decrypt(encrypt));
   ```
   To get MD5 checksum for a string
   
   ```java
   String str = "some string";
   String checksum = CryptUtil.md5Digest(str.getBytes("UTF-8"));
   ```
   
**4. Handling http calls**
  
  `HttpUtil` is a highly scalable URL handling methods in Java which developed based on apache httpclient library. It uses `PooledConnectionFactory` from apache library to optimize call to the same server.
  
  Simple usage of getting response from a URL
   
   ```java
   //Simple get request
   String response = HttpUtil.executeGet(<url>, null);
      
   //with custom headers
   Map<String, String> customHeaders = new HashMap<>();
   customHeaders.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5)");
   String response = HttpUtil.executeGet(url, "", customHeaders);
   logger.info(response);
      
   //download a file from internet and stores in specified directory
   HttpUtil.downloadFile(<url>, null, <directory>, <file>);
   ```
   
**5. Json handling**

  `JsonUtil` contains highly used json conversion methods. Here are the few examples
  
   ```java
   // converting json to java bean
   TestBean testBean = JsonUtil.fromJson(jsonString, TestBean.class);
      
   //converting Java bean to json
   String jsonString = JsonUtil.toJson(testBean);
      
   //converting json to java map, need to send concrete map and its key/value types
   Map<String, Object> map = JsonUtil.fromJson(jsonString, new TypeReference<Map<String, Object>>(){});
      
   //converting map to json
   String jsonString = JsonUtil.toJson(map);
      
   //converting json to list of java beans
   List<TestBean> testBeanList = JsonUtil.fromJson(jsonString, new TypeReference<List<TestBean>>(){})
      
   //converting list to json
   String jsonString = JsonUtil.toJson(testBeanList)
   ```
   
**6. Common helper functions** 

  `CommonUtil` contains utility methods 
   
   ```java
   //replacing all special characters from string
   String str = CommonUtil.replaceAllSpecialChars(<send dirty string>);
      
   //If file exceeds given size, it will roll over and generates new name
   String fileName = CommonUtil.rolloverFilename(String fileName, long size);
      
   //Converts image to base64 encoded string
   String base64EncodedStr = CommonUtil.convertImageToBase64Encode(<image file name>);
   ```
   
      
