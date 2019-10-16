# Simple Messaging MQTT (SMM)

### A tiny library for MQTT android client

![](https://i.ibb.co/pjR6Jp5/carbon-16.png)

## About
- This library based on [Paho](https://www.eclipse.org/paho/) from Eclipse
- Simple implementation in your activity or service
- Simple implementation with any MQTT Cloud
- Support java and kotlin

## Setup MQTT Cloud
Use any MQTT Cloud like [cloudmqtt.com](https://www.cloudmqtt.com/) or [cloudamqp.com](https://www.cloudamqp.com/) or whatever. <br>
Go to configuration, copy your server, port, username and password. Just like that!

## Download
[ ![Download](https://api.bintray.com/packages/kucingapes/utsman/com.utsman.messaging/images/download.svg) ](https://bintray.com/kucingapes/utsman/com.utsman.messaging/_latestVersion)
```gradle
implementation "com.utsman.messaging:smm:${latest_version}"

// for androidx support, just add
implementation 'androidx.legacy:legacy-support-v4:1.0.0'
```


## Let's code!

### Setup server
In ```AndroidManifest.xml```, add the following element as a child of the ```<application>``` element, by inserting it just before the closing ```</application>``` tag.

```xml
<meta-data android:name="com.utsman.smm.SERVER"
           android:value="tcp://server_url:port"/>

<meta-data android:name="com.utsman.smm.USERNAME"
           android:value="username"/>

<meta-data android:name="com.utsman.smm.PASSWORD" 
           android:value="password"/>

<meta-data android:name="com.utsman.smm.CLIENT_ID"
           android:value="client_id"/>
```

### Setup receiver in activity or fragment or service
```kotlin
Message.subscribe(context, "topic") { senderId, data ->
    // message arrived with JSONObject data
    val name = data.getString("name")
}
```

### Setup in button for publish data
```kotlin
// setup your JSONObject data
val data = JSONObject()
data.putString("name", "Sarah")

// publish your data
Message.publish(context, "topic", data)
```

---
```

Copyright 2019 Muhammad Utsman

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```