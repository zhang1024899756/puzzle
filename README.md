# puzzle
Android拼图游戏，使用Android studio运行源代码，附上开发报告，玩家排行榜功能现在不能用了，因为服务器干别的去了，可以自己在本地创建一个后台服务，我也在note里留了个[server](https://github.com/zhang1024899756/notes/tree/master/puzzle_server)模板


![界面](./picture/jiemian1.png)

### version

```
android {
    compileSdkVersion 28
    defaultConfig {
        applicationId "com.game.puzzle"
        minSdkVersion 14
        targetSdkVersion 26
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
    productFlavors {
    }
    buildToolsVersion '28.0.3'
}
```

