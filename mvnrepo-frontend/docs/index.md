# 公开仓库

```kotlin
repositories {
    maven("http://127.0.0.1:8088/releases")
}
```

```kotlin
repositories {
    maven("http://127.0.0.1:8088/snapshots")
}
```

# 私有仓库

```kotlin
repositories {
    maven("http://127.0.0.1:8088/private")
}
```

> 请把上面的地址改成你的服务器地址